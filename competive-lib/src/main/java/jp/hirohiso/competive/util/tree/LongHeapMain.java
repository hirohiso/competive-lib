package jp.hirohiso.competive.util.tree;

public class LongHeapMain {

    static void main() {

    }


    public static class MaxLongHeap{

    }


    public static class LongMinHeap {
        private long[] heap;
        private int size;

        public LongMinHeap(int initialCapacity) {
            heap = new long[Math.max(1, initialCapacity)];
        }

        public int size() {
            return size;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public void clear() {
            size = 0;
        }

        public long peek() {
            if (size == 0) {
                throw new IllegalStateException("Heap is empty");
            }
            return heap[0];
        }

        public void add(long value) {
            if (size == heap.length) {
                grow();
            }

            int i = size++;
            while (i > 0) {
                int parent = (i - 1) >>> 1;
                long parentValue = heap[parent];

                if (parentValue <= value) {
                    break;
                }

                heap[i] = parentValue;
                i = parent;
            }
            heap[i] = value;
        }

        public long poll() {
            if (size == 0) {
                throw new IllegalStateException("Heap is empty");
            }

            long result = heap[0];
            long value = heap[--size];

            if (size == 0) {
                return result;
            }

            int i = 0;
            int half = size >>> 1; // 葉でない頂点の範囲

            while (i < half) {
                int left = (i << 1) + 1;
                int right = left + 1;

                int child = left;
                long childValue = heap[left];

                if (right < size && heap[right] < childValue) {
                    child = right;
                    childValue = heap[right];
                }

                if (value <= childValue) {
                    break;
                }

                heap[i] = childValue;
                i = child;
            }

            heap[i] = value;
            return result;
        }

        private void grow() {
            int oldCapacity = heap.length;
            int newCapacity = oldCapacity + (oldCapacity >>> 1) + 1;
            heap = java.util.Arrays.copyOf(heap, newCapacity);
        }
    }
}
