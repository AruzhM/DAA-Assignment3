package graph;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GraphReader {

    public static Graph read(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);

        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Empty graph file");
        }

        String[] header = lines.get(0).trim().split("\\s+");

        if (header.length != 3) {
            throw new IllegalArgumentException("Invalid graph header");
        }

        int n = Integer.parseInt(header[0]);
        int m = Integer.parseInt(header[1]);

        if (m < 0) {
            throw new IllegalArgumentException("Negative edge count");
        }

        boolean directed;

        if (header[2].equals("directed")) {
            directed = true;
        } else if (header[2].equals("undirected")) {
            directed = false;
        } else {
            throw new IllegalArgumentException("Unknown graph type");
        }

        if (lines.size() != m + 1) {
            throw new IllegalArgumentException(
                    "Expected " + m + " edge lines"
            );
        }

        Graph graph = new Graph(n, directed);

        for (int i = 1; i <= m; i++) {
            String[] parts = lines.get(i).trim().split("\\s+");

            if (parts.length != 3) {
                throw new IllegalArgumentException(
                        "Invalid edge at line " + (i + 1)
                );
            }

            int u = Integer.parseInt(parts[0]);
            int v = Integer.parseInt(parts[1]);
            int w = Integer.parseInt(parts[2]);

            graph.addEdge(u, v, w);
        }

        return graph;
    }
}
