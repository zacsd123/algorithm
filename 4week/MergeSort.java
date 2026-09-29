import java.util.Arrays;

public class MergeSort {
    public static void main(String[] args) {
        int[] list = {9, 4, 8, 7, 6, 5, 3, 2, 1, 0}; // 임의의 리스트
        // list = getIntBubbleSort(getRandomList(1));
        System.out.println(Arrays.toString(getIntMergeSort(list.clone())));
    }

    public static int[] getIntMergeSort(int[] list) {

        // 리스트의 길이가 1이라면 나누지 않고 바로 리턴
        if (list.length <= 1) {
            return list;
        }

        // 절반의 위치를 구하는데 홀수/짝수를 나누어 구분
        // 홀수면 나눴을 떄 뒷부분의 리스트가 더 긺
        int h1 = list.length / 2;
        int h2 = h1;
        if (list.length % 2 != 0) {
            h2++;
        } 

        // 앞/뒤 나눠 저장할 리스트 만들기
        int[] listF = new int[h1];
        int[] listS = new int[h2];

        for (int i = 0; i < h1; i++) {
            listF[i] = list[i];
        }
        for (int i = 0; i < h2; i++) {
            listS[i] = list[i+h1];
        }

        // System.out.println(Arrays.toString(listF) + ", " + Arrays.toString(listS));
        
        // 나눈 두 리스트를 합치는 것을 리턴하는데, 합쳐지는 두 리스트 또한 나눠버림. 
        // 그니까 쭉쭉쭉 나눠버린 한 개 짜리 리스트들을 doMerge 에서 합침
        return doMerge(getIntMergeSort(listF), getIntMergeSort(listS));
    }

    public static int[] doMerge(int[] listF, int[] listS) {
        
        // 두 개의 리스트를 합치니까 두 개의 길이만큼의 리스트 생성
        int[] result = new int[listF.length + listS.length];
        
        // i 는 listF의 인덱스, j 는 listS의 인덱스
        int i = 0;
        int j = 0;

        // 전부 합칠때까지 반복
        while (true) {

            // 만약 listF 의 i 번째 원소가 listS 의 j 번째 원소보다 작다면
            // 작은 원소를 먼저 result 에 넣음
            // 아니면 반대로 넣음
            if (listF[i] < listS[j]) {
                result[i+j] = listF[i];
                i++;
            } else {
                result[i+j] = listS[j];
                j++;
            }

            // 그러던 와중에 만약 i 가 listF의 길이만큼 커졌다면
            // 즉, listF 의 원소를 다 넣었다면 나머지 listS 원소를 result 에 넣음
            // 반대의 경우 똑같이 함
            if ((listF.length <= i)) {
                for (int k = j; k < listS.length; k++) {
                    result[i+k] = listS[k];
                    j++;
                }
            } else if ((listS.length <= j)) {
                for (int l = i; l < listF.length; l++) {
                    result[l+j] = listF[l];
                    i++;
                }
            }

            // 그리고 i + j, 즉 둘 다 전부 다 넣었다면 반복 멈춤
            if (i+j >= result.length) {
                break;
            }
        }

        return result;
    }
}