package week2.a3;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.StringTokenizer;

public class SchoolBus {

  private static FastScanner in;
  private static int INF = 1000 * 1000 * 1000;

  public static void main(String[] args) {
    in = new FastScanner();
    try {
      printAnswer(solveSchoolBus(readData()));
    } catch (IOException exception) {
      System.err.print("Error during reading: " + exception.toString());
    }
  }

  static Answer solveSchoolBus(final int[][] graph) {
    // This solution tries all the possible sequences of stops.
    // It is too slow to pass the problem.
    // Implement a more efficient algorithm here.
    int n = graph.length;
    Integer[] p = new Integer[n];
    for (int i = 0; i < n; ++i) {
      p[i] = i;
    }

    class Solver {

      int bestAnswer = INF;
      List<Integer> bestPath;


      int bit1Num(int x) {
        int pop = 0;
        while (x > 0) {
          pop += 1;
          x = x & (x - 1);
        }
        return pop;
      }

      List<Integer> subsetSizeSContain1(int s) {
        List<Integer> res = new ArrayList<>();
        for (int k = 1; k < 1 << n; k += 2) {
          if (bit1Num(k) == s) {
            res.add(k);
          }
        }
        return res;
      }

      int remove(int subset, int pos) {
        return subset ^ (1 << pos);
      }

      boolean contain(int subset, int pos) {
        return ((1 << pos) & subset) != 0;
      }

      public void solve(Integer[] prev, int n) {
        int[][] C = new int[1 << n][n];

        int[][] path = new int[1 << n][n];
        path[1][0] = -1;

        for (int cycleLength = 2; cycleLength <= n; cycleLength++) {
          List<Integer> subsets = subsetSizeSContain1(cycleLength);
          for (int subset : subsets) {
            C[subset][0] = INF;
            for (int i = 1; i < n; i++) {
              if (!contain(subset, i)) {
                continue;
              }
              C[subset][i] = INF;
              for (int j = 0; j < n; j++) {
                if (!contain(subset, j)) {
                  continue;
                }
                if (j == i) {
                  continue;
                }
                int flipSet = remove(subset, i);
                if (C[flipSet][j] + graph[i][j] < C[subset][i]) {
                  C[subset][i] = C[flipSet][j] + graph[i][j];
                  path[subset][i] = j;
                }
              }
            }
          }
        }

        final int fullSet = (1 << n) - 1;
        int last = -1;
        for (int i = 1; i < n; i++) {
          if (C[fullSet][i] + graph[i][0] < bestAnswer) {
            bestAnswer = C[fullSet][i] + graph[i][0];
            last = i;
          }
        }

        if (bestAnswer < INF) {

          int len = n;
          prev[len - 1] = last;
          len--;
          int set = fullSet;

          while (set > 1) {
            // if (len <= 0) {
            //   break;
            // }
            int tmp = path[set][last];
            prev[len - 1] = tmp;
            set = remove(set, last);
            last = tmp;
            len--;
          }
        }

        bestPath = Arrays.asList(prev);
      }
    }
    Solver solver = new Solver();
    solver.solve(p, n);
    if (solver.bestAnswer == INF) {
      return new Answer(-1, new ArrayList<Integer>());
    }
    List<Integer> bestPath = solver.bestPath;
    for (int i = 0; i < bestPath.size(); ++i) {
      bestPath.set(i, bestPath.get(i) + 1);
    }
    if (bestPath.get(0) != 1) {
      throw new IllegalArgumentException("something get wrong");
    }
    return new Answer(solver.bestAnswer, bestPath);
  }

  private static int[][] readData() throws IOException {
    int n = in.nextInt();
    int m = in.nextInt();
    int[][] graph = new int[n][n];

    for (int i = 0; i < n; ++i) {
      for (int j = 0; j < n; ++j) {
        graph[i][j] = INF;
      }
    }

    for (int i = 0; i < m; ++i) {
      int u = in.nextInt() - 1;
      int v = in.nextInt() - 1;
      int weight = in.nextInt();
      graph[u][v] = graph[v][u] = weight;
    }
    return graph;
  }

  static void printAnswer(final Answer answer) {
    System.out.println(answer.weight);
    if (answer.weight == -1) {
      return;
    }
    for (int x : answer.path) {
      System.out.print(x + " ");
    }
    System.out.println();
  }

  static class Answer {

    int weight;
    List<Integer> path;

    public Answer(int weight, List<Integer> path) {
      this.weight = weight;
      this.path = path;
    }
  }

  static class FastScanner {

    private BufferedReader reader;
    private StringTokenizer tokenizer;

    public FastScanner() {
      reader = new BufferedReader(new InputStreamReader(System.in));
      tokenizer = null;
    }

    public String next() throws IOException {
      while (tokenizer == null || !tokenizer.hasMoreTokens()) {
        tokenizer = new StringTokenizer(reader.readLine());
      }
      return tokenizer.nextToken();
    }

    public int nextInt() throws IOException {
      return Integer.parseInt(next());
    }
  }

}
