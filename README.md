# compare-and-swap-sort
Can a lock free algorithm maintain and detect a partial order violation?

This algorithm has 100 threads that concurrently try insert a number into a doubly linked list and then read the doubly linked list and.

There is a lot of contention, as you can only read the linked list if you have a claim on it. You can only write to the linked list if you have a claim.

On my dualcore machine, this takes 20 seconds.
