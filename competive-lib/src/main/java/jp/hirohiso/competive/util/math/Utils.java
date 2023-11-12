package jp.hirohiso.competive.util.math;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

public class Utils {
    public static void main(String[] args) {
        BinaryHelper binaryHelper = new BinaryHelper(0, 1, i -> i <= 1);

        System.out.println(binaryHelper.search());

        var array = new int[]{1,3,5,7,9};
        System.out.println(binarySearchNearest(array,6));
    }

    //与えられた数を下限として、一番近い値を返却する
    public static int binarySearchNearest(int[] array, int key) {
        int i = Arrays.binarySearch(array, key);
        //変換
        i = i >= 0 ? i : ~i;
        int temp1 = i < array.length ? array[i] : array[i - 1];

        int j = (i + array.length - 1) % array.length;
        int temp2 = array[j];
        return Math.abs(temp1 - key) > Math.abs(temp2 - key) ? temp2 : temp1;
    }

    public static class BinaryHelper {
        private int low;
        private int high;
        private int mid;

        private Predicate<Integer> predicate;

        public BinaryHelper(int l, int h, Predicate<Integer> p) {
            low = l;
            high = h;
            mid = (low + high) / 2;
            predicate = p;
        }

        // 範囲 真偽 からもっとも偽に近い真の値を返す。
        public int search() {
            while (hasNext()) {
                contains(predicate.test(mid()));
            }
            return low;
        }

        public int mid() {
            return mid;
        }

        public boolean hasNext() {
            return !(high == low + 1);
        }

        public void contains(boolean b) {
            if (b) {
                low = mid;
                mid = (low + high) / 2;
            } else {
                high = mid;
                mid = (low + high) / 2;
            }
        }
    }

    //尺取り法　ベース
    public static void syakutori() {
        //尺取り法
        int start = 0;
        int end = 1;

        while (true) {
            //endを進めても条件を満たす場合はendを進める
            end++;

            //endを進めると条件を満たさない場合はstartを進める
            start++;
            //条件内

            //start,endが末尾まで達して進められなくなった場合は探索を終える
            if (false) {
                break;
            }
        }
    }
}
