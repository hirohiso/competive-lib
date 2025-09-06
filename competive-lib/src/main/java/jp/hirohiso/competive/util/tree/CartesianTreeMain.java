package jp.hirohiso.competive.util.tree;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

public class CartesianTreeMain {
    public static void main(String[] args) {
        var arr = new Integer[]{9, 3, 7, 1, 8, 12, 10, 20, 15, 18, 5};
        var tree = new CartesianTree<>(arr, Comparator.naturalOrder());
        System.out.println(Arrays.toString(tree.parent));
        System.out.println(Arrays.toString(tree.left));
        System.out.println(Arrays.toString(tree.right));

    }

    static class CartesianTree<T>{

        int size = 0;
        int root;

        int[] parent;
        int[] left;
        int[] right;
        Comparator<T> comp;
        T[] arr;

        public CartesianTree(T[] arr, Comparator<T> comp){
            size = arr.length;
            parent = new int[size];
            left = new int[size];
            right = new int[size];
            Arrays.fill(parent, -1);
            Arrays.fill(left, -1);
            Arrays.fill(right, -1);
            this.arr = Arrays.copyOf(arr,arr.length);
            this.comp = comp;
            build();
        }

        private void build(){
            var stack = new LinkedList<Integer>();
            for(var i = 0; i < size; i++){
                var t = arr[i];
                if(stack.isEmpty()){
                    stack.addLast(i);
                }else{
                    Integer top = null;
                    while(!stack.isEmpty() && comp.compare(arr[stack.peekLast()], t) > 0){
                        top = stack.pollLast();
                    }
                    if(top != null){
                        left[i] = top;
                        parent[top] = i;
                    }
                    if(!stack.isEmpty()){
                        right[stack.peekLast()] = i;
                        parent[i] = stack.peekLast();
                    }
                    stack.addLast(i);
                }
            }
        }
    }
}
