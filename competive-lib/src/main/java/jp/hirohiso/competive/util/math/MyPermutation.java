package jp.hirohiso.competive.util.math;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MyPermutation {
    public static void main(String[] args){
        int[] arr = new int[]{1,2,3,4,5,6,7,8,9,10};

        do{
            //System.out.println(Arrays.toString(arr));
        }while (intNextPermutation(arr));

    }

    public static boolean intNextPermutation(int[] arr){
        int size = arr.length;
        for (int i = size - 2; i >=0; i--) {
            if(arr[i] < arr[i+1]){
                int j = size;
                do{
                    j--;
                }while (!(arr[i] < arr[j]));
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                Arrays.sort(arr,i+1,size);
                return true;
            }
            if(i == 0){
                int[] temp = Arrays.copyOf(arr,size);
                for (int j = 0; j < size; j++) {
                    arr[j] = temp[(size-1) -j];
                }
            }
        }
        return false;
    }
    public static <T extends Comparable<T>> boolean myNextPermutation(T[] array){
        return false;
    }
}
