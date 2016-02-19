<<<<<<< ab2d5be4cfdea53140e66d062eddd82e9f5ccaef
public class ArrayDeque {
	
	private int[] items;
	private int size;
	private int nextF;
	private int nextL;
	private static int RFACTOR = 2;

	//Constructor
	public ArrayDeque(){
		size = 0;
		items = new int[8]; 
		nextF = 3;
		nextL = 4;
	}


	//Main Method
	public static void main(String[] args) {
		ArrayDeque pop = ArrayDeque();
		pop.addFirst(6);
		int s = 1;
	}



	public void addFirst(int kapow){
		items[nextF] = kapow;


		if(nextF == nextL){
			resize(RFACTOR);
		}

		if(nextF <= 0){
			nextF = items.length - 1;
		}
		else{
			nextF -= 1;
		}

		size+=1

	}

	private void resize(int capacity) {
    	int[] a = new int[capacity];
    	// System.arraycopy(items, 0, a, 0, size);
    	items = a;    	
    }


	
}
=======
public class ArrayDeque<Item> {

    private Item[] items;
    private int size;
    private int front;
    private int back;
    private int rFactor = 2;

    //Constructor
    public ArrayDeque() {
        size = 0;
        items = (Item[]) new Object[8];
        front = 0;
        back = 0;
    }


    //Main Method
    // public static void main(String[] args) {
    //  ArrayDeque values = new ArrayDeque();
    //  values.addFirst(10);
    //  values.addLast(15);
    // }


    //Resize Methods

    private void growResize() {
        Item[] newItems = (Item []) new Object[size * rFactor];
        if (front > back) {
            System.arraycopy(items, front, newItems, 0, items.length - front);
            System.arraycopy(items, 0, newItems, items.length - front, back + 1);
        } else {
            System.arraycopy(items, front, newItems, 0, back - front + 1);
        }
        front = 0;
        back = size() - 1;
        items = newItems;
    }

    private void shrinkResize() {
        Item[] newItems = (Item []) new Object[size * rFactor];
        if (front > back) {
            System.arraycopy(items, front, newItems, 0, items.length - front);
            System.arraycopy(items, 0, newItems, items.length - front, back + 1);
        } else {
            System.arraycopy(items, front, newItems, 0, back - front + 1);
        }
        front = 0;
        back = size() - 1;
        items = newItems;
    }

    public void addFirst(Item kapow) {


        //Change Front
        if (front <= 0) {
            front = items.length - 1;
        } else {
            front -= 1;
        }

        items[front] = kapow;
        size += 1;

        //single item edge case
        if (size == 1) {
            back = front;
        }

        if (size() == items.length -1) {
            growResize();
        }



    }

    public void addLast(Item kapow) {


        //Change Back
        if (back >= items.length - 1) {
            back = 0;
        } else {
            back += 1;
        }

        items[back] = kapow;
        size += 1;

        //single item edge case
        if (size == 1) {
            front = back;
        }

        if (size() == items.length - 1) {
            growResize();
        }

    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = front; i < items.length; i++) {
            System.out.print(items[i] + " ");
        }
        for (int b = 0; b <= back; b++) {
            System.out.print(items[b] + " ");
        }
    }

    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        }



        Item removed = items[front];
        items[front] = null;
        size -= 1;

        //single item edge case
        if (size == 0) {
            front = back;
            return removed;
        }

        if (front >= items.length - 1) {
            front = 0;
        } else {
            front += 1;
        }

        if ((items.length >= 16 && (size() / items.length) <= 0.25) && size >= 1) {
            shrinkResize();
        }

        return removed;

    }

    public Item removeLast() {
        if (isEmpty()) {
            return null;
        }



        Item removed = items[back];
        items[back] = null;
        size -= 1;

        //single item edge case
        if (size == 0) {
            back = front;
            return removed;
        }

        if (back <= 0) {
            back = items.length - 1;
        } else {
            back -= 1;
        }

        if ((items.length >= 16 && (size() / items.length) <= 0.25) && size >= 1) {
            shrinkResize();
        }

        return removed;

    }

    public Item get(int index) {
        if (index > size() - 1) {
            return null;
        }
        if (index + front < items.length) {
            return items[index + front];
        } else {
            return items[index + front - items.length];
        }
    }
}


>>>>>>> -a
