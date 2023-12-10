import java.util.ArrayList;

// Binary Heap implementation to store song objects. Max heap unless specified otherwise.
public class BinaryHeap {
    private int size;
    public ArrayList<Song> array;
    private final boolean isMaxHeap;  // Boolean value denoting whether the binary heap is max or min heap
    private final String sortBy;  // Value of the song object to sort by (name, playCount, heartache, roadTrip, blissful)
    public int elementCount;  // Decreases immediately when a song is removed via remove function in EpicBland class

    BinaryHeap() {
        this(true, "name");
    }
    BinaryHeap(boolean isMaxHeap, String sortBy) {
        this.size = 0;
        this.elementCount = 0;
        this.array = new ArrayList<>();
        this.array.add(null);
        this.isMaxHeap = isMaxHeap;
        this.sortBy = sortBy;
    }
    BinaryHeap(Song[] items, boolean isMaxHeap, String sortBy) {
        this.size = 0;
        this.elementCount = 0;
        this.isMaxHeap = isMaxHeap;
        this.sortBy = sortBy;
        this.array = new ArrayList<>();
        this.array.add(null);
        for(Song item : items) {
            if(item == null) {
                break;
            }
            this.array.add(item);
            this.size++;
        }
        buildHeap();
    }

    // Return the number of items in the binary heap
    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    // Return the item at the top of the max-min heap
    public Song peek() {
        return array.get(1);
    }

    // Remove the top item from the heap and return it
    public Song pop() {
        Song topItem = peek();
        Song lastItem = this.array.get(this.size);
        this.array.remove(this.size);  // Remove the last item from the max heap
        this.array.set(1, lastItem);  // Place the last item in the heap to the top
        this.size--;
        percolateDown(1);

        return topItem;
    }

    // Insert an item to the max-min heap
    public void insert(Song item) {
        this.elementCount++;
        int hole = ++this.size;
        this.array.add(item);

        if(this.isMaxHeap) {
            while(hole > 1 && item.compare(this.array.get(hole/2), this.sortBy, true) > 0) {
                Song parent = this.array.get(hole/2);
                this.array.set(hole, parent);
                this.array.set(hole/2, item);
                hole /= 2;
            }
        }
        else {
            while(hole > 1 && item.compare(this.array.get(hole/2), this.sortBy, false) < 0) {
                Song parent = this.array.get(hole/2);
                this.array.set(hole, parent);
                this.array.set(hole/2, item);
                hole /= 2;
            }
        }
    }

    // Turn the binary heap into a max-min heap
    private void buildHeap() {
        for(int i = this.size / 2; i > 0; i--) {
            percolateDown(i);
        }
    }

    // Move an item to its correct position
    private void percolateDown(int hole) {
        Song tmp = this.array.get(hole);
        int child;

        // While current hole position has a child
        while(hole * 2 <= this.size) {
            child = hole * 2;

            if(this.isMaxHeap) {  // Max heap case
                // If right child is greater than the left child, move there
                if(child != this.size && this.array.get(child + 1).compare(this.array.get(child), this.sortBy, true) > 0) {
                    child++;
                }
                if(this.array.get(child).compare(tmp, this.sortBy, true) > 0) {
                    this.array.set(hole, this.array.get(child));
                }
                else {
                    break;
                }
            }
            else {  // Min heap case
                // If right child is less than the left child
                if(child != this.size && this.array.get(child + 1).compare(this.array.get(child), this.sortBy, false) < 0) {
                    child++;
                }
                if(this.array.get(child).compare(tmp, this.sortBy, false) < 0) {
                    this.array.set(hole, this.array.get(child));
                }
                else {
                    break;
                }
            }

            hole = child;
        }

        this.array.set(hole, tmp);
    }
}
