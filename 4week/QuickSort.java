import java.util.Arrays;

public class QuickSort {
    public static void main(String[] args) {
        int[] list = {9, 4, 8, 7, 6, 5, 3, 2, 1}; // 임의의 리스트
        // list = getIntBubbleSort(getRandomList(1));
        System.out.println(Arrays.toString(getIntQuickSort(list.clone())));
    }

    public static int[] getIntQuickSort(int[] list) {

        // 모든 구간을 퀵소트합니다
        quick(list, 0, list.length - 1);

        return list;
    }

    public static void quick(int[] list, int l, int h) {

        // low 가 high 보다 크거나 같다면 없는거니.. 리턴
        if (l >= h) {
            return;
        }
        
        // 중간을 나누는 요소의 인덱스를 받으며 리스트를 나눔
        int p = divided(list, l, h);

        // p 를 중심으로 좌/우로 나눠서 퀵소트함
        quick(list, l, p - 1);
        quick(list, p + 1, h);
    }

    public static int divided(int[] list, int l, int h) {

        // 나누는 요소의 기준은 맨 마지막 요소로 고정
        int p = list[h];

        // 맨 처음, 즉 기준보다 작은 것들을 위치할 인덱스는 low
        int i = l;

        // low 부터 high 까지 반복한ㄷㅏ
        for (int j = l; j < h; j++) {

            // 만약 j 번째 요소가 기준보다 작다면 i 번째 요소와 변경함
            if (list[j] < p) {
                int f = list[i];
                list[i] = list[j];
                list[j] = f;

                i++;
            }
        }

        // 그리고 기준이 된 요소를 i + 1, 즉 기준보다 작은 요소들
        // 바로 뒤로 이동시킴
        int f = list[i];
        
        list[i] = p;
        list[h] = f;

        return i;
    }
}