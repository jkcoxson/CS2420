import java.util.*;

public class Tree<E extends Comparable<? super E>> {
  private BinaryTreeNode root; // Root of tree

  // this should be final, sue me if I wasn't supposed to change this I guess
  private final String name; // Name of tree


  /**
   * Create an empty tree
   *
   * @param label Name of tree
   */
  // some of your constructors aren't used at all,
  // should I just remove them since they cause warnings?
  @SuppressWarnings("unused")
  public Tree(String label) {
    name = label;
  }

  /**
   * Create BST from ArrayList
   *
   * @param arr   List of elements to be added
   * @param label Name of tree
   */
  @SuppressWarnings("unused")
  public Tree(ArrayList<E> arr, String label) {
    name = label;
    for (E key : arr) {
      insert(key);
    }
  }

  /**
   * Create BST from Array
   *
   * @param arr   List of elements to be added
   * @param label Name of tree
   */
  public Tree(E[] arr, String label) {
    name = label;
    for (E key : arr) {
      insert(key);
    }
  }

  /**
   * Return a string containing the tree contents as a tree with one node per line
   * I tried to paste an example, but my beautiful auto formatter destroyed it and
   * I decided I don't care.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(name).append("\n");
    printSideways(root, sb, 0);
    return sb.toString();
  }

  private void printSideways(BinaryTreeNode node, StringBuilder sb, int depth) {
    if (node == null)
      return;

    // Print right subtree first
    printSideways(node.right, sb, depth + 1);

    // Indentation for current node
      sb.append("  ".repeat(Math.max(0, depth))); // 2 spaces per level

    // Node key and parent
    sb.append(node.key).append(" [");

    // I just want you to know that the ternary operator has valid use cases, and
    // this would be one of them :(
    if (node.parent == null) {

      sb.append("no parent");
    } else {
      sb.append(node.parent.key);
    }
    sb.append("]\n");

    // Print left subtree
    printSideways(node.left, sb, depth + 1);
  }

  /**
   * Return a string containing the tree contents as a single line
   * make this: Tree 1: 10 14 25 55 56 58 60 63
   */
  public String inOrderToString() {
    StringBuilder sb = new StringBuilder();
    sb.append(name).append(": ");
    inOrderHelper(root, sb);
    return sb.toString().trim(); // remove trailing space
  }

  private void inOrderHelper(BinaryTreeNode node, StringBuilder sb) {
    if (node == null)
      return;

    inOrderHelper(node.left, sb);

    sb.append(node.key);
    sb.append(" ");

    inOrderHelper(node.right, sb);
  }

  /**
   * reverse left and right children recursively
   */
  public void flip() {
    flipHelper(root);
  }

  // this is very cursed, why are we flipping the rules of the bst :(
  private void flipHelper(BinaryTreeNode node) {
    if (node == null)
      return;

    // Swap left and right children
    BinaryTreeNode temp = node.left;
    node.left = node.right;
    node.right = temp;

    // Recurse on children
    flipHelper(node.left);
    flipHelper(node.right);
  }

  /**
   * Returns the in-order successor of the specified node
   * 
   * @param node node from which to find the in-order successor
   */
  public BinaryTreeNode inOrderSuccessor(BinaryTreeNode node) {
    // you know what would be funny? is if node was null, because you can do that in
    // java because it's a dangerous language. If only someone could have created a
    // solution to this possibility...

    if (node.right != null) {
      return leftmost(node.right);
    }

    return successorUp(node);
  }

  // Recursive helper to find leftmost node
  private BinaryTreeNode leftmost(BinaryTreeNode node) {
    if (node.left == null)
      return node;
    return leftmost(node.left);
  }

  // Recursive helper to go up parent chain
  private BinaryTreeNode successorUp(BinaryTreeNode node) {
    if (node.parent == null) {
      return null; // reached root, no successor
    }
    if (node.parent.left == node) {
      return node.parent; // parent is successor
    }

    return successorUp(node.parent); // keep going up weeeeee
  }

  /**
   * Counts number of nodes in specified level
   *
   * @param level Level in tree, root is zero
   * @return count of number of nodes at specified level
   */
  public int nodesInLevel(int level) {
    return nodesInLevelHelper(root, level);
  }

  private int nodesInLevelHelper(BinaryTreeNode node, int level) {
    if (node == null)
      return 0;

    if (level == 0)
      return 1; // current node is at target level

    // recurse to children at next lower level
    return nodesInLevelHelper(node.left, level - 1) +
        nodesInLevelHelper(node.right, level - 1);
  }

  /**
   * Print all paths from root to leaves
   * example:
   * 25 10 14
   * 25 60 55 58 56
   * 25 60 63
   */
  public void printAllPaths() {
    ArrayList<E> path = new ArrayList<>();
    printAllPathsHelper(root, path);
  }

  private void printAllPathsHelper(BinaryTreeNode node, ArrayList<E> path) {
    if (node == null)
      return;

    // Add current node to path
    path.add(node.key);

    // If leaf, print the path
    if (node.left == null && node.right == null) {
      for (int i = 0; i < path.size(); i++) {
        if (i > 0)
          System.out.print(" ");
        System.out.print(path.get(i));
      }
      System.out.println();
    } else {
      // Recurse on left and right
      printAllPathsHelper(node.left, path);
      printAllPathsHelper(node.right, path);
    }

    // Backtrack: remove current node before returning
    path.removeLast();
  }

  /**
   * Counts all non-null binary search trees embedded in tree
   *
   * @return Count of embedded binary search trees
   */
  public int countBST() {
    return (int) countBSTRecursive(root)[0]; // return only the total count
  }

  /**
   * Recursive helper to count BSTs.
   * Returns Object[] = {Integer count, E min, E max}
   */
  private Object[] countBSTRecursive(BinaryTreeNode node) {
    if (node == null) {
      return new Object[] { 0, null, null }; // empty subtree
    }

    Object[] left = countBSTRecursive(node.left);
    Object[] right = countBSTRecursive(node.right);

    int count = (Integer) left[0] + (Integer) right[0];

    // For those of us who come from real languages, this is basically the same as
    // Rust's Box<dyn E> where E is a trait. Except in Java everything is heap,
    // because JVM. Honestly, what's even the point.
    // Anyways, we're doing this because Node isn't guaranteed to be an int.
    // Also, IntelliJ can cry that my comments are "informal" oh no I'm soooo sorry
    E min = node.key;
    E max = node.key;

    // E? More like reeeeeeee

    boolean isBST = true;

    // Check left subtree
    if (left[1] != null) {
      @SuppressWarnings("unchecked") // look, unless we assume that we're only gonna be passing ints to this tree,
                                     // this is what it's gonna have to be. It's the type of the node, so this can't
                                     // fail anyways.
      E leftMin = (E) left[1];
      @SuppressWarnings("unchecked")
      E leftMax = (E) left[2];
      if (node.key.compareTo(leftMax) <= 0)
        isBST = false;
      if (leftMin.compareTo(min) < 0)
        min = leftMin;
    }

    // Check right subtree
    if (right[1] != null) {
      @SuppressWarnings("unchecked")
      E rightMin = (E) right[1];
      @SuppressWarnings("unchecked")
      E rightMax = (E) right[2];
      if (node.key.compareTo(rightMin) >= 0)
        isBST = false;
      if (rightMax.compareTo(max) > 0)
        max = rightMax;
    }

    if (isBST)
      count += 1; // current subtree is BST

    return new Object[] { count, min, max };
  }

  /**
   * Insert into a bst tree; duplicates are allowed
   *
   * @param x the item to insert.
   */
  public void insert(E x) {
    root = insert(x, root, null);
  }

  // this method isn't even tested....
  public BinaryTreeNode getByKey(E key) {
    return getByKeyHelper(root, key);
  }

  private BinaryTreeNode getByKeyHelper(BinaryTreeNode node, E key) {
    if (node == null) {
      return null; // I know you said to assume that the key is in the tree, but we're doing this
                   // anyways. Sue me.
    }

    int cmp = key.compareTo(node.key);

    if (cmp < 0) {
      return getByKeyHelper(node.left, key); // go left
    } else if (cmp > 0) {
      return getByKeyHelper(node.right, key); // go right
    } else {
      return node; // found
    }
  }

  /**
   * Balance the tree
   * do the AVL thing
   */
  public void balanceTree() {
    ArrayList<BinaryTreeNode> nodes = new ArrayList<>();
    // get them in order
    collectNodesInOrder(root, nodes);

    root = buildBalanced(nodes, 0, nodes.size() - 1, null);
  }

  private void collectNodesInOrder(BinaryTreeNode node, ArrayList<BinaryTreeNode> nodes) {
    if (node == null) {
      return;
    }
    collectNodesInOrder(node.left, nodes);
    nodes.add(node);
    collectNodesInOrder(node.right, nodes);
  }

  private BinaryTreeNode buildBalanced(ArrayList<BinaryTreeNode> nodes, int start, int end, BinaryTreeNode parent) {
    if (start > end) {
      return null;
    }

    int mid = (start + end) / 2;
    BinaryTreeNode node = nodes.get(mid);

    node.parent = parent; // fix parent reference
    node.left = buildBalanced(nodes, start, mid - 1, node);
    node.right = buildBalanced(nodes, mid + 1, end, node);

    return node;
  }

  /**
   * Internal method to insert into a subtree.
   * In tree is balanced, this routine runs in O(log n)
   *
   * @param x the item to insert.
   * @param t the node that roots the subtree.
   * @return the new root of the subtree.
   */
  private BinaryTreeNode insert(E x, BinaryTreeNode t, BinaryTreeNode parent) {
    if (t == null) {
      return new BinaryTreeNode(x, null, null, parent);
    }

    int compareResult = x.compareTo(t.key);
    if (compareResult < 0) {
      t.left = insert(x, t.left, t);
    } else {
      t.right = insert(x, t.right, t);
    }

    return t;
  }

  /**
   * Internal method to find an item in a subtree.
   * This routine runs in O(log n) as there is only one recursive call that is
   * executed and the work
   * associated with a single call is independent of the size of the tree: a=1,
   * b=2, k=0
   *
   * @param x is item to search for.
   * @param t the node that roots the subtree.
   *          SIDE EFFECT: Sets local variable curr to be the node that is found
   * @return node containing the matched item.
   */
  @SuppressWarnings("unused")
  private boolean contains(E x, BinaryTreeNode t) {
    if (t == null)
      return false;

    int compareResult = x.compareTo(t.key);

    if (compareResult < 0)
      return contains(x, t.left);
    else if (compareResult > 0)
      return contains(x, t.right);
    else {
      return true; // Match
    }
  }

  // Basic node stored in unbalanced binary trees
  public class BinaryTreeNode {
    E key; // The data/key for the node
    BinaryTreeNode left; // Left child
    BinaryTreeNode right; // Right child
    BinaryTreeNode parent; // Parent node

    // Constructors
    @SuppressWarnings("unused") // why is this here if we never use it??
    BinaryTreeNode(E theElement) {
      this(theElement, null, null, null);
    }

    BinaryTreeNode(E theElement, BinaryTreeNode lt, BinaryTreeNode rt, BinaryTreeNode pt) {
      key = theElement;
      left = lt;
      right = rt;
      parent = pt;
    }

    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Node:");
      sb.append(key);
      if (parent == null) {
        sb.append("<>");
      } else {
        sb.append("<");
        sb.append(parent.key);
        sb.append(">");
      }

      return sb.toString();
    }
  }
}
