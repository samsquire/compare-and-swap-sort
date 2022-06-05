package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CompareAndSwapSortDoublyLinkedList<V> {
    public AtomicInteger idGenerator = new AtomicInteger(0);
    public AtomicReference<CompareAndSwapSortDoublyLinkedList<V>> root = new AtomicReference<>();
    public V value;
    public AtomicReference<CompareAndSwapSortDoublyLinkedList<V>> next = new AtomicReference<>();
    public AtomicReference<CompareAndSwapSortDoublyLinkedList<V>> previous = new AtomicReference<>();
    public AtomicInteger dirty = new AtomicInteger();
    public AtomicInteger claim = new AtomicInteger();
    public AtomicInteger last_claim = new AtomicInteger(Integer.MAX_VALUE);
    public List<CompareAndSwapDoublyLinkedListThread> threads = new ArrayList<>();

    public CompareAndSwapSortDoublyLinkedList(V value) {
        this.value = value;
        this.root.set(this);
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
        claim.set(idGenerator.incrementAndGet());
    }

    public CompareAndSwapSortDoublyLinkedList<V> insertBeginning(CompareAndSwapDoublyLinkedListThread thread, V value) {
        while (isDirty()) {
            Thread.yield();
            // System.out.println("Root is dirty");
        }
        claim(thread);
        setDirty();
        while (claim.get() > last_claim.get()) {
            Thread.yield();
            System.out.println("Trying to get claim");
            Integer minimum = last_claim.get();
            for (CompareAndSwapDoublyLinkedListThread others : threads) {
                if (others.threadId.get() < minimum) {
                    minimum = others.threadId.get();
                }
            }
            last_claim.set(minimum);
        }
        boolean trying = true;
        CompareAndSwapSortDoublyLinkedList<V> newBeginning = new CompareAndSwapSortDoublyLinkedList<V>(value);
        newBeginning.root.set(this);
        while (trying) {
            CompareAndSwapSortDoublyLinkedList<V> originalNext = next.get();
            CompareAndSwapSortDoublyLinkedList<V> originalPrevious = previous.get();
            if (originalPrevious != null) {
                if (!originalPrevious.previous.compareAndSet(originalPrevious, newBeginning)) {
                    continue;
                }
            }

            if (!next.compareAndSet(originalNext, newBeginning)) {
                continue;
            }
            newBeginning.next.set(originalNext);
            trying = false;
        }
        root.get().dirty.set(0);
        thread.threadId.set(Integer.MAX_VALUE);

        return newBeginning;
    }


}