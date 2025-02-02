package jp.hirohiso.competive.util.math;

import java.util.Arrays;

public class NksuffleMain {
    public static void main(String[] args) {
        int[] array = {1, 2, 3, 4, 5, 6, 7};
        int k = 3;

        do {
            System.out.println("Updated array: " + Arrays.toString(array));
        } while (ShuffleUtil.nextShuffle(array, k));

        //
        var arr = new int[6];
        Arrays.setAll(arr, i -> i);
        var ans = 0;
        do {
            ans++;
            System.out.println("Updated array: " + Arrays.toString(arr));
        } while (ShuffleUtil.nextShuffle(arr, 4));
        System.out.println(ans);
    }
}

//参考：https://ngtkana.hatenablog.com/entry/2021/11/08/000209?_gl=1*rzimq9*_gcl_au*ODMyMjk1MTkuMTY5OTAxODAzOA..
class ShuffleUtil {
    public static boolean nextShuffle(int[] array, int k) {
        int n = array.length;
        if (n == k) {
            return false;
        }

        // 左側で条件に合う位置を見つける
        int i = -1;
        for (int idx = k - 1; idx >= 0; idx--) {
            if (array[idx] < array[n - 1]) {
                i = idx;
                break;
            }
        }
        if (i == -1) {
            return false;
        }

        // 右側で条件に合う位置を見つける
        int j = -1;
        for (int idx = k; idx < n; idx++) {
            if (array[i] < array[idx]) {
                j = idx;
                break;
            }
        }
        if (j == -1) {
            return false;
        }
        // スワップ
        swap(array, i, j);
        // スワップの長さ計算
        int swapLen = Math.min(k - (i + 1), n - k - (j - k + 1));
        // 部分スワップ
        swapSubArrays(array, k - swapLen, j + 1, swapLen);
        // 左側を左回転
        rotateLeft(array, i + 1, swapLen, k);
        // 右側を右回転
        rotateRight(array, j + 1, swapLen);

        return true;
    }

    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private static void swapSubArrays(int[] array, int start1, int start2, int len) {
        for (int i = 0; i < len; i++) {
            swap(array, start1 + i, start2 + i);
        }
    }

    private static void rotateLeft(int[] array, int start, int distance, int k) {
        if (distance <= 0) return;
        int[] temp1 = Arrays.copyOfRange(array, start, k - distance);
        int[] temp2 = Arrays.copyOfRange(array, k - distance, k);
        System.arraycopy(temp2, 0, array, start, temp2.length);
        System.arraycopy(temp1, 0, array, start + temp2.length, temp1.length);
    }

    private static void rotateRight(int[] array, int start, int distance) {
        if (distance <= 0) return;
        int[] temp1 = Arrays.copyOfRange(array, start + distance, array.length);
        int[] temp2 = Arrays.copyOfRange(array, start, start + distance);
        System.arraycopy(temp1, 0, array, start, temp1.length);
        System.arraycopy(temp2, 0, array, start + temp1.length, temp2.length);
    }

    public static void main(String[] args) {
        int[] array = {1, 2, 3, 4, 5, 6};
        int k = 3;

        boolean result = nextShuffle(array, k);
        System.out.println("Next shuffle possible: " + result);
        System.out.println("Updated array: " + Arrays.toString(array));
    }
}

