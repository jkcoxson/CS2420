public class HashingPoetry {
  public static void main(String[] args) {
    WritePoetry poem = new WritePoetry();

    System.out.println("--- Sam ---");
    System.out.println(poem.writePoem("green.txt", "sam", 30, false));
    System.out.println();

    System.out.println("--- Lester ---");
    System.out.println(poem.writePoem("lester.txt", "lester", 30, false));
    System.out.println();

    System.out.println("--- How Many ---");
    System.out.println(poem.writePoem("how-many.txt", "how", 50, false));
    System.out.println();

    testUpdatedHashTable();
  }

  /**
   * Demonstrates the updated HashTable class with two generic parameters.
   */
  private static void testUpdatedHashTable() {
    HashTable<String, Integer> table = new HashTable<>();

    table.insert("one", 1);
    table.insert("two", 2);
    table.insert("three", 3);
    table.insert("four", 4);
    table.insert("five", 5);
    table.insert("six", 6);

    System.out.println();
    System.out.printf("The value associated with one is: %d\n", table.find("one"));
    System.out.printf("The value associated with two is: %d\n", table.find("two"));
    System.out.printf("The value associated with three is: %d\n", table.find("three"));
    System.out.printf("The value associated with four is: %d\n", table.find("four"));
    System.out.printf("The value associated with five is: %d\n", table.find("five"));
    System.out.printf("The value associated with six is: %d\n", table.find("six"));

    System.out.println(table.toString(6));
  }
}
