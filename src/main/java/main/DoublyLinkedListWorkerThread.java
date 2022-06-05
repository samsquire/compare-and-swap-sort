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



                List<OwnedInteger> current_run = new ArrayList<>();
                boolean finished = false;


                while (!finished) {
                    CompareAndSwapSortDoublyLinkedList<OwnedInteger> current = ll;
                    while (current.root.get().isDirty()) { // we need to restart

                    }
                    root.claim(this);
                    root.setDirty();



                    boolean waitingForTurn = true;
                    while (waitingForTurn) {
                        int thisClaim = root.claim.get();
                        while (thisClaim > root.last_claim.get() || root.last_claim.get() == Integer.MAX_VALUE) {
                            Integer originalMinimum = root.last_claim.get();
                            // System.out.println("Trying to get claim");
                            Integer minimum = threadId.get();

                            for (CompareAndSwapDoublyLinkedListThread others : root.threads) {

                                if (others.threadId.get() < threadId.get() && others.threadId.get() > originalMinimum) {
                                    // System.out.println("Found new minimum read");
                                    minimum = others.threadId.get();
                                }
                            }

                            if (!root.last_claim.compareAndSet(originalMinimum, minimum)) {
                                // System.out.println("Failed to set last claim");
                                continue;
                            } else {
                                // System.out.println(String.format("Set read minimum to %d %d", root.claim.get(), minimum));

                            }

                        }
                        waitingForTurn = false;
                    }
                    // System.out.println("Trying read");
                    while (current != null) {


                        current_run.add(current.value);
                        current = current.next.get();
                        if (current == null) {
                            finished = true;
                            root.unclaim();
                            threadId.set(Integer.MAX_VALUE);

                            runs.add(current_run);

                            // root.last_claim.set(Integer.MAX_VALUE);
                        }
                    }
                }




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
