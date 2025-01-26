package jp.hirohiso.competive.util.math;

import java.util.Arrays;
import java.util.BitSet;

public class NextPairMain {
    public static void main(String[] args) {
        {
            var arr = new int[]{0, 1, 2, 3};
            do {
                System.out.println(Arrays.toString(arr));
            } while (nextPairing(arr));
        }

        {
            var arr = new int[]{0, 1, 2, 3, 4, 5};
            do {
                System.out.println(Arrays.toString(arr));
            } while (nextGroup(arr));
        }
    }

    public static boolean nextPairing(int[] arr) {
        var n = arr.length;
        var used = 0;
        for (int i = n - 1; i >= 0; i--) {
            used |= (1 << arr[i]);
            if (i % 2 == 1 && arr[i] < (Integer.BYTES * 8 - 1) - Integer.numberOfLeadingZeros(used)) {
                arr[i] = Integer.numberOfTrailingZeros(used >> (arr[i] + 1)) + arr[i] + 1;
                used ^= 1 << arr[i];
                for (int j = i + 1; j < n; j++) {
                    arr[j] = Integer.numberOfTrailingZeros(used);
                    used ^= 1 << arr[j];
                }
                return true;
            }
        }
        return false;
    }


    //k組の3人組を生成する
    //arr.length == 3 * n;
    public static boolean nextGroup(int[] arr) {
        var M = 3;
        var n = arr.length;
        var usedInAll = new BitSet(n);
        var usedInGroup = new BitSet(n);
        for (int i = n - 1; i >= 0; i--) {
            usedInGroup.set(arr[i], true);
            if (i % M == 0) {
                usedInAll.or(usedInGroup);
                usedInGroup.clear();
                continue;
            }
            if (arr[i] < usedInAll.nextSetBit(arr[i])) {
                //arr[i]より大きいusedInAllの要素のうち最小の値をarr[i]にする
                arr[i] = usedInAll.nextSetBit(arr[i]);
                usedInAll.set(arr[i], false);
                //usedInGroupsをusedInAllにマージ
                usedInAll.or(usedInGroup);
                usedInGroup.clear();
                //arr[i]と同一のグループ内はarr[j]にarr[j - 1]より大きい値を入れる
                var j = i + 1;
                for (; j % M != 0; j++) {
                    arr[j] = usedInAll.nextSetBit(arr[j - 1]);
                    usedInAll.set(arr[j], false);
                }
                //残りを昇順に入れる
                for (; j < n; j++) {
                    arr[j] = usedInAll.nextSetBit(0);
                    usedInAll.set(arr[j], false);
                }
                return true;
            }
        }
        return false;
    }


    //m人組をk組作る組み合わせ列挙を行う関数を返却するジェネレータ
    public static class GroupGenerator {
        public static Func generator(int number) {
            return (arr) -> {
                var M = number;
                var n = arr.length;
                var usedInAll = new BitSet(n);
                var usedInGroup = new BitSet(n);
                for (int i = n - 1; i >= 0; i--) {
                    usedInGroup.set(arr[i], true);
                    if (i % M == 0) {
                        usedInAll.or(usedInGroup);
                        usedInGroup.clear();
                        continue;
                    }
                    if (arr[i] < usedInAll.nextSetBit(arr[i])) {
                        //arr[i]より大きいusedInAllの要素のうち最小の値をarr[i]にする
                        arr[i] = usedInAll.nextSetBit(arr[i]);
                        usedInAll.set(arr[i], false);
                        //usedInGroupsをusedInAllにマージ
                        usedInAll.or(usedInGroup);
                        usedInGroup.clear();
                        //arr[i]と同一のグループ内はarr[j]にarr[j - 1]より大きい値を入れる
                        var j = i + 1;
                        for (; j % M != 0; j++) {
                            arr[j] = usedInAll.nextSetBit(arr[j - 1]);
                            usedInAll.set(arr[j], false);
                        }
                        //残りを昇順に入れる
                        for (; j < n; j++) {
                            arr[j] = usedInAll.nextSetBit(0);
                            usedInAll.set(arr[j], false);
                        }
                        return true;
                    }
                }
                return false;
            };
        }

        interface Func {
            boolean nextGroups(int[] arr);
        }
    }
}
