package jp.hirohiso.competive.util.string;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class StringAlgorithmsTest {
    static List<String> samples() {
        var result =
                new ArrayList<>(
                        List.of("a", "aaaaaaa", "abababab", "banana", "abccba", "racecar", "zyx"));
        var rng = new Random(441);
        for (int t = 0; t < 100; t++) {
            var s = new StringBuilder();
            for (int i = 0, n = 1 + rng.nextInt(30); i < n; i++)
                s.append((char) ('a' + rng.nextInt(4)));
            result.add(s.toString());
        }
        return result;
    }

    @Test
    void Z配列を直接比較() {
        for (String s : samples()) {
            int[] z = new int[s.length()];
            for (int i = 0; i < s.length(); i++)
                while (i + z[i] < s.length() && s.charAt(z[i]) == s.charAt(i + z[i])) z[i]++;
            assertArrayEquals(z, ZAlgorithmMain.zAlgorithm(s), s);
        }
    }

    @Test
    void 回文半径を中心から探索() {
        for (String s : samples()) {
            String expanded = "#" + String.join("#", s.split("")) + "#";
            int[] expected = new int[expanded.length()];
            for (int i = 0; i < expected.length; i++) {
                int r = 0;
                while (i - r >= 0
                        && i + r < expected.length
                        && expanded.charAt(i - r) == expanded.charAt(i + r)) r++;
                expected[i] = r;
            }
            assertArrayEquals(expected, ManacherMain.manacher(s, "#"), s);
        }
    }

    @Test
    void suffix配列を部分文字列ソートと比較() {
        for (String s : samples()) {
            Integer[] order = new Integer[s.length()];
            Arrays.setAll(order, i -> i);
            Arrays.sort(order, Comparator.comparing(s::substring));
            int[] expected = Arrays.stream(order).mapToInt(Integer::intValue).toArray();
            assertArrayEquals(expected, SuffixArrayMain.SuffixArray.sa(s), s);
        }
    }

    @Test
    void Trieの完全一致と上書きをMapと比較() {
        var trie = new TrieMain.Trie<Integer>();
        var expected = new HashMap<String, Integer>();
        int value = 0;
        for (String s : samples()) {
            expected.put(s, value);
            trie.add(s, value++);
            for (String q : samples()) assertEquals(expected.get(q), trie.get(q), q);
        }
        trie.add("", 123);
        assertEquals(123, trie.get(""));
        assertNull(trie.get("unknown"));
    }

    @Test
    void LCPを隣接suffixの直接比較で確認() {
        var inputs = new ArrayList<>(samples());
        inputs.add("");
        for (String s : inputs) {
            int[] sa = SuffixArrayMain.SuffixArray.sa(s), expected = new int[s.length()];
            for (int i = 1; i < sa.length; i++)
                while (sa[i] + expected[i] < s.length()
                        && sa[i - 1] + expected[i] < s.length()
                        && s.charAt(sa[i] + expected[i]) == s.charAt(sa[i - 1] + expected[i]))
                    expected[i]++;
            assertArrayEquals(expected, SuffixArrayMain.SuffixArray.lca(s, sa), s);
        }
    }
}
