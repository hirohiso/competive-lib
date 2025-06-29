package jp.hirohiso.competive.util.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class ExtraCombinationMain {
    public static void main(String[] args) {
        var nk = new ElementsByKGroup(1, 4, true,
                arr -> System.out.println(Arrays.toString(arr))
        );
        nk.solve();


        var bell = new DistinctElementsByKGroup(8, 8, true,
                list -> {
                    list.stream().forEach(i ->
                            System.out.print(Integer.toBinaryString(i) + " "));
                    System.out.println();
                    var sets = new LinkedList<LinkedList<Integer>>();
                    for (var i : list) {
                        var set = new LinkedList<Integer>();
                        for (int j = 0; j < 8; j++) {
                            if ((i & (1 << j)) != 0) {
                                set.add(j);
                            }
                        }
                        sets.add(set);
                    }
                });
        bell.solve();

        {
            var ans = new int[]{0};
            for (int i = 20; i <= 40; i++) {
                var temp = new ElementsByKGroup(i, 4, true,
                        arr -> {
                            System.err.println(Arrays.toString(arr));
                            ans[0]++;
                        }

                );
                temp.solve();
            }
            System.out.println(ans[0] * 4 * 3 * 2 * 1);
        }
    }


    //区別のないn個の要素を区別のないk個のグループに分割し、Fを適用する
    //b : 0個のグループも許容する
    public static class ElementsByKGroup {

        private F f;
        private int n;
        private int k;

        private boolean allow_empty;

        public ElementsByKGroup(int n, int k, boolean allow_empty, F fun) {
            this.f = fun;
            this.n = n;
            this.k = k;
            this.allow_empty = allow_empty;
        }

        public void solve() {
            var ans = new int[k];
            if (!allow_empty) {
                Arrays.fill(ans, 1);
                dfs(ans, n - k, k);
            } else {
                dfs(ans, n, k);
            }
        }

        private void dfs(int[] ans, int n, int k) {
            if (n == k) {
                rangeAdd(ans, k, 1);
                this.f.apply(ans);
                rangeAdd(ans, k, -1);
            } else if (n > k && k != 1) {
                rangeAdd(ans, k, 1);
                dfs(ans, n - k, k);
                rangeAdd(ans, k, -1);
            }
            if (k == 1 && n != k) {
                rangeAdd(ans, k, n);
                this.f.apply(ans);
                rangeAdd(ans, k, -n);
            } else if (k > 1) {
                dfs(ans, n, k - 1);
            }
        }

        interface F {
            void apply(int[] in);
        }

        private void rangeAdd(int[] ans, int k, int add) {
            for (int i = 0; i < k; i++) {
                ans[i] += add;
            }
        }
    }


    //区別できるN個の要素を区別のないK個のグループに分割する
    //0個の要素を許すか許さないか
    public static class DistinctElementsByKGroup {

        private F f;
        private int n;
        private int k;

        boolean allow_empty;

        public DistinctElementsByKGroup(int n, int k, boolean allow_empty, F f) {
            this.f = f;
            this.n = n;
            this.k = k;
            this.allow_empty = allow_empty;
        }

        public void solve() {
            var ans = new ArrayList<Integer>(k);
            dfs(ans);
        }

        private void dfs(ArrayList<Integer> ans) {
            {
                var chk = 0;
                for (int i = 0; i < ans.size(); i++) {
                    chk += ans.get(i);
                }
                if (chk == (1 << n) - 1) {
                    if (allow_empty || (!allow_empty && ans.size() == k)) {
                        f.apply(ans);
                    }
                    return;
                }
            }

            for (int i = 0; i < ans.size(); i++) {
                ans.set(i, ans.get(i) << 1);
            }
            for (int i = 0; i < ans.size(); i++) {
                ans.set(i, ans.get(i) ^ 1);
                dfs(ans);
                ans.set(i, ans.get(i) ^ 1);
            }

            if (ans.size() < k) {
                ans.add(1);
                dfs(ans);
                ans.remove(ans.size() - 1);
            }
            for (int i = 0; i < ans.size(); i++) {
                ans.set(i, ans.get(i) >> 1);
            }
        }

        interface F {
            void apply(ArrayList<Integer> in);
        }
    }
}



