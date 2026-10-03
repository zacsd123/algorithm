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


// 문제 제출 시 사용하는 코드

// public String getLargestNumber(int[] list) {
//     if (list.length == 0) {
//         return "";
//     }

//     int maxLen = 0;
//     for (int i = 0; i < list.length; i++) {
//         int len = getLength(list[i]);
//         if (maxLen < len) {
//             maxLen = len;
//         }
//     }

//     int keyLen = maxLen * 2;
    
//     int[][] keys = new int[list.length][];
//     for (int i = 0; i < list.length; i++) {
//         keys[i] = getKey(list[i], keyLen);
//     }
    
//     int[] order = getRadixSort(keys, keyLen);

//     StringBuilder sb = new StringBuilder();
//     for (int i = 0; i < order.length; i++) {
//         sb.append(list[order[i]]);
//     }
    
//     if (sb.charAt(0) == '0') {
//         return "0";
//     }

//     return sb.toString();
// }