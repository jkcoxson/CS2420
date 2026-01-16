import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;

public class LadderGame {
  ArrayList<ArrayList<String>> dictionaryWords; // snake_case is superior to camelCase (I use Rust btw)

  // why is pair not a language feature what the heck
  // it's the year of our Lord 2026
  private static class Pair<A, B> {
    A a;
    B b;

    public Pair(A set_a, B set_b) {
      a = set_a;
      b = set_b;
    }
  }

  public LadderGame(String dictionaryFile) {
    dictionaryWords = new ArrayList<>();
    readDictionary(dictionaryFile);
  }

  public void play(String start, String end) {
    if (start.length() != end.length()) {
      System.out.println("They're not the same length, dummy >:(");
      return;
    }

    WordInfo current = new WordInfo(start, 0);
    Optional<Pair<WordInfo, Integer>> result = playIteration(current, end);

    if (result.isEmpty()) {
      System.out.printf("%s -> %s : No ladder was found%n",
          start, end);
    } else {
      System.out.printf("%s -> %s : %d moves [%s] total enqueues %d%n",
          start, end, result.get().a.getMoves(), result.get().a.getHistory(), result.get().b);
    }
  }

  private Optional<Pair<WordInfo, Integer>> playIteration(WordInfo start, String end) {
    Queue<WordInfo> queue = new Queue<>();
    HashSet<String> visited = new HashSet<>();

    queue.enqueue(start);
    visited.add(start.getWord());
    int enqs = 0;

    while (!queue.isEmpty()) {
      Optional<WordInfo> currentO = queue.dequeue();
      if (currentO.isEmpty()) {
        return Optional.empty(); // should never happen, but "Intelli"J throws a warning
      }

      WordInfo current = currentO.get();

      if (current.getWord().equals(end)) {
        return Optional.of(new Pair<>(current, enqs));
      }

      for (String next : oneAway(current.getWord(), false)) {
        if (!visited.contains(next)) {
          visited.add(next);

          enqs += 1;
          queue.enqueue(new WordInfo(
              next,
              current.getMoves() + 1,
              current.getHistory() + " " + next));
        }
      }
    }

    return Optional.empty();
  }

  public ArrayList<String> oneAway(String word, boolean withRemoval) {
    ArrayList<String> words = new ArrayList<>();
    ArrayList<String> candidates = dictionaryWords.get(word.length());

    Iterator<String> it = candidates.iterator();
    while (it.hasNext()) {
      String candidate = it.next();
      int differences = 0;

      for (int i = 0; i < candidate.length(); i++) {
        if (candidate.charAt(i) != word.charAt(i)) {
          differences++;
          if (differences > 1)
            break; // it don't matter anymore
        }
      }

      if (differences == 1) {
        words.add(candidate);
        if (withRemoval) {
          it.remove(); // this would be UB in a real programming language :D
        }
      }
    }

    return words;
  }

  public void listWords(int length, int howMany) {
    if (dictionaryWords.size() < length) {
      System.out.println("That length is too big >:(");
      return;
    }
    ArrayList<String> selected = dictionaryWords.get(length);

    for (int i = 0; i < Math.min(howMany, selected.size()); i++) {
      System.out.println(selected.get(i));
    }
  }

  /*
   * Reads a list of words from a file, putting all words of the same length into
   * the same array.
   */
  private void readDictionary(String dictionaryFile) {
    File file = new File(dictionaryFile);
    ArrayList<String> allWords = new ArrayList<>();

    //
    // Track the longest word, because that tells us how big to make the array.
    int longestWord = 0;
    try (Scanner input = new Scanner(file)) {
      //
      // Start by reading all the words into memory.
      while (input.hasNextLine()) {
        String word = input.nextLine().toLowerCase();
        allWords.add(word);
        longestWord = Math.max(longestWord, word.length());
      }

      for (int i = 0; i <= longestWord; i++) {
        dictionaryWords.add(new ArrayList<>());
      }

      for (String word : allWords) {
        dictionaryWords.get(word.length()).add(word);
      }

    }
    // try catch is a terrible way to do error handling and nobody likes it :(
    catch (java.io.IOException ex) {
      System.out.println("An error occurred trying to read the dictionary: " + ex);
    }
  }
}
