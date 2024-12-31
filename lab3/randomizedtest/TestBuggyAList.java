package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by hug.
 */

public class TestBuggyAList {
  // YOUR TESTS HERE
  @Test
  public void testThreeAddThreeRemove() {
      AListNoResizing<Integer> correct = new AListNoResizing<>();
      BuggyAList<Integer> broken = new BuggyAList<>();

      correct.addLast(5);
      correct.addLast(10);
      correct.addLast(15);

      broken.addLast(5);
      broken.addLast(10);
      broken.addLast(15);

      assertEquals(correct.size(), broken.size());

      assertEquals(correct.removeLast(), broken.removeLast());
      assertEquals(correct.removeLast(), broken.removeLast());
      assertEquals(correct.removeLast(), broken.removeLast());
  }
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();
        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                broken.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int broken_size = broken.size();
                assertEquals(size,broken_size);
            } else if (operationNumber == 2){
                if (L.size() > 0){
                    int last_item = L.getLast();
                    int broken_last_item = broken.getLast();
                    assertEquals(last_item,broken_last_item);
                }
            }
            else {
                if (L.size() > 0){
                    int last_item = L.removeLast();
                    int broken_last_item = broken.removeLast();
                    assertEquals(last_item,broken_last_item);
                }
            }

        }

    }

}
