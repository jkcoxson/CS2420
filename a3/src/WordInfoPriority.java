public class WordInfoPriority implements Comparable<WordInfoPriority> {
  final private String word;
  final private int moves;
  final private int priority;
  final private String history;

  public WordInfoPriority(String word, int moves, int estimatedWork) {
    this.word = word;
    this.moves = moves;
    this.priority = moves + estimatedWork;
    this.history = word;
  }

  public WordInfoPriority(String word, int moves, int estimatedWork, String history) {
    this.word = word;
    this.moves = moves;
    this.priority = moves + estimatedWork;
    this.history = history;
  }

  // revolutionary concept
  public String getWord() {
    return word;
  }

  public int getMoves() {
    return moves;
  }

  public int getPriority() {
    return priority;
  }

  public String getHistory() {
    return history;
  }

  // impl Ord for WordInfoPriority, except verbose and sad
  @Override
  public int compareTo(WordInfoPriority other) {
    return Integer.compare(this.priority, other.priority);
  }

  // #[derive(Debug)] would have been nice but nooooo
  @Override
  public String toString() {
    return String.format("Word %s Moves %d Priority %d : History[%s]",
        word, moves, priority, history);
  }
}
