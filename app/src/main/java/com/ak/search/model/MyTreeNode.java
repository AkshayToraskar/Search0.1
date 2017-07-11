package com.ak.search.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by dg hdghfd on 11-07-2017.
 */

public class MyTreeNode<T>{
    private T data = null;
    private List<MyTreeNode> children = new ArrayList<>();
    private MyTreeNode parent = null;

    public MyTreeNode(T data) {
        this.data = data;
    }



    public void addChild(MyTreeNode child) {
        child.setParent(this);
        this.children.add(child);
    }

    public void addChild(T data) {
        MyTreeNode<T> newChild = new MyTreeNode<>(data);
        newChild.setParent(this);
        children.add(newChild);
    }

    public void removeChild(int index){
        children.remove(index);
    }

    public void addChildren(List<MyTreeNode> children) {
        for(MyTreeNode t : children) {
            t.setParent(this);
        }
        this.children.addAll(children);
    }

    public List<MyTreeNode> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private void setParent(MyTreeNode parent) {
        this.parent = parent;
    }

    public MyTreeNode getParent() {
        return parent;
    }







    //class to iterate over the set of values rowwize
    static class InOrderIterator implements Iterator<NestedData> {
        //queue to handle elements of tree
        final Queue<MyTreeNode> queue = new LinkedList<MyTreeNode>();

        //constructor to add elements
        public InOrderIterator(MyTreeNode tree) {
            queue.add(tree);
        }

        //boolean check for next element in queue
        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        //get the  next element
        @Override
        public NestedData next() {
            MyTreeNode node = queue.remove();
            queue.addAll(node.children);

            return
                    (NestedData) node.data;
        }

        //check for exceptions
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // traverse the tree
    public Iterable<NestedData> inOrderView = new Iterable<NestedData>() {
        @Override
        public Iterator<NestedData> iterator() {
            return new InOrderIterator(MyTreeNode.this);
        }
    };


}