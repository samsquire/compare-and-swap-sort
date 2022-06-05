package main;

import java.util.ArrayList;
import java.util.List;

public class Runner {
    public static void main(String[] args) throws InterruptedException {
        List<DoublyLinkedListWorkerThread> threads = new ArrayList<>();
        main.CompareAndSwapSortDoublyLinkedList<OwnedInteger> ll = new CompareAndSwapSortDoublyLinkedList<OwnedInteger>(new OwnedInteger(null, -1));
        for (int i = 0 ; i < 100 ; i ++) {
            DoublyLinkedListWorkerThread worker = new DoublyLinkedListWorkerThread(ll);
            threads.add(worker);
            ll.addThread(worker);
            worker.start();
        }
        for (int i = 0 ; i < 100 ; i ++) {
            threads.get(i).join();
        }

        for (int i = 0 ; i < 100 ; i ++) {
            threads.get(i).assertCorrectness();
        }

        }
}