package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CompareAndSwapSortDoublyLinkedList<V> {
    public final CompareAndSwapDoublyLinkedListThread thread;
    public AtomicInteger idGenerator = new AtomicInteger(0);
    public AtomicReference<CompareAndSwapSortDoublyLinkedList<V>> root = new AtomicReference<>();
    public V value;
    public AtomicReference<CompareAndSwapSortDoublyLinkedList<V>> next = new AtomicReference<>();
    public AtomicReference<CompareAndSwapSortDoublyLinkedList<V>> previous = new AtomicReference<>();
    public AtomicInteger dirty = new AtomicInteger();
    public AtomicInteger claim = new AtomicInteger();
    public AtomicInteger last_claim = new AtomicInteger(Integer.MAX_VALUE);
    public List<CompareAndSwapDoublyLinkedListThread> threads = new ArrayList<>();

    public CompareAndSwapSortDoublyLinkedList(CompareAndSwapDoublyLinkedListThread thread, V value) {
        this.value = value;
        this.root.set(this);
        this.thread = thread;
    }

    public void addThread(CompareAndSwapDoublyLinkedListThread thread) {
        this.threads.add(thread);
        thread.threadId.set(Integer.MAX_VALUE);
    }


    public void setDirty() {
        root.get().dirty.set(1);
    }

    public boolean isDirty() {
        return root.get().dirty.get() == 1;
    }

    public void claim(CompareAndSwapDoublyLinkedListThread thread) {
        int nextId = idGenerator.getAndIncrement();
        thread.threadId.set(nextId);
        claim.set(nextId);

    }

    public CompareAndSwapSortDoublyLinkedList<V> insertBeginning(CompareAndSwapDoublyLinkedListThread thread, V value) {

        // System.out.println(String.format("%d %d", claim.get(), last_claim.get()));
        boolean waitingForTurn = true;
        CompareAndSwapSortDoublyLinkedList<V> newBeginning = new CompareAndSwapSortDoublyLinkedList<V>(thread, value);
        newBeginning.root.set(this);
        int thisClaim = claim.get();
        while (waitingForTurn) {
            while (isDirty()) {
                // Thread.yield();
                // System.out.println("Root is dirty");
            }

            claim(thread);
            setDirty();
            // System.out.printf("Waiting for a turn");

            while (thisClaim > last_claim.get() || last_claim.get() == Integer.MAX_VALUE) {
                Integer originalMinimum = last_claim.get();
                Integer minimum = thread.threadId.get();
                // Thread.yield();
                // System.out.println("Trying to get claim");


                for (CompareAndSwapDoublyLinkedListThread others : threads) {

                    if (others.threadId.get() < thread.threadId.get() && others.threadId.get() > originalMinimum) {
                        // System.out.println(String.format("Found new minimum write %d", others.threadId.get()));
                        minimum = others.threadId.get();
                    }
                }

                if (!last_claim.compareAndSet(originalMinimum, minimum)) {
                    continue;
                }


            }


            // System.out.println("Trying write");


            CompareAndSwapSortDoublyLinkedList<V> originalNext = next.get();
            CompareAndSwapSortDoublyLinkedList<V> originalPrevious = null;
            if (originalNext != null) {
                if (!next.compareAndSet(originalNext, newBeginning)) {

                    continue;
                }

                originalPrevious = originalNext.previous.get();
                if (!originalNext.previous.compareAndSet(originalPrevious, newBeginning)) {
                    // System.out.println("Race 1");
                    continue;
                }

            }



            newBeginning.next.set(originalNext);
            waitingForTurn = false;
        }

        unclaim();
        thread.threadId.set(Integer.MAX_VALUE);
        // last_claim.set(Integer.MAX_VALUE);

        // System.out.println("Finished updating link list");

        return newBeginning;
    }

    public void unclaim() {
        root.get().dirty.set(0);
    }


}