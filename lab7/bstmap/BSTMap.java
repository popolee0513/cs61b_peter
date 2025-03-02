package bstmap;

import java.util.Set;
import java.util.Iterator;


public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{

    private int size;
    private Node root;
    private class Node {
        private K k;
        private V v;
        private Node left;
        private Node right;
        public Node(K key, V value, Node l, Node r){
            this.k = key;
            this.v = value;
            this.left = l;
            this.right = r;
        }
        public V getValue(K key) {
            return this.v;
        }
    }

    public BSTMap() {
        size = 0;
        root = null;
    }

    @Override
    public void clear() {
           root = null;
           size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return find(root, key) != null;
    }

    @Override
    public V get(K key) {
        if (!containsKey(key)) {
            return null;
        }
        Node out = find(root, key);
        return out.getValue(key);
    }

    @Override
    public int size() {
        return size;
    }

    private Node find(Node bst, K key) {
        if (bst == null) {
            return null;
        }
        else if ( bst.k.equals(key)) {
            return bst;
        }
        else if (bst.k.compareTo(key) > 0) {
            return find(bst.left,key);
        } else {
            return find(bst.right,key);
        }
    }

    private Node insertNode(Node node, Node bst) {
           if ( bst == null ) {
               return node;
           }
           if (bst.k.compareTo(node.k) > 0) {
               bst.left = insertNode(node, bst.left);
           }
           else if (bst.k.compareTo(node.k) < 0) {
               bst.right = insertNode(node, bst.right);
           }
           return bst;
    }

    @Override
    public void put(K key, V value) {
        if (size == 0) {
            root = new Node(key, value,null,null);
        }
        else {
            Node new_node = new Node(key, value,null,null);
            insertNode(new_node,root);
        }
        size += 1;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
