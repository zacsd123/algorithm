import java.util.Arrays;

public class HeapSort {
    public static void main(String[] args) {
        int[] list = {9, 4, 8, 7, 6, 5, 3, 2, 1}; // 임의의 리스트
        // list = getIntBubbleSort(getRandomList(1));
        System.out.println(Arrays.toString(getIntHeapSort(list.clone())));
    }

    public static int[] getIntHeapSort(int[] list) {

        // 먼저 리스트를 최소힙 리스트로 만든다. 
        int[] heaplist = makeSmallHeaplist(list);

        // System.out.println(Arrays.toString(heaplist));

        int[] result = new int[list.length];

        // 사실 for 문 써도 되지만 처음에 while 로 적어서 반복함..
        // 어차피 heaplist, 즉 n 만큼 반복하는 while 문임
        int i = 0;
        while (true) {

            // 더 뽑아낼 heaplist 가 없으면 반복 종료
            if (heaplist.length == i) {
                break;
            }

            // 힙 리스트의 맨 앞 요소는 항상 제일 작은 값이니
            // 이 값을 리스트에 순서대로 저장함
            result[i] = heaplist[0];

            // System.out.println(Arrays.toString(heaplist));

            i++;
            
            // 맨 마지막 원소를 맨 앞으로 보내고 다시 힙 구조를 만듦
            heaplist[0] = heaplist[heaplist.length - i];
            heaplist = siftDown(heaplist, heaplist.length - i);
        }

        return result;
    }

    // heap 구조로 바꾸는 함수
    public static int[] makeSmallHeaplist(int[] list) {

        // 리턴할 결과 리스트 만들기
        int[] result = new int[list.length];

        for (int i = 0; i < list.length; i++) {

            // 힙 구조 만드는 순서가 일단 맨 뒤에 떄려넣고 부모랑 비교하는거기 때문에
            // 일단 맨 뒤에 때려넣음
            result[i] = list[i];

            // i 는 맨 뒤 요소의 인덱스, 즉 자식 요소이므로
            // j 에다가 새로 저장시킴. 이게 바뀌기 때문에..
            int j = i;
            
            while (true) {

                // n 은 부모 요소의 인덱스임
                // 자식요소의 인덱스인 j 에 따라 바뀌기 때문에 
                // 매 반복마다 구해주어야함
                int n;

                // 만약 자식 요소의 인덱스가 2의 배수가 아니면
                if (j%2 == 1) {

                    // j 에 1을 더하고 구함
                    n = (j+1)/2 - 1;
                } else {

                    // 아니면 그냥 구함
                    n = j/2 - 1;
                }
                // 위 식이 어떻게 나왔나면, 부모의 인덱스가 n 일 때, 
                // 자식 요소의 인덱스는 (n+1)*2 or (n+1)*2+1 인데
                // 힙 구조는 완전 이진트리를 보장하기 때문에 2개씩 요소를 나눌 수 있음
                // 그래서 저렇게 구하는데.. 사실 리스트 나열해서 인덱스 구하면
                // 바로 알 수 있긴해요
    
                // 구해진 부모 인덱스가 0보다 작으면 멈춤
                // = 현재 인덱스, j 가 최상위 라는 것
                if (n < 0) {
                    break;
                }

                // 그래서 부모 요소 값과 자식 요소 값을 먼저 구하고
                int c = result[j];
                int p = result[n];

                // 비교 후 만약 자식 값이 부모보다 작으면 위치를 바꾸고
                // = 최소힙. 최대는 반대로 하면 됨
                // 자리가 바뀌었으니 현재 위치는 j 가 아닌 n 이 되므로 j 를 n 으로 바꿈
                if (c < p) {
                    result[j] = p;
                    result[n] = c;

                    j = n;
                } else {

                    // 아니면 멈춤 왜냐면 올바른 위치이니까..
                    break;
                }
            }
        }

        return result;
    }

    public static int[] siftDown(int[] list, int size) {

        // 부모 인덱스변수
        int p = 0;

        while (true) {

            // 자식 인덱스 구하기
            int cl = p * 2 + 1;
            int cr = p * 2 + 2;

            // 그리고 일단 제일 작은건 부모니까 부모로 정함
            int smallest = p;

            // 여기서 자식들 중 젤 작은 것들보다 작은 것이 있다면
            // 그걸로 바꿈
            if (cl < size && list[cl] < list[smallest]) {
                smallest = cl;
            }
            if (cr < size && list[cr] < list[smallest]) {
                smallest = cr;
            }

            // 근데 변함이 없다? 그럼 부모가 젤 작은거니까 안바뀌어도 됨..
            // 그래서 멈춤
            if (smallest == p) {
                break;
            }

            // 아니면 부모랑 자식 위치를 바꿈
            int a = list[p];
            list[p] = list[smallest];
            list[smallest] = a;
            p = smallest;
        }
        
            return list;
    }
}
