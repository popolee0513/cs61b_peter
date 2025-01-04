package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T> {
    private T[] items;
    private int size,front_index,end_index;
    public ArrayDeque(){
        items = (T[]) new Object[8];
        size = 0;
        front_index = 0;
        end_index = 0;
    }

    public void resize(int capacity){
        T[] a = (T[]) new Object[capacity];
        int counter = 0 ;
        for (int i = front_index + 1; i < end_index; i++) {
            // Calculate the correct index considering wrap-around
            int index = (i < 0) ? (i + items.length) : i;// This handles the wrap-around logic
            a[counter] = items[index];
            counter++;
        }
        items = a;
        end_index =  size;
        front_index = -1;
    }
    @Override
    public void addFirst(T item) {
        if (size == items.length){
           resize(items.length*2);
        }
        if (size == 0) {
            items[0] = item;
            end_index += 1;
        } else {
            if ( front_index < 0 ){
                items[items.length + front_index] = item;
            }
            else{
                items[front_index] = item;
            }
        }
        front_index -= 1;
        size += 1;
    }
    @Override
    public void addLast(T item) {
        if (size == items.length){
            resize(items.length*2);
        }
        if (size == 0) {
            items[0] = item;
            front_index -=1;
        } else {
            if ( end_index < 0 ){
                items[items.length + end_index] = item;
            }
            else{
                items[end_index] = item;
            }
        }
        end_index += 1;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        if (size == 0) {
            System.out.println();
        } else {
            int len = items.length;
            // Start and end indices
            int start = front_index + 1;
            int end = end_index - 1;

           // Single loop to handle both ranges
            for (int i = start; i <= end; i++) {
                // Calculate the actual index with wrap-around
                int index = (i < 0) ? (i + len) : i;  // If i is negative, adjust the index by adding len

                // If the item at 'index' is not null
                if (items[index] != null) {
                    boolean isLast = (i == end);
                    System.out.print(items[index]);

                    if (!isLast) {
                        System.out.print(",");
                    }
                }
            }
            // New line after printing the entire deque
            System.out.println();
        }
    }

    @Override
    public T removeFirst() {
        if (size == 0){
            return null;
        }
        if ((size < items.length / 4) && (items.length >= 16)) {
            resize(items.length / 4);
        }
        T removed ;
        if (front_index + 1 >= 0 ){
            removed = items[front_index + 1];
            items[front_index + 1] = null;
        }
        else{
            removed = items[front_index + 1 + items.length];
            items[front_index + 1 + items.length] = null;

        }
        front_index += 1;
        size -=1;
        return removed;
     }

    @Override
    public T removeLast() {
        if (size == 0){
            return null;
        }
        if ((size < items.length / 4) && (items.length >= 16)) {
            resize(items.length / 4);
        }
        T removed ;
        if (end_index - 1 >= 0 ){
            removed = items[end_index - 1];
            items[end_index - 1] = null;
        }
        else{
            removed = items[end_index - 1 + items.length];
            items[end_index - 1 + items.length] = null;
        }
        end_index -= 1;
        size -=1 ;
        return removed;
    }
    public Iterator<T> iterator(){
        return null;
    }

    public boolean equals(Object o){
        return false;
    }

    @Override
    public T get(int index) {
        if (index + front_index + 1 < 0)
        {
            index = index + front_index + 1 + items.length;
        }
       else{
            index = index + front_index + 1;
        }
        return items[index];
    }
}