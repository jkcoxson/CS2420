import java.util.*;

public class WordFreqInfo {
  private final String word;
  private int occurCount;
  private final ArrayList<Frequency> followList;

  public WordFreqInfo(String word, int count) {
    this.word = word;
    this.occurCount = count;
    this.followList = new ArrayList<>();
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Word :").append(word).append(":");
    sb.append(" (").append(occurCount).append(") : ");
    for (Frequency f : followList) {
      sb.append(f.toString());
    }

    return sb.toString();
  }

  public void updateFollows(String follow) {
    this.occurCount++;
    boolean updated = false;
    for (Frequency f : followList) {
      if (follow.compareTo(f.follow) == 0) {
        f.followCount++;
        updated = true;
      }
    }
    if (!updated) {
      followList.add(new Frequency(follow, 1));
    }
  }

  public int getOccurCount() {
    return this.occurCount;
  }

  public String getFollowWord(int count) {
    for (Frequency f : followList) {
      count -= f.followCount;
      if (count < 0) {
        return f.follow;
      }
    }
    return null;
  }

  private static class Frequency {
    String follow;
    int followCount;

    public Frequency(String follow, int ct) {
      this.follow = follow;
      this.followCount = ct;
    }

    @Override
    public String toString() {
      return follow + " [" + followCount + "] ";
    }

  }
}
