package main;

import java.util.ArrayList;
import java.util.List;

public class Runner {
    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        List<DoublyLinkedListWorkerThread> threads = new ArrayList<>();
        main.CompareAndSwapSortDoublyLinkedList<OwnedInteger> ll = new CompareAndSwapSortDoublyLinkedList<OwnedInteger>(new OwnedInteger(null, -1));
        for (int i = 0 ; i < 100 ; i ++) {
            DoublyLinkedListWorkerThread worker = new DoublyLinkedListWorkerThread(ll);
            threads.add(worker);
            ll.addThread(worker);

        }
        for (int i = 0 ; i < 100; i++) {
            threads.get(i).start();
        }
        for (int i = 0 ; i < 100 ; i ++) {
            threads.get(i).join();
        }
        long end = System.currentTimeMillis();
        System.out.println(String.format("Finished %dms %ds, checking results...", end - start, (end - start) / 1000));
        for (int i = 0 ; i < 100 ; i ++) {
            threads.get(i).assertCorrectness();
        }

        }
}