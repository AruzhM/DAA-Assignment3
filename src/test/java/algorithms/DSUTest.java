package algorithms;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DSUTest {

    @Test
    void testUnionAndFind() {
        DSU dsu = DSU.makeSet(5);

        assertEquals(5, dsu.components());

        assertTrue(dsu.union(0, 1));
        assertTrue(dsu.union(1, 2));

        assertEquals(dsu.find(0), dsu.find(2));
        assertEquals(3, dsu.components());

        assertFalse(dsu.union(0, 2));
        assertEquals(3, dsu.components());
    }

    @Test
    void testIsolatedVertices() {
        DSU dsu = DSU.makeSet(4);

        assertEquals(4, dsu.components());
        assertNotEquals(dsu.find(0), dsu.find(1));
    }

    @Test
    void testCounters() {
        DSU dsu = DSU.makeSet(3);

        dsu.union(0, 1);

        assertEquals(1, dsu.getSuccessfulUnions());
        assertTrue(dsu.getFindCalls() >= 2);
    }
}
