import java.util.Arrays;

public class BubbleSort {
    public static void main(String[] args) {
        int[] list = {9, 8, 7, 6, 5, 4, 3, 2, 1};
        // list = getIntBubbleSort(getRandomList(1));
        System.out.println(Arrays.toString(getIntBubbleSort(list.clone())));
    }

    public static int[] getIntBubbleSort(int[] list) {
        for (int i = 0; i < list.length-1; i++) {
            boolean isChanged = false;
            for (int j = 0; j < list.length - i - 1; j++) {
                int first = list[j];
                int second = list[j+1];
                
                if (first > second) {
                    isChanged = true;
                    list[j] = second;
                    list[j+1] = first;
                }
            }

            if (!isChanged) {
                break;
            }
        }

        return list;
    }
}