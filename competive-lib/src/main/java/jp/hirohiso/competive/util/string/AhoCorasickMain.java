package jp.hirohiso.competive.util.string;

import java.util.*;
import java.util.function.IntConsumer;

/**
 * Aho-Corasickアルゴリズムの実装と使用例
 * <p>
 * Aho-Corasickアルゴリズムは、複数のパターン文字列を同時に検索できる文字列検索アルゴリズムです。
 * Trieとfailure link（失敗遷移）を使用して、テキスト内の全てのパターンマッチを効率的に検出します。
 * </p>
 * <p>
 * 時間計算量：
 * <ul>
 *   <li>構築: O(sum of pattern lengths)</li>
 *   <li>検索: O(text length + number of matches)</li>
 * </ul>
 * </p>
 *
 * @see AhoCorasick
 */
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

    /**
     * Aho-Corasickアルゴリズムの実装クラス
     * <p>
     * 複数のパターン文字列を登録し、テキスト内で全てのパターンを効率的に検索します。
     * 小文字アルファベット('a'～'z')のみに対応しています。
     * </p>
     *
     * @param <T> パターンに関連付ける値の型
     */
    public static class AhoCorasick<T> {
        /** アルファベットのサイズ（'a'から'z'までの26文字） */
        static final int CHAR_SIZE = 26;
        /** 文字コードのオフセット（'a'の文字コード） */
        static final int MARGIN = 'a';

        /**
         * Aho-Corasickオートマトンを構築します。
         */
        public AhoCorasick() {
            this.nodes.add(root);
        }

        /**
         * Trieの各ノードを表すクラス
         */
        public static class Node {
            /** ノードID（ノードの一意識別子） */
            int id;
            /** 子ノードの配列（各文字に対応） */
            Node[] children = new Node[CHAR_SIZE];
            /** 失敗遷移（failure link）の遷移先 */
            Node fail;
            /** このノードで受理されるパターンIDのリスト */
            List<Integer> accept = new ArrayList<>();
            /** このノードを通るパターンの数 */
            int existCount = 0;

            /**
             * 新しいノードを構築します。
             *
             * @param id ノードID
             */
            public Node(int id) {
                this.id = id;
            }
        }

        /** Trieの根ノード */
        private final Node root = new Node(0);
        /** 全てのノードのリスト */
        private final List<Node> nodes = new ArrayList<>();
        /** パターンに関連付けられた値のリスト */
        private final List<T> values = new ArrayList<>();

        /**
         * ノードに受理するパターンIDを直接追加します。
         *
         * @param node 対象ノード
         * @param id パターンID
         */
        public void updateDirect(Node node, int id) {
            node.accept.add(id);
        }

        /**
         * パターンを再帰的に追加します（内部メソッド）。
         *
         * @param word 追加するパターン文字列
         * @param value パターンに関連付ける値
         * @param stringIdx 現在処理中の文字インデックス
         * @param node 現在のノード
         * @param id パターンID
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

        /**
         * パターンを指定されたIDで追加します。
         *
         * @param word 追加するパターン文字列
         * @param value パターンに関連付ける値
         * @param id パターンID
         */
        public void add(String word, T value, int id) {
            add(word, value, 0, nodes.get(0), id);
        }

        /**
         * パターンを追加します（IDは自動採番）。
         *
         * @param word 追加するパターン文字列
         * @param value パターンに関連付ける値
         */
        public void add(String word, T value) {
            add(word, value, nodes.get(0).existCount);
        }

        /**
         * パターンを値なしで追加します。
         *
         * @param word 追加するパターン文字列
         */
        public void add(String word) {
            add(word, null);
        }

        /**
         * failure link（失敗遷移）を構築します。
         * <p>
         * 全てのパターンを追加した後、queryメソッドを呼ぶ前に必ず実行する必要があります。
         * BFSを使用してTrieのノードを走査し、各ノードのfailure linkを設定します。
         * </p>
         * <p>
         * 時間計算量: O(全パターンの長さの合計 × アルファベットサイズ)
         * </p>
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
         * テキストを走査してマッチ箇所を返します。
         * <p>
         * テキスト内で一致するパターンを見つけた場合、callbackを呼び出してパターンIDを渡します。
         * build()メソッドを事前に呼び出しておく必要があります。
         * </p>
         * <p>
         * 時間計算量: O(テキストの長さ + マッチ数)
         * </p>
         *
         * @param text 検索対象のテキスト
         * @param callback マッチした際に呼ばれるコールバック（パターンIDを受け取る）
         * @return マッチ結果のリスト
         */
        public List<Match<T>> query(String text, IntConsumer callback) {
            return query(text, callback, 0, nodes.get(0));
        }

        /**
         * テキストを走査してマッチ箇所を再帰的に検索します（内部メソッド）。
         *
         * @param text 検索対象のテキスト
         * @param callback マッチした際に呼ばれるコールバック
         * @param strIndex 現在処理中のテキストのインデックス
         * @param node 現在のノード
         * @param matchList マッチ結果を格納するリスト
         */
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

        /**
         * テキストを走査してマッチ箇所を返します（開始位置とノード指定版）。
         *
         * @param text 検索対象のテキスト
         * @param callback マッチした際に呼ばれるコールバック
         * @param strIndex 開始インデックス
         * @param node 開始ノード
         * @return マッチ結果のリスト
         */
        public List<Match<T>> query(String text, IntConsumer callback, int strIndex, Node node) {
            var list = new ArrayList<Match<T>>();
            query(text, callback, strIndex, node, list);
            return list;
        }

        /**
         * パターンマッチの結果を表すクラス
         *
         * @param <T> パターンに関連付けられた値の型
         */
        public static class Match<T> {
            /** マッチした位置（終端のインデックス） */
            public int pos;
            /** パターンに関連付けられた値 */
            public T value;

            /**
             * マッチ結果を構築します。
             *
             * @param pos マッチした位置
             * @param value 関連付けられた値
             */
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
