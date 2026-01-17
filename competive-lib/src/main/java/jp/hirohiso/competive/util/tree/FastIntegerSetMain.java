package jp.hirohiso.competive.util.tree;

import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;

public class FastIntegerSetMain {
    public static void main(String[] args) {
        new FastIntegerSet(64);
        new FastIntegerSet(63);
        new FastIntegerSet(65);

        new FastIntegerSet(128);
        new FastIntegerSet(129);
        var set = new FastIntegerSet(262144);
        var tree = new TreeSet<Integer>();

        var rnd = new Random();
        for (int i = 0; i < 262144; i++) {
            set.add(i);
            tree.add(i);
        }
        for (int i = 0; i < 2 * 1_000; i++) {
            var list = new LinkedList<Integer>();
            for (int j = 0; j < 100; j++) {
                var v = rnd.nextInt(262144);
                list.add(v);
            }
            for (var v : list) {
                set.remove(v);
                tree.remove(v);
            }

            if (set.first() != tree.first()) {
                System.err.println(tree);
                System.err.println();
            }
            for (int j = 0; j < 100; j++) {
                var v = rnd.nextInt(262144);
                if(set.contains(v) != tree.contains(v)){
                    System.err.println(tree);
                    System.err.println();
                }
            }
            for (var v : list) {
                set.add(v);
                tree.add(v);
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
            if (v < 0 || v >= N) return false;
            var index = (size - rowSize) + (v / BASE);
            return ((this.arr[index] >> (v % BASE)) & 1) != 0;
        }

        public void add(int v) {
            if (v < 0 || v >= N) return;
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
                if (f != 0) {
                    break;
                }
                bit = (index - 1) % BASE;
                index = (index - 1) / BASE;
            }
        }

        public void remove(int v) {
            if (v < 0 || v >= N) return;
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
                if (this.arr[index] != 0) {
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

            // vがNを超える場合は-1を返す
            if (v >= N) {
                return -1;
            }

            var index = (size - rowSize) + (v / BASE);
            var pos = v % BASE;

            // 現在のノードでv以降のビットを探す
            var mask = arr[index] & (~0L << pos);
            if (mask != 0) {
                // 同じノード内に見つかった
                var bit = Long.numberOfTrailingZeros(mask);
                var result = (v / BASE) * BASE + bit;
                // 結果がN未満ならば返す、そうでなければ親ノードに上がる
                if (result < N) {
                    return result;
                }
            }

            // 親ノードに上がっていく
            while (index > 0) {
                var parentIndex = (index - 1) / BASE;
                var childPos = (index - 1) % BASE; // 親の中で自分がどの子だったか

                // 親ノードで次の有効なブランチを探す（childPos より右）
                var mask2 = (childPos == 63) ? 0L : (arr[parentIndex] & (~0L << (childPos + 1)));
                if (mask2 != 0) {
                    var bit = Long.numberOfTrailingZeros(mask2);
                    var result = findMin(parentIndex * BASE + 1 + bit);
                    // 結果がNを超えないかチェック
                    if (result >= N) {
                        return -1;
                    }
                    return result;
                }

                index = parentIndex;
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
            while (index > 0) {
                var parentIndex = (index - 1) / BASE;
                var childPos = (index - 1) % BASE;

                // 親ノードで前の有効なブランチを探す（childPos より左）
                var mask2 = (childPos == 0) ? 0L : (arr[parentIndex] & ((1L << childPos) - 1));
                if (mask2 != 0) {
                    var bit = 63 - Long.numberOfLeadingZeros(mask2);
                    var result = findMax(parentIndex * BASE + 1 + bit);
                    // 結果が有効範囲内かチェック
                    if (result < 0 || result >= N) {
                        return -1;
                    }
                    return result;
                }

                index = parentIndex;
            }
            return -1;
        }

        // 指定したインデックスのサブツリーの最小値を探す
        private int findMin(int index) {
            // リーフレベルまで降りる
            while (index < size - rowSize) {
                if (arr[index] == 0) {
                    return -1; // 空のノード
                }
                var bit = Long.numberOfTrailingZeros(arr[index]);
                index = index * BASE + 1 + bit;
            }

            // リーフノードで最小のビットを見つける
            if (arr[index] == 0) {
                return -1; // 空のリーフノード
            }
            var leafIndex = index - (size - rowSize);
            var bit = Long.numberOfTrailingZeros(arr[index]);
            return leafIndex * BASE + bit;
        }

        // 指定したインデックスのサブツリーの最大値を探す
        private int findMax(int index) {
            // リーフレベルまで降りる
            while (index < size - rowSize) {
                if (arr[index] == 0) {
                    return -1; // 空のノード
                }
                var bit = 63 - Long.numberOfLeadingZeros(arr[index]);
                index = index * BASE + 1 + bit;
            }

            // リーフノードで最大のビットを見つける
            if (arr[index] == 0) {
                return -1; // 空のリーフノード
            }
            var leafIndex = index - (size - rowSize);
            var bit = 63 - Long.numberOfLeadingZeros(arr[index]);
            return leafIndex * BASE + bit;
        }
    }
}
