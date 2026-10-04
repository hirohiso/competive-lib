package jp.hirohiso.competive.util.string;

public class TrieMain {
    public static void main(String[] args) {
        var trie = new Trie<String>();
        trie.add("hello", "world");
        trie.add("helloo", "world2");
        trie.add("hellooo", "world3");

        System.out.println(trie.get("hello")); // world3
        System.out.println(trie.get("helloo")); // world3
        System.out.println(trie.get("hellooo")); // world3
    }

    //valを乗せたTrie
    /**
     * 小文字英字 a～z をキーとする Trie 辞書。
     * <p>add は完全一致キーの値を設定し、同じキーは上書きする。空文字列も可。
     * get は完全一致の値、未登録なら null。接頭辞だけ一致しても値は返さない。
     * 長さ L の操作は O(L)、領域は登録文字数の合計に比例する。</p>
     */
    public static class Trie<T> {
        static class TrieNode<T> {
            TrieNode[] children = new TrieNode[26];
            boolean isEnd;
            T value;
        }

        TrieNode<T> root = new TrieNode<>();

        /**
         * Trieにvalを追加
         *
         * @param word
         * @param value
         */
        public void add(String word, T value) {
            TrieNode<T> node = root;
            for (char c : word.toCharArray()) {
                int index = c - 'a';
                if (node.children[index] == null) {
                    node.children[index] = new TrieNode<>();
                }
                node = node.children[index];
            }
            node.isEnd = true;
            node.value = value;
        }

        /**
         * Trieを探索してvalを取得
         *
         * @param word
         * @return
         */
        public T get(String word) {
            TrieNode<T> node = root;
            for (char c : word.toCharArray()) {
                int index = c - 'a';
                if (node.children[index] == null) {
                    return null;
                }
                node = node.children[index];
            }
            return node.isEnd ? node.value : null;
        }
    }


    //バイナリTrie
    //大きめなN * Lenの追加/削除に耐えれるようにオブジェクトプールを利用
    public static class BinaryTrie {
        private final int[] childO;
        private final int[] childX;
        private final int[] count;

        // 0はnull、1がroot
        private int used = 2;
        private int freeHead = 0;

        BinaryTrie(int capacity) {
            childO = new int[capacity];
            childX = new int[capacity];
            count = new int[capacity];
        }

        public void add(char[] word, int value) {
            int node = 1;
            count[node] += value;
            for (char c : word) {
                int next;

                if (c == 'o') {
                    next = childO[node];
                    if (next == 0) {
                        next = allocate();
                        childO[node] = next;
                    }
                } else {
                    next = childX[node];
                    if (next == 0) {
                        next = allocate();
                        childX[node] = next;
                    }
                }
                node = next;
                count[node] += value;
            }
        }

        public void remove(char[] word, int value) {
            int node = 1;
            count[node] -= value;

            for (int depth = 0; depth < word.length; depth++) {
                char c = word[depth];
                int child = c == 'o'
                        ? childO[node]
                        : childX[node];

                count[child] -= value;
                if (count[child] == 0) {
                    // 親から切り離す
                    if (c == 'o') {
                        childO[node] = 0;
                    } else {
                        childX[node] = 0;
                    }

                    // この先は、このwordだけが通っていた一本道
                    int current = child;

                    for (int p = depth + 1; p < word.length; p++) {
                        int next = word[p] == 'o'
                                ? childO[current]
                                : childX[current];

                        release(current);
                        current = next;
                    }

                    release(current);
                    return;
                }

                node = child;
            }
        }

        private int allocate() {
            if (freeHead != 0) {
                int node = freeHead;
                freeHead = childO[node];

                childO[node] = 0;
                childX[node] = 0;
                count[node] = 0;

                return node;
            }

            return used++;
        }

        private void release(int node) {
            // 使用していないchildOをfree listのnextとして利用
            childO[node] = freeHead;
            childX[node] = 0;
            count[node] = 0;
            freeHead = node;
        }
    }
}
