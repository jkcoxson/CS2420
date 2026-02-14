import java.util.HashSet;
import java.util.Optional;

public class LadderGameExhaustive extends LadderGame {
  public LadderGameExhaustive(String dictionaryFile) {
    super(dictionaryFile);
  }

  // brute force approach. Checks every possibility like a for loop
  @Override
  public void play(String start, String end) {
    System.out.printf("Seeking exhaustive solution from %s -> %s%n", start, end);

    if (start.length() != end.length()) {
      System.out.printf("%s -> %s : No ladder was found%n", start, end);
      return;
    }

    Queue<WordInfo> queue = new Queue<>();
    HashSet<String> visited = new HashSet<>();
    int enqs = 0;

    queue.enqueue(new WordInfo(start, 0));
    visited.add(start);

    while (!queue.isEmpty()) {
      Optional<WordInfo> currentOpt = queue.dequeue();
      if (currentOpt.isEmpty()) {
        return;
      }
      WordInfo current = currentOpt.get();

      if (current.getWord().equals(end)) {
        System.out.printf("[%s] total enqueues %d%n", current.getHistory(), enqs);
        return;
      }

      // don't remove from dictionary (false) since we share it across play() calls.
      // in Rust, the borrow checker would have caught this at compile time :D
      // who decided everything is basically arc<mutex<t>>
      for (String next : oneAway(current.getWord(), false)) {
        if (!visited.contains(next)) {
          visited.add(next);
          enqs++;
          queue.enqueue(new WordInfo(
              next,
              current.getMoves() + 1,
              current.getHistory() + " " + next));
        }
      }
    }

    System.out.printf("%s -> %s : No ladder was found%n", start, end);
  }
}
