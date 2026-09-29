import java.util.Arrays;
import java.util.Random;

public class RandomListGenerator {

    // 시드를 고정해두면 매번 같은 리스트가 나와서 정렬끼리 공정하게 비교할 수 있음
    // 다른 리스트가 필요하면 setSeed() 로 바꾸면 됨
    private static Random random = new Random(42);

    public static void main(String[] args) {
        int n = 10;
        System.out.println("완전 랜덤     : " + Arrays.toString(getRandomList(n)));
        System.out.println("거의 정렬     : " + Arrays.toString(getNearlySortedList(n)));
        System.out.println("정렬됨        : " + Arrays.toString(getSortedList(n)));
        System.out.println("역정렬        : " + Arrays.toString(getReversedList(n)));
        System.out.println("거의 역정렬   : " + Arrays.toString(getNearlyReversedList(n)));
        System.out.println("중복 많음     : " + Arrays.toString(getFewUniqueList(n)));
        System.out.println("전부 같은 값  : " + Arrays.toString(getAllSameList(n)));
    }

    public static void setSeed(long seed) {
        random = new Random(seed);
    }

    // 1. 완전 랜덤
    // 0 ~ n-1 을 섞은 리스트 (중복 없음)
    public static int[] getRandomList(int n) {
        int[] list = getSortedList(n);
        shuffle(list);
        return list;
    }

    // 2. 애매한 랜덤 (거의 정렬되어 있음)
    // 정렬된 리스트에서 전체의 약 5% 정도만 무작위로 자리를 바꿈
    public static int[] getNearlySortedList(int n) {
        return getNearlySortedList(n, 0.05);
    }

    // ratio 로 얼마나 섞을지 직접 정할 수 있음 (0.0 이면 정렬 그대로, 1.0 에 가까울수록 랜덤)
    public static int[] getNearlySortedList(int n, double ratio) {
        int[] list = getSortedList(n);
        randomSwap(list, ratio);
        return list;
    }

    // 3. 이미 정렬된 리스트 (0, 1, 2, ..., n-1)
    public static int[] getSortedList(int n) {
        int[] list = new int[n];
        for (int i = 0; i < n; i++) {
            list[i] = i;
        }
        return list;
    }

    // 4. 완전한 역정렬 (제일 큰 요소가 맨 앞, n-1, n-2, ..., 0)
    public static int[] getReversedList(int n) {
        int[] list = new int[n];
        for (int i = 0; i < n; i++) {
            list[i] = n - 1 - i;
        }
        return list;
    }

    // 5. 거의 역정렬 (역정렬에서 약 5% 만 자리를 바꿈)
    public static int[] getNearlyReversedList(int n) {
        int[] list = getReversedList(n);
        randomSwap(list, 0.05);
        return list;
    }

    // 6. 중복이 많은 리스트 (0 ~ 9 사이의 값만 들어감)
    public static int[] getFewUniqueList(int n) {
        int[] list = new int[n];
        for (int i = 0; i < n; i++) {
            list[i] = random.nextInt(10);
        }
        return list;
    }

    // 7. 모든 값이 같은 리스트
    public static int[] getAllSameList(int n) {
        int[] list = new int[n];
        Arrays.fill(list, 7);
        return list;
    }

    // 피셔-예이츠 셔플: 뒤에서부터 앞쪽의 아무 원소랑 바꿈
    private static void shuffle(int[] list) {
        for (int i = list.length - 1; i > 0; i--) {
            swap(list, i, random.nextInt(i + 1));
        }
    }

    // 리스트 길이 * ratio 만큼 아무 두 원소의 자리를 바꿈
    // 길이가 작아서 0번이 되면 안 섞이니까 ratio > 0 이면 최소 1번은 바꿈
    private static void randomSwap(int[] list, double ratio) {
        int n = list.length;
        if (n < 2 || ratio <= 0) {
            return;
        }
        int swapCount = Math.max(1, (int) (n * ratio));
        for (int i = 0; i < swapCount; i++) {
            swap(list, random.nextInt(n), random.nextInt(n));
        }
    }

    private static void swap(int[] list, int i, int j) {
        int temp = list[i];
        list[i] = list[j];
        list[j] = temp;
    }
}
