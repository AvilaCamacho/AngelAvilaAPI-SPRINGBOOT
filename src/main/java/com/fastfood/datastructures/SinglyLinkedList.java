package com.fastfood.datastructures;

import com.fastfood.model.Pedido;

public class SinglyLinkedList {
    private static class Node {
        Pedido data;
        Node next;
        Node(Pedido p) { this.data = p; }
    }

    private Node head;
    private int size = 0;

    public void add(Pedido pedido) {
        if (head == null) {
            head = new Node(pedido);
        } else {
            Node cur = head;
            while (cur.next != null) cur = cur.next;
            cur.next = new Node(pedido);
        }
        size++;
    }

    public Pedido findById(int id) {
        Node cur = head;
        while (cur != null) {
            if (cur.data.getId() == id) return cur.data;
            cur = cur.next;
        }
        return null;
    }

    public boolean removeById(int id) {
        Node cur = head;
        Node prev = null;
        while (cur != null) {
            if (cur.data.getId() == id) {
                if (prev == null) head = cur.next;
                else prev.next = cur.next;
                size--;
                return true;
            }
            prev = cur;
            cur = cur.next;
        }
        return false;
    }

    public int size() { return size; }

    public java.util.List<Pedido> toList() {
        java.util.List<Pedido> out = new java.util.ArrayList<>();
        Node cur = head;
        while (cur != null) {
            out.add(cur.data);
            cur = cur.next;
        }
        return out;
    }

    // Método recursivo para total de montos
    public double totalRecursivo() {
        return totalRecursivoHelper(head);
    }

    private double totalRecursivoHelper(Node node) {
        if (node == null) return 0.0;
        return node.data.getMonto() + totalRecursivoHelper(node.next);
    }
}