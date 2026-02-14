import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class LadderGame {
  ArrayList<ArrayList<String>> dictionaryWords; // snake_case is superior to camelCase (I use Rust btw)

  public LadderGame(String dictionaryFile) {
    dictionaryWords = new ArrayList<>();
    readDictionary(dictionaryFile);
  }

  public abstract void play(String start, String end);

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
