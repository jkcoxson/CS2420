// QuadraticProbing Hash table class
//
// CONSTRUCTION: an approximate initial size or default of 101
//
// ******************PUBLIC OPERATIONS*********************
// bool insert( x, v )    --> Insert x with value v
// bool remove( x )       --> Remove x
// bool contains( x )     --> Return true if x is present
// void makeEmpty( )      --> Remove all items

import java.util.Arrays;

/**
 * Probing table implementation of hash tables.
 * Note that all "matching" is based on the equals method.
 *
 * @author Mark Allen Weiss (based on code from)
 */
public class HashTable<K, V> {
  /**
   * Construct the hash table.
   */
  public HashTable() {
    this(DEFAULT_TABLE_SIZE);
  }

  /**
   * Init the hash table.
   */
  public HashTable(int size) {
    allocateArray(size);
    doClear();
  }

  /**
   * Insert into the hash table. If the item is
   * already present, do nothing.
   */
  public void insert(K key, V value) {
    int currentPos = findPos(key);
    if (isActive(currentPos)) {
      return;
    }

    storage[currentPos] = new HashEntry<>(key, value, true);
    currentActiveEntries++;

    // occupiedCount tracks both active and lazily-deleted cells; rehash when
    // the table is more than half full.
    if (++occupiedCount > storage.length / 2) {
      rehash();
    }
  }

  public String toString(int limit) {
    StringBuilder sb = new StringBuilder();
    int count = 0;
    for (int i = 0; i < storage.length && count < limit; i++) {
      if (storage[i] != null && storage[i].isActive) {
        sb.append(String.format("%d: %s[%s]\n", i, storage[i].key, storage[i].value));
        count++;
      }
    }
    return sb.toString();
  }

  /**
   * Expand the hash table.
   */
  private void rehash() {
    HashEntry<K, V>[] oldArray = storage;

    allocateArray(2 * oldArray.length);
    occupiedCount = 0;
    currentActiveEntries = 0;

    for (var entry : oldArray) {
      if (entry != null && entry.isActive) {
        insert(entry.key, entry.value);
      }
    }
  }

  private int findPos(K x) {
    int offset = 1;
    int currentPos = myHash(x);

    while (storage[currentPos] != null && !storage[currentPos].key.equals(x)) {
      currentPos += offset;
      offset += 2;
      if (currentPos >= storage.length) {
        currentPos -= storage.length;
      }
    }

    return currentPos;
  }

  public int size() {
    return currentActiveEntries;
  }

  public V find(K x) {
    int currentPos = findPos(x);
    if (!isActive(currentPos)) {
      return null;
    } else {
      return storage[currentPos].value;
    }
  }

  private boolean isActive(int currentPos) {
    return storage[currentPos] != null && storage[currentPos].isActive;
  }

  private void doClear() {
    occupiedCount = 0;
    Arrays.fill(storage, null);
  }

  private int myHash(K x) {
    int hashVal = x.hashCode();

    hashVal %= storage.length;
    // Java's % can return a negative value for negative hashCodes, so force
    // the result into [0, length).
    if (hashVal < 0) {
      hashVal += storage.length;
    }

    return hashVal;
  }

  private static class HashEntry<K, V> {
    public K key;
    public V value;
    public boolean isActive;

    public HashEntry(K key, V value, boolean active) {
      this.key = key;
      this.value = value;
      this.isActive = active;
    }
  }

  private static final int DEFAULT_TABLE_SIZE = 101;

  private HashEntry<K, V>[] storage;
  private int occupiedCount;
  private int currentActiveEntries;

  // Java cannot create a generic array directly, so we create a raw array and
  // suppress the resulting unchecked-cast warning.
  @SuppressWarnings({ "unchecked" })
  private void allocateArray(int arraySize) {
    storage = new HashEntry[nextPrime(arraySize)];
  }

  private static int nextPrime(int n) {
    if (n % 2 == 0) {
      n++;
    }

    while (!isPrime(n)) {
      n += 2;
    }

    return n;
  }

  private static boolean isPrime(int n) {
    if (n == 2 || n == 3) {
      return true;
    }

    if (n == 1 || n % 2 == 0) {
      return false;
    }

    for (int i = 3; i * i <= n; i += 2) {
      if (n % i == 0) {
        return false;
      }
    }

    return true;
  }
}
