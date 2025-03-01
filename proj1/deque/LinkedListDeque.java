package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T>
{
    private class Node
    {
        private T item;
        private Node next;
        private Node prev;

        Node(T item, Node next, Node prev)
        {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }

    private int size;
    private Node head;
    private Node tail;

    public LinkedListDeque()
    {
        head = new Node(null, null, null);
        tail = new Node(null, null, null);
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    @Override
    public void addFirst(T item)
    {
        Node newNode = new Node(item, null, null);
        newNode.next = head.next;
        head.next.prev = newNode;
        head.next = newNode;
        newNode.prev = head;
        size++;
    }

    @Override
    public void addLast(T item)
    {
        Node newNode = new Node(item, null, null);
        newNode.next = tail;
        tail.prev.next = newNode;
        newNode.prev = tail.prev;
        tail.prev = newNode;
        size++;
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public void printDeque()
    {
        // Implement this method if needed
    }

    @Override
    public T removeFirst()
    {
        if (size == 0)
        {
            return null;
        }

        Node removed = head.next;
        head.next = removed.next;
        removed.next.prev = head;
        size--;
        return removed.item;
    }

    @Override
    public T removeLast()
    {
        if (size == 0)
        {
            return null;
        }

        Node removed = tail.prev;
        tail.prev = removed.prev;
        removed.prev.next = tail;
        size--;
        return removed.item;
    }

    @Override
    public T get(int index)
    {
        if (index >= size)
        {
            return null;
        }

        Node current = head.next;
        for (int i = 0; i < index; i++)
        {
            current = current.next;
        }
        return current.item;
    }

    public T getRecursive(int index)
    {
        if (index >= size || index < 0)
        {
            return null;  // Check for invalid index
        }
        return getNodeRecursive(index, head.next);
    }

    private T getNodeRecursive(int index, Node current)
    {
        if (index == 0)
        {
            return current.item;
        }
        else
        {
            return getNodeRecursive(index - 1, current.next);
        }
    }

    @Override
    public Iterator<T> iterator()
    {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T>
    {
        private int wizPos;

        LinkedListIterator()
        {
            wizPos = 0;
        }

        @Override
        public boolean hasNext()
        {
            return wizPos < size;
        }

        @Override
        public T next()
        {
            T returnItem = get(wizPos);
            wizPos++;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null)
        {
            return false;
        }
        if (this == o)
        {
            return true; // optimization
        }
        if (this.getClass() != o.getClass())
        {
            return false;
        }
        LinkedListDeque<T> other = (LinkedListDeque<T>) o;
        if (this.size() != other.size())
        {
            return false;
        }
        for (int i = 0; i < size(); i++)
        {
            if (!other.get(i).equals(this.get(i)))
            {
                return false;
            }
        }
        return true;
    }
}

