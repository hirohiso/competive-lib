package jp.hirohiso.competive.util.string;

import java.util.*;
import java.util.function.IntConsumer;

public class AhoCorasickMain {
    public static void main(String[] args) {
        var aho = new AhoCorasick<Integer>();

        aho.add("hoge", 1);
        aho.add("hogehoge", 2);
        aho.add("gehoge", 3);
        aho.add("hogehger", 4);
        aho.add("ogeoge", 5);

        for (var n : aho.nodes) {
            System.out.println(n.id);
            System.out.println(n.accept);
        }
        aho.build();

        var list = aho.query("hogehogeogeoge", System.out::println);
        System.out.println(list);

    }

    public static class AhoCorasick<T> {
        static final int CHAR_SIZE = 26; // 'a'から'z'までの文字数
        static final int MARGIN = 'a';

        public AhoCorasick() {
            this.nodes.add(root);
        }

        public static class Node {
            int id; // ノードID
            Node[] children = new Node[CHAR_SIZE]; // 子ノード
            Node fail; // 失敗遷移
            List<Integer> accept = new ArrayList<>(); // マッチした値リスト
            int existCount = 0; // このノードを通るパターンの数

            public Node(int id) {
                this.id = id;
            }
        }

        private final Node root = new Node(0);
        private final List<Node> nodes = new ArrayList<>();
        private final List<T> values = new ArrayList<>();

        public void updateDirect(Node node, int id) {
            node.accept.add(id);
        }

        /**
         * パターンを追加
         */
        public void add(String word, T value, int stringIdx, Node node, int id) {
            if (word.length() == stringIdx) {
                updateDirect(node, id);
                values.add(node.id, value);
            } else {
                var c = word.charAt(stringIdx) - MARGIN;
                if (node.children[c] == null) {
                    var newNode = new Node(nodes.size());
                    node.children[c] = newNode;
                    nodes.add(newNode);
                    values.add(null);
                }
                var nextNode = node.children[c];
                node.existCount++;
                add(word, value, stringIdx + 1, nextNode, id);
            }
        }

        public void add(String word, T value, int id) {
            add(word, value, 0, nodes.get(0), id);
        }

        public void add(String word, T value) {
            add(word, value, nodes.get(0).existCount);
        }

        public void add(String word) {
            add(word, null);
        }

        /**
         * failure link 構築
         */
        public void build() {
            Queue<Node> q = new ArrayDeque<>();

            // root直下のfailはrootに張る
            for (int c = 0; c < 26; c++) {
                if (root.children[c] != null) {
                    root.children[c].fail = root;
                    q.add(root.children[c]);
                } else {
                    root.children[c] = root; // 遷移が無ければrootに戻す
                }
            }

            // BFSでfailure linkを構築
            while (!q.isEmpty()) {
                Node cur = q.poll();
                for (int c = 0; c < 26; c++) {
                    Node nxt = cur.children[c];
                    if (nxt != null) {
                        nxt.fail = cur.fail.children[c];
                        q.add(nxt);
                    } else {
                        cur.children[c] = cur.fail.children[c];
                    }
                }
            }
        }

        /**
         * テキストを走査してマッチ箇所を返す
         * <p>
         * text内で一致するテキストを見つけた場合、callbackを呼び出し対応する頂点を渡します
         */
        public List<Match<T>> query(String text, IntConsumer callback) {
            return query(text, callback, 0, nodes.get(0));
        }

        private void query(String text, IntConsumer callback, int strIndex, Node node, List<Match<T>> matchList) {
            for (var id : node.accept) {
                callback.accept(id);
                matchList.add(new Match<>(strIndex, values.get(node.id)));
            }
            if (strIndex == text.length()) {
                return;
            } else {
                var c = text.charAt(strIndex) - MARGIN;
                if (node.children[c] == null) {
                    return;
                }
                query(text, callback, strIndex + 1, node.children[c], matchList);
            }
        }

        public List<Match<T>> query(String text, IntConsumer callback, int strIndex, Node node) {
            var list = new ArrayList<Match<T>>();
            query(text, callback, strIndex, node, list);
            return list;
        }

        /**
         * マッチ結果
         */
        public static class Match<T> {
            public int pos; // マッチした位置（終端のインデックス）
            public T value; // 登録した値

            public Match(int pos, T value) {
                this.pos = pos;
                this.value = value;
            }

            @Override
            public String toString() {
                return "Match{" + "pos=" + pos + ", value=" + value + '}';
            }
        }
    }
}
