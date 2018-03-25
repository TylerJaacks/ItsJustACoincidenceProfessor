class DoublyLinkedList{
	private Link head;
	private Link tail;

	public DoublyLinkedList(){
		head = null;
		tail = null;
	}

	public void insertHead(int x){
		Link newLink = new Link(x);
		if(isEmpty())
			tail = newLink;
		else
			head.previous = newLink;
		newLink.next = head;
		head = newLink;
	}

	public void insertTail(int x){
		Link newLink = new Link(x);
		newLink.next = null;
		newLink.previous = tail;
		tail = newLink;
	}

	public Link deleteHead(){
		Link temp = head;
		head = head.next;
		head.previous = null;
		if(head == null)
			tail = null;
		return temp;
	}

	public Link deleteTail(){
		Link temp = tail;
		tail = tail.previous;
 		tail.next = null;
		return temp;
	}

	public Link delete(int x){
		Link current = head;

		while(current.value != x)
			current = current.next;

		if(current == head)
			deleteHead();

		else if(current == tail)
			deleteTail();

		else{
			current.previous.next = current.next;
			current.next.previous = current.previous;
		}
		return current;
	}

	public void insertOrdered(int x){
		Link newLink = new Link(x);
		Link current = head;
		while(current != null && x > current.value)
			current = current.next;

		if(current == head)
			insertHead(x);

		else if(current == null)
			insertTail(x);

		else{
			newLink.previous = current.previous;
			current.previous.next = newLink;
			newLink.next = current;
			current.previous = newLink;
		}
	}

	public boolean isEmpty(){
		return(head == null);
	}

	public void display(){
		Link current = head;
		while(current!=null){
			current.displayLink();
			current = current.next;
		}
		System.out.println();
	}
}

class Link{
	public int value;

	public Link next;

	public Link previous;

	public Link(int value){
		this.value = value;
	}

	public void displayLink(){
		System.out.print(value+" ");
	}

	public static void main(String args[]){
		DoublyLinkedList myList = new DoublyLinkedList();

		myList.insertHead(13);
		myList.insertHead(7);
		myList.insertHead(10);
		myList.display();

		myList.insertTail(11);
		myList.display();

		myList.deleteTail();
		myList.display();

		myList.delete(7);
		myList.display();

		myList.insertOrdered(23);
		myList.insertOrdered(67);
		myList.insertOrdered(3);
		myList.display();
	}
}
