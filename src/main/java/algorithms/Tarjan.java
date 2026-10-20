package algorithms;
import graph.Graph;
import graph.Graph.Edge;
import java.util.*;

public class Tarjan {

    public record Result(
            int componentCount,
            int[] componentOf,
            long edgeScans,
            int maxDepth
    ) {}

    private static class Frame {
        int vertex;
        int nextIndex;

        Frame(int vertex) {
            this.vertex = vertex;
        }
    }

    public static Result findSCC(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Tarjan requires a directed graph"
            );
        }

        int n = graph.vertexCount();

        int[] index = new int[n];
        int[] lowlink = new int[n];
        int[] componentOf = new int[n];

        Arrays.fill(index, -1);
        Arrays.fill(componentOf, -1);

        boolean[] onStack = new boolean[n];

        Deque<Integer> active = new ArrayDeque<>();
        Deque<Frame> dfs = new ArrayDeque<>();

        int timer = 0;
        int componentCount = 0;
        long edgeScans = 0;
        int maxDepth = 0;

        for (int start = 0; start < n; start++) {
            if (index[start] != -1) {
                continue;
            }

            dfs.push(new Frame(start));

            while (!dfs.isEmpty()) {
                maxDepth = Math.max(maxDepth, dfs.size());

                Frame frame = dfs.peek();
                int u = frame.vertex;

                if (index[u] == -1) {
                    index[u] = timer;
                    lowlink[u] = timer;
                    timer++;

                    active.push(u);
                    onStack[u] = true;
                }

                List<Edge> neighbors = graph.neighbors(u);

                if (frame.nextIndex < neighbors.size()) {
                    Edge edge = neighbors.get(frame.nextIndex++);
                    edgeScans++;

                    int v = edge.to();

                    if (index[v] == -1) {
                        dfs.push(new Frame(v));
                    } else if (onStack[v]) {
                        lowlink[u] = Math.min(
                                lowlink[u],
                                index[v]
                        );
                    }
                } else {
                    dfs.pop();

                    if (!dfs.isEmpty()) {
                        int parent = dfs.peek().vertex;

                        lowlink[parent] = Math.min(
                                lowlink[parent],
                                lowlink[u]
                        );
                    }

                    if (lowlink[u] == index[u]) {
                        int v;

                        do {
                            v = active.pop();
                            onStack[v] = false;
                            componentOf[v] = componentCount;
                        } while (v != u);

                        componentCount++;
                    }
                }
            }
        }

        return new Result(
                componentCount,
                componentOf,
                edgeScans,
                maxDepth
        );
    }
}
