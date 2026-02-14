// Jackson Coxson

import java.util.Optional; // in Rust we trust

// cool generics in Java look like Rust (aka the best language known to man)
class Node<T> {
  T data;
  Node<T> next;

  Node(T new_data) {
    data = new_data;
    next = null;
  }
}

public class Queue<T> {
  private Node<T> front;
  private Node<T> rear;
  int size;

  public Queue() {
    front = null;
    rear = null;
    size = 0;
  }

  public void enqueue(T new_data) {
    Node<T> new_node = new Node<>(new_data);
    if (isEmpty()) {
      // stupid ambiguity
      front = new_node;
      rear = new_node;
      size = 1;
    } else {
      rear.next = new_node;
      rear = new_node;
      size = size + 1;
    }
  }

  // don't mutate returned elements or dragons will appear >:(
  public Optional<T> dequeue() {
    if (isEmpty()) {
      return Optional.empty();
    }

    // is this a move? or copy? or reference? I guess we'll never know (it's a
    // reference, but Java should still be more explicit)
    T temp = front.data;
    front = front.next;
    if (front == null) {
      rear = null;
    }
    size = size - 1;
    return Optional.of(temp);
  }

  public Optional<T> front() {
    if (isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(front.data);
  }

  public boolean isEmpty() {
    return front == null;
  }
}
