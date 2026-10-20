package algorithms;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class DSUReferenceTest {

    @Test
    void compareWithSimpleReference() {
        Random random = new Random(42);

        for (int test = 0; test < 100; test++) {
            int n = 2 + random.nextInt(99);

            DSU dsu = DSU.makeSet(n);

            // Simple reference implementation.
            int[] label = new int[n];

            for (int i = 0; i < n; i++) {
                label[i] = i;
            }

            int expectedComponents = n;

            for (int operation = 0; operation < 500;
                 operation++) {

                int x = random.nextInt(n);
                int y = random.nextInt(n);

                int oldX = label[x];
                int oldY = label[y];

                boolean shouldMerge = oldX != oldY;

                boolean merged = dsu.union(x, y);

                assertEquals(shouldMerge, merged);

                if (shouldMerge) {
                    for (int i = 0; i < n; i++) {
                        if (label[i] == oldY) {
                            label[i] = oldX;
                        }
                    }

                    expectedComponents--;
                }

                assertEquals(
                        expectedComponents,
                        dsu.components()
                );

                // Compare connectivity for all vertices.
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        assertEquals(
                                label[i] == label[j],
                                dsu.find(i) == dsu.find(j),
                                "Connectivity mismatch"
                        );
                    }
                }
            }
        }
    }

    @Test
    void testRepeatedUnions() {
        DSU dsu = DSU.makeSet(5);

        assertTrue(dsu.union(0, 1));
        assertFalse(dsu.union(0, 1));
        assertFalse(dsu.union(1, 0));

        assertEquals(4, dsu.components());
        assertEquals(1, dsu.getSuccessfulUnions());
    }
}
