package jp.hirohiso.competive.util.math;

import java.util.Arrays;

public class NksuffleMain {
    public static void main(String[] args) {
        int[] array = {1, 2, 3, 4, 5, 6};
        int k = 2;

        do {
            System.out.println("Updated array: " + Arrays.toString(array));
        } while (ShuffleUtil.nextShuffle(array, k));

        //
        var arr = new int[100000];
        Arrays.setAll(arr, i -> i);
        var ans = 0;
        do {
            ans++;
        } while (ShuffleUtil.nextShuffle(arr, 1));
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
        swapSubArrays(array, k - swapLen, k, j, j + swapLen);

        // 左側を左回転
        rotateLeft(array, i + 1, k - (i + 1 + swapLen));

        // 右側を右回転
        rotateRight(array, k + j - k + swapLen, n - k - (j - k + swapLen));

        return true;
    }

    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private static void swapSubArrays(int[] array, int start1, int end1, int start2, int end2) {
        for (int i = 0; i < (end1 - start1); i++) {
            swap(array, start1 + i, start2 + i);
        }
    }

    private static void rotateLeft(int[] array, int start, int distance) {
        if (distance <= 0) return;
        int[] temp = Arrays.copyOfRange(array, start, start + distance);
        System.arraycopy(array, start + distance, array, start, array.length - start - distance);
        System.arraycopy(temp, 0, array, array.length - distance, temp.length);
    }

    private static void rotateRight(int[] array, int start, int distance) {
        if (distance <= 0) return;
        int[] temp = Arrays.copyOfRange(array, array.length - distance, array.length);
        System.arraycopy(array, start, array, start + distance, array.length - start - distance);
        System.arraycopy(temp, 0, array, start, temp.length);
    }

    public static void main(String[] args) {
        int[] array = {1, 2, 3, 4, 5, 6};
        int k = 3;

        boolean result = nextShuffle(array, k);
        System.out.println("Next shuffle possible: " + result);
        System.out.println("Updated array: " + Arrays.toString(array));
    }
}

