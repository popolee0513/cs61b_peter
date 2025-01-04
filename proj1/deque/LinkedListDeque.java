package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>{
    private class Node {
        public T item;
        public Node next;
        public Node prev;
        public Node(T i, Node n, Node m) {
            item = i;
            next = n;
            prev = m;
        }
    }
    private Node front;
    private  Node end;
    private int size;


    public LinkedListDeque() {
        front = new Node(null, null, null);
        end = new Node(null, null, null);
        front.next = end;
        end.prev = front;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
           Node nextnode = front.next;
           front.next = new Node(item, front.next, front);
           nextnode.prev = front.next;
           size += 1;
    }

    @Override
    public void addLast(T item) {
          Node nextnode = end.prev;
          end.prev = new Node(item, end, end.prev);
          nextnode.next = end.prev;
          size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node cur_node = front.next;
        for (int i = 0; i < size; i++) {
            System.out.print(cur_node.item);
            cur_node = cur_node.next;
            if (i < size- 1) {
                System.out.print(" ");  // Add a space after each element except the last one
            }
        }
        // Print a new line after the loop
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0){
            return null;
        }
        size -= 1;
        T remove_item = front.next.item;
        Node next_item = front.next.next;
        next_item.prev  = front;
        front.next = next_item;
        return remove_item;
    }

    @Override
    public T removeLast() {
        if (size == 0){
            return null;
        }
        size -= 1;
        T remove_item = end.prev.item;
        Node prev_item = end.prev.prev;
        prev_item.next = end;
        end.prev = prev_item;
        return remove_item;
    }

    @Override
    public T get(int index) {
        Node start = front.next;
        int count = 0;
        while (count < index){
            start  = start.next;
            count += 1;
        }
        return start.item;
    }

    public T getRecursive(int index){
        return null ;
    }

    public Iterator<T> iterator(){
        return null;
    }

    public boolean equals(Object o){
        return false;
    }

}