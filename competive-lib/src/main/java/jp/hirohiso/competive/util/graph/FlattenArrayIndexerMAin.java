package jp.hirohiso.competive.util.graph;

import java.util.function.Function;

public class FlattenArrayIndexerMAin {
    public static void main(String[] args) {
        var indexer = new Indexer(2, 2, 2, 2, 2, 2, 10, 10, 10);
        System.err.println(indexer.size());
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
