import java.util.Arrays;

public class InsertSort {
    public static void main(String[] args) {
        int[] list = {9, 4, 8, 7, 6, 5, 3, 2, 1};
        // list = getIntBubbleSort(getRandomList(1));
        System.out.println(Arrays.toString(getIntInsertSort(list.clone())));
    }

    public static int[] getIntInsertSort(int[] list) {
        for (int i = 1; i < list.length; i++) {
            int j = 1;
            while (true) {

                // System.out.println((i-j) + ", " + (i-j+1));
                // System.out.println(Arrays.toString(list)+", "+i+", "+j);
                
                if (i-j >= 0) {
                    if (list[i-j] > list[i-j+1]) {
                        int first = list[i-j+1];
                        int second = list[i-j];
                        
                        list[i-j+1] = second;
                        list[i-j] = first;
                        
                        j++;
                        
                    } else {
                        // System.out.println("------------------------");
                        break;
                    }
                } else { 
                    // System.out.println("------------------------");
                    break;
                }
            }
        }
        return list;
    }
}
