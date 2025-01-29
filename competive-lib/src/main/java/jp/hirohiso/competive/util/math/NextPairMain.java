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

        {
            var ans = 0;
            var arr = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
            do {
                ans++;
                System.out.println(Arrays.toString(arr));
            } while (nextNNKKGroup(arr));
            System.out.println(ans);
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


    //2組の2人組と2組の3人組を生成する
    //arr.length == 10;
    public static boolean nextNNKKGroup(int[] arr) {
        var groupStart = new BitSet();
        groupStart.set(0);
        groupStart.set(4);
        var subgroupStart = new BitSet();
        subgroupStart.set(0);
        subgroupStart.set(2);
        subgroupStart.set(4);

        var n = arr.length;
        var usedInAll = new BitSet(n);
        var usedInGroup = new BitSet(n);
        var usedInSubGroup = new BitSet(n);


        //ルール
        //subGroup内では入れ替えない
        //subGroup間では入れ替えが可能
        //Group間でも入れ替えが可能
        //subGroup内は昇順に並んでいる
        //Group内ではsubGroupの開始要素が昇順に並んでいる
        for (int i = n - 1; i >= 0; i--) {
            usedInSubGroup.set(arr[i], true);
            //swapするさいの参照先
            if (subgroupStart.get(i)) {
                //Groupの開始要素:
                //同一Groupではない自分より後ろで自分よりも大きな要素集合を参照
                //subGroupの開始要素:
                //同一Groupではない自分より後ろで自分よりも大きな要素集合を参照
                if (arr[i] < usedInAll.nextSetBit(arr[i])) {
                    arr[i] = usedInAll.nextSetBit(arr[i]);
                    usedInAll.set(arr[i], false);
                    usedInAll.or(usedInGroup);
                    usedInAll.or(usedInSubGroup);
                } else {
                    usedInGroup.or(usedInSubGroup);
                    usedInSubGroup.clear();
                    if (groupStart.get(i)) {
                        usedInAll.or(usedInGroup);
                        usedInGroup.clear();
                    }
                    continue;
                }
            } else {
                //その他の要素:
                //同一subGroupではない自分より後ろで自分より大きな要素集合を参照
                if (arr[i] < usedInGroup.nextSetBit(arr[i]) || arr[i] < usedInAll.nextSetBit(arr[i])) {
                    usedInAll.or(usedInGroup);
                    arr[i] = usedInAll.nextSetBit(arr[i]);
                    usedInAll.set(arr[i], false);
                    usedInAll.or(usedInSubGroup);
                } else {
                    continue;
                }
            }
            //swapした後の動き
            //swapした要素をkとする
            //subGroup内ではkより大きい値を昇順で埋めいく
            var j = i + 1;
            var k = subgroupStart.previousSetBit(i);
            for (;(!subgroupStart.get(j)) && j < n; j++) {
                arr[j] = usedInAll.nextSetBit(arr[j - 1]);
                usedInAll.set(arr[j], false);
            }
            //Group内ではkのsubGroupの開始要素lより大きい要素を昇順で埋めていく
            for (; (!groupStart.get(j)) && j < n; j++) {
                arr[j] = usedInAll.nextSetBit(arr[k]);
                if(arr[j] == -1){
                    System.out.println(Arrays.toString(arr));
                }
                usedInAll.set(arr[j], false);
            }
            //別なGroupは残りの要素を昇順で埋めていく
            for (; j < n; j++) {
                arr[j] = usedInAll.nextSetBit(0);
                usedInAll.set(arr[j], false);
            }
            return true;
            //subGroup内の開始要素はusedInSubGroup.previousSetBit()
            //Group内の開始要素はusedInSGroup.previousSetBit()
            //でアクセスできる

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
