package jp.hirohiso.competive.util.tree;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class LazySegmentationTree {
    /*
    「セグ木に乗る」とは
    やりたい演算がセグメント木に乗るとか乗らないとかいう話は、
    結局のところこれらの関数 op mapping composition を適切に定義できるかどうか？という点に尽きます。単位元と恒等写像はどうにでもなるので。
    op は通常のセグメント木にも存在する取得クエリの演算で、これが結合法則を満たすことがまず必要です。この説明はこちらの記事に詳しく書いています。

    遅延伝播を行う時にはそれに加えて、以下の2つの性質を満たすような関数を見つける必要があります。
    mapping：操作を、全てのノードが持つ data に同じ計算式で作用させることができること。このとき区間幅など必要な情報を data 側に持たせて補うのはOK。
    composition：複数の操作を連続して行うという操作（合成写像）を、あたかも1回の操作であるかのように扱って lazy に持たせることができること。
     */

    public static void main(String[] args) {

        //区間中の最大値を計算
        //区間計算は値の足し算
        //区間加算操作・区間最大値取得
        LazySegmentTree<Integer, Integer> st = new LazySegmentTree<>(
                new Integer[]{1, 1, 2, 3, 5, 8, 13},
                (i, j) -> Math.max(i, j),
                (i, j) -> i + j,
                (i, j) -> i + j,
                () -> 0,
                () -> 0);
        st.debug();

        System.out.println("区間[0,0):" + st.getRange(0, 1));
        System.out.println("区間[0,7):" + st.getRange(0, 7));
        System.out.println("区間[0,6):" + st.getRange(0, 6));
        System.out.println("区間[2,5):" + st.getRange(2, 5));
        st.apply(0, 1, 5);
        System.out.println("区間[0,0):" + st.getRange(0, 1));
        System.out.println("区間[0,7):" + st.getRange(0, 7));
        System.out.println("区間[0,6):" + st.getRange(0, 6));
        System.out.println("区間[2,5):" + st.getRange(2, 5));
        st.apply(1, 3, 10);
        System.out.println("区間[0,0):" + st.getRange(0, 1));
        System.out.println("区間[0,7):" + st.getRange(0, 7));
        System.out.println("区間[0,6):" + st.getRange(0, 6));
        System.out.println("区間[2,5):" + st.getRange(2, 5));

        System.out.println("---------------------");

        //区間加算操作・区間和取得
        var sum = new LazySegmentTree<Pair, Integer>(
                new Pair[]{
                        new Pair(1, 1),
                        new Pair(3, 1),
                        new Pair(2, 1),
                        new Pair(4, 1),
                        new Pair(3, 1),
                        new Pair(5, 1),
                },
                (p, q) -> new Pair(p.v + q.v, p.n + q.n),
                (i, p) -> new Pair(p.v + p.n * i, p.n),
                (i, j) -> i + j,
                () -> new Pair(0, 1),
                () -> 0
        );
        System.out.println("区間[0,0):" + sum.getRange(0, 1).v);
        System.out.println("区間[0,4):" + sum.getRange(0, 4).v);
        System.out.println("区間[2,6):" + sum.getRange(2, 6).v);
        System.out.println("区間[6,8):" + sum.getRange(6, 8).v);
        sum.apply(0, 8, 10);
        sum.apply(0, 4, 100);
        System.out.println("区間[0,0):" + sum.getRange(0, 1).v);
        System.out.println("区間[0,4):" + sum.getRange(0, 4).v);
        System.out.println("区間[2,6):" + sum.getRange(2, 6).v);
        System.out.println("区間[6,8):" + sum.getRange(6, 8).v);


        {
            //Range Affine Range Sum
            record Node(int v, int size) {
                static public Node e() {
                    return new Node(0, 0);
                }

                public Node add(Node other) {
                    return new Node(this.v + other.v, this.size + other.size);
                }
            }
            ;

            record Act(int a, int b) {
                static public Act e() {
                    return new Act(1, 0);
                }

                public Node apply(Node node) {
                    return new Node(node.v * a + b * node.size, node.size);
                }

                public Act compose(Act other) {
                    return new Act(this.a * other.a, this.b * other.a + this.b);
                }
            }

            var arr = new Integer[]{1, 1, 2, 3, 5, 8, 13};
            var lst = new LazySegmentTree<Node, Act>(
                    Arrays.stream(arr).map(n -> new Node(n, 1)).toArray(Node[]::new),
                    Node::add,
                    Act::apply,
                    Act::compose,
                    Node::e,
                    Act::e
            );
        }

        {
            //range update range sum
            record Node(int v, int size) {
                static public Node e() {
                    return new Node(0, 0);
                }

                public Node add(Node other) {
                    return new Node(this.v + other.v, this.size + other.size);
                }
            }
            ;

            record Act(int update) {
                static public Act e() {
                    return new Act(0);
                }

                public Node apply(Node node) {
                    return new Node(update * node.size, node.size);
                }

                public Act compose(Act other) {
                    return new Act(this.update);
                }
            }

            var arr = new Integer[]{1, 1, 2, 3, 5, 8, 13};
            var lst = new LazySegmentTree<Node, Act>(
                    Arrays.stream(arr).map(n -> new Node(n, 1)).toArray(Node[]::new),
                    Node::add,
                    Act::apply,
                    Act::compose,
                    Node::e,
                    Act::e
            );
        }
    }

    record Pair(int v, int n) {
    }

    ;

    //遅延評価セグメンテーション木
    //T : Data
    //U : Lazy
    public static class LazySegmentTree<T, U> {
        //配列
        private Object[] array, lazy;
        //最下段のサイズ
        private int rowSize;
        //集約関数
        private BinaryOperator<T> operator;
        //遅延評価関数<U,Tを受取>
        //Tに対して遅延値Uを作用させて値を更新する
        private BiFunction<U, T, T> mapping;
        //遅延値Uの加算
        private BinaryOperator<U> composition;

        //初期化関数T
        private Supplier<T> e;
        //初期化関数U
        private Supplier<U> id;

        public LazySegmentTree(T[] input, BinaryOperator<T> ope, BiFunction<U, T, T> mapping, BinaryOperator<U> composition,
                               Supplier<T> e, Supplier<U> id) {
            int size = input.length;
            int n = 1;
            while (n < size) {
                n *= 2;
            }
            rowSize = n;
            //nは最下段に必要な要素数。
            //配列全体は2*n-1必要
            this.array = new Object[2 * n - 1];
            this.lazy = new Object[2 * n - 1];
            this.operator = ope;
            this.mapping = mapping;
            this.composition = composition;
            this.e = e;
            this.id = id;
            for (int i = 0; i < size; i++) {
                setType(i + n - 1, input[i]);
            }
            for (int i = n - 2; i >= 0; i--) {
                T a = getType(2 * i + 1);
                T b = getType(2 * i + 2);
                setType(i, this.operator.apply(a, b));
            }
        }

        /**
         * startからendまでの区間にfを作用させる
         *
         * @param start
         * @param end
         * @param f
         */
        public void apply(int start, int end, U f) {
            apply(start, end, 0, 0, rowSize, f);
        }

        private void apply(int start, int end, int k, int l, int r, U f) {
            //k番目の要素を評価
            eval(k, l, r);
            if (r <= start || end <= l) {
                return;
            }
            //被覆しているならlazyに格納したあと評価
            if (start <= l && r <= end) {
                mergeLazy(k, f);
                eval(k, l, r);
            } else {
                //異なるなら子の要素を計算して,値を更新
                apply(start, end, k * 2 + 1, l, (l + r) / 2, f);
                apply(start, end, k * 2 + 2, (l + r) / 2, r, f);
                T a = getType(2 * k + 1);
                T b = getType(2 * k + 2);
                setType(k, this.operator.apply(a, b));
            }
        }

        public T getRange(int start, int end) {
            return getRange(start, end, 0, 0, rowSize);
        }

        private T getRange(int start, int end, int k, int l, int r) {
            if (r <= start || end <= l) {
                return e.get();
            }
            eval(k, l, r);
            if (start <= l && r <= end) {
                return getType(k);
            }
            T a = getRange(start, end, k * 2 + 1, l, (l + r) / 2);
            T b = getRange(start, end, k * 2 + 2, (l + r) / 2, r);
            return this.operator.apply(a, b);
        }

        //遅延評価
        private void eval(int k, int l, int r) {
            if (getLazy(k) != id.get()) {
                T a = getType(k);
                U b = getLazy(k);
                setType(k, this.mapping.apply(b, a));
                if (r - l > 1) {
                    mergeLazy(2 * k + 1, b);
                    mergeLazy(2 * k + 2, b);
                }
                setLazy(k, id.get());
            }
        }

        private void mergeLazy(int k, U f) {
            setLazy(k, this.composition.apply(f, getLazy(k)));
        }

        @SuppressWarnings("unchecked")
        private T getType(int index) {
            return (T) Objects.requireNonNullElse(this.array[index], e.get());
        }

        private void setType(int index, T e) {
            this.array[index] = (Object) e;
        }

        @SuppressWarnings("unchecked")
        private U getLazy(int index) {
            return (U) Objects.requireNonNullElse(this.lazy[index], id.get());
        }

        private void setLazy(int index, U e) {
            this.lazy[index] = (Object) e;
        }

        public void debug() {
            for (Object i : this.array) {
                System.out.println(i);
            }
        }

    }

}
