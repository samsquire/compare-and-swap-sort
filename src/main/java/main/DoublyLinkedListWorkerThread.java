package main;

import main.CompareAndSwapSortDoublyLinkedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DoublyLinkedListWorkerThread extends CompareAndSwapDoublyLinkedListThread {
    private final CompareAndSwapSortDoublyLinkedList<OwnedInteger> ll;
    private List<List<OwnedInteger>> runs = new ArrayList<>();

    public DoublyLinkedListWorkerThread(CompareAndSwapSortDoublyLinkedList<OwnedInteger> ll) {
        this.ll = ll;
    }

    public void run() {
        CompareAndSwapSortDoublyLinkedList<OwnedInteger> root = ll;
        for (int i = 0 ; i < 100; i++) {
            CompareAndSwapSortDoublyLinkedList<OwnedInteger> newBeginning = ll.insertBeginning(this, new OwnedInteger(this, i));

            CompareAndSwapSortDoublyLinkedList<OwnedInteger> current = newBeginning;

                List<OwnedInteger> current_run = new ArrayList<>();
                boolean finished = false;
                while (!finished) {

                    while (current.root.get().isDirty()) { // we need to restart

                    }
                    root.setDirty();
                    root.claim(this);
                    while (root.claim.get() > root.last_claim.get()) {

                        // System.out.println("Trying to get claim");
                        Integer minimum = root.last_claim.get();
                        for (CompareAndSwapDoublyLinkedListThread others : root.threads) {

                            if (others.threadId.get() < minimum && others.threadId.get() != Integer.MAX_VALUE) {
                                minimum = others.threadId.get();
                            }
                        }
                        root.last_claim.set(minimum);
                    }

                    while (current != null) {


                        current_run.add(current.value);
                        current = current.next.get();
                        if (current == null) {
                            finished = true;
                            root.unclaim();
                        }
                    }
                }
                runs.add(current_run);


        }
    }

    public void assertCorrectness() {
        for (List<OwnedInteger> run : runs) {
            Collections.reverse(run);
            int previous = -1;
            for (OwnedInteger current : run) {

                if (current.thread == this) {
                    // System.out.println(previous);
                    // System.out.println(current.value);
                    assert current.value == previous + 1 : String.format("%d %d", previous, current.value);
                    previous = current.value;
                    // System.out.println(String.format("new previous %d", previous));
                }
            }
        }
    }
}
