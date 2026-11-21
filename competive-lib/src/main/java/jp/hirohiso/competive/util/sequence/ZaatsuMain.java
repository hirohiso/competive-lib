package jp.hirohiso.competive.util.sequence;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;

public class ZaatsuMain {
    public static void main(String[] args) {
        var temp = new long[]{1245, 23, 46959865, 2, -6, 325, 6, 1000000, 6, 123, 23};
        System.out.println(Arrays.toString(temp));
        System.out.println(Arrays.toString(arrayComp(temp)));
        {
            var ac = new ArrCompress(temp);
            System.out.println(Arrays.toString(ac.dic));
            System.out.println(ac.map);
        }
    }


    static class ArrCompress {
        long[] dic;
        TreeMap<Long, Integer> map;

        public ArrCompress(long[] original) {
            var arr = arrayComp(original);

            var max = Arrays.stream(arr).max().getAsInt();
            dic = new long[max + 1];
            map = new TreeMap<>();
            for (int i = 0; i < arr.length; i++) {
                dic[arr[i]] = original[i];
                map.put(original[i], arr[i]);
            }
        }

        public int getAsComp(long v) {
            return map.getOrDefault(v, -1);
        }

        public int getCeilAsComp(long v) {
            return map.ceilingEntry(v).getValue();
        }

        public int getFloorAsComp(long v) {
            return map.floorEntry(v).getValue();
        }


        static int[] arrayComp(long[] arr) {
            var result = new int[arr.length];
            var temp = new long[arr.length][2];
            for (int i = 0; i < arr.length; i++) {
                temp[i][0] = i;
                temp[i][1] = arr[i];
            }
            Arrays.sort(temp, Comparator.comparing(i -> i[1]));
            var rank = -1;
            for (int i = 0; i < temp.length; i++) {
                if (i == 0) {
                    rank = 0;
                } else {
                    rank = temp[i][1] == temp[i - 1][1] ? rank : rank + 1;
                }
                result[(int) temp[i][0]] = rank;
            }
            return result;
        }

    }


    /**
     * 値の大小関係を保つ0始まりの座標圧縮。
     * <p>戻り値は入力順の順位。等しい値は同順位で順位に欠番はない。
     * 空配列も可。入力配列は変更しない。時間 O(N log N)、領域 O(N)。</p>
     */
    static long[] arrayComp(long[] arr) {
        var result = new long[arr.length];
        var temp = new long[arr.length][2];
        for (int i = 0; i < arr.length; i++) {
            temp[i][0] = i;
            temp[i][1] = arr[i];
        }
        Arrays.sort(temp, Comparator.comparing(i -> i[1]));
        var rank = -1;
        for (int i = 0; i < temp.length; i++) {
            if (i == 0) {
                rank = 0;
            } else {
                rank = temp[i][1] == temp[i - 1][1] ? rank : rank + 1;
            }
            result[(int) temp[i][0]] = rank;
        }
        return result;
    }
}
