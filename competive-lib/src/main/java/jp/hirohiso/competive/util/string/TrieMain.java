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
    public static class Trie<T>{
        static class TrieNode<T>{
            TrieNode[] children = new TrieNode[26];
            boolean isEnd;
            T value;
        }
        TrieNode<T> root = new TrieNode<>();

        /**
         * Trieにvalを追加
         * @param word
         * @param value
         */
        public void add(String word , T value){
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
         * @param word
         * @return
         */
        public T get(String word){
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
}
