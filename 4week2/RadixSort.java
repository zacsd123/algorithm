public class RadixSort {
    public int[] getRadixSort(int[][] keys, int keyLen) {
        int n = keys.length;
        
        int[] order = new int[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }
        
        for (int pos = keyLen - 1; pos >= 0; pos--) {
            int[] count = new int[10];
            for (int i = 0; i < n; i++) {
                int d = keys[order[i]][pos];
                count[d]++;
            }
            
            int[] start = new int[10];
            int sum = 0;
            for (int d = 9; d >= 0; d--) {
                start[d] = sum;
                sum += count[d];
            }
            
            int[] next = new int[n];
            for (int i = 0; i < n; i++) {
                int d = keys[order[i]][pos];
                next[start[d]] = order[i];
                start[d]++;
            }

            order = next;
        }

        return order;
    }

    public int[] getKey(int n, int keyLen) {
        int len = getLength(n);

        int[] digits = new int[len];
        
        for (int j = len - 1; j >= 0; j--) {
            digits[j] = n % 10;
            n = n / 10;
        }
        
        int[] key = new int[keyLen];
        for (int i = 0; i < keyLen; i++) {
            key[i] = digits[i % len];
        }

        return key;
    }

    public int getLength(int n) {
        int i = 0;
        while (true)  {
            i++;
            if (n/10 == 0) {
                break;
            } else {
                n = n/10;
            }
        }
        return i;
    }
}
