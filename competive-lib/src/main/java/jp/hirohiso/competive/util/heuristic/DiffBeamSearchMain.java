package jp.hirohiso.competive.util.heuristic;

import java.util.ArrayList;
import java.util.Arrays;

public class DiffBeamSearchMain {
    public static void main(String[] args) {
        Solver solver = new Solver();
        long[] ans = solver.solve();

        // TODO PROBLEM: packed action 列を出力形式に変換する
        System.out.println(ans.length);
        for (long x : ans) {
            System.out.println(x);
        }
    }
    // =========================================================
    // Solver
    // =========================================================
    static final class Solver {
        long[] solve() {
            // TODO PROBLEM: 入力を受けて初期 State を作る
            DiffBeamSearchTemplate.MyState state = new DiffBeamSearchTemplate.MyState(10);

            // TODO PROBLEM: rootEvaluator を作る
            DiffBeamSearchTemplate.MyEvaluator rootEvaluator = new DiffBeamSearchTemplate.MyEvaluator(0);

            // TODO PROBLEM: rootHash を作る
            int rootHash = 0;

            int maxTurn = 100;
            int beamWidth = 100;
            int nodeCapacity = 1_000_000;

            DiffBeamSearchTemplate.Tree<DiffBeamSearchTemplate.MyEvaluator, DiffBeamSearchTemplate.MyState> tree = new DiffBeamSearchTemplate.Tree<>(state, nodeCapacity, rootEvaluator, rootHash);
            DiffBeamSearchTemplate.Selector<DiffBeamSearchTemplate.MyEvaluator> selector = new DiffBeamSearchTemplate.Selector<>(beamWidth);

            int[] curr = new int[beamWidth];
            int currSize = 0;
            int[] next = new int[beamWidth];

            for (int turn = 0; turn < maxTurn; turn++) {
                selector.clear();
                tree.dfs(selector);

                if (selector.hasFinished()) {
                    DiffBeamSearchTemplate.Candidate<DiffBeamSearchTemplate.MyEvaluator> c = selector.finished().get(0);
                    long[] prefix = tree.restorePath(c.parent);
                    long[] ans = Arrays.copyOf(prefix, prefix.length + 1);
                    ans[prefix.length] = c.actionPacked;
                    return ans;
                }

                int nextSize = 0;
                for (int i = 0; i < selector.size(); i++) {
                    next[nextSize++] = tree.addLeaf(selector.get(i));
                }

                if (nextSize == 0) break;

                for (int i = 0; i < currSize; i++) {
                    tree.removeIfLeaf(curr[i]);
                }

                int[] tmp = curr;
                curr = next;
                next = tmp;
                currSize = nextSize;
            }

            if (currSize == 0) return new long[0];

            int best = tree.bestLeaf(curr, currSize);
            return tree.restorePath(best);
        }
    }
}
/*
既に述べたように、状態遷移に必要な情報を Action で保持し、評価結果の比較に必要な情報を Evaluator で保持することを想定しています。

一方で、Action と Evaluator に、より多くの情報を持たせることもできます。

状態が変数 x をメンバとして保持し、状態を更新するときに毎度 x を更新するものとします。
このとき、x を状態ではなく Action や Evaluator が保持するようにすれば、Euler Tour における x の更新を省略できます。
状態の関数内で x を使用したいときには、Action や Evaluator のメンバにアクセスすればよいです。
x の後退処理（Euler Tour における子から親への遷移）を実装する必要がなくなるというメリットもあります。
例えば、複数の評価項目があるときに、Evaluator で各評価項目の値を保持するということが考えられます。
また、後退処理がなくなるため、浮動小数点数も扱いやすくなります6。
一方で、Action や Evaluator の使用メモリが小さいほどよいという側面もあるため、全ての変数を Action や Evaluator に保持すればいいわけではありません。
更新が面倒で使用メモリが少ない変数だけを Action や Evaluator に保持するとよいと思います。
例えば、状態内で探索木の深さを管理する場合、深さを更新するときにインクリメントやデクリメントという非常に軽い処理しか行われないため、
深さは状態に保持すればよいと思います。
ちなみに、Action と State を空にして Evaluator で全ての情報を保持するようにすると、愚直なビームサーチらしくなります。

 */

// 差分更新ビームサーチの最小テンプレート
class DiffBeamSearchTemplate {

    // =========================================================
    // Action
    // =========================================================
    static final class Action {
        // TODO PROBLEM: int にするか long にするか決める
        final long packed;

        Action(long packed) {
            this.packed = packed;
        }

        // TODO PROBLEM: 操作の encode を定義する
        static Action of(int a, int b, int c, int d) {
            long packed = ((long) a)
                    | ((long) b << 16)
                    | ((long) c << 32)
                    | ((long) d << 48);
            return new Action(packed);
        }

        // TODO PROBLEM: 必要な decode アクセサを定義する
        int a() { return (int) (packed & 0xFFFFL); }
        int b() { return (int) ((packed >>> 16) & 0xFFFFL); }
        int c() { return (int) ((packed >>> 32) & 0xFFFFL); }
        int d() { return (int) ((packed >>> 48) & 0xFFFFL); }
    }

    // =========================================================
    // Evaluator
    // =========================================================
    interface Evaluator {
        int score();
    }

    static final class MyEvaluator implements Evaluator {
        // TODO PROBLEM: 評価に必要な差分情報を定義する
        final int value;

        MyEvaluator(int value) {
            this.value = value;
        }

        @Override
        public int score() {
            // TODO PROBLEM: 評価関数を定義する
            return value;
        }
    }

    // =========================================================
    // Candidate
    // =========================================================
    static final class Candidate<E extends Evaluator> {
        // TODO PROBLEM: Action に合わせて型をそろえる
        long actionPacked;
        E evaluator;
        int hash;
        int parent;
        int score;
    }

    interface CandidateAcceptor<E extends Evaluator> {
        // TODO PROBLEM: Action に合わせて型をそろえる
        void accept(long actionPacked, E evaluator, int hash, int parent, boolean finished);
    }

    // =========================================================
    // State
    // =========================================================
    interface BeamState<E extends Evaluator> {
        void moveForward(Action action);
        void moveBackward(Action action);
        void expand(E evaluator, int hash, int parentNodeId, CandidateAcceptor<E> acceptor);
    }

    static final class MyState implements BeamState<MyEvaluator> {
        // TODO PROBLEM: 状態として保持するものを書く
        int[][] board;

        MyState(int n) {
            board = new int[n][n];
            // TODO PROBLEM: 初期状態を作る
        }

        @Override
        public void moveForward(Action action) {
            // TODO PROBLEM: action を適用する
            // TODO PROBLEM: 必要なら盤面・位置・統計量を更新する
            // TODO PROBLEM: swap系ならその処理を書く
        }

        @Override
        public void moveBackward(Action action) {
            // TODO PROBLEM: forward の逆操作を書く
            // TODO PROBLEM: swap系なら moveForward(action) を再利用できることがある
        }

        @Override
        public void expand(MyEvaluator evaluator, int hash, int parentNodeId, CandidateAcceptor<MyEvaluator> acceptor) {
            // TODO PROBLEM: 現状態から候補手を列挙する
            // TODO PROBLEM: 各候補について nextHash を計算する
            // TODO PROBLEM: 各候補について nextEvaluator を計算する
            // TODO PROBLEM: finished 判定を書く
            // TODO PROBLEM: acceptor.accept(...) を呼ぶ

            /*
            for (...) {
                Action action = Action.of(...);
                int nextHash = ...;
                MyEvaluator nextEvaluator = new MyEvaluator(...);
                boolean finished = ...;
                acceptor.accept(action.packed, nextEvaluator, nextHash, parentNodeId, finished);
            }
            */
        }
    }

    // =========================================================
    // Tree Node / Pool
    // =========================================================
    static final class Node<E extends Evaluator> {
        // TODO PROBLEM: Action に合わせて型をそろえる
        long actionPacked;
        E evaluator;
        int hash;
        int parent = -1;
        int child = -1;
        int left = -1;
        int right = -1;
    }

    static final class NodePool<E extends Evaluator> {
        private final Node<E>[] data;
        private final int[] garbage;
        private int used = 0;
        private int garbageSize = 0;

        @SuppressWarnings("unchecked")
        NodePool(int capacity) {
            data = (Node<E>[]) new Node[capacity];
            for (int i = 0; i < capacity; i++) data[i] = new Node<>();
            garbage = new int[capacity];
        }

        int alloc() {
            if (garbageSize > 0) return garbage[--garbageSize];
            return used++;
        }

        void free(int idx) {
            garbage[garbageSize++] = idx;
        }

        Node<E> get(int idx) {
            return data[idx];
        }
    }

    // =========================================================
    // Selector
    // =========================================================
    static final class Selector<E extends Evaluator> implements CandidateAcceptor<E> {
        private final int beamWidth;
        private final Candidate<E>[] cand;
        private int size = 0;
        private int worstIndex = -1;
        private final ArrayList<Candidate<E>> finished = new ArrayList<>();

        @SuppressWarnings("unchecked")
        Selector(int beamWidth) {
            this.beamWidth = beamWidth;
            this.cand = (Candidate<E>[]) new Candidate[beamWidth];
            for (int i = 0; i < beamWidth; i++) cand[i] = new Candidate<>();
        }

        void clear() {
            size = 0;
            worstIndex = -1;
            finished.clear();
        }

        @Override
        public void accept(long actionPacked, E evaluator, int hash, int parent, boolean finishedFlag) {
            int score = evaluator.score();

            if (finishedFlag) {
                Candidate<E> c = new Candidate<>();
                c.actionPacked = actionPacked;
                c.evaluator = evaluator;
                c.hash = hash;
                c.parent = parent;
                c.score = score;
                finished.add(c);
                return;
            }

            if (size < beamWidth) {
                fill(cand[size], actionPacked, evaluator, hash, parent, score);
                if (worstIndex == -1 || cand[worstIndex].score < score) worstIndex = size;
                size++;
            } else if (score < cand[worstIndex].score) {
                fill(cand[worstIndex], actionPacked, evaluator, hash, parent, score);
                recomputeWorst();
            }
        }

        private static <E extends Evaluator> void fill(Candidate<E> c, long actionPacked, E evaluator, int hash, int parent, int score) {
            c.actionPacked = actionPacked;
            c.evaluator = evaluator;
            c.hash = hash;
            c.parent = parent;
            c.score = score;
        }

        private void recomputeWorst() {
            int wi = 0;
            for (int i = 1; i < size; i++) {
                if (cand[wi].score < cand[i].score) wi = i;
            }
            worstIndex = wi;
        }

        int size() { return size; }
        Candidate<E> get(int i) { return cand[i]; }
        boolean hasFinished() { return !finished.isEmpty(); }
        ArrayList<Candidate<E>> finished() { return finished; }
    }

    // =========================================================
    // Tree
    // =========================================================
    static final class Tree<E extends Evaluator, S extends BeamState<E>> {
        private final S state;
        private final NodePool<E> pool;
        private final int root;

        Tree(S state, int capacity, E rootEval, int rootHash) {
            this.state = state;
            this.pool = new NodePool<>(capacity);
            this.root = pool.alloc();
            Node<E> r = pool.get(root);
            r.actionPacked = 0L;
            r.evaluator = rootEval;
            r.hash = rootHash;
        }

        void dfs(Selector<E> selector) {
            int v = root;
            while (true) {
                v = moveToLeaf(v);
                Node<E> node = pool.get(v);
                state.expand(node.evaluator, node.hash, v, selector);

                v = moveToAncestor(v);
                if (v == root) break;
                v = moveToRight(v);
            }
        }

        private int moveToLeaf(int v) {
            int child = pool.get(v).child;
            while (child != -1) {
                v = child;
                state.moveForward(new Action(pool.get(v).actionPacked));
                child = pool.get(v).child;
            }
            return v;
        }

        private int moveToAncestor(int v) {
            while (v != root && pool.get(v).right == -1) {
                state.moveBackward(new Action(pool.get(v).actionPacked));
                v = pool.get(v).parent;
            }
            return v;
        }

        private int moveToRight(int v) {
            state.moveBackward(new Action(pool.get(v).actionPacked));
            v = pool.get(v).right;
            state.moveForward(new Action(pool.get(v).actionPacked));
            return v;
        }

        int addLeaf(Candidate<E> c) {
            int p = c.parent;
            int sibling = pool.get(p).child;
            int v = pool.alloc();
            Node<E> nv = pool.get(v);
            nv.actionPacked = c.actionPacked;
            nv.evaluator = c.evaluator;
            nv.hash = c.hash;
            nv.parent = p;
            nv.child = -1;
            nv.left = -1;
            nv.right = sibling;

            pool.get(p).child = v;
            if (sibling != -1) pool.get(sibling).left = v;
            return v;
        }

        void removeIfLeaf(int v) {
            if (pool.get(v).child == -1) removeLeaf(v);
        }

        private void removeLeaf(int v) {
            while (true) {
                Node<E> node = pool.get(v);
                int left = node.left;
                int right = node.right;

                if (left == -1) {
                    int parent = node.parent;
                    if (parent == -1) throw new IllegalStateException("root removed");
                    pool.free(v);
                    pool.get(parent).child = right;
                    if (right != -1) {
                        pool.get(right).left = -1;
                        return;
                    }
                    v = parent;
                } else {
                    pool.free(v);
                    pool.get(left).right = right;
                    if (right != -1) pool.get(right).left = left;
                    return;
                }
            }
        }

        long[] restorePath(int v) {
            int len = 0;
            for (int u = v; pool.get(u).parent != -1; u = pool.get(u).parent) len++;
            long[] path = new long[len];
            int idx = len - 1;
            while (pool.get(v).parent != -1) {
                path[idx--] = pool.get(v).actionPacked;
                v = pool.get(v).parent;
            }
            return path;
        }

        int bestLeaf(int[] nodes, int size) {
            int best = nodes[0];
            for (int i = 1; i < size; i++) {
                if (pool.get(nodes[i]).evaluator.score() < pool.get(best).evaluator.score()) {
                    best = nodes[i];
                }
            }
            return best;
        }
    }
}