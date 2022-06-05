package main;

import java.util.concurrent.atomic.AtomicInteger;

public class CompareAndSwapDoublyLinkedListThread extends Thread {
    public volatile AtomicInteger threadId = new AtomicInteger();

}
