import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

public class SortBenchmark {

    // 측정할 리스트 길이들
    static final int[] SIZES = {1000, 5000, 10000, 100000};

    // 같은 조건에서 몇 번 반복해서 평균을 낼지
    static final int REPEAT = 3;

    // 한 번 정렬하는 데 이 시간(ms)을 넘기면 더 반복하지 않고,
    // 그 정렬 + 리스트 조합은 더 큰 n 에서 건너뜀 (안 그러면 몇 분씩 걸릴 수 있음)
    static final long TIME_LIMIT_MS = 2000;

    // 비교할 정렬들 (이름, 정렬 함수)
    static final String[] SORT_NAMES = {"Bubble", "Select", "Insert", "Merge", "Quick", "Heap"};
    static final UnaryOperator<int[]>[] SORTS = makeSorts();

    // 테스트할 리스트 종류들 (이름, 리스트 만드는 함수)
    static final String[] LIST_NAMES = {"완전 랜덤", "거의 정렬", "정렬됨", "역정렬", "거의 역정렬", "중복 많음"};
    static final IntFunction<int[]>[] LISTS = makeLists();

    @SuppressWarnings("unchecked")
    static UnaryOperator<int[]>[] makeSorts() {
        return new UnaryOperator[] {
            (UnaryOperator<int[]>) BubbleSort::getIntBubbleSort,
            (UnaryOperator<int[]>) SelectSort::getIntSelectSort,
            (UnaryOperator<int[]>) InsertSort::getIntInsertSort,
            (UnaryOperator<int[]>) MergeSort::getIntMergeSort,
            (UnaryOperator<int[]>) QuickSort::getIntQuickSort,
            (UnaryOperator<int[]>) HeapSort::getIntHeapSort,
        };
    }

    @SuppressWarnings("unchecked")
    static IntFunction<int[]>[] makeLists() {
        return new IntFunction[] {
            (IntFunction<int[]>) RandomListGenerator::getRandomList,
            (IntFunction<int[]>) RandomListGenerator::getNearlySortedList,
            (IntFunction<int[]>) RandomListGenerator::getSortedList,
            (IntFunction<int[]>) RandomListGenerator::getReversedList,
            (IntFunction<int[]>) RandomListGenerator::getNearlyReversedList,
            (IntFunction<int[]>) RandomListGenerator::getFewUniqueList,
        };
    }

    // 재귀 정렬(Quick 등)이 최악의 경우 깊이 n 까지 들어가도 버티도록 스택 크기를 키움
    // 기본 스택(보통 1MB 이하)이면 정렬된 리스트에서 QuickSort 가 StackOverflowError 가 남
    static final long STACK_SIZE = 256L * 1024 * 1024; // 256MB

    // main 스레드의 스택 크기는 코드로 못 바꾸니까,
    // 스택을 크게 잡은 새 스레드를 만들어서 거기서 벤치마크를 돌림
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(null, SortBenchmark::runBenchmark, "benchmark", STACK_SIZE);
        thread.start();
        thread.join();
    }

    static void runBenchmark() {
        warmUp();

        // skipped[s][l] 가 true 면 s번 정렬은 l번 리스트에서 너무 느렸다는 뜻
        boolean[][] skipped = new boolean[SORTS.length][LISTS.length];

        for (int n : SIZES) {
            System.out.println();
            System.out.println("===== n = " + n + " (단위: ms, " + REPEAT + "회 평균) =====");

            // 표 머리줄
            System.out.printf("%-12s", "");
            for (String name : SORT_NAMES) {
                System.out.printf("%10s", name);
            }
            System.out.println();

            for (int l = 0; l < LISTS.length; l++) {
                // 같은 n, 같은 종류면 모든 정렬이 똑같은 리스트를 받도록 한 번만 만듦
                RandomListGenerator.setSeed(42);
                int[] original = LISTS[l].apply(n);

                System.out.print(padKorean(LIST_NAMES[l], 12));
                for (int s = 0; s < SORTS.length; s++) {
                    String cell;
                    if (skipped[s][l]) {
                        cell = "-";
                    } else {
                        cell = measure(SORTS[s], original);
                        if (cell.endsWith("*")) {
                            skipped[s][l] = true;
                        }
                    }
                    System.out.printf("%10s", cell);
                }
                System.out.println();
            }
        }

        System.out.println();
        System.out.println("*  : " + TIME_LIMIT_MS + "ms 를 넘겨서 1회만 측정함 (이후 더 큰 n 은 건너뜀)");
        System.out.println("-  : 이전 n 에서 너무 느려서 건너뜀");
        System.out.println("SO : 재귀가 너무 깊어서 StackOverflowError 발생");
    }

    // 정렬 하나를 REPEAT 번 돌려서 평균 시간(ms)을 문자열로 돌려줌
    // 결과가 틀리면 "WRONG", 재귀가 너무 깊어서 터지면 "SO" 를 돌려줌
    static String measure(UnaryOperator<int[]> sort, int[] original) {
        int[] answer = original.clone();
        Arrays.sort(answer);

        long total = 0;
        int count = 0;
        boolean tooSlow = false;
        while (count < REPEAT) {
            // 원본을 망가뜨리지 않도록 매번 복사본을 넘김 (복사 시간은 측정에서 뺌)
            int[] input = original.clone();
            int[] result;

            long start = System.nanoTime();
            try {
                result = sort.apply(input);
            } catch (StackOverflowError e) {
                return "SO";
            }
            long elapsed = System.nanoTime() - start;
            total += elapsed;
            count++;

            if (!Arrays.equals(result, answer)) {
                return "WRONG";
            }

            if (elapsed / 1_000_000 > TIME_LIMIT_MS) {
                tooSlow = true;
                break;
            }
        }

        double avgMs = total / (double) count / 1_000_000;
        return String.format("%.2f", avgMs) + (tooSlow ? "*" : "");
    }

    // 자바는 처음 몇 번 실행할 때 느림 (JIT 컴파일 때문)
    // 그래서 본 측정 전에 작은 리스트로 몇 번 돌려서 예열해둠
    static void warmUp() {
        for (int i = 0; i < 20; i++) {
            int[] list = RandomListGenerator.getRandomList(500);
            for (UnaryOperator<int[]> sort : SORTS) {
                sort.apply(list.clone());
            }
        }
    }

    // 한글은 콘솔에서 두 칸을 차지해서 printf 로 줄 맞추면 삐뚤어짐
    // 그래서 한글 글자 수만큼 공백을 덜 붙여서 맞춤
    static String padKorean(String s, int width) {
        int len = 0;
        for (char c : s.toCharArray()) {
            len += (c >= '가' && c <= '힣') ? 2 : 1;
        }
        return s + " ".repeat(Math.max(0, width - len));
    }
}
