package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private final Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> comparator) {
        super();
        this.comparator = comparator;
    }

    public T max() {
        return max(this.comparator);  // Use the class field 'comparator' here
    }

    public T max(Comparator<T> comp) {
        if (this.isEmpty()) {
            return null;
        }

        int maxIndex = 0;
        for (int i = 1; i < this.size(); i++) {
            if (comp.compare(this.get(i), this.get(maxIndex)) > 0) {  // Use 'comp' here
                maxIndex = i;
            }
        }
        return this.get(maxIndex);
    }
}

