package jp.hirohiso.competive.util.string;

import java.util.*;
import java.util.stream.IntStream;

public class SuffixArrayMain {
    public static void main(String[] args) {


        var rnd = new Random();

        var sumNaive = 0L;
        var sumIS = 0L;
        var times = 1000;
        for (int i = 0; i < times; i++) {
            var sb = new StringBuilder();
            for (int j = 0; j < rnd.nextInt(5000, 5001); j++) {
                var b = rnd.nextInt(26);
                sb.append((char) (b + 'a'));
            }
            var str = sb.toString();
            //System.err.println(str);

            var now = System.currentTimeMillis();
            var n = NaiveSuffixArray.sa(str);
            sumNaive += System.currentTimeMillis() - now;
            now = System.currentTimeMillis();
            var is = SuffixArray.sa(str + "$");
            sumIS += System.currentTimeMillis() - now;

        }

        System.out.println("Naive:" + (double) sumNaive / times);
        System.out.println("IS:" + (double) sumIS / times);
    }

    public static class SuffixArray {
        static final int BASE = '$';

        public static int[] sa(String in) {
            var cs = (in).chars().toArray();

            var max = Integer.MIN_VALUE;
            for (int i = 0; i < cs.length; i++) {
                cs[i] -= BASE;
                max = Math.max(cs[i], max);
            }
            return sa_is(cs, max);
        }

        private static int[] sa_is(int[] array, int maxLetter) {
            //System.err.println(Arrays.toString(array));

            if (array.length == 0) {
                return new int[]{};
            }
            if (array.length == 1) {
                return new int[]{0};
            }
            var isSType = new BitSet(array.length);
            var kindCount = new int[maxLetter + 1];//32bit * maxLetter

            //末尾処理
            //isSType.set(array.length - 1);
            kindCount[array[array.length - 1]]++;

            //その他の文字
            for (int i = array.length - 2; i >= 0; i--) {
                kindCount[array[i]]++;
                if (array[i] > array[i + 1]) {
                    //noting
                } else if (array[i] < array[i + 1]) {
                    isSType.set(i);
                } else {
                    isSType.set(i, isSType.get(i + 1));
                }
            }
            //System.err.println(isSType);

            var rightPartition = new int[kindCount.length];
            for (int i = 0; i < kindCount.length; i++) {
                if (i == 0) {
                    rightPartition[i] = kindCount[i];
                } else {
                    rightPartition[i] = rightPartition[i - 1] + kindCount[i];
                }
            }

            var lmsMap = new int[array.length];
            Arrays.fill(lmsMap, -1);
            var lms = new ArrayList<Integer>();
            for (int i = 1; i < array.length; i++) {
                if (!isSType.get(i - 1) && isSType.get(i)) {
                    lmsMap[i] = lms.size();
                    lms.add(i);
                }
            }
            var sa = new int[array.length];
            Arrays.fill(sa, -1);

            interface InducedSort {
                int[] exec(int[] sa, int[] lms);
            }

            InducedSort inducedSort = (_sa, _lms) -> {
                Arrays.fill(_sa, -1);
                //put lms
                {
                    var pr = Arrays.copyOf(rightPartition, rightPartition.length);
                    for (int i = _lms.length - 1; i >= 0; i--) {
                        var lm = _lms[i];
                        var ch = array[lm];
                        pr[ch]--;
                        _sa[pr[ch]] = lm;
                    }
                }

                //put l
                {
                    var pl = new int[rightPartition.length];
                    for (int i = 0; i < pl.length - 1; i++) {
                        pl[i + 1] += rightPartition[i];
                    }

                    {
                        var ch = array[array.length - 1];
                        _sa[pl[ch]] = array.length - 1;
                        pl[ch]++;
                    }
                    for (int i = 0; i < array.length; i++) {
                        if (_sa[i] > 0 && !isSType.get(_sa[i] - 1)) {
                            var ch = array[_sa[i] - 1];
                            _sa[pl[ch]] = _sa[i] - 1;
                            pl[ch]++;
                        }
                    }
                }

                //put s
                {
                    var pr = Arrays.copyOf(rightPartition, rightPartition.length);

                    for (int i = array.length - 1; i >= 0; i--) {
                        if (_sa[i] > 0 && isSType.get(_sa[i] - 1)) {
                            var ch = array[_sa[i] - 1];
                            pr[ch]--;
                            _sa[pr[ch]] = _sa[i] - 1;
                        }
                    }
                }

                return _sa;
            };

            //update sa
            sa = inducedSort.exec(sa, lms.stream().mapToInt(i -> i).toArray());
            if (lms.isEmpty()) {
                return sa;
            }

            var sortedLsm = Arrays.stream(sa).filter(idx -> lmsMap[idx] != -1).toArray();
            {
                //System.err.println("sortedLsm:" + Arrays.toString(sortedLsm));
                //System.err.println("lms" + lms);
                var newMaxLetter = 0;
                var newArray = new int[sortedLsm.length];
                for (int i = 1; i < sortedLsm.length; i++) {
                    var l = sortedLsm[i - 1];
                    var r = sortedLsm[i];

                    var lLen = ((lmsMap[l] + 1 < lms.size()) ? lms.get(lmsMap[l] + 1) : array.length) - l;
                    var rLen = ((lmsMap[r] + 1 < lms.size()) ? lms.get(lmsMap[r] + 1) : array.length) - r;
                    if (lLen != rLen ||
                            IntStream.range(0, lLen).anyMatch(
                                    idx -> array[l + idx] != array[r + idx] || isSType.get(l + idx) != isSType.get(r + idx)
                            )
                    ) {
                        newMaxLetter++;
                    }
                    newArray[lmsMap[r]] = newMaxLetter;
                }


                if (newMaxLetter + 1 < sortedLsm.length) {
                    ///System.err.println("sortedLsm before");
                    //System.err.println(Arrays.toString(newArray));
                    //System.err.println(newMaxLetter);
                    var newSa = sa_is(newArray, newMaxLetter);
                    //System.err.println("sortedLsm before");
                    //System.err.println(Arrays.toString(sortedLsm));
                    sortedLsm = Arrays.stream(newSa).map(lms::get).toArray();
                    //System.err.println("sortedLsm after");
                    //System.err.println(Arrays.toString(sortedLsm));
                }
            }
            //System.err.println("======");
            //System.err.println(Arrays.toString(sa));
            //System.err.println(Arrays.toString(sortedLsm));
            return inducedSort.exec(sa, sortedLsm);
        }


    }

    public static class NaiveSuffixArray {
        public static int[] sa(String in) {
            var str = in + "$";
            var arr = new Integer[str.length()];
            Arrays.setAll(arr, i -> i);
            Arrays.sort(arr, Comparator.comparing(str::substring));
            var ret = new int[arr.length];
            Arrays.setAll(ret, i -> arr[i]);
            return ret;
        }
    }
}
