package jp.hirohiso.competive.util.sequence;

import java.util.Arrays;
import java.util.Comparator;

public class ZaatsuMain {
    public static void main(String[] args) {
        var temp = new long[]{1245,23,46959865,2,-6,325,6,1000000};
        System.out.println(Arrays.toString(temp));
        System.out.println(Arrays.toString(arrayComp(temp)));
    }


    private static long[] arrayComp(long[] arr){
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
