# DAA Assignment 3 — Graph Algorithms

**Course:** Design and Analysis of Algorithms  
**Topic:** DSU, MST, SCC, Topological Sorting and Shortest Paths  
**Implementation:** Java 25, Maven, JUnit 5

## 1. Introduction

This project implements and evaluates six graph algorithms: Disjoint Set Union (DSU), Kruskal, Prim, Kosaraju, Kahn's topological sorting, and DAG shortest paths. Two bonus features, Tarjan's SCC algorithm and Condensation Graph with Critical Path, are also included.

The graph representation uses adjacency lists and supports both directed and undirected weighted graphs. Algorithm correctness is evaluated using JUnit tests, cross-checking with independent algorithms, and deterministic reference values.

The benchmark covers five vertex counts (1,000; 3,162; 10,000; 31,623; 100,000) and two edge densities (4V and 20V). This produces 50 algorithm measurements. Execution times are reported as the median of five runs, while operation counters provide the main evidence for complexity analysis.

All ten benchmark configurations reproduced the required MST weights, SCC counts, and DAG distance sums.

## 2. Theoretical Complexity

| Algorithm | Time complexity | Extra space | Justification |
|---|---|---|---|
| DSU find/union | Amortized O(α(V)) | O(V) | Path compression and union by size keep trees shallow. |
| Kruskal | O(E log E) | O(V + E) | Sorting dominates; DSU processes edges efficiently. |
| Prim | O((V + E) log(V + E)) | O(V + E) | A priority queue maintains candidate edges. |
| Kosaraju | O(V + E) | O(V + E) | Two DFS passes and construction of the reversed graph. |
| Kahn | O(V + E) | O(V) | Each vertex is queued once and each edge is scanned once. |
| DAG Shortest Paths | O(V + E) | O(V) | Topological ordering followed by one relaxation pass. |
| Tarjan (bonus) | O(V + E) | O(V) | Discovery indices and low-link values are updated during one DFS traversal. |
| Critical Path on DAG (bonus) | O(V + E) | O(V) | Longest distances are calculated in topological order. |

Here, V denotes the number of vertices, E denotes the number of edges, and α(V) is the inverse Ackermann function. Space estimates exclude the input graph unless otherwise noted. For DSU, the O(V) entry represents the stored parent and size arrays.

## 3. Empirical Results and Operation Counters

The benchmark records `dsuOps`, `edgeScans`, `comparisons`, and `maxDepth` directly inside the algorithms.

For visualization, counted operations are defined as:

**operations = dsuOps + edgeScans + comparisons**

This is a consistent operation-count proxy, not a count of every CPU instruction. Weight comparisons in Kruskal sorting and Prim's priority queue are included.

### 3.1 Log-Log Slope Analysis

| Algorithm | Sparse: operations | Dense: operations | Edge-scan slope |
|---|---:|---:|---:|
| Kruskal | 1.037 | 1.012 | 1.000 |
| Prim | 1.101 | 1.085 | 1.000 |
| Kosaraju | 1.000 | 1.000 | 1.000 |
| Kahn | 1.000 | 1.000 | 1.000 |
| DAG Shortest Paths | 1.000 | 1.000 | 1.000 |

The traversal algorithms show slopes of 1.000, consistent with linear edge-scanning work for fixed edge density. Kruskal and Prim have operation slopes slightly above 1 because they perform additional priority-queue or sorting comparisons.

Kruskal's edge-scan slope remains 1.000 because this implementation scans all E edges once. Its sorting work is reflected in the comparison counter rather than the edge-scan counter. These measured slopes describe the benchmark's finite range and do not replace asymptotic proofs.

### 3.2 Counter-to-Formula Ratios

For sparse graphs, E = 4V.

| Algorithm | Ratio | Observed values |
|---|---|---|
| Kruskal | dsuOps / E | 4.8040–4.8250 |
| Prim | edgeScans / (V + E) | 1.6000 |
| Kosaraju | edgeScans / (V + E) | 1.6000 |
| Kahn | edgeScans / (V + E) | 0.8000 |
| DAG Shortest Paths | edgeScans / (V + E) | 0.8000 |

Kruskal's DSU-operation ratio stays close to 4.82 as graph size increases.

Kosaraju scans exactly 2E edges during its two DFS passes. Prim also scans 2E adjacency entries because each undirected edge is represented in both directions. Kahn and DAG relaxation each scan exactly E edges.

For E = 4V, the ratios become 2E/(V + E) = 1.6 and E/(V + E) = 0.8. Their stability supports the expected linear traversal behavior.

## 4. Benchmark Visualizations

### 4.1 Operations vs Graph Size

![Operations versus number of vertices](results/plots/operations_vs_n.png)

The first plot uses logarithmic axes and compares the growth of counted operations for all five benchmarked algorithms at two edge densities.

Traversals grow approximately linearly under fixed-density scaling. Kruskal and Prim perform additional weight comparisons, producing slightly steeper operation-growth trends.

### 4.2 Edge Scans per Edge

![Edge scans per edge](results/plots/scans_per_edge.png)

The second plot displays edgeScans / E as the number of vertices increases.

The horizontal lines indicate stable edge-scanning ratios. Kosaraju and Prim produce a ratio of 2, while Kruskal, Kahn, and DAG Shortest Paths produce a ratio of 1.

This is consistent with how the implementations traverse their stored edges.

### 4.3 Kruskal's Share of Examined Edges

![Kruskal edge examination share](results/plots/kruskal_edge_share.png)

The third plot compares the fraction of examined edges for sparse and dense graphs.

Both curves overlap at 1.0 because the implemented Kruskal algorithm sorts and examines every edge, even after a spanning tree has been constructed.

Consequently, the graph does not demonstrate an early-stopping advantage. The difference between sparse and dense inputs is instead reflected in the absolute number of processed edges and sorting comparisons.

## 5. Correctness Arguments

### 5.1 Minimum Spanning Tree — Cut Property

Consider any cut that divides the vertices of an undirected graph into two disjoint sets. A minimum-weight edge crossing this cut is safe to add to a minimum spanning tree, provided the current chosen edges can be extended to an MST. This follows from the exchange argument: if an MST does not contain the selected light edge, adding it creates a cycle, and a crossing edge of equal or greater weight can be removed without increasing total weight. Kruskal processes edges in nondecreasing weight order and uses DSU to avoid creating cycles. Whenever Kruskal accepts an edge, it is a safe minimum-weight connection between two different components. Prim applies the same cut property by selecting the lightest available edge connecting the current tree to an unvisited vertex. Therefore, both algorithms construct minimum spanning trees, or minimum spanning forests for disconnected graphs, and must produce the same minimum total weight.

### 5.2 DAG Shortest Paths — Induction on Topological Order

A directed acyclic graph has a topological ordering in which every edge goes from an earlier vertex to a later vertex. All vertices with in-degree zero are initialized with distance zero, representing a virtual super-source. Initially, these source distances are correct because no incoming edge can provide an alternative path. Assume that all vertices processed before vertex v have correct shortest distances. Since every predecessor of v appears earlier in the topological ordering, all possible incoming relaxations have already been performed when v is processed. Therefore, the current distance of v is the minimum possible distance from the virtual super-source. By induction, a single pass through the topological order computes the correct shortest distance for every vertex. Negative edge weights are safe because a DAG has no directed cycles, so repeated relaxation around a negative cycle is impossible.

## 6. Choosing the Appropriate Algorithm

| Situation | Recommended algorithm | Reason |
|---|---|---|
| MST with an edge list | Kruskal | Sorting edges and DSU efficiently avoid cycles. |
| MST with adjacency lists | Prim | A priority queue grows the tree through adjacent edges. |
| Disconnected undirected graph | Kruskal or Prim | Both can construct a minimum spanning forest. |
| Strongly connected components | Kosaraju | Two DFS passes provide a straightforward solution. |
| SCCs with fewer edge scans | Tarjan | One DFS pass avoids constructing a reversed graph. |
| Dependency scheduling in a DAG | Kahn | Produces a valid ordering based on in-degrees. |
| Detecting directed cycles through ordering | Kahn | An incomplete topological order identifies a cycle. |
| Shortest paths in a DAG | DAG Shortest Paths | Linear traversal, including support for negative weights. |
| Critical path in an acyclic dependency network | DAG longest-path relaxation | Calculates maximum accumulated path weight efficiently. |

Graph density and representation affect practical performance. Kruskal is especially convenient when an edge list is already available. Prim works naturally with adjacency lists and priority queues. For dependency graphs, Kahn is appropriate when a topological order is required, while SCC algorithms are useful for identifying mutually dependent jobs.

## 7. Bonus Implementations

### 7.1 Tarjan's SCC (+10%)

Tarjan uses `index[]`, `lowlink[]`, and a stack of active vertices to identify strongly connected components during one DFS traversal.

Both Tarjan and Kosaraju require O(V + E) time. However, Tarjan scans E edges, while Kosaraju scans 2E edges across two DFS passes, excluding the separate reverse-graph construction work from the scan metric.

The implementation uses an explicit DFS stack to avoid recursion-related stack overflow. Component partitions are checked against Kosaraju and an independent reachability-based reference.

### 7.2 Condensation and Critical Path (+5%)

The condensation graph replaces each strongly connected component with a single vertex. Edges connecting different components become edges in the condensed graph, which is always a DAG.

The implemented critical-path calculation finds a maximum-weight path by processing vertices in topological order. Original weights are preserved for edges between different components, including parallel alternatives.

This technique is relevant to task scheduling and dependency analysis. Longest-path calculations are efficient in DAGs because there are no directed cycles, whereas longest simple paths in general directed graphs are computationally difficult.

## 8. Execution Time and Reproducibility

Execution times were measured in milliseconds using the median of five runs. On the graph with 100,000 vertices and 2,000,000 edges, Kruskal recorded 988.276 ms, Prim 1,999.716 ms, Kosaraju 709.894 ms, Kahn 132.672 ms, and DAG Shortest Paths 258.455 ms.

These values reflect this particular machine and JVM execution environment. Garbage collection, JIT compilation, memory allocation, and background processes can affect timings. Therefore, conclusions about asymptotic complexity rely primarily on reproducible operation counters rather than wall-clock measurements.

The deterministic generator uses `Random(42)`. All ten benchmark configurations matched the provided reference results.

## 9. Conclusion

The project demonstrates the correctness and expected complexity behavior of six core graph algorithms and two bonus implementations.

Kruskal and Prim agree on minimum spanning tree weights, Kosaraju and Tarjan identify strongly connected components, Kahn validates dependency ordering, and DAG relaxation computes shortest distances even with negative edge weights.

Operation counts and log-log slopes provide empirical support for the theoretical complexity analysis. The implementations are validated through automated tests, deterministic benchmark results, and cross-checking with independent algorithms.

The final results, plots, source code, and tests provide a reproducible foundation for comparing graph algorithms across different sizes and densities.
