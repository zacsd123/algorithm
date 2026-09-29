import java.util.Arrays;

public class SelectSort {
    public static void main(String[] args) {
        int[] list = {9, 4, 8, 7, 6, 5, 3, 2, 1};
        // list = getIntBubbleSort(getRandomList(1));
        System.out.println(Arrays.toString(getIntSelectSort(list.clone())));
    }

    public static int[] getIntSelectSort(int[] list) {
        for (int i = 0; i < list.length; i++) {
            int maxIdx = 0;
            for (int j = 0; j < list.length - i; j++) {
                if (list[maxIdx] < list[j]) {
                    maxIdx = j;
                }
            }

            int sml = list[list.length-1-i];
            int max = list[maxIdx];

            list[list.length-1-i] = max;
            list[maxIdx] = sml;
        }
        return list;
    }
}