package com.fastfood.datastructures;

public class StackHistorial {
    private static class Node {
        HistorialOperacion data;
        Node next;
        Node(HistorialOperacion d) { this.data = d; }
    }

    private Node top;

    public void push(HistorialOperacion op) {
        Node n = new Node(op);
        n.next = top;
        top = n;
    }

    public HistorialOperacion pop() {
        if (top == null) return null;
        HistorialOperacion op = top.data;
        top = top.next;
        return op;
    }

    public boolean isEmpty() {
        return top == null;
    }
}