package jp.hirohiso.competive.util.tree;

import java.util.Random;
import java.util.TreeSet;

public class FastIntegerSetMain {
    public static void main(String[] args) {
        new FastIntegerSet(64);
        new FastIntegerSet(63);
        new FastIntegerSet(65);

        new FastIntegerSet(128);
        new FastIntegerSet(129);
        var set = new FastIntegerSet(2 * 100_000);
        var tree = new TreeSet<Integer>();

        var rnd = new Random();
        for (int i = 0; i < 2 * 100_000; i++) {
            var v = rnd.nextInt(2 * 100_000);
            set.add(v);
            tree.add(v);

            if(set.first() != tree.first()){
                System.err.println(tree);
                System.err.println();
            }
            var v2 = rnd.nextInt(2 * 100_000);
            set.remove(v2);
            tree.remove(v2);
            if(set.first() != tree.first()){
                System.err.println(tree);
                System.err.println();
            }
        }


        // higher/lowerのテスト
        System.err.println("\n--- higher/lower test ---");
        var testSet = new FastIntegerSet(1000);
        testSet.add(10);
        testSet.add(50);
        testSet.add(100);
        testSet.add(500);
        testSet.add(999);

        System.err.println("higher(9) = " + testSet.higher(9) + " (expected: 10)");
        System.err.println("higher(10) = " + testSet.higher(10) + " (expected: 50)");
        System.err.println("higher(49) = " + testSet.higher(49) + " (expected: 50)");
        System.err.println("higher(50) = " + testSet.higher(50) + " (expected: 100)");
        System.err.println("higher(999) = " + testSet.higher(999) + " (expected: -1)");

        System.err.println("lower(11) = " + testSet.lower(11) + " (expected: 10)");
        System.err.println("lower(50) = " + testSet.lower(50) + " (expected: 10)");
        System.err.println("lower(100) = " + testSet.lower(100) + " (expected: 50)");
        System.err.println("lower(500) = " + testSet.lower(500) + " (expected: 100)");
        System.err.println("lower(10) = " + testSet.lower(10) + " (expected: -1)");

        System.err.println("lower(1000) = " + testSet.lower(1000) + " (expected: 999)");
    }


    //64分木による高速な順序付き整数集合
    static class FastIntegerSet {
        static final int BASE = 64;

        int N;
        int cnt = 0;
        int size = 0;
        int rowSize = 0;

        long[] arr;

        public FastIntegerSet(int n) {
            this.N = n;
            var rowSize = 1;//下段のサイズを決定
            var len = 0;
            while (rowSize * BASE < n) {
                len += rowSize;
                rowSize *= BASE;
            }
            len += rowSize;
            size = len;
            this.rowSize = rowSize;
            arr = new long[len];
        }

        public boolean contains(int v) {
            var index = (size - rowSize) + (v / BASE);
            return ((this.arr[index] >> (v % BASE)) & 1) != 0;
        }

        public void add(int v) {
            if (contains(v)) {
                return;
            }
            cnt++;
            var index = (size - rowSize) + (v / BASE);
            var bit = v % BASE;
            while (true) {
                var f = this.arr[index];
                this.arr[index] |= (1L << bit);
                if (index == 0) {
                    break;
                }
                if (f > 0) {
                    break;
                }
                bit = (index - 1) % BASE;
                index = (index - 1) / BASE;
            }
        }

        public void remove(int v) {
            if (!contains(v)) {
                return;
            }
            cnt--;
            var index = (size - rowSize) + (v / BASE);
            var bit = v % BASE;
            while (true) {
                this.arr[index] &= ~(1L << bit);
                if (index == 0) {
                    break;
                }
                if (this.arr[index] > 0) {
                    break;
                }
                bit = (index - 1) % BASE;
                index = (index - 1) / BASE;
            }
        }

        public int first() {
            return higher(-1);
        }

        public int last() {
            return lower(N);
        }

        // vより大きい最小の整数を返す（存在しない場合は-1）
        public int higher(int v) {
            if (v >= N - 1) {
                return -1;
            }
            v++;
            var index = (size - rowSize) + (v / BASE);
            var pos = v % BASE;

            // 現在のノードでv以降のビットを探す
            var mask = arr[index] & (~0L << pos);
            if (mask != 0) {
                // 同じノード内に見つかった
                var bit = Long.numberOfTrailingZeros(mask);
                return (v / BASE) * BASE + bit;
            }

            // 親ノードに上がっていく
            var value = v;
            while (index > 0) {
                var parentIndex = (index - 1) / BASE;
                var parentPos = (value / BASE) % BASE;

                // 親ノードで次の有効なブランチを探す
                mask = arr[parentIndex] & (~0L << (parentPos + 1));
                if (mask != 0) {
                    // 次のブランチが見つかった
                    var bit = Long.numberOfTrailingZeros(mask);
                    return findMin(parentIndex * BASE + 1 + bit);
                }

                index = parentIndex;
                value = value / BASE;
            }

            return -1;
        }

        // vより小さい最大の整数を返す（存在しない場合は-1）
        public int lower(int v) {
            if (v <= 0) {
                return -1;
            }
            v--;
            var index = (size - rowSize) + (v / BASE);
            var pos = v % BASE;

            // 現在のノードでv以前のビットを探す
            // pos == 63の場合、(1L << 64)は(1L << 0)になってしまうため特別処理
            var mask = (pos == 63) ? arr[index] : (arr[index] & ((1L << (pos + 1)) - 1));
            if (mask != 0) {
                // 同じノード内に見つかった
                var bit = 63 - Long.numberOfLeadingZeros(mask);
                return (v / BASE) * BASE + bit;
            }

            // 親ノードに上がっていく
            var value = v;
            while (index > 0) {
                var parentIndex = (index - 1) / BASE;
                var parentPos = (value / BASE) % BASE;

                // 親ノードで前の有効なブランチを探す
                mask = arr[parentIndex] & ((1L << parentPos) - 1);
                if (mask != 0) {
                    // 前のブランチが見つかった
                    var bit = 63 - Long.numberOfLeadingZeros(mask);
                    return findMax(parentIndex * BASE + 1 + bit);
                }

                index = parentIndex;
                value = value / BASE;
            }

            return -1;
        }

        // 指定したインデックスのサブツリーの最小値を探す
        private int findMin(int index) {
            // リーフレベルまで降りる
            while (index < size - rowSize) {
                var bit = Long.numberOfTrailingZeros(arr[index]);
                index = index * BASE + 1 + bit;
            }

            // リーフノードで最小のビットを見つける
            var leafIndex = index - (size - rowSize);
            var bit = Long.numberOfTrailingZeros(arr[index]);
            return leafIndex * BASE + bit;
        }

        // 指定したインデックスのサブツリーの最大値を探す
        private int findMax(int index) {
            // リーフレベルまで降りる
            while (index < size - rowSize) {
                var bit = 63 - Long.numberOfLeadingZeros(arr[index]);
                index = index * BASE + 1 + bit;
            }

            // リーフノードで最大のビットを見つける
            var leafIndex = index - (size - rowSize);
            var bit = 63 - Long.numberOfLeadingZeros(arr[index]);
            return leafIndex * BASE + bit;
        }
    }
}
