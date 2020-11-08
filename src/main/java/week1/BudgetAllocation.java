package week1;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class BudgetAllocation {

  private final InputReader reader;
  private final OutputWriter writer;

  public BudgetAllocation(InputReader reader, OutputWriter writer) {
    this.reader = reader;
    this.writer = writer;
  }

  public static void main(String[] args) {
    InputReader reader = new InputReader(System.in);
    OutputWriter writer = new OutputWriter(System.out);
    new BudgetAllocation(reader, writer).run();
    writer.writer.flush();
  }

  class SparseVector {

    List<Integer> index = new ArrayList<Integer>();
    List<Integer> coff = new ArrayList<Integer>();
  }

  class ConvertILPToSat {

    int[][] A;
    int[] b;

    ConvertILPToSat(int n, int m) {
      A = new int[n][m];
      b = new int[n];
    }

    SparseVector makeSparse(int[] coff) {
      SparseVector vector = new SparseVector();
      for (int i = 0; i < coff.length; i++) {
        if (coff[i] != 0) {
          vector.index.add(i);
          vector.coff.add(coff[i]);
        }
      }
      return vector;
    }

    boolean eof(int[] bits) {
      for (int i = 0; i < bits.length; i++) {
        if (bits[i] != 1) {
          return false;
        }
      }
      return true;
    }

    int[] next(int[] bits) {
      int idx = -1;
      for (int i = bits.length - 1; i >= 0; i--) {
        if (bits[i] == 0) {
          idx = i;
          break;
        }
      }
      bits[idx] = 1;
      for (int i = idx + 1; i < bits.length; i++) {
        bits[i] = 0;
      }
      return bits;
    }


    boolean violate(int[] bits, SparseVector vector, int bias) {
      int sum = 0;
      for (int i = 0; i < bits.length; i++) {
        sum += bits[i] * vector.coff.get(i);
      }
      // System.out.println("kkkkkkkk");
      // System.out.println(bias);
      if (sum > bias) {
        return true;
      }
      return false;
    }

    String clause(int[] bits, SparseVector vector) {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < bits.length; i++) {
        if (bits[i] == 1) {
          builder.append(-(vector.index.get(i) + 1));
        } else {
          builder.append(vector.index.get(i) + 1);
        }
        builder.append(" ");
      }
      builder.append("0\n");
      return builder.toString();
    }

    List<String> ineqClause(SparseVector vector, int bias) {
      int[] bits = new int[vector.coff.size()];
      List<String> result = new ArrayList<String>();
      // System.out.println("++++++++++++");
      // System.out.println(bias);
      while (true) {
        if (violate(bits, vector, bias)) {
          result.add(clause(bits, vector));
        }
        if (eof(bits)) {
          break;
        }

        bits = next(bits);
      }
      return result;
    }


    boolean emptyViolation(SparseVector[] ineq, int eqNum) {
      for (int i = 0; i < eqNum; i++) {
        if (ineq[i].coff.size() == 0 && b[i] < 0) {
          return true;
        }
      }
      return false;
    }

    List<String> computeClause(SparseVector[] ineq, int eqNum) {
      List<String> result = new ArrayList<String>();
      for (int i = 0; i < eqNum; i++) {
        SparseVector vector = ineq[i];
        int bias = b[i];
        result.addAll(ineqClause(vector, bias));
      }
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
      int eqNum = b.length;
      SparseVector[] ineq = new SparseVector[eqNum];
      for (int i = 0; i < eqNum; i++) {
        ineq[i] = makeSparse(A[i]);
      }

      if (emptyViolation(ineq, eqNum)) {
        result.add("1 0\n");
        result.add("-1 0\n");
        writer.printf("%d %d\n", result.size(), 1);
      } else {
        result = computeClause(ineq, eqNum);
        if (result.isEmpty()) {
          result.add("1 -1 0\n");
          writer.printf("%d %d\n", result.size(), 1);
        } else {
          // System.out.println("-------------");
          writer.printf("%d %d\n", result.size(), A[0].length);
        }
      }

      for (String clause : result) {
        writer.printf("%s", clause);
      }
    }
  }

  public void run() {
    int n = reader.nextInt();
    int m = reader.nextInt();

    ConvertILPToSat converter = new ConvertILPToSat(n, m);
    for (int i = 0; i < n; ++i) {
      for (int j = 0; j < m; ++j) {
        converter.A[i][j] = reader.nextInt();
      }
    }
    for (int i = 0; i < n; ++i) {
      converter.b[i] = reader.nextInt();
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
