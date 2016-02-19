public class LinkedListDeque<Item> {
	
	private class Node{
		public Node prev; /* previous node */
		public Item item;
		public Node next;

		public Node(Node p, Item i, Node h) {
			prev = p;
			item = i;
			next = h;		
		}

    }

	//Class Variables
	private Node sentF;
	private int size;
	private Node sentB;

	//Constructors
	public LinkedListDeque(){
		size = 0;
        sentB = new Node(sentF, null, null);
		sentF = new Node(null, null, sentB);
		sentB.prev = sentF;
	}

	// public LinkedListDeque(Item x){
	// 	size = 1;
 //        sentB = new Node(sentF, null, null);
	// 	sentF = new Node(null, null, sentB);
 //        sentF.next = new Node(sentF, x, sentB);
 //        sentB.prev = sentF.next;
	// }


	//main method
//	public static void main(String[] args) {
//        LinkedListDeque<Integer> pop = new LinkedListDeque<Integer>();
//        pop.addLast(0);
//		Integer z = pop.removeLast();
//		boolean g = pop.isEmpty();
//
//	}

//
	//methods
	public void addFirst(Item x){
		Node oldFront = sentF.next;
		Node newFront = new Node(sentF, x, oldFront);
		oldFront.prev = newFront;
		sentF.next = newFront;
        size +=1;



	}

	public void addLast(Item x){
		Node oldLast = sentB.prev;
		Node newLast= new Node(oldLast, x, sentB);
		sentB.prev = newLast;
		oldLast.next = newLast;
		size +=1;
        
	}
//
	public boolean isEmpty(){

		return sentF.next.next == null;
	}

	public int size(){
		return size;
	}

	public void printDeque(){
		Node first = sentF.next;
		while(first != sentB){
			System.out.print(first.item + " ");
			first = first.next;

		}
	}

	public Item removeFirst(){
		if (isEmpty()){
			return null;
		}
		else{
            Item removedItem = sentF.next.item;
            sentF.next = sentF.next.next;
			sentF.next.prev = sentF;
            size -=1;
            return removedItem;
		}
	}

	public Item removeLast(){
		if (isEmpty()){
			return null;
		}
		else{
            Item removedItem = sentB.prev.item;
			sentB.prev = sentB.prev.prev;
			sentB.prev.next = sentB;

            size -=1;
            return removedItem;
		}

	}

	public Item get(int index){
		if(index > size() -1){
			return null;
		}
		else{
			Node copyList = sentF.next;
			while(index > 0){
				copyList = copyList.next;
				index--;
			}
				return copyList.item;

		}
	}

	public Item getRecursive(int index){
        return helpGetRecursive(sentF, index);
	}

    private Item helpGetRecursive(Node r, int index){
        if(index > size() -1){
            return null;
        }
        if (index == 0){
            return r.next.item;
        }
        else {
            return helpGetRecursive(r.next, index - 1);
        }
    }

}

