package week2.a2;

import java.io.*;
import java.util.*;


class PlanParty {

  static class Vertex {

    Vertex() {
      this.weight = 0;
      this.children = new ArrayList<Integer>();
    }

    int weight;
    ArrayList<Integer> children;
  }

  static Vertex[] ReadTree() throws IOException {
    InputStreamReader inputStream = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(inputStream);
    StreamTokenizer tokenizer = new StreamTokenizer(reader);

    tokenizer.nextToken();
    int verticesCount = (int) tokenizer.nval;

    Vertex[] tree = new Vertex[verticesCount];

    for (int i = 0; i < verticesCount; ++i) {
      tree[i] = new Vertex();
      tokenizer.nextToken();
      tree[i].weight = (int) tokenizer.nval;
    }

    for (int i = 1; i < verticesCount; ++i) {
      tokenizer.nextToken();
      int from = (int) tokenizer.nval;
      tokenizer.nextToken();
      int to = (int) tokenizer.nval;
      tree[from - 1].children.add(to - 1);
      tree[to - 1].children.add(from - 1);
    }

    return tree;
  }

  static void dfs(Vertex[] tree, int vertex, int parent, int[] fun) {
    if (fun[vertex] != 0) {
      return;
    }
    int sum1 = 0;
    for (int child : tree[vertex].children) {
      if (child != parent) {
        dfs(tree, child, vertex, fun);
        sum1 += fun[child];
      }
    }

    int sum2 = tree[vertex].weight;
    for (int child : tree[vertex].children) {
      if (child == parent) {
        continue;
      }
      for (int grandChild : tree[child].children) {
        if (grandChild == vertex) {
          continue;
        }
        sum2 += fun[grandChild];
      }
    }

    fun[vertex] = Math.max(sum1, sum2);

    // This is a template function for processing a tree using depth-first search.
    // Write your code here.
    // You may need to add more parameters to this function for child processing.
  }

  static int MaxWeightIndependentTreeSubset(Vertex[] tree) {
    int size = tree.length;
    if (size == 0) {
      return 0;
    }
    int[] fun = new int[size];
    dfs(tree, 0, -1, fun);

    // You must decide what to return.
    return fun[0];
  }

  public static void main(String[] args) throws IOException {
    // This is to avoid stack overflow issues
    new Thread(null, new Runnable() {
      public void run() {
        try {
          new PlanParty().run();
        } catch (IOException e) {
        }
      }
    }, "1", 1 << 26).start();
  }

  public void run() throws IOException {
    Vertex[] tree = ReadTree();
    int weight = MaxWeightIndependentTreeSubset(tree);
    System.out.println(weight);
  }
}
