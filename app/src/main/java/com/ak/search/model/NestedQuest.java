package com.ak.search.model;

import android.util.Log;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by dg hdghfd on 10-07-2017.
 */

public class NestedQuest {
    final NestedData data;
    final List<NestedQuest> children;

    // constructor
    public NestedQuest(NestedData data, NestedQuest... children) {
        this.data=data;
        this.children = Arrays.asList(children);
    }


    //class to iterate over the set of values rowwize
    static class InOrderIterator implements Iterator<NestedData> {
        //queue to handle elements of tree
        final Queue<NestedQuest> queue = new LinkedList<NestedQuest>();

        //constructor to add elements
        public InOrderIterator(NestedQuest tree) {
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
            NestedQuest node = queue.remove();
            queue.addAll(node.children);

            return
                    node.data;
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
            return new InOrderIterator(NestedQuest.this);
        }
    };
}