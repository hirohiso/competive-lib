package jp.hirohiso.competive.util.tree;
//遅延評価セグメンテーション木
//T : Data
//U : Lazy

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LazySegmentTreeBottom {

    public static void main(String[] args) {

    }

    record Pair(int v, int n) {
    }

    /**
     * 区間作用と順序付き集約を行う反復型遅延セグメント木。
     * <p>0始まり、区間は半開 {@code [left,right)}。構築 O(N)、更新・照会・
     * 境界探索 O(log N)、全体集約 O(1)、領域 O(N)。
     * composition(new,old) は「old の後に new」で、再帰版とは引数順が逆。
     * mapping は集約と整合し、e/id は単位元。値と作用は不変とし equals が使えること。
     * 境界探索の述語は単調で単位元に対して真であること。</p>
     */
    public static class LazySegmentTree<T, U> {
        private final int n, size, log;
        private final T[] data;
        private final U[] lazy;

        private final BinaryOperator<T> op;
        private final Supplier<T> e;
        private final BiFunction<U, T, T> mapping;
        private final BinaryOperator<U> composition;
        private final Supplier<U> id;

        @SuppressWarnings("unchecked")
        public LazySegmentTree(int size, BinaryOperator<T> op, Supplier<T> e,
                               BiFunction<U, T, T> mapping, BinaryOperator<U> composition, Supplier<U> id) {
            this.op = op;
            this.e = e;
            this.mapping = mapping;
            this.composition = composition;
            this.id = id;
            this.size = size;
            var k = 1;
            while (k < size) {
                k <<= 1;
            }
            n = k;

            log = Integer.numberOfTrailingZeros(n);

            data = (T[]) new Object[n << 1];
            lazy = (U[]) new Object[n];
            Arrays.fill(lazy, id.get());
            Arrays.fill(data, e.get());
        }

        public LazySegmentTree(T[] input, BinaryOperator<T> op, Supplier<T> e,
                               BiFunction<U, T, T> mapping, BinaryOperator<U> composition, Supplier<U> id) {
            this(input.length, op, e, mapping, composition, id);
            build(input);
        }

        private void build(T[] input) {
            System.arraycopy(input, 0, data, n, input.length);
            for (int i = n - 1; i > 0; i--) {
                data[i] = op.apply(data[i << 1], data[i << 1 | 1]);
            }
        }

        private void push(int k) {
            if (lazy[k].equals(id.get())) {
                return;
            }
            var lk = k << 1;
            var rk = k << 1 | 1;
            data[lk] = mapping.apply(lazy[k], data[lk]);
            data[rk] = mapping.apply(lazy[k], data[rk]);
            if (lk < n) {
                lazy[lk] = composition.apply(lazy[k], lazy[lk]);
                lazy[rk] = composition.apply(lazy[k], lazy[rk]);
            }
            lazy[k] = id.get();
        }

        private void pushTo(int k) {
            for (int i = log; i > 0; i--) {
                push(k >> i);
            }
        }

        private void pushTo(int lk, int rk) {
            for (int i = log; i > 0; i--) {
                if (((lk >> i) << i) != lk) {
                    push(lk >> i);
                }
                if (((rk >> i) << i) != rk) {
                    push((rk - 1) >> i);
                }
            }
        }

        private void updateFrom(int k) {
            k >>= 1;
            while (k > 0) {
                data[k] = op.apply(data[k << 1], data[k << 1 | 1]);
                k >>= 1;
            }
        }

        private void updateFrom(int lk, int rk) {
            for (int i = 1; i <= log; i++) {
                if (((lk >> i) << i) != lk) {
                    var lki = lk >> i;
                    data[lki] = op.apply(data[lki << 1], data[lki << 1 | 1]);
                }
                if (((rk >> i) << i) != rk) {
                    var rki = (rk - 1) >> i;
                    data[rki] = op.apply(data[rki << 1], data[rki << 1 | 1]);
                }
            }
        }

        public void set(int index, T value) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index out of bounds: " + index);
            }
            int k = n + index;
            pushTo(k);
            data[k] = value;
            updateFrom(k);
        }

        public T get(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index out of bounds: " + index);
            }
            int k = n + index;
            pushTo(k);
            return data[k];
        }

        public T prod(int left, int right) {
            if (left < 0 || right > size || left > right) {
                throw new IndexOutOfBoundsException("Invalid range: [" + left + ", " + right + ")");
            }
            if (left == right) {
                return e.get();
            }
            left += n;
            right += n;
            pushTo(left,right);
            T resLeft = e.get();
            T resRight = e.get();
            while (left < right) {
                if ((left & 1) == 1) {
                    resLeft = op.apply(resLeft, data[left++]);
                }
                if ((right & 1) == 1) {
                    resRight = op.apply(data[--right], resRight);
                }
                left >>= 1;
                right >>= 1;
            }
            return op.apply(resLeft, resRight);
        }

        public T allProd() {
            return data[1];
        }

        public void apply(int p, U f) {
            if (p < 0 || p >= size) {
                throw new IndexOutOfBoundsException("Index out of bounds: " + p);
            }
            int k = n + p;
            pushTo(k);
            data[k] = mapping.apply(f, data[k]);
            updateFrom(k);
        }

        public void apply(int left, int right, U f) {
            if (left < 0 || right > size || left > right) {
                throw new IndexOutOfBoundsException("Invalid range: [" + left + ", " + right + ")");
            }
            if (left == right) {
                return;
            }
            left += n;
            right += n;
            pushTo(left, right);
            for (int nl = left, nr = right; nl < nr;) {
                if((nl & 1) == 1) {
                    data[nl] = mapping.apply(f, data[nl]);
                    if(nl < n) {
                        lazy[nl] = composition.apply(f, lazy[nl]);
                    }
                    nl++;
                }
                if((nr & 1) == 1) {
                    nr--;
                    data[nr] = mapping.apply(f, data[nr]);
                    if(nr < n) {
                        lazy[nr] = composition.apply(f, lazy[nr]);
                    }
                }
                nl >>= 1;
                nr >>= 1;
            }
            updateFrom(left, right);
        }

        public int maxRight(int left, Predicate<T> predicate) {
            if (left < 0 || left > size) {
                throw new IndexOutOfBoundsException("Index out of bounds: " + left);
            }
            if (!predicate.test(e.get())) {
                return left;
            }
            if (left == size) {
                return size;
            }
            left += n;
            pushTo(left);
            T sum = e.get();
            do {
                while ((left & 1) == 0) {
                    left >>= 1;
                }
                if (!predicate.test(op.apply(sum, data[left]))) {
                    while (left < n) {
                        push(left);
                        left <<= 1;
                        if (predicate.test(op.apply(sum, data[left]))) {
                            sum = op.apply(sum, data[left]);
                            left++;
                        }
                    }
                    return left - n;
                }
                sum = op.apply(sum, data[left++]);
            } while ((left & -left) != left);
            return size;
        }

        public int minLeft(int right, Predicate<T> predicate) {
            if (right < 0 || right > size) {
                throw new IndexOutOfBoundsException("Index out of bounds: " + right);
            }
            if (!predicate.test(e.get())) {
                return right;
            }
            if (right == 0) {
                return 0;
            }
            right += n;
            pushTo(right - 1);
            T sum = e.get();
            do {
                right--;
                while (right > 1 && (right & 1) == 1) {
                    right >>= 1;
                }
                if (!predicate.test(op.apply(data[right], sum))) {
                    while (right < n) {
                        push(right);
                        right = (right << 1) + 1;
                        if (predicate.test(op.apply(data[right], sum))) {
                            sum = op.apply(data[right], sum);
                            right--;
                        }
                    }
                    return right + 1 - n;
                }
                sum = op.apply(data[right], sum);
            } while ((right & -right) != right);
            return 0;
        }
    }
}

