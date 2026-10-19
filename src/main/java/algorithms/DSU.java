package algorithms;

public class DSU {

    private final int[] parent;
    private final int[] size;

    private int components;
    private long findCalls;
    private long successfulUnions;

    public DSU(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative size");
        }

        parent = new int[n];
        size = new int[n];
        components = n;

        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public static DSU makeSet(int n) {
        return new DSU(n);
    }

    public int find(int x) {
        findCalls++;

        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }

        return parent[x];
    }

    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return false;
        }

        if (size[rootX] < size[rootY]) {
            int temp = rootX;
            rootX = rootY;
            rootY = temp;
        }

        parent[rootY] = rootX;
        size[rootX] += size[rootY];

        components--;
        successfulUnions++;

        return true;
    }

    public int components() {
        return components;
    }

    public long getFindCalls() {
        return findCalls;
    }

    public long getSuccessfulUnions() {
        return successfulUnions;
    }

    public long getDsuOps() {
        return findCalls + successfulUnions;
    }
}
