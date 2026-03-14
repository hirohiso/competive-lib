# 競技プログラミング用ライブラリ

競技プログラミング（AtCoder等）で使用するアルゴリズムとデータ構造のJavaライブラリ集です。

**パッケージ**: `jp.hirohiso.competive.util`

**総ファイル数**: 72個

## ディレクトリ構造

```
src/main/java/jp/hirohiso/competive/util/
├── tree/       - ツリー/セグメント木系データ構造（12ファイル）
├── graph/      - グラフアルゴリズム（16ファイル）
├── math/       - 数学的計算と変換（24ファイル）
├── string/     - 文字列処理アルゴリズム（8ファイル）
├── sequence/   - 数列と部分列処理（4ファイル）
├── range/      - 範囲管理と集約（4ファイル）
└── examples/   - 使用例とテクニック（4ファイル）
```

---

## ライブラリ一覧

### 1. tree - データ構造とツリー（12ファイル）

| クラス名 | ファイル | 機能 |
|---------|---------|------|
| UnionFind / DisjointSetUnion | [UnionFind.java](src/main/java/jp/hirohiso/competive/util/tree/UnionFind.java) | Union-Find（素集合）データ構造 |
| SegmentTree | [SegmentationTree.java](src/main/java/jp/hirohiso/competive/util/tree/SegmentationTree.java) | セグメント木。区間クエリと要素更新 |
| LazySegmentationTree | [LazySegmentationTree.java](src/main/java/jp/hirohiso/competive/util/tree/LazySegmentationTree.java) | 遅延伝播セグメント木 |
| LazySegmentTreeBottom | [LazySegmentTreeBottom.java](src/main/java/jp/hirohiso/competive/util/tree/LazySegmentTreeBottom.java) | セグメント木（ボトムアップ方式） |
| FenwickTree | [FenwickTreeMain.java](src/main/java/jp/hirohiso/competive/util/tree/FenwickTreeMain.java) | フェンウィック木（BIT）。累積和の高速計算 |
| SparseTable | [SparseTableMain.java](src/main/java/jp/hirohiso/competive/util/tree/SparseTableMain.java) | スパーステーブル。静的RMQをO(1) |
| DoblingTree | [DoblingTreeMain.java](src/main/java/jp/hirohiso/competive/util/tree/DoblingTreeMain.java) | 倍増法。LCA（最小公通祖先）計算 |
| CartesianTree | [CartesianTreeMain.java](src/main/java/jp/hirohiso/competive/util/tree/CartesianTreeMain.java) | カルテシアン木 |
| TreeDiameter | [TreeDiameterMain.java](src/main/java/jp/hirohiso/competive/util/tree/TreeDiameterMain.java) | 木の直径計算 |
| ReRootingTree | [ReRootingTreeMain.java](src/main/java/jp/hirohiso/competive/util/tree/ReRootingTreeMain.java) | 全方位木DP（Re-rooting） |
| SternBrocotTree | [SternBrocotTreeMain.java](src/main/java/jp/hirohiso/competive/util/tree/SternBrocotTreeMain.java) | スターンブロコット木 |
| DualSegmentTree | [DualSegmentTreeMain.java](src/main/java/jp/hirohiso/competive/util/tree/DualSegmentTreeMain.java) | 双対セグメント木 |

### 2. graph - グラフアルゴリズム（16ファイル）

| クラス名 | ファイル | 機能 |
|---------|---------|------|
| Dijkstra | [Dijkstra.java](src/main/java/jp/hirohiso/competive/util/graph/Dijkstra.java) | ダイクストラ法。最短経路計算 |
| DijkstraLight | [DijkstraLight.java](src/main/java/jp/hirohiso/competive/util/graph/DijkstraLight.java) | ダイクストラ法（軽量版） |
| Scc | [Scc.java](src/main/java/jp/hirohiso/competive/util/graph/Scc.java) | 強連結成分分解（SCC） |
| Warshall | [Warshall.java](src/main/java/jp/hirohiso/competive/util/graph/Warshall.java) | ワーシャル・フロイド法。全点対最短経路 |
| Dinic | [Dinic.java](src/main/java/jp/hirohiso/competive/util/graph/Dinic.java) | 最大フロー（ディニック法） |
| KruskalMethod | [KruskalMethod.java](src/main/java/jp/hirohiso/competive/util/graph/KruskalMethod.java) | クラスカル法。最小全域木 |
| PrimMethod | [PrimMethod.java](src/main/java/jp/hirohiso/competive/util/graph/PrimMethod.java) | プリム法。最小全域木 |
| ArraysGraph | [ArraysGraph.java](src/main/java/jp/hirohiso/competive/util/graph/ArraysGraph.java) | グラフの隣接配列表現 |
| MetaGraph | [MetaGraph.java](src/main/java/jp/hirohiso/competive/util/graph/MetaGraph.java) | グラフメタ情報管理 |
| FunctionalGraph | [FunctionalGraphMain.java](src/main/java/jp/hirohiso/competive/util/graph/FunctionalGraphMain.java) | 関数型グラフ |
| BinaryGraph | [BinaryGraphMain.java](src/main/java/jp/hirohiso/competive/util/graph/BinaryGraphMain.java) | 二部グラフ |
| NamoriGraph | [NamoriGraphMain.java](src/main/java/jp/hirohiso/competive/util/graph/NamoriGraphMain.java) | 花火グラフ（Namori Graph） |
| FlattenArrayIndexer | [FlattenArrayIndexerMAin.java](src/main/java/jp/hirohiso/competive/util/graph/FlattenArrayIndexerMAin.java) | 平坦化配列インデックス管理 |
| ManhattanMst | [ManhattanMstMain.java](src/main/java/jp/hirohiso/competive/util/graph/ManhattanMstMain.java) | マンハッタン距離MST |
| MinMaxFlow | [MinMaxFlowMain.java](src/main/java/jp/hirohiso/competive/util/graph/MinMaxFlowMain.java) | 最小最大フロー |
| xxxFastSearch | [xxxFastSearchMain.java](src/main/java/jp/hirohiso/competive/util/graph/xxxFastSearchMain.java) | 高速探索アルゴリズム |

### 3. math - 数学関連（24ファイル）

| クラス名 | ファイル | 機能 |
|---------|---------|------|
| Prime | [Prime.java](src/main/java/jp/hirohiso/competive/util/math/Prime.java) | 素数判定・エラトステネスの篩・素因数分解 |
| Combination | [Combination.java](src/main/java/jp/hirohiso/competive/util/math/Combination.java) | 二項係数計算（mod対応） |
| Gcd | [Gcd.java](src/main/java/jp/hirohiso/competive/util/math/Gcd.java) | 最大公約数・最小公倍数 |
| Gamma | [Gamma.java](src/main/java/jp/hirohiso/competive/util/math/Gamma.java) | ガンマ関数 |
| Erf | [Erf.java](src/main/java/jp/hirohiso/competive/util/math/Erf.java) | 誤差関数 |
| FastFourierTransform | [FftLib.java](src/main/java/jp/hirohiso/competive/util/math/FftLib.java) | 高速フーリエ変換（FFT） |
| NttLib | [NttLib.java](src/main/java/jp/hirohiso/competive/util/math/NttLib.java) | 数論変換（NTT） |
| NttMain | [NttMain.java](src/main/java/jp/hirohiso/competive/util/math/NttMain.java) | NTT使用例 |
| MatrixLib | [MatrixLib.java](src/main/java/jp/hirohiso/competive/util/math/MatrixLib.java) | 行列演算（積・累乗） |
| ModLong | [ModLong.java](src/main/java/jp/hirohiso/competive/util/math/ModLong.java) | モジュロ演算対応長整数 |
| MyPermutation | [MyPermutation.java](src/main/java/jp/hirohiso/competive/util/math/MyPermutation.java) | 順列生成・処理 |
| Utils | [Utils.java](src/main/java/jp/hirohiso/competive/util/math/Utils.java) | 数学ユーティリティ |
| NumberSequence | [NumberSequence.java](src/main/java/jp/hirohiso/competive/util/math/NumberSequence.java) | 数列処理 |
| EulerPhi | [EulerPhiMain.java](src/main/java/jp/hirohiso/competive/util/math/EulerPhiMain.java) | オイラーのトーシェント関数 |
| ExtraCombination | [ExtraCombinationMain.java](src/main/java/jp/hirohiso/competive/util/math/ExtraCombinationMain.java) | 拡張二項係数 |
| Partitions | [PartitionsMain.java](src/main/java/jp/hirohiso/competive/util/math/PartitionsMain.java) | 分割（Partition） |
| NextPair | [NextPairMain.java](src/main/java/jp/hirohiso/competive/util/math/NextPairMain.java) | ペア列挙 |
| Nksuffle | [NksuffleMain.java](src/main/java/jp/hirohiso/competive/util/math/NksuffleMain.java) | シャッフル・パーミュテーション |
| ArgumentSort | [ArgumentSort.java](src/main/java/jp/hirohiso/competive/util/math/ArgumentSort.java) | 引数ソート（argsort） |
| StaticRangeSumSolve | [StaticRangeSumSolve.java](src/main/java/jp/hirohiso/competive/util/math/StaticRangeSumSolve.java) | 静的範囲和計算 |
| StaticMatrixRangeSumSolve | [StaticMatrixRangeSumSolve.java](src/main/java/jp/hirohiso/competive/util/math/StaticMatrixRangeSumSolve.java) | 2D範囲和計算 |
| SqrtNewton | [SqrtNewtonMain.java](src/main/java/jp/hirohiso/competive/util/math/SqrtNewtonMain.java) | ニュートン法による平方根 |
| Polygon2D | [Polygon2DMain.java](src/main/java/jp/hirohiso/competive/util/math/Polygon2DMain.java) | 2D多角形計算 |
| TempMain | [TempMain.java](src/main/java/jp/hirohiso/competive/util/math/TempMain.java) | 試験的実装 |

### 4. string - 文字列処理（8ファイル）

| クラス名 | ファイル | 機能 |
|---------|---------|------|
| RollingHash | [RollingHashMain.java](src/main/java/jp/hirohiso/competive/util/string/RollingHashMain.java) | ローリングハッシュ。高速文字列マッチング |
| SuffixArray | [SuffixArrayMain.java](src/main/java/jp/hirohiso/competive/util/string/SuffixArrayMain.java) | サフィックス配列 |
| Trie | [TrieMain.java](src/main/java/jp/hirohiso/competive/util/string/TrieMain.java) | Trie木。プレフィックス木 |
| ZAlgorithm | [ZAlgorithmMain.java](src/main/java/jp/hirohiso/competive/util/string/ZAlgorithmMain.java) | Zアルゴリズム。パターンマッチング |
| Manacher | [ManacherMain.java](src/main/java/jp/hirohiso/competive/util/string/ManacherMain.java) | マナッハーのアルゴリズム。最長回文 |
| AhoCorasick | [AhoCorasickMain.java](src/main/java/jp/hirohiso/competive/util/string/AhoCorasickMain.java) | Aho-Corasick。複数パターンマッチング |
| RunLength | [RunLengthMain.java](src/main/java/jp/hirohiso/competive/util/string/RunLengthMain.java) | ラン長圧縮 |
| CalcCharNext | [CalcCharNextMain.java](src/main/java/jp/hirohiso/competive/util/string/CalcCharNextMain.java) | 次の文字位置計算 |

### 5. sequence - 数列とリスト処理（4ファイル）

| クラス名 | ファイル | 機能 |
|---------|---------|------|
| LongestIncreasingSubsequence | [Sequence.java](src/main/java/jp/hirohiso/competive/util/sequence/Sequence.java) | 最長増加部分列（LIS） |
| ArraysMerge | [ArraysMergeMain.java](src/main/java/jp/hirohiso/competive/util/sequence/ArraysMergeMain.java) | 複数配列のマージ |
| SimpleList | [SimpleListMain.java](src/main/java/jp/hirohiso/competive/util/sequence/SimpleListMain.java) | シンプルなリスト実装 |
| Zaatsu | [ZaatsuMain.java](src/main/java/jp/hirohiso/competive/util/sequence/ZaatsuMain.java) | 座標圧縮（座値） |

### 6. range - 範囲処理（4ファイル）

| クラス名 | ファイル | 機能 |
|---------|---------|------|
| RangeSet | [RangeSetMain.java](src/main/java/jp/hirohiso/competive/util/range/RangeSetMain.java) | 範囲集合。重複しない範囲の管理 |
| RangeMap | [RangeMapMain.java](src/main/java/jp/hirohiso/competive/util/range/RangeMapMain.java) | 範囲マップ |
| SlidingWindowAggregation | [SlidingWindowAggregationMain.java](src/main/java/jp/hirohiso/competive/util/range/SlidingWindowAggregationMain.java) | スライディングウィンドウ集約 |
| PriorityRangeSum | [PriorityRangeSumMain.java](src/main/java/jp/hirohiso/competive/util/range/PriorityRangeSumMain.java) | 優先度付き範囲和 |

### 7. examples - 使用例とテクニック（4ファイル）

| クラス名 | ファイル | 機能 |
|---------|---------|------|
| BitSearch | [BitSearch.java](src/main/java/jp/hirohiso/competive/util/examples/BitSearch.java) | ビット操作を用いた探索 |
| Pq | [Pq.java](src/main/java/jp/hirohiso/competive/util/examples/Pq.java) | 優先度キュー使用例 |
| SwitchLRUD | [SwitchLRUD.java](src/main/java/jp/hirohiso/competive/util/examples/SwitchLRUD.java) | グリッド問題用の上下左右移動 |
| banhei | [banhei.java](src/main/java/jp/hirohiso/competive/util/examples/banhei.java) | 番兵テクニック |

---

## 特徴

- **ジェネリクス対応**: 多くのクラスがジェネリクスで実装され、様々な型に対応
- **関数型インターフェース活用**: `BinaryOperator`, `Supplier`, `Function`などを活用
- **main メソッド付き**: 各ファイルに使用例を含む
- **競技プログラミング最適化**: AtCoder等の競技プログラミングに特化した実装

## 使い方

各ライブラリは独立して使用可能です。必要なファイルをコピーして使用してください。

```java
// 例: Union-Findを使用
import jp.hirohiso.competive.util.tree.UnionFind;

UnionFind uf = new UnionFind(n);
uf.union(a, b);
if (uf.isSame(a, b)) {
    // 同じ集合
}
```

---

*最終更新: 2025-12-30*
