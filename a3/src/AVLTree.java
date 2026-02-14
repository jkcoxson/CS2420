import java.lang.*;

/**
 * Implements an AVL tree.
 * Note that all "matching" is based on the compareTo method.
 * Based on code provided by Mark Allen Weiss (CS 2420 book author)
 */
public class AVLTree<E extends Comparable<? super E>> {
  /**
   * Construct the tree.
   */
  public AVLTree() {
    root = null;
  }

  /**
   * Insert into the tree; duplicates are ignored.
   *
   * @param value the item to insert.
   */
  public void insert(E value) {
    root = insert(value, root);
  }

  // Pop the smallest element. Like BTreeMap::pop_first() except we also
  // have to rebalance because trees in Java don't take care of themselves
  public E deleteMin() {
    if (isEmpty()) {
      throw new RuntimeException(); // Rust would make this a Result<E, Error>. who thought exceptions were a good
                                    // idea?
    }
    E min = findMin(root).value;
    root = deleteMin(root);
    return min;
  }

  // Walk left until we hit the wall. The leftmost node is the min.
  private AvlNode findMin(AvlNode node) {
    while (node.left != null) {
      node = node.left;
    }
    return node;
  }

  // Recursively yeet the min node, then rebalance on the way back up.
  // The min has no left child, so we just replace it with its right child.
  private AvlNode deleteMin(AvlNode node) {
    if (node.left == null) {
      return node.right; // no left child = we found the min. drop it. drop it!
    }
    node.left = deleteMin(node.left);
    return balance(node);
  }

  /**
   * Find the largest item in the tree.
   *
   * @return the largest item of null if empty.
   */
  public E findMax() {
    if (isEmpty()) {
      throw new RuntimeException();
    }

    return findMax(root).value;
  }

  /**
   * Find an item in the tree and return a reference to it.
   * Returns null if not found because Java doesn't have Option<&T>.
   */
  public E find(E value) {
    AvlNode node = root;
    while (node != null) {
      int cmp = value.compareTo(node.value);
      if (cmp < 0) {
        node = node.left;
      } else if (cmp > 0) {
        node = node.right;
      } else {
        return node.value; // returns the actual stored object (shared ref in real languages)
      }
    }
    return null; // None, but make it nullable
  }

  /**
   * Find an item in the tree.
   *
   * @param value the item to search for.
   * @return true if x is found.
   */
  public boolean contains(E value) {
    return contains(value, root);
  }

  // /**
  // * Make the tree logically empty.
  // * never used, so we'll remove it so there's no warnings
  // */

  /**
   * Test if the tree is logically empty.
   *
   * @return true if empty, false otherwise.
   */
  public boolean isEmpty() {
    return root == null;
  }

  // Prints the tree sideways (right subtree on top, left on bottom).
  // Like Debug printing a BTreeMap, but we have to do it ourselves
  // because Java's toString is about as useful as a screen door on a submarine
  public void printTree(String label) {
    System.out.println(label);
    printTree(root, "");
  }

  // reverse in-order traversal: right, self, left. Prints value(height).
  // String concatenation for indent is O(n^2) but the GC will deal with it
  private void printTree(AvlNode node, String indent) {
    if (node == null) {
      return;
    }
    printTree(node.right, indent + "  ");
    System.out.println(indent + node.value + "(" + node.height + ")");
    printTree(node.left, indent + "  ");
  }

  private static final int ALLOWED_IMBALANCE = 1;

  // Assume t is either balanced or within one of being balanced
  private AvlNode balance(AvlNode node) {
    if (node == null) {
      return null;
    }

    if (height(node.left) - height(node.right) > ALLOWED_IMBALANCE) {
      if (height(node.left.left) >= height(node.left.right)) {
        node = rightRotation(node);
      } else {
        node = doubleRightRotation(node);
      }
    } else if (height(node.right) - height(node.left) > ALLOWED_IMBALANCE) {
      if (height(node.right.right) >= height(node.right.left)) {
        node = leftRotation(node);
      } else {
        node = doubleLeftRotation(node);
      }
    }

    node.height = Math.max(height(node.left), height(node.right)) + 1;
    return node;
  }

  /**
   * Internal method to insert into a subtree. Duplicates are allowed
   *
   * @param value the item to insert.
   * @param node  the node that roots the subtree.
   * @return the new root of the subtree.
   */
  private AvlNode insert(E value, AvlNode node) {
    if (node == null) {
      return new AvlNode(value, null, null);
    }

    int compareResult = value.compareTo(node.value);

    if (compareResult < 0) {
      node.left = insert(value, node.left);
    } else {
      node.right = insert(value, node.right);
    }

    return balance(node);
  }

  /**
   * Internal method to find the largest item in a subtree.
   *
   * @param node the node that roots the tree.
   * @return node containing the largest item.
   */
  private AvlNode findMax(AvlNode node) {
    if (node == null) {
      return null;
    }

    while (node.right != null) {
      node = node.right;
    }
    return node;
  }

  /**
   * Internal method to find an item in a subtree.
   *
   * @param value is item to search for.
   * @param node  the node that roots the tree.
   * @return true if x is found in subtree.
   */
  private boolean contains(E value, AvlNode node) {
    while (node != null) {
      int compareResult = value.compareTo(node.value);

      if (compareResult < 0) {
        node = node.left;
      } else if (compareResult > 0) {
        node = node.right;
      } else {
        return true; // Match
      }
    }

    return false; // No match
  }

  /**
   * Return the height of node t, or -1, if null.
   */
  private int height(AvlNode node) {
    if (node == null) {
      return -1;
    }

    return node.height;
  }

  /**
   * Rotate binary tree node with left child.
   * For AVL trees, this is a single rotation for case 1.
   * Update heights, then return new root.
   */
  private AvlNode rightRotation(AvlNode node) {
    AvlNode theLeft = node.left;
    node.left = theLeft.right;
    theLeft.right = node;
    node.height = Math.max(height(node.left), height(node.right)) + 1;
    theLeft.height = Math.max(height(theLeft.left), node.height) + 1;
    return theLeft;
  }

  /**
   * Rotate binary tree node with right child.
   * For AVL trees, this is a single rotation for case 4.
   * Update heights, then return new root.
   */
  private AvlNode leftRotation(AvlNode node) {
    AvlNode theRight = node.right;
    node.right = theRight.left;
    theRight.left = node;
    node.height = Math.max(height(node.left), height(node.right)) + 1;
    theRight.height = Math.max(height(theRight.right), node.height) + 1;
    return theRight;
  }

  /**
   * Double rotate binary tree node: first left child
   * with its right child; then node k3 with new left child.
   * For AVL trees, this is a double rotation for case 2.
   * Update heights, then return new root.
   */
  private AvlNode doubleRightRotation(AvlNode node) {
    node.left = leftRotation(node.left);
    return rightRotation(node);
  }

  /**
   * Double rotate binary tree node: first right child
   * with its left child; then node k1 with new right child.
   * For AVL trees, this is a double rotation for case 3.
   * Update heights, then return new root.
   */
  private AvlNode doubleLeftRotation(AvlNode node) {
    node.right = rightRotation(node.right);
    return leftRotation(node);
  }

  private class AvlNode {
    AvlNode(E value, AvlNode left, AvlNode right) {
      this.value = value;
      this.left = left;
      this.right = right;
      height = 0;
    }

    E value; // The data in the node
    AvlNode left; // Left child
    AvlNode right; // Right child
    int height; // Height
  }

  /**
   * The tree root.
   */
  private AvlNode root;
}
