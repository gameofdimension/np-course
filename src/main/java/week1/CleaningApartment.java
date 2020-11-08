package week1;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

public class CleaningApartment {

  private final InputReader reader;
  private final OutputWriter writer;

  public CleaningApartment(InputReader reader, OutputWriter writer) {
    this.reader = reader;
    this.writer = writer;
  }

  public static void main(String[] args) {
    InputReader reader = new InputReader(System.in);
    OutputWriter writer = new OutputWriter(System.out);
    new CleaningApartment(reader, writer).run();
    writer.writer.flush();
  }

  class Edge {

    int from;
    int to;

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Edge edge = (Edge) o;
      return from == edge.from &&
          to == edge.to;
    }

    @Override
    public int hashCode() {
      return from * 31 + to;
    }
  }

  class ConvertHampathToSat {

    int numVertices;
    Edge[] edges;

    ConvertHampathToSat(int n, int m) {
      numVertices = n;
      edges = new Edge[m];
      for (int i = 0; i < m; ++i) {
        edges[i] = new Edge();
      }
    }

    int varnum(int node, int pos) {
      return node * numVertices + pos + 1;
    }

    List<String> vortexInPathExactlyOnce(int vortex) {
      List<String> result = new ArrayList<String>();
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < numVertices; i++) {
        builder.append(varnum(vortex, i));
        builder.append(" ");
      }
      builder.append("0\n");
      result.add(builder.toString());
      for (int i = 0; i < numVertices; i++) {
        for (int j = i + 1; j < numVertices; j++) {
          result.add(String.format("%d %d 0\n", -varnum(vortex, i), -varnum(vortex, j)));
        }
      }
      return result;
    }

    List<String> positionOccupiedExactlyOnce(int pos) {
      List<String> result = new ArrayList<String>();
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < numVertices; i++) {
        builder.append(varnum(i, pos));
        builder.append(" ");
      }
      builder.append("0\n");
      result.add(builder.toString());
      for (int i = 0; i < numVertices; i++) {
        for (int j = i + 1; j < numVertices; j++) {
          result.add(String.format("%d %d 0\n", -varnum(i, pos), -varnum(j, pos)));
        }
      }
      return result;
    }

    List<String> vortexInPath() {
      List<String> result = new ArrayList<String>();
      for (int i = 0; i < numVertices; i++) {
        result.addAll(vortexInPathExactlyOnce(i));
      }
      return result;
    }

    List<String> posOccupied() {
      List<String> result = new ArrayList<String>();
      for (int i = 0; i < numVertices; i++) {
        result.addAll(positionOccupiedExactlyOnce(i));
      }
      return result;
    }

    List<String> edgeNotContained(int from, int to) {
      List<String> result = new ArrayList<String>();
      for (int i = 0; i < numVertices - 1; i++) {
        result.add(String.format("%d %d 0\n", -varnum(from, i), -varnum(to, i + 1)));
      }
      return result;
    }

    List<String> notEdge() {
      Set<Edge> allEdges = new HashSet<Edge>();
      for (Edge edge : edges) {
        allEdges.add(edge);
        Edge reverse = new Edge();
        reverse.from = edge.to;
        reverse.to = edge.from;
        allEdges.add(reverse);
      }

      List<String> result = new ArrayList<String>();
      int count = 0;
      for (int i = 0; i < numVertices; i++) {
        for (int j = i + 1; j < numVertices; j++) {
          Edge me = new Edge();
          me.from = i + 1;
          me.to = j + 1;
          if (!allEdges.contains(me)) {
            count += 1;
            result.addAll(edgeNotContained(i, j));
            result.addAll(edgeNotContained(j, i));
          }
        }
      }
      // System.out.println("------------------");
      // System.out.println(count);
      return result;
    }

    void printEquisatisfiableSatFormula() {

      // This solution prints a simple satisfiable formula
      // and passes about half of the tests.
      // Change this function to solve the problem.
      // writer.printf("3 2\n");
      // writer.printf("1 2 0\n");
      // writer.printf("-1 -2 0\n");
      // writer.printf("1 -2 0\n")
      List<String> result = new ArrayList<String>();
      result.addAll(vortexInPath());
      result.addAll(posOccupied());
      result.addAll(notEdge());

      writer.printf("%d %d\n", result.size(), numVertices * numVertices);
      for (String clause : result) {
        writer.printf("%s", clause);
      }
    }
  }

  public void run() {
    int n = reader.nextInt();
    int m = reader.nextInt();

    ConvertHampathToSat converter = new ConvertHampathToSat(n, m);
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
