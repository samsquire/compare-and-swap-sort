package main;

public class OwnedInteger {
    public DoublyLinkedListWorkerThread thread;
    public Integer value;

    public OwnedInteger(DoublyLinkedListWorkerThread thread, Integer value) {

        this.thread = thread;
        this.value = value;
    }
}
