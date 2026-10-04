# テストと不具合検出

## 実行環境

Java 21 のJDKをインストールしてリポジトリのルートで実行する。
GradleはWrapperが取得するのでグローバルインストールは不要。
初回のGradle本体・依存取得にはネットワーク接続が必要。

```sh
# macOSで複数のJDKがある場合
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
./gradlew check
./gradlew pitest
./gradlew stressTest

# 1クラスだけのテスト
./gradlew test --tests '*FenwickTreeTest'
```

Gradle 9.6.1 / Java Toolchain 21 / JUnit Jupiter 5.12.2 / JaCoCo 0.8.14 /
PIT 1.19.1 / Gradle PIT plugin 1.19.0 / PIT JUnit plugin 1.2.3。
Wrapper配布ZIPのSHA-256と依存バージョンを固定している。
JDKのベンダー・パッチ版は固定せず、利用環境にインストールしたJava 21を使用する。
IDEではルートのGradleプロジェクトを読み込み、テストもGradle経由で実行する。
旧pom.xmlは削除済み。

依存を意図して変更したときだけ、次のコマンドでlockfileも更新する。

```sh
./gradlew check pitest --write-locks
```

## テストの範囲

本体85ファイルのうち、利用頻度と不具合の影響を重視した次の30ファイルを対象にした。
期待値は、同じ実装の複製ではなく小規模な愚直解や数学的性質で求める。
乱択は固定seedを使用し、失敗時の入力・操作位置を確認できるようにする。

| 分野 | 対象ファイル（.java省略） | 主な期待値・ケース |
|---|---|---|
| tree（8） | UnionFind、SegmentationTree、LazySegmentationTree、LazySegmentTreeBottom、DualSegmentTreeMain、FenwickTreeMain、SparseTableMain、DoblingTreeMain | 連結成分の愚直管理、配列更新、全区間走査、親の逐次探索。1要素、2冪前後、非可換な作用、遅延値を残した連続操作 |
| graph（7） | DijkstraLight、Warshall、Scc、Dinic、MinCostFlowMain、KruskalMethod、DoublingMain | 独立したBellman–Ford、相互到達性、カット全列挙、容量全列挙、全域木全列挙、逐次遷移。非連結・平行辺・自己ループ・残余辺 |
| range（3） | RangeSetMain、SlidingWindowAggregationMain、PriorityRangeSumMain | 小さい整数集合、キューの文字列連結、ソート後の先頭K個。隣接・分割・重複・K変更 |
| string（5） | ZAlgorithmMain、ManacherMain、SuffixArrayMain、RollingHashMain、TrieMain | 直接文字比較、中心からの回文探索、suffixソートとLCP、独立した多項式ハッシュ、Map。周期・同一文字・接頭辞・上書き |
| math（5） | Gcd、Prime、Combination、MatrixLib、NttLib | Bézout等式、試し割り、Pascalの三角形、逐次行列乗算、二重ループ畳み込みと直接DFT。整数幅・平方数・指数0 |
| sequence（2） | Sequence、ZaatsuMain | O(N²)のLISと復元列検証、ソートと重複除去。重複・負値・int/longの端点 |

内部クラスの対象は `quality-targets.txt` を正とする。
UnionFindは通常版・ポテンシャル付き・集約値付きが対象で、旧UnionF/UnionFindNodeは対象外。
Trieは文字列辞書版のみ（BinaryTrieは対象外）。Suffix ArrayはSA-ISとLCPが対象で、
別のNaiveSuffixArrayは対象外。PriorityRangeSumはlong版とジェネリクス版、
Fenwick・LISも両実装を含む。数学ヘルパーの付随関数や必要な内部クラスも含める。
その他の旧実装、examples、ヒューリスティック、特殊関数は今回の対象外。

privateだったライブラリの入口は必要なものだけpackage-privateにし、同一パッケージのテストから呼ぶ。
コピー利用の構造を維持し、反射によるテストや本番コードのテスト用分岐は追加しない。

## 制約と過剰なテストを避ける方針

前提条件は各実装のJavadocに記載した。無効入力の例外網羅や並行実行は対象にしない。
特に以下の違いを意識する。

- 区間は原則半開区間。ただしFenwickのsum(i)は[0,i]、sum(-1)=0。
- 再帰型遅延セグメント木のcompositionは(old,new)、反復型・双対木は(new,old)。
- Sparse Tableの演算は冪等、Fenwickの汎用版と差分UnionFindはアーベル群。
- Dijkstraは非負重み。Dinicと最小費用流は始点と終点が異なること。
  最小費用流の初期費用は非負。負閉路検出はWarshallで検証する。
- Dijkstra/SCC/Kruskalは辺追加後に一度solveする想定。
  Dinicは再実行すると残余グラフの追加流量を返す。最小費用流は呼出しごとに元の容量から再構築する。
- SCC/Dinicは再帰DFSのため、非常に深い鎖でのスタック上限は残る。
  `stressTest`は非再帰の最短路10万頂点と、セグメント木・UnionFind20万要素を確認する。
  全実装の最大制約や速度を保証するベンチマークではない。
- Z/Manacher/LISは非空入力。Manacherの戻り値は区切り文字挿入後の半径。
  SA-ISは文字値が'$'以上。Rolling Hashは衝突のない識別を保証しない。
- Combinationは素数modと2以上の前処理サイズを前提にする。
  CRTは互いに素な法、NTTは998244353上で2^23以下の変換長。
  任意精度化はせず、演算の中間値がlongに収まる競プロの制約を扱う。

## レポートと判定

| コマンド | 内容 | レポート |
|---|---|---|
| `./gradlew test` | 通常のJUnitテスト、成功後のJaCoCoレポート | `build/reports/tests/test/index.html` |
| `./gradlew check` | 通常テスト＋Javadoc検証＋行90%以上・分岐85%以上の確認 | `build/reports/jacoco/test/html/index.html` |
| `./gradlew pitest` | 標準変異演算子、mutation score 80%以上 | `build/reports/pitest/index.html` |
| `./gradlew stressTest` | 大入力のスモークテスト（PITから除外） | `build/reports/tests/stressTest/index.html` |

閾値は品質の下限であり、不具合がない証明ではない。
`quality-targets.txt`はJaCoCo/PITで共有し、内部クラスも含める。
PITではmain/debug/toStringを除外する。JaCoCoはクラス単位で対象を絞るため、
ユーティリティの外側クラスにあるmainや表示メソッドも分母に残る場合がある。
分母を減らすためのダミーテストは追加しない。

PITのTIMED_OUTは、ループ条件の変異などで終了しなくなった結果であり、
JUnitのアサーションで検出したKILLEDとは分けて記録する。
SURVIVEDは正当入力での同値変異、計算量だけに影響する変異、入力制約外、
未検証の振る舞いのどれかを調べる。NO_COVERAGEは実行範囲の不足として別に確認する。
等価性の証明が難しいものまで「同値」と断定しない。

## 発見・修正した不具合

テスト失敗を確認したものを修正し、回帰テストを残した。

| 対象 | 不具合 | 再現例・テスト |
|---|---|---|
| Fenwick（long） | 和の一時変数がintになり桁あふれ | 20億を2回加算した和が負になる／FenwickTreeTest |
| Fenwick（汎用） | 配列初期化で内部集計値を構築しない | [2,3,5]の区間和が10にならない／RegressionTest |
| Dual Segment Tree | 根の作用を取得時に適用しない、更新順を伝播しない | 全体代入の後に部分加算／DualSegmentTreeTest・RegressionTest |
| SWAG | 移し替えと前後の集約順が逆 | 文字列キューの集約でndがdnになる／SwagTest |
| Doubling | 構築中の段を参照しnullが演算に入る | 2回以上の遷移を文字列連結で集約／DoublingTest |
| Prime | 篩の上限と平方根の端点を含まない | prime(4)[4]がtrue／PrimeTest |
| Segment Tree | 空区間が指定単位元を返さない、minLeftの探索が不正 | 空区間和と全端点の線形探索比較／SegmentTreeTest |
| 反復型Lazy Segment Tree | 葉の遅延配列への書込み、minLeftの伝播・探索、maxRightの端点 | 全体更新後の一点取得・連続更新・境界探索／LazySegmentTreeTest・PendingUpdatesTest・RegressionTest |
| Warshall | 平行辺と自己ループが既存の短い距離を上書きする | 重み3の後に8を追加／ShortestPathTest |
| Kruskal | 総和の一時変数がint | 非連結2辺で総和40億／KruskalTest |
| PriorityRangeSum（汎用） | 遅延削除で値ではなく参照を比較 | キャッシュ外のLong値と重複削除／PriorityRangeSumTest |
| LIS（int） | 番兵のInteger.MAX_VALUEと入力を区別できない | [MIN_VALUE,0,MAX_VALUE]の長さが2になる／SequenceTest |
| Rolling Hash | 窓から除去した後の負の剰余を正規化しない | ababababの長さ6の窓／RollingHashTest |
| 拡張互除法 | longの係数をintに切り詰める | extgcd(10000000007,3)でBézout等式が崩れる／ArithmeticTest |

LCAの深さ調整は調査時に疑ったが、鎖・星・乱択木の全頂点対比較で失敗せず、実装変更はしていない。

対象30ファイルのJavadocは `./gradlew javadoc` でも生成できる（`build/docs/javadoc/index.html`）。

## 計測結果

2026-10-04に `./gradlew check stressTest pitest` および補強後の `./gradlew check pitest` を実行。
通常58テスト、大入力2テストが成功。対象30ファイルのJavadocもエラーなく生成した。
作業前のコミット `445fa63` を一時ディレクトリへ復元し、テストからのアクセス範囲だけを変更して
補強途中の53テストを実行したところ20テストが失敗した。修正後はすべて成功している。

| 指標 | 結果 |
|---|---:|
| JaCoCo 行 | 94.4% (1929/2043) |
| JaCoCo 分岐 | 92.4% (848/918) |
| PIT 全変異 | 1650 |
| アサーション等による KILLED | 1388 (84.1%) |
| TIMED_OUT | 70 |
| MEMORY_ERROR | 1 |
| SURVIVED | 177 |
| NO_COVERAGE | 14 |
| PITが検出扱いにする割合（時間切れ・メモリ超過も含む） | 88.4% |

PIT実行にはこの環境で約3分。時間切れなどの内訳は環境によって変わり得る。
メモリ超過が記録された変異はUnionFindの初期化ループのインクリメントを減算へ変えたもので、
要素追加が終わらなくなる。元のコードの通常テストでは発生していない。

### ライブラリ別の範囲

行・分岐はJaCoCoの対象クラスの合計。PITの列は時間切れ等を除く直接KILLED数/全変異数。
表示用コードのため行カバレッジが低いファイルは、上記の分母の扱いも参照する。

| ファイル | 行 | 分岐 | PIT 直接KILLED/全変異 |
|---|---:|---:|---:|
| `graph/DijkstraLight.java` | 100.0% (77/77) | 100.0% (20/20) | 26/38 |
| `graph/Dinic.java` | 100.0% (65/65) | 100.0% (32/32) | 25/46 |
| `graph/DoublingMain.java` | 100.0% (28/28) | 100.0% (14/14) | 19/21 |
| `graph/KruskalMethod.java` | 94.8% (55/58) | 85.0% (17/20) | 12/18 |
| `graph/MinCostFlowMain.java` | 91.7% (177/193) | 92.9% (52/56) | 75/110 |
| `graph/Scc.java` | 100.0% (99/99) | 97.2% (35/36) | 58/60 |
| `graph/Warshall.java` | 93.8% (30/32) | 81.2% (26/32) | 26/31 |
| `math/Combination.java` | 100.0% (19/19) | 87.5% (7/8) | 25/25 |
| `math/Gcd.java` | 95.8% (69/72) | 92.3% (24/26) | 72/79 |
| `math/MatrixLib.java` | 77.8% (63/81) | 87.5% (35/40) | 45/50 |
| `math/NttLib.java` | 98.0% (99/101) | 97.4% (37/38) | 69/81 |
| `math/Prime.java` | 83.0% (39/47) | 96.7% (29/30) | 35/41 |
| `range/PriorityRangeSumMain.java` | 100.0% (87/87) | 100.0% (42/42) | 48/52 |
| `range/RangeSetMain.java` | 93.9% (77/82) | 81.2% (39/48) | 32/44 |
| `range/SlidingWindowAggregationMain.java` | 100.0% (34/34) | 100.0% (12/12) | 12/12 |
| `sequence/Sequence.java` | 98.8% (79/80) | 100.0% (24/24) | 43/53 |
| `sequence/ZaatsuMain.java` | 72.2% (13/18) | 100.0% (8/8) | 11/11 |
| `string/ManacherMain.java` | 71.4% (20/28) | 100.0% (16/16) | 26/31 |
| `string/RollingHashMain.java` | 100.0% (97/97) | 100.0% (18/18) | 79/88 |
| `string/SuffixArrayMain.java` | 99.0% (96/97) | 98.6% (69/70) | 106/119 |
| `string/TrieMain.java` | 100.0% (20/20) | 100.0% (10/10) | 5/5 |
| `string/ZAlgorithmMain.java` | 82.6% (19/23) | 100.0% (12/12) | 18/21 |
| `tree/DoblingTreeMain.java` | 98.6% (70/71) | 97.4% (37/38) | 40/50 |
| `tree/DualSegmentTreeMain.java` | 91.5% (43/47) | 80.0% (16/20) | 42/49 |
| `tree/FenwickTreeMain.java` | 100.0% (40/40) | 100.0% (12/12) | 37/43 |
| `tree/LazySegmentTreeBottom.java` | 94.4% (152/161) | 83.0% (88/106) | 156/185 |
| `tree/LazySegmentationTree.java` | 89.7% (61/68) | 83.3% (25/30) | 77/83 |
| `tree/SegmentationTree.java` | 86.0% (86/100) | 87.1% (54/62) | 93/112 |
| `tree/SparseTableMain.java` | 100.0% (23/23) | 100.0% (10/10) | 25/27 |
| `tree/UnionFind.java` | 96.9% (94/97) | 100.0% (28/28) | 51/65 |

### 計測を受けて補強した点と残る範囲

初回PITでは、遅延木を毎回全区間照会していたため、遅延伝播の削除変異が生存した。
照会せずに連続更新する操作列、一点更新と境界探索の混在を追加し、該当箇所の検出を補強した。
初期値を上書きする前の双対木の照会、UnionFindの代表の戻り値、ハッシュの独立計算、
逆辺によるフローの組み替え、多数の異なる頂点を含む最小費用流も追加した。
最終的な残存変異を確認し、次の範囲を残す判断とした。スコアを上げるための対象除外はしていない。

| 残る種類 | 確認した内容・判断 |
|---|---|
| 入力制約外 | 添字範囲の例外分岐、単位元でfalseとなる境界探索の述語、異なる行列サイズ等。契約外入力の例外網羅は今回行わない |
| 到達しない処理 | RangeSetの番兵が存在するため到達しない早期return、最小費用流内部ヒープのsize/peek/clear。ヒープの初期容量は2×辺数で通常の緩和ではgrowに到達しない |
| 使用例 | Primeのmain内の表示ラムダがPITのNO_COVERAGEに残る。JaCoCoではmain/debug/toStringの行も一部残る |
| 値に影響しない処理 | ゼロ初期化直後のfill、直後に上書きする配列初期化、未使用のManacherのresult配列、固定mod同士のsameModなど |
| 同じ答えで性能が変わり得るもの | UnionFindのサイズ・rankによる併合方向、Dijkstraの古い候補の除外や比較器、Z/Manacherの再利用境界、セグメント木の節点スキップ等。内部形状の固定や時間閾値で無理に検出しない |
| 数値域・確保量に依存 | 2冪長の余分な確保、NTTのパディング量、固定modで使わない上位ビット演算、祖先表の余分な段など。すべての数値域・等価性を証明したという意味ではない |

`SURVIVED`をすべて同値と断定したものではない。上記の性能差や実装条件に依存するものは
今後の拡張時に再確認する。今回の完了条件は、正当入力での重要な計算結果・更新順序の検証と、
生存変異・未到達範囲の確認であり、100%の数値達成ではない。

設定の参考: [Gradle PIT plugin](https://plugins.gradle.org/plugin/info.solidsoft.pitest/1.19.0)、
[PITの対象クラス・内部クラス指定](https://pitest.org/quickstart/maven/)、
[JaCoCoのカバレッジカウンタ](https://www.jacoco.org/jacoco/trunk/doc/counters.html)。

