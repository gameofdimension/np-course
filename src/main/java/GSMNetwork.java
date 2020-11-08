import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class GSMNetwork {

  private final InputReader reader;
  private final OutputWriter writer;

  public GSMNetwork(InputReader reader, OutputWriter writer) {
    this.reader = reader;
    this.writer = writer;
  }

  public static void main(String[] args) {
    InputReader reader = new InputReader(System.in);
    OutputWriter writer = new OutputWriter(System.out);
    new GSMNetwork(reader, writer).run();
    writer.writer.flush();
  }

  class Edge {

    int from;
    int to;
  }

  class ConvertGSMNetworkProblemToSat {

    int numVertices;
    Edge[] edges;

    boolean vars[][];

    ConvertGSMNetworkProblemToSat(int n, int m) {
      numVertices = n;
      edges = new Edge[m];
      for (int i = 0; i < m; ++i) {
        edges[i] = new Edge();
      }

      vars = new boolean[numVertices][3];
    }

    List<String> exactlyOnce(int nodeIndex) {
      int var1 = varIndex(nodeIndex, 0);
      int var2 = varIndex(nodeIndex, 1);
      int var3 = varIndex(nodeIndex, 2);
      return Arrays.asList(String.format("%d %d %d 0\n", var1, var2, var3),
          String.format("%d %d 0\n", -var1, -var2),
          String.format("%d %d 0\n", -var1, -var3),
          String.format("%d %d 0\n", -var3, -var2));
    }

    int varIndex(int nodeIndex, int color) {
      return 1 + nodeIndex * 3 + color;
    }

    List<String> notSameColor(int i, int j) {

      List<String> result = new ArrayList<String>();
      // int[][] candidates = new int[3][2];
      for (int iter = 0; iter < 3; iter++) {
        // candidates[iter] = new int[]{varIndex(i, iter), -1 * varIndex(j, iter)};
        result.add(String.format("%d %d 0\n", -varIndex(i, iter), -varIndex(j, iter)));
      }

      // for (int a = 0; a < 2; a++) {
      //   for (int b = 0; b < 2; b++) {
      //     for (int c = 0; c < 2; c++) {
      //       result.add(String
      //           .format("%d %d %d 0\n", candidates[0][a], candidates[1][b], candidates[2][c]));
      //     }
      //   }
      // }
      return result;
    }

    void printEquisatisfiableSatFormula() {
      // This solution prints a simple satisfiable formula
      // and passes about half of the tests.
      // Change this function to solve the problem.
      // writer.printf("3 2\n");
      // writer.printf("1 2 0\n");
      // writer.printf("-1 -2 0\n");
      // writer.printf("1 -2 0\n");
      List<String> result = new ArrayList<String>();
      for (int i = 0; i < numVertices; i++) {
        result.addAll(exactlyOnce(i));
      }
      for (Edge edge : edges) {
        result.addAll(notSameColor(edge.from - 1, edge.to - 1));
      }

      writer.printf("%d %d\n", result.size(), numVertices * 3);
      for (String clause : result) {
        writer.printf("%s", clause);
      }
    }
  }

  public void run() {
    int n = reader.nextInt();
    int m = reader.nextInt();

    ConvertGSMNetworkProblemToSat converter = new ConvertGSMNetworkProblemToSat(n, m);
    for (int i = 0; i < m; ++i) {
      converter.edges[i].from = reader.nextInt();
      converter.edges[i].to = reader.nextInt();
    }

    converter.printEquisatisfiableSatFormula();
  }

  static class InputReader {

    public BufferedReader reader;
    public StringTokenizer tokenizer;

    public InputReader(InputStream stream) {
      reader = new BufferedReader(new InputStreamReader(stream), 32768);
      tokenizer = null;
    }

    public String next() {
      while (tokenizer == null || !tokenizer.hasMoreTokens()) {
        try {
          tokenizer = new StringTokenizer(reader.readLine());
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
      return tokenizer.nextToken();
    }

    public int nextInt() {
      return Integer.parseInt(next());
    }

    public double nextDouble() {
      return Double.parseDouble(next());
    }

    public long nextLong() {
      return Long.parseLong(next());
    }
  }

  static class OutputWriter {

    public PrintWriter writer;

    OutputWriter(OutputStream stream) {
      writer = new PrintWriter(stream);
    }

    public void printf(String format, Object... args) {
      writer.print(String.format(Locale.ENGLISH, format, args));
    }
  }
}
