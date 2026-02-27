import java.io.File;
import java.util.Random;
import java.util.Scanner;

public class WritePoetry {
  public String writePoem(String file, String startWord, int length, boolean printHashtable) {
    HashTable<String, WordFreqInfo> table = buildHashTable(file);

    if (printHashtable) {
      System.out.print(table.toString(table.size()));
    }

    StringBuilder poem = new StringBuilder();
    String currentWord = startWord.toLowerCase();
    poem.append(currentWord);

    Random rnd = new Random();
    int count = 1; // startWord is already token #1
    // True immediately after a punctuation token ends a line, so the next word
    // is placed at the start of the new line without a leading space.
    boolean atLineStart = false;

    while (count < length) {
      WordFreqInfo info = table.find(currentWord);
      if (info == null || info.getOccurCount() == 0) {
        break;
      }

      // Pick a random index in [0, occurCount) and let getFollowWord map it
      // to a specific follow word weighted by observed frequency.
      int randomCount = rnd.nextInt(info.getOccurCount());
      String nextWord = info.getFollowWord(randomCount);
      if (nextWord == null) {
        break;
      }

      count++;

      // Check the last character (consistent with how buildHashTable tokenizes):
      // punctuation tokens are always single non-alphabetic characters, and
      // words like "they're" end alphabetically so they're treated as words.
      if (!Character.isAlphabetic(nextWord.charAt(nextWord.length() - 1))) {
        poem.append(nextWord).append("\n");
        atLineStart = true;
      } else {
        if (!atLineStart) {
          poem.append(" ");
        }
        poem.append(nextWord);
        atLineStart = false;
      }

      currentWord = nextWord;
    }

    String result = poem.toString();
    // Drop a trailing newline left by the last punctuation token so the caller
    // sees a clean string (println adds its own newline).
    if (result.endsWith("\n")) {
      result = result.substring(0, result.length() - 1);
    }

    // End with a period if the poem doesn't already finish with punctuation.
    if (!result.isEmpty() && Character.isAlphabetic(result.charAt(result.length() - 1))) {
      result += ".";
    }

    return result;
  }

  private HashTable<String, WordFreqInfo> buildHashTable(String filename) {
    HashTable<String, WordFreqInfo> table = new HashTable<>();
    String prevToken = null;

    try (Scanner fileScanner = new Scanner(new File(filename))) {
      while (fileScanner.hasNextLine()) {
        String line = fileScanner.nextLine();
        String[] parts = line.split(" ");

        for (String part : parts) {
          String lowered = part.toLowerCase();
          if (lowered.isEmpty()) {
            continue;
          }

          char lastChar = lowered.charAt(lowered.length() - 1);

          if (!Character.isAlphabetic(lastChar)) {
            String wordPart = lowered.substring(0, lowered.length() - 1);
            String punct = String.valueOf(lastChar);

            if (!wordPart.isEmpty()) {
              if (prevToken != null) {
                updateTable(table, prevToken, wordPart);
              }
              prevToken = wordPart;
            }

            // prevToken is now the word (or whatever came before a bare
            // punctuation token), so record what follows it.
            if (prevToken != null) {
              updateTable(table, prevToken, punct);
            }
            prevToken = punct;
          } else {
            if (prevToken != null) {
              updateTable(table, prevToken, lowered);
            }
            prevToken = lowered;
          }
        }
      }
    } catch (java.io.IOException ex) {
      System.out.println("Error reading file: " + ex);
    }

    return table;
  }

  private void updateTable(HashTable<String, WordFreqInfo> table, String word, String follow) {
    WordFreqInfo info = table.find(word);
    if (info == null) {
      info = new WordFreqInfo(word, 0);
      table.insert(word, info);
    }
    info.updateFollows(follow);
  }
}
