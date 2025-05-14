/* Labb 2 i DD2350 Algoritmer, datastrukturer och komplexitet    */
/* Se labbinstruktionerna i kursrummet i Canvas                  */
/* Ursprunglig författare: Viggo Kann KTH viggo@nada.kth.se      */
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class ClosestWords {
  TreeSet<String> closestWords = null;
  int closestDistance = -1;

  private int partDist(String w1, String w2, int w1len, int w2len) {
    // Ensure w1 is the shorter string to minimize memory usage
    if (w1len > w2len) {
      return partDist(w2, w1, w2len, w1len);
    }

    int[] prevRow = new int[w1len + 1];
    int[] currRow = new int[w1len + 1];

    // Initialize the first row
    for (int i = 0; i <= w1len; i++) {
      prevRow[i] = i;
    }

    // Fill the dp table using only two rows
    for (int j = 1; j <= w2len; j++) {
      currRow[0] = j;
      
      int minInRow = j;
      for (int i = 1; i <= w1len; i++) {
        if (w1.charAt(i - 1) == w2.charAt(j - 1)) {
          currRow[i] = prevRow[i - 1];
        } else {
          currRow[i] = 1 + Math.min(prevRow[i - 1], Math.min(prevRow[i], currRow[i - 1]));
        }
        minInRow = Math.min(minInRow, currRow[i]);
      }
      
      // Early termination if the minimum value in the current row exceeds the closestDistance
      if (minInRow > closestDistance && closestDistance != -1) {
        return closestDistance + 1;
      }

      // Swap rows
      int[] temp = prevRow;
      prevRow = currRow;
      currRow = temp;
    }

    return prevRow[w1len];
  }

  public int distance(String w1, String w2) {
    return partDist(w1, w2, w1.length(), w2.length());
  }

  public ClosestWords(String w, List<String> wordList) {
    closestWords = new TreeSet<>();
    for (String s : wordList) {
      int dist = distance(w, s);
      // System.out.println("d(" + w + "," + s + ")=" + dist);
      if (dist < closestDistance || closestDistance == -1) {
        closestDistance = dist;
        closestWords.clear();
        closestWords.add(s);
      }
      else if (dist == closestDistance) {
        closestWords.add(s);
      }
    }
  }

  public int getMinDistance() {
    return closestDistance;
  }

  public List<String> getClosestWords() {
    return List.copyOf(closestWords);
  }
}
