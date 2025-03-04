package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimes1() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }
    @Test
    public void testSquarePrimes2() {
        IntList lst = IntList.of(14, 15, 16, 18, 17);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 18 -> 289", lst.toString());
        assertTrue(changed);
    }
    @Test
    public void testSquarePrimes3() {
        IntList lst = IntList.of(14, 15, 16, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 18", lst.toString());
        assertFalse(changed);
    }
    @Test
    public void testSquarePrimes4() {
        IntList lst = IntList.of(15, 11, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("15 -> 121 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }


}
