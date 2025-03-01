package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T>
{
    private T[] items;
    private int size;
    private int start;
    private int end;

    /** Creates an empty list. */
    public ArrayDeque()
    {
        items = (T[]) new Object[8];
        size = 0;
        start = -1;
        end = 0;
    }

    private void resize(int capacity)
    {
        T[] a = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++)
        {
            a[i] = get(i);
        }
        items = a;
        end = size;
        start = -1;
    }

    private int convertToPositive(int index)
    {
        while (index < 0)
        {
            index += items.length;
        }
        return index;
    }

    @Override
    public void addLast(T item)
    {
        if (size == items.length)
        {
            resize(size * 2);
        }
        if (size == 0)
        {
            items[0] = item;
            end = 1;
            start = -1;
        }
        else
        {
            if (end < 0)
            {
                items[convertToPositive(end)] = item;
            }
            else if (end >= items.length)
            {
                items[end % items.length] = item;
            }
            else
            {
                items[end] = item;
            }
            end++;
        }
        size++;
    }

    @Override
    public void addFirst(T item)
    {
        if (size == items.length)
        {
            resize(size * 2);
        }
        if (size == 0)
        {
            items[0] = item;
            end = 1;
            start = -1;
        }
        else
        {
            if (start < 0)
            {
                items[convertToPositive(start)] = item;
            }
            else if (start >= items.length)
            {
                items[start % items.length] = item;
            }
            else
            {
                items[start] = item;
            }
            start--;
        }
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

        T item;
        if (items.length >= 16 && size - 1 < items.length / 4)
        {
            resize(items.length / 4);
        }

        if (start + 1 < 0)
        {
            item = items[convertToPositive(start + 1)];
            items[convertToPositive(start + 1)] = null;
        }
        else
        {
            item = items[(start + 1) % items.length];
            items[(start + 1) % items.length] = null;
        }
        start++;
        size--;

        return item;
    }

    @Override
    public T removeLast()
    {
        if (size == 0)
        {
            return null;
        }

        if (items.length >= 16 && size - 1 < items.length / 4)
        {
            resize(items.length / 4);
        }

        T item;
        if (end - 1 < 0)
        {
            item = items[convertToPositive(end - 1)];
            items[convertToPositive(end - 1)] = null;
        }
        else
        {
            item = items[(end - 1) % items.length];
            items[(end - 1) % items.length] = null;
        }
        end--;
        size--;

        return item;
    }

    @Override
    public T get(int index)
    {
        if (index >= items.length)
        {
            return null;
        }
        return items[convertToPositive(start + 1 + index) % items.length];
    }

    @Override
    public Iterator<T> iterator()
    {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T>
    {
        private int wizPos;

        ArrayDequeIterator()
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
        ArrayDeque<T> other = (ArrayDeque<T>) o;
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


