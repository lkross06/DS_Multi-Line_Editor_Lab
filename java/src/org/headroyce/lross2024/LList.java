package org.headroyce.lross2024;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Brian Sea
 *
 * An iterable sigularly linked list data structure
 * @param <T> the type of data stored in the list
 */
public class LList<T> implements Iterable<T>{

    private int size;
    private Node<T> head;

    /**
     * Initializes an empty list
     * Big Oh - O(1)
     */
    public LList(){
        size = 0;
        head = new Node<>(null);
    }

    /**
     * Gets the size of the linked list.  Size is a read-only attribute.
     * @return the number of elements in the list
     * Big Oh - O(1)
     */
    public int size(){
       /* Node<T> curr = head;
        int currSize = 0;
        //if the head value is null we know that the list is empty

        while (curr.next != null) {
            curr = curr.next;
            currSize += 1;

        }
        currSize += 1;

        this.size = currSize;*/
        return this.size;
    }

    /**
     * Add to the end of the linked list
     * @param data the data to add to the list
     * @return true on success, false otherwise
     * Big Oh - O(n)
     */
    public boolean add( T data ){
        //make a new node with value as data
        Node<T> newNode = new Node<>(data);
        //keep track with placeholder node

        Node<T> curr = head;
        //loop through LList to find end of list
        while (curr.next != null){
            curr = curr.next;
        }
        curr.next = newNode;


        //update size
        this.size += 1;

        return true;
    }

    /**
     * Inserts data into the list after the index provided.
     * @param data the data to insert into the linked list
     * @param place the index to insert after. -1 indicates before head, > size indicates at the end of the list
     * @return true on success, false otherwise
     * Big Oh - O(n)
     */
    public boolean insert( T data, int place ){
        Node<T> newNode = new Node<>(data);
        int prevIndex = place;
        //check for bad inputs
        if (place < -1 ){
            return false;
        }
        //check if list is empty, or inserted at beginning of list
        if (place == -1 || head.next == null){
            head.next = newNode;
            //since we're done we can end the method
            size += 1;
            return true;
        }
        //check if inserted after last element or place > size
        if (place > size - 1){
            //change the size for later
            prevIndex = size - 1;
        }
        //now we do the main loop
        Node<T> curr = head.next;
        for (int i = 0; i < prevIndex; i++){
            curr = curr.next;
        }
        Node<T> tempNode = curr.next;
        curr.next = newNode;
        newNode.next = tempNode;

        //yay
        size += 1;
        return true;
    }

    /**
     * Removes an element from the list at the index provided.
     * @param place index to remove; <= 0 indicates removal of first element; > size indicates removal of last element
     * @return the data that was removed
     * Big Oh - O(n)
     */
    public T remove( int place ){

        Node<T> prevNode = head;
        Node<T> curr = head.next;
        T rtnData = null;

        //check for empty list
        if (head.next == null){
            //no value can be removed
            return rtnData;
        }
        if (place <= 0){
            //remove first element
            rtnData = curr.data;
            head.next = curr.next;

            size -= 1;
            return rtnData;
        }
        if (place > size){
            //remove last element
            while (curr.next != null){
                prevNode = curr;
                curr = curr.next;
            }
            rtnData = curr.data;
            prevNode.next = null;

            size -= 1;
            return rtnData;
        }

        //the main loop
        for (int i = 0; i < place - 1; i++){
            prevNode = curr;
            curr = curr.next;
        }
        rtnData = curr.data;
        prevNode.next = curr.next;

        //update size
        size -= 1;
        return rtnData;
    }

    /**
     * Gets the data from a provided index (stating at index zero)
     * @param place the index to retrieve data from
     * @return the data at index place
     * @throws ArrayIndexOutOfBoundsException if place is outside the list
     * Big Oh - O(n)
     */
    public T get( int place ){

        Node<T> curr = head.next;
        int index = 0;
        T rtn = null;

        //check if it exceeded array
        if (place < 0 || place > size - 1){
            throw new ArrayIndexOutOfBoundsException();
        } else{
            //loop through LList to find end of list
            while (index <= place){
                rtn = curr.data;
                curr = curr.next;
                index++;
            }
        }

        return rtn;
    }

    /**
     * Convert the LList into a String
     * @return a String in format [E0, E1, E2, ...]
     * Big Oh - O(n)
     */
    public String toString(){
        String[] rtn = new String[this.size];
        Node<T> currNode = head;
        for ( int i = 0; i < this.size; i++ ){
            rtn[i] = currNode.data.toString();
            currNode = currNode.next;
        }
        return Arrays.toString(rtn);
    }

    /**
     * Provides an iterator for the list
     * @return a new iterator starting at the first element of the list
     * Big Oh - O(1)
     */
    @Override
    public Iterator<T> iterator() {
        return new LListIterator<T>();
    }

    /**
     * Prints the linked list to the console
     * Big Oh - O(n)
     */
    public void print(){
//
//		Node<T> current = this.head.next;
//        int spot = 0;
//        while( current != null ){
//            System.out.println(spot+": " + current.data.toString());
//            spot = spot + 1;
//            current = current.next;
//        }
    }

    /**
     * Nodes that specific to the linked list
     * @param <E> the type of the Node. It must by T or extend it.
     */
    private class Node<E extends T>{
        private E data;
        private Node<E> next;

        /**
         * Initializes new node, sets pointer node to false
         * @param data value for node
         * Big Oh - O(1)
         */
        public Node( E data ){
            this.data = data;
            this.next = null;
        }

        /**
         * gets the value of this node
         * @return value of this node
         * Big Oh - O(1)
         */
        public E getValue(){
            return data;
        }
    }

    /**
     * The iterator that is used for our list.
     */
    private class LListIterator<E extends T> implements Iterator<E>{

        private Node<T> curr;

        /**
         * Initializes iterator pointer node
         * Big Oh - O(1)
         */
        public LListIterator(){
            curr = head.next;
        }

        /**
         * Checks if the iterator node is at the end of the list
         * @return true if next node is null, false otherwise
         * Big Oh - O(1)
         */
        @Override
        public boolean hasNext() {
            //true if next() is NOT null, checks if you're at end of list
            return curr != null;
        }

        /**
         * Moves iterator node to next value
         * @return previous data value
         * Big Oh - O(1)
         */
        @Override
        public E next() {
            //moves to next value, returns prev value
            E rtnData = (E) curr.data;
            curr = curr.next;
            return rtnData;
        }
    }



}
