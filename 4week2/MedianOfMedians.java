class MedianOfMedians {
    public static void main(String[] args) {

    }

    public static int getMedianOfMedians(int[] list, int k) {

        if (list.length == 1) {
            return list[0];
        }

        int div = list.length / 5;
        int mod = list.length % 5;

        int[] medians = new int[div + (mod == 0 ? mod : 1)];
        for (int i = 0; i < medians.length; i++) {
            int[] split = new int[i != medians.length-1 ? 5 : (mod != 0 ? mod : 5)];
            
            for (int j = 0; j < 5; j++) {
                if (j+i*5 < list.length) {
                    split[j] = list[i*5+j];
                }
            }
            medians[i] = getMedian(split);
        }

        int medianOfmedians = getMedianOfMedians(medians, medians.length/2);

        int f = 0;
        int m = 0;
        int s = 0;

        for (int i = 0; i < list.length; i++) {
            if (list[i] < medianOfmedians) {
                f++;
            } else if (list[i] == medianOfmedians) {
                m++;
            } else {
                s++;
            }
        }

        int[] listF = new int[f];
        int[] listM = new int[m];
        int[] listS = new int[s];

        f = 0;
        m = 0;
        s = 0;

        for (int i = 0; i < list.length; i++) {
            if (list[i] < medianOfmedians) {
                listF[f] = list[i];
                f++;
            } else if (list[i] == medianOfmedians) {
                listM[m] = list[i];
                m++;
            } else {
                listS[s] = list[i];
                s++;
            }
        }


        if (k < f) {
            return getMedianOfMedians(listF, k);
        } else if (k < f + m) {
            return listM[0];
        } else {
            return getMedianOfMedians(listS, k - f - m);
        }
    }

    public static int getMedian(int[] list) {

        for (int i = 0; i < list.length; i++) {
            for (int j = 0; j < list.length-i-1; j++) {
                if (list[j] > list[j+1]) {
                    int a = list[j];
                    list[j] = list[j+1];
                    list[j+1] = a;
                }
            }
        }

        return list[list.length/2];
    }
}