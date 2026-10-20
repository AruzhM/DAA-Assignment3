package algorithms;
import graph.Graph;
import graph.Graph.Edge;
import java.util.*;

public class Kosaraju {

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
            this.nextIndex = 0;
        }
    }

    public static Result findSCC(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kosaraju requires a directed graph"
            );
        }

        int n = graph.vertexCount();
        boolean[] visited = new boolean[n];
        List<Integer> finishOrder = new ArrayList<>(n);

        long edgeScans = 0;
        int maxDepth = 0;

        // First pass: DFS finishing order.
        for (int start = 0; start < n; start++) {
            if (visited[start]) continue;

            Deque<Frame> stack = new ArrayDeque<>();
            visited[start] = true;
            stack.push(new Frame(start));

            while (!stack.isEmpty()) {
                maxDepth = Math.max(maxDepth, stack.size());

                Frame frame = stack.peek();
                List<Edge> neighbors =
                        graph.neighbors(frame.vertex);

                if (frame.nextIndex < neighbors.size()) {
                    Edge edge = neighbors.get(frame.nextIndex++);
                    edgeScans++;

                    int v = edge.to();

                    if (!visited[v]) {
                        visited[v] = true;
                        stack.push(new Frame(v));
                    }
                } else {
                    finishOrder.add(frame.vertex);
                    stack.pop();
                }
            }
        }

        // Reverse the graph.
        Graph reversed = new Graph(n, true);

        for (Edge edge : graph.edges()) {
            reversed.addEdge(
                    edge.to(),
                    edge.from(),
                    edge.weight()
            );
        }

        // Second pass: discover components.
        int[] componentOf = new int[n];
        Arrays.fill(componentOf, -1);

        int componentCount = 0;

        for (int i = finishOrder.size() - 1; i >= 0; i--) {
            int start = finishOrder.get(i);

            if (componentOf[start] != -1) continue;

            Deque<Integer> stack = new ArrayDeque<>();
            stack.push(start);
            componentOf[start] = componentCount;

            while (!stack.isEmpty()) {
                maxDepth = Math.max(maxDepth, stack.size());

                int u = stack.pop();

                for (Edge edge : reversed.neighbors(u)) {
                    edgeScans++;

                    int v = edge.to();

                    if (componentOf[v] == -1) {
                        componentOf[v] = componentCount;
                        stack.push(v);
                    }
                }
            }

            componentCount++;
        }

        return new Result(
                componentCount,
                componentOf,
                edgeScans,
                maxDepth
        );
    }
}
