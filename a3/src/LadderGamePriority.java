public class LadderGamePriority extends LadderGame {
  public LadderGamePriority(String dictionaryFile) {
    super(dictionaryFile);
  }

  private static class WordTracker implements Comparable<WordTracker> {
    String word;
    int ladderLength; // mutable field on a tree node...

    WordTracker(String word, int ladderLength) {
      this.word = word;
      this.ladderLength = ladderLength;
    }

    // Ord impl that only cares about the word, not the length.
    @Override
    public int compareTo(WordTracker other) {
      return this.word.compareTo(other.word);
    }
  }

  @Override
  public void play(String start, String end) {
    System.out.printf("Seeking A* solution from %s -> %s%n", start, end);

    if (start.length() != end.length()) {
      System.out.printf("%s -> %s : No ladder was found%n", start, end);
      return;
    }

    // Two AVL trees: one is the priority queue (sorted by priority),
    // one tracks visited words. Two BTreeMaps but with extra steps and no derive
    // macros.
    AVLTree<WordInfoPriority> queue = new AVLTree<>();
    AVLTree<WordTracker> visited = new AVLTree<>();
    int enqs = 0;

    queue.insert(new WordInfoPriority(start, 0, estimateWork(start, end)));
    visited.insert(new WordTracker(start, 0));

    while (!queue.isEmpty()) {
      // deleteMin is our priority queue pop. Returns the word with the
      // lowest f(n) = g(n) + h(n). Like BinaryHeap::pop
      WordInfoPriority current = queue.deleteMin();

      if (current.getWord().equals(end)) {
        System.out.printf("[%s] total enqueues %d%n", current.getHistory(), enqs);
        return;
      }

      // false = don't remove from dictionary. A* might need to revisit words
      // through shorter paths. The exhaustive search can't relate.
      for (String next : oneAway(current.getWord(), false)) {
        int newMoves = current.getMoves() + 1;
        // constructing a dummy just to do a lookup... in Rust we'd just
        // use a HashMap<String, usize> like civilized people.
        WordTracker lookup = new WordTracker(next, 0);
        WordTracker existing = visited.find(lookup);

        if (existing == null) {
          // new word discovered
          visited.insert(new WordTracker(next, newMoves));
          queue.insert(new WordInfoPriority(
              next, newMoves, estimateWork(next, end),
              current.getHistory() + " " + next));
          enqs++;
        } else if (newMoves < existing.ladderLength) {
          // found a shorter path! mutate the visited entry in-place.
          // interior mutability go brrrrrrrrr, thanks garbage collector :D
          existing.ladderLength = newMoves;
          queue.insert(new WordInfoPriority(
              next, newMoves, estimateWork(next, end),
              current.getHistory() + " " + next));
          enqs++;
        }
      }
    }

    System.out.printf("%s -> %s : No ladder was found%n", start, end);
  }

  // Count how many chars still differ from the goal.
  // In Rust this would be a one-liner with .zip().filter().count()...
  private int estimateWork(String current, String goal) {
    int diff = 0;
    for (int i = 0; i < current.length(); i++) {
      if (current.charAt(i) != goal.charAt(i)) {
        diff++;
      }
    }
    return diff;
  }
}
