package jp.hirohiso.competive.util.math;

import java.util.*;

//出典
//https://joisino.hatenablog.com/entry/constant
//https://scispace.com/pdf/dynamic-generation-of-discrete-random-variates-1vhd7mzgjd.pdf
public class DynamicDiscreteSamplerMain {
    public static void main(String[] args) {
        double[] weights = {1.0, 2.0, 7.0, 1000.0, 0.5};

        DynamicDiscreteSampler sampler =
                new DynamicDiscreteSampler(weights, new Random(0));

        int[] count = new int[weights.length];

        for (int t = 0; t < 200_000; t++) {
            count[sampler.sample()]++;
        }

        System.out.println("before update");
        System.out.println(Arrays.toString(count));

        sampler.update(1, 500.0);
        sampler.update(3, 10.0);

        Arrays.fill(count, 0);

        for (int t = 0; t < 200_000; t++) {
            count[sampler.sample()]++;
        }

        System.out.println("after update");
        System.out.println(Arrays.toString(count));
    }


    static class DynamicDiscreteSampler {
        private final int n;
        private final double[] w;
        private double total = 0.0;
        private final Random rnd;

        private final HashMap<Integer, Bucket> buckets = new HashMap<>();

        // rangeKey -> rangeWeight
        private final HashMap<Integer, Double> rangeSum = new HashMap<>();

        private final RangeSampler rangeSampler;

        private static class Bucket {
            final ArrayList<Integer> ids = new ArrayList<>();
            final HashMap<Integer, Integer> pos = new HashMap<>();
            double sum = 0.0;

            void add(int id, double weight) {
                pos.put(id, ids.size());
                ids.add(id);
                sum += weight;
            }

            void remove(int id, double weight) {
                int p = pos.remove(id);
                int last = ids.remove(ids.size() - 1);
                if (p < ids.size()) {
                    ids.set(p, last);
                    pos.put(last, p);
                }
                sum -= weight;
                if (Math.abs(sum) < 1e-12) sum = 0.0;
            }

            boolean isEmpty() {
                return ids.isEmpty();
            }
        }

        public DynamicDiscreteSampler(double[] initialWeights) {
            this(initialWeights, new Random());
        }

        public DynamicDiscreteSampler(double[] initialWeights, Random rnd) {
            this.n = initialWeights.length;
            this.w = initialWeights.clone();
            this.rnd = rnd;
            this.rangeSampler = new RangeSampler(rnd);

            for (int i = 0; i < n; i++) {
                if (w[i] < 0) throw new IllegalArgumentException("weight must be non-negative");
                if (w[i] > 0) {
                    int k = rangeOf(w[i]);
                    buckets.computeIfAbsent(k, key -> new Bucket()).add(i, w[i]);
                    addRangeSum(k, w[i]);
                    total += w[i];
                }
            }

            rangeSampler.rebuildAll(rangeSum);
        }

        // x in [2^k, 2^(k+1))
        private static int rangeOf(double x) {
            return Math.getExponent(x);
        }

        private void addRangeSum(int range, double delta) {
            rangeSum.put(range, rangeSum.getOrDefault(range, 0.0) + delta);
            if (Math.abs(rangeSum.get(range)) < 1e-12) {
                rangeSum.remove(range);
            }
        }

        public void update(int i, double newWeight) {
            if (i < 0 || i >= n) throw new IndexOutOfBoundsException();
            if (newWeight < 0) throw new IllegalArgumentException("weight must be non-negative");

            double old = w[i];

            if (old > 0) {
                int oldK = rangeOf(old);
                Bucket b = buckets.get(oldK);
                b.remove(i, old);
                if (b.isEmpty()) buckets.remove(oldK);

                addRangeSum(oldK, -old);
                rangeSampler.updateRange(oldK, rangeSum.getOrDefault(oldK, 0.0));

                total -= old;
            }

            w[i] = newWeight;

            if (newWeight > 0) {
                int newK = rangeOf(newWeight);
                buckets.computeIfAbsent(newK, key -> new Bucket()).add(i, newWeight);

                addRangeSum(newK, newWeight);
                rangeSampler.updateRange(newK, rangeSum.get(newK));

                total += newWeight;
            }
        }

        public int sample() {
            if (total <= 0) throw new IllegalStateException("all weights are zero");

            int range = rangeSampler.sample();
            Bucket b = buckets.get(range);
            return sampleFromBucket(range, b);
        }

        private int sampleFromBucket(int range, Bucket b) {
            double upper = Math.scalb(1.0, range + 1);

            while (true) {
                int id = b.ids.get(rnd.nextInt(b.ids.size()));
                if (rnd.nextDouble() * upper < w[id]) {
                    return id;
                }
            }
        }

        public double totalWeight() {
            return total;
        }

        // ------------------------------------------------------------
        // 第一層・第二層・小サイズ用構造
        // ------------------------------------------------------------

        private static class RangeSampler {
            private static final int BLOCK_SIZE = 32;

            private final Random rnd;

            // blockId -> block
            private final HashMap<Integer, Block> blocks = new HashMap<>();

            // 第一層: blockId を重みに比例して選ぶ
            private AliasTable topAlias = new AliasTable();

            RangeSampler(Random rnd) {
                this.rnd = rnd;
            }

            void rebuildAll(HashMap<Integer, Double> rangeSum) {
                blocks.clear();

                for (Map.Entry<Integer, Double> e : rangeSum.entrySet()) {
                    int range = e.getKey();
                    double weight = e.getValue();
                    if (weight <= 0) continue;

                    int blockId = Math.floorDiv(range, BLOCK_SIZE);
                    blocks.computeIfAbsent(blockId, Block::new).setRangeWeight(range, weight);
                }

                for (Block b : blocks.values()) {
                    b.rebuildAlias();
                }

                rebuildTopAlias();
            }

            void updateRange(int range, double newWeight) {
                int blockId = Math.floorDiv(range, BLOCK_SIZE);
                Block b = blocks.get(blockId);

                if (newWeight <= 0) {
                    if (b != null) {
                        b.removeRange(range);
                        if (b.isEmpty()) {
                            blocks.remove(blockId);
                        } else {
                            b.rebuildAlias();
                        }
                    }
                } else {
                    if (b == null) {
                        b = new Block(blockId);
                        blocks.put(blockId, b);
                    }
                    b.setRangeWeight(range, newWeight);
                    b.rebuildAlias();
                }

                rebuildTopAlias();
            }

            int sample() {
                if (blocks.isEmpty()) {
                    throw new IllegalStateException("no positive ranges");
                }

                int blockId = topAlias.sample(rnd);
                Block b = blocks.get(blockId);
                return b.sample(rnd);
            }

            private void rebuildTopAlias() {
                int m = blocks.size();
                int[] ids = new int[m];
                double[] weights = new double[m];

                int idx = 0;
                for (Block b : blocks.values()) {
                    ids[idx] = b.blockId;
                    weights[idx] = b.sum;
                    idx++;
                }

                topAlias.build(ids, weights);
            }
        }

        private static class Block {
            final int blockId;

            // range -> weight
            final HashMap<Integer, Double> weight = new HashMap<>();
            double sum = 0.0;

            // 第二層: block 内の range を選ぶ
            final AliasTable alias = new AliasTable();

            Block(int blockId) {
                this.blockId = blockId;
            }

            void setRangeWeight(int range, double newWeight) {
                double old = weight.getOrDefault(range, 0.0);
                if (newWeight <= 0) {
                    removeRange(range);
                    return;
                }

                weight.put(range, newWeight);
                sum += newWeight - old;
            }

            void removeRange(int range) {
                Double old = weight.remove(range);
                if (old != null) {
                    sum -= old;
                    if (Math.abs(sum) < 1e-12) sum = 0.0;
                }
            }

            boolean isEmpty() {
                return weight.isEmpty();
            }

            void rebuildAlias() {
                int m = weight.size();
                int[] ids = new int[m];
                double[] ws = new double[m];

                int idx = 0;
                for (Map.Entry<Integer, Double> e : weight.entrySet()) {
                    ids[idx] = e.getKey();
                    ws[idx] = e.getValue();
                    idx++;
                }

                alias.build(ids, ws);
            }

            int sample(Random rnd) {
                return alias.sample(rnd);
            }
        }

        private static class AliasTable {
            private int[] id = new int[0];
            private int[] alias = new int[0];
            private double[] prob = new double[0];

            void build(int[] ids, double[] weights) {
                int n = ids.length;
                this.id = ids.clone();
                this.alias = new int[n];
                this.prob = new double[n];

                if (n == 0) return;

                double sum = 0.0;
                for (double w : weights) sum += w;

                double[] scaled = new double[n];
                ArrayDeque<Integer> small = new ArrayDeque<>();
                ArrayDeque<Integer> large = new ArrayDeque<>();

                for (int i = 0; i < n; i++) {
                    scaled[i] = weights[i] * n / sum;
                    if (scaled[i] < 1.0) small.add(i);
                    else large.add(i);
                }

                while (!small.isEmpty() && !large.isEmpty()) {
                    int s = small.removeLast();
                    int l = large.removeLast();

                    prob[s] = scaled[s];
                    alias[s] = l;

                    scaled[l] = scaled[l] + scaled[s] - 1.0;
                    if (scaled[l] < 1.0) small.add(l);
                    else large.add(l);
                }

                while (!large.isEmpty()) {
                    prob[large.removeLast()] = 1.0;
                }
                while (!small.isEmpty()) {
                    prob[small.removeLast()] = 1.0;
                }
            }

            int sample(Random rnd) {
                int n = id.length;
                if (n == 0) throw new IllegalStateException("empty alias table");

                int i = rnd.nextInt(n);
                if (rnd.nextDouble() < prob[i]) {
                    return id[i];
                } else {
                    return id[alias[i]];
                }
            }
        }
    }

}
