package com.fastfood.datastructures;

import com.fastfood.model.Pedido;

public class QueuePedidos {
    private static class Node {
        Pedido data;
        Node next;
        Node(Pedido p) { this.data = p; }
    }

    private Node front;
    private Node rear;

    public void enqueue(Pedido p) {
        Node n = new Node(p);
        if (rear != null) rear.next = n;
        rear = n;
        if (front == null) front = n;
    }

    public Pedido dequeue() {
        if (front == null) return null;
        Pedido p = front.data;
        front = front.next;
        if (front == null) rear = null;
        return p;
    }

    public boolean isEmpty() {
        return front == null;
    }

    // Método auxiliar para remover por id si se cancela
    public boolean removeById(int id) {
        Node cur = front, prev = null;
        while (cur != null) {
            if (cur.data.getId() == id) {
                if (prev == null) front = cur.next;
                else prev.next = cur.next;
                if (cur == rear) rear = prev;
                return true;
            }
            prev = cur;
            cur = cur.next;
        }
        return false;
    }
}