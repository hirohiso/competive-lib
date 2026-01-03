package jp.hirohiso.competive.util.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AutomatonDPMain {

    public static void main(String[] args) {
        var automaton = new AutomatonDp<State, Long, Integer>(
                new Indexer(0, 0, 0),
                Long::sum,
                new ArrayList<>(),
                (state, p) -> {
                    return new ArrayList<>();
                },
                State::fromArray,
                State::toArray,
                0L
        );
        //automaton.set();
        automaton.solve();
        //automaton.get();
    }


    //オートマトンDPの汎用化
    //状態 S
    //状態で管理するモノイド M
    //状態と配列のマッピング indexer
    //遷移先状態のモノイドに対して遷移元状態のモノイドを演算 operator
    //パラメータを与えるiterator<P>
    //状態とパラメータから次の状態を列挙するiterator
    //モノイドMの単位元
    static class AutomatonDp<S, M, P> {
        Indexer indexer;
        BiFunction<M, M, M> operator;
        ArrayList<P> parameterList;
        BiFunction<S, P, ArrayList<S>> nextStateListProvider;
        Function<int[], S> stateFromArray;
        Function<S, int[]> stateToArray;
        M ie;

        M[] dp;

        @SuppressWarnings("unchecked")
        public AutomatonDp(Indexer indexer,
                           BiFunction<M, M, M> operator,
                           ArrayList<P> parameterList,
                           BiFunction<S, P, ArrayList<S>> nextStateListProvider,
                           Function<int[], S> stateFromArray,
                           Function<S, int[]> stateToArray,
                           M ie) {
            this.indexer = indexer;
            this.operator = operator;
            this.parameterList = parameterList;
            this.nextStateListProvider = nextStateListProvider;
            dp = (M[]) (new Object[indexer.size()]);
            Arrays.fill(dp, ie);
            this.ie = ie;
            this.stateFromArray = stateFromArray;
            this.stateToArray = stateToArray;

        }

        public void set(S state, M val) {
            var pos = indexer.idxOf(state, stateToArray);
            dp[pos] = val;
        }

        @SuppressWarnings("unchecked")
        public void solve() {
            for (var p : parameterList) {
                var next = (M[]) (new Object[indexer.size()]);
                Arrays.fill(next, ie);
                for (var idx = 0; idx < dp.length; idx++) {
                    var state = indexer.coordsTo(idx, stateFromArray);
                    var nextStateList = nextStateListProvider.apply(state, p);
                    for (var s : nextStateList) {
                        var nextIdx = indexer.idxOf(s, stateToArray);
                        next[nextIdx] = operator.apply(next[nextIdx], dp[idx]);
                    }
                }
                dp = next;
            }
        }

        public M get(S s) {
            return dp[indexer.idxOf(s, stateToArray)];
        }
    }


    record State(int s) {
        public int[] toArray() {
            return new int[]{};
        }

        public static State fromArray(int[] arr) {
            return new State(arr[0]);
        }
    }

    static class Indexer {
        private final int[] dims;
        private final int[] mult;

        /**
         * dims = {2, 2, 2, An0, An1, An2}
         */
        public Indexer(int... dims) {
            this.dims = dims.clone();
            this.mult = new int[dims.length];

            var m = 1;
            for (var i = dims.length - 1; i >= 0; i--) {
                mult[i] = m;
                m *= dims[i];
            }
        }

        /**
         * flatten index
         */
        public int idx(int... coords) {
            if (coords.length != dims.length) {
                throw new IllegalArgumentException("coord length mismatch");
            }
            var pos = 0;
            for (var i = 0; i < dims.length; i++) {
                pos += coords[i] * mult[i];
            }
            return pos;
        }

        /**
         * unflatten -> multidimensional
         */
        public int[] coords(int pos) {
            var c = new int[dims.length];
            for (var i = 0; i < dims.length; i++) {
                c[i] = (pos / mult[i]) % dims[i];
            }
            return c;
        }

        /**
         * total size
         */
        public int size() {
            var s = 1;
            for (int d : dims) s *= d;
            return s;
        }

        public <T> int idxOf(T state, Function<T, int[]> mapper) {
            return idx(mapper.apply(state));
        }

        public <T> T coordsTo(int pos, Function<int[], T> mapper) {
            var arr = coords(pos);
            return mapper.apply(arr);
        }
    }

}
