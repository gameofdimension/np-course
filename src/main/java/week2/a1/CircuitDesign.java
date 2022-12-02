package week2.a1;

// import edu.princeton.cs.algs4.Digraph;
// import edu.princeton.cs.algs4.KosarajuSharirSCC;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

class CircuitDesign {

  private final InputReader reader;
  private final OutputWriter writer;

  public CircuitDesign(InputReader reader, OutputWriter writer) {
    this.reader = reader;
    this.writer = writer;
  }

  public static void main(String[] args) {
    // InputReader reader = new InputReader(System.in);
    // OutputWriter writer = new OutputWriter(System.out);
    // new CircuitDesign(reader, writer).run();
    // writer.writer.flush();

    new Thread(null, new Runnable() {
      public void run() {
        InputReader reader = new InputReader(System.in);
        OutputWriter writer = new OutputWriter(System.out);
        new CircuitDesign(reader, writer).run();
        writer.writer.flush();
      }
    }, "1", 1 << 26).start();
  }

  class Clause {

    int firstVar;
    int secondVar;
  }

  class TwoSatisfiability {

    Map<String, Integer> st = new HashMap<>();  // string -> index
    String[] keys;           // index  -> string

    int numVars;
    Clause[] clauses;

    TwoSatisfiability(int n, int m) {
      numVars = n;
      clauses = new Clause[m];
      for (int i = 0; i < m; ++i) {
        clauses[i] = new Clause();
      }
    }

    String nameOf(int v) {
      return keys[v];
    }

    int indexOf(String s) {
      return st.get(s);
    }

    boolean isSatisfiableNaive(int[] result) {
      // This solution tries all possible 2^n variable assignments.
      // It is too slow to pass the problem.
      // Implement a more efficient algorithm here.
      for (int mask = 0; mask < (1 << numVars); ++mask) {
        for (int i = 0; i < numVars; ++i) {
          result[i] = (mask >> i) & 1;
        }

        boolean formulaIsSatisfied = true;

        for (Clause clause : clauses) {
          boolean clauseIsSatisfied = false;
          if ((result[Math.abs(clause.firstVar) - 1] == 1) == (clause.firstVar < 0)) {
            clauseIsSatisfied = true;
          }
          if ((result[Math.abs(clause.secondVar) - 1] == 1) == (clause.secondVar < 0)) {
            clauseIsSatisfied = true;
          }
          if (!clauseIsSatisfied) {
            formulaIsSatisfied = false;
            break;
          }
        }

        if (formulaIsSatisfied) {
          return true;
        }
      }
      return false;
    }

    boolean isSatisfiable(Digraph digraph, int[] result) {
      KosarajuSharirSCC kosarajuSharirSCC = new KosarajuSharirSCC(digraph);
      for (int i = 1; i <= numVars; i++) {
        int v = indexOf(String.valueOf(i));
        int nv = indexOf(String.valueOf(-i));
        if (kosarajuSharirSCC.stronglyConnected(v, nv)) {
          // System.out.println(i);
          // System.out.println(kosarajuSharirSCC.id(v));
          // System.out.println(kosarajuSharirSCC.id(nv));
          // for (int k = 0; k < digraph.V; k++) {
          //   if (kosarajuSharirSCC.id(k) == 156717) {
          //     System.out.println(k + " " + nameOf(k));
          //   }
          // }
          return false;
        }
      }

      List<Set<Integer>> sccToV = new ArrayList<>();
      for (int i = 0; i < kosarajuSharirSCC.count(); i++) {
        sccToV.add(new HashSet<>());
      }
      for (int v = 0; v < digraph.V(); v++) {
        sccToV.get(kosarajuSharirSCC.id(v)).add(v);
      }

      for (int i = 0; i < kosarajuSharirSCC.count(); i++) {
        for (Integer v : sccToV.get(i)) {
          String literal = nameOf(v);
          int val = Integer.parseInt(literal);
          if (val == 0) {
            throw new IllegalArgumentException("wrong literal " + literal);
          }

          int idx = Math.abs(val) - 1;
          if (result[idx] == 0) {
            result[idx] = val;
          }
        }
      }

      // for (int i = 0; i < result.length; i++) {
      //   if (result[i] == 0) {
      //     throw new IllegalArgumentException("not assigned " + i);
      //   }
      // }

      return true;
    }

    void addNode(String from) {
      if (!st.containsKey(from)) {
        st.put(from, st.size());
      }
      // if (!st.containsKey(to)) {
      //   st.put(to, st.size());
      // }
    }

    void addEdge(Digraph digraph, String from, String to) {
      digraph.addEdge(st.get(from), st.get(to));
    }

    Digraph makeDigraph(int n, int m, Clause[] clauses) {
      Digraph digraph = new Digraph(2 * n);
      // for (Clause clause : clauses) {
      //   addNode(String.valueOf(clause.firstVar));
      //   addNode(String.valueOf(-clause.firstVar));
      //   addNode(String.valueOf(clause.secondVar));
      //   addNode(String.valueOf(-clause.secondVar));
      // }
      for (int i = 1; i <= n; i++) {
        addNode(String.valueOf(i));
        addNode(String.valueOf(-i));
      }
      keys = new String[st.size()];
      for (String name : st.keySet()) {
        keys[st.get(name)] = name;
      }

      for (Clause clause : clauses) {
        addEdge(digraph, String.valueOf(-clause.firstVar), String.valueOf(clause.secondVar));
        addEdge(digraph, String.valueOf(-clause.secondVar), String.valueOf(clause.firstVar));
      }
      for (int i = 1; i <= n; i++) {
        addEdge(digraph, String.valueOf(i), String.valueOf(i));
        addEdge(digraph, String.valueOf(-i), String.valueOf(-i));
      }
      return digraph;
    }
  }

  public void run() {
    int n = reader.nextInt();
    int m = reader.nextInt();

    TwoSatisfiability twoSat = new TwoSatisfiability(n, m);
    for (int i = 0; i < m; ++i) {
      twoSat.clauses[i].firstVar = reader.nextInt();
      twoSat.clauses[i].secondVar = reader.nextInt();
    }

    Digraph digraph = twoSat.makeDigraph(n, m, twoSat.clauses);

    int result[] = new int[n];
    if (twoSat.isSatisfiable(digraph, result)) {
      writer.printf("SATISFIABLE\n");
      for (int i = 1; i <= n; ++i) {
        if (result[i - 1] > 0) {
          writer.printf("%d", i);
        } else if (result[i - 1] == 0) {
          writer.printf("%d", i);
        } else {
          writer.printf("%d", -i);
        }
        if (i < n) {
          writer.printf(" ");
        } else {
          writer.printf("\n");
        }
      }
    } else {
      writer.printf("UNSATISFIABLE\n");
    }
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

  /**
   * @author yzq, yzq
   * @date 2020-11-22.
   */
  public static class Bag<Item> implements Iterable<Item> {

    private Node<Item> first;    // beginning of bag
    private int n;               // number of elements in bag

    // helper linked list class
    private static class Node<Item> {

      private Item item;
      private Node<Item> next;
    }

    /**
     * Initializes an empty bag.
     */
    public Bag() {
      first = null;
      n = 0;
    }

    /**
     * Returns true if this bag is empty.
     *
     * @return {@code true} if this bag is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
      return first == null;
    }

    /**
     * Returns the number of items in this bag.
     *
     * @return the number of items in this bag
     */
    public int size() {
      return n;
    }

    /**
     * Adds the item to this bag.
     *
     * @param item the item to add to this bag
     */
    public void add(Item item) {
      Node<Item> oldfirst = first;
      first = new Node<Item>();
      first.item = item;
      first.next = oldfirst;
      n++;
    }


    /**
     * Returns an iterator that iterates over the items in this bag in arbitrary order.
     *
     * @return an iterator that iterates over the items in this bag in arbitrary order
     */
    public Iterator<Item> iterator() {
      return new ListIterator<Item>(first);
    }

    // an iterator, doesn't implement remove() since it's optional
    private class ListIterator<Item> implements Iterator<Item> {

      private Node<Item> current;

      public ListIterator(Node<Item> first) {
        current = first;
      }

      public boolean hasNext() {
        return current != null;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }

      public Item next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        Item item = current.item;
        current = current.next;
        return item;
      }
    }

    /**
     * Unit tests the {@code Bag} data type.
     *
     * @param args the command-line arguments
     */
    // public static void main(String[] args) {
    //   Bag<String> bag = new Bag<String>();
    //   while (!StdIn.isEmpty()) {
    //     String item = StdIn.readString();
    //     bag.add(item);
    //   }
    //
    //   StdOut.println("size of bag = " + bag.size());
    //   for (String s : bag) {
    //     StdOut.println(s);
    //   }
    // }

  }

  /**
   * @author yzq, yzq
   * @date 2020-11-22.
   */
  public static class DepthFirstOrder {

    private boolean[] marked;          // marked[v] = has v been marked in dfs?
    private int[] pre;                 // pre[v]    = preorder  number of v
    private int[] post;                // post[v]   = postorder number of v
    private Queue<Integer> preorder;   // vertices in preorder
    private Queue<Integer> postorder;  // vertices in postorder
    private int preCounter;            // counter or preorder numbering
    private int postCounter;           // counter for postorder numbering

    /**
     * Determines a depth-first order for the digraph {@code G}.
     *
     * @param G the digraph
     */
    public DepthFirstOrder(Digraph G) {
      pre = new int[G.V()];
      post = new int[G.V()];
      postorder = new Queue<Integer>();
      preorder = new Queue<Integer>();
      marked = new boolean[G.V()];
      for (int v = 0; v < G.V(); v++) {
        if (!marked[v]) {
          dfs(G, v);
        }
      }

      assert check();
    }

    /**
     * Determines a depth-first order for the edge-weighted digraph {@code G}.
     *
     * @param G the edge-weighted digraph
     */
    public DepthFirstOrder(EdgeWeightedDigraph G) {
      pre = new int[G.V()];
      post = new int[G.V()];
      postorder = new Queue<Integer>();
      preorder = new Queue<Integer>();
      marked = new boolean[G.V()];
      for (int v = 0; v < G.V(); v++) {
        if (!marked[v]) {
          dfs(G, v);
        }
      }
    }

    // run DFS in digraph G from vertex v and compute preorder/postorder
    private void dfs(Digraph G, int v) {
      marked[v] = true;
      pre[v] = preCounter++;
      preorder.enqueue(v);
      for (int w : G.adj(v)) {
        if (!marked[w]) {
          dfs(G, w);
        }
      }
      postorder.enqueue(v);
      post[v] = postCounter++;
    }

    // run DFS in edge-weighted digraph G from vertex v and compute preorder/postorder
    private void dfs(EdgeWeightedDigraph G, int v) {
      marked[v] = true;
      pre[v] = preCounter++;
      preorder.enqueue(v);
      for (DirectedEdge e : G.adj(v)) {
        int w = e.to();
        if (!marked[w]) {
          dfs(G, w);
        }
      }
      postorder.enqueue(v);
      post[v] = postCounter++;
    }

    /**
     * Returns the preorder number of vertex {@code v}.
     *
     * @param v the vertex
     * @return the preorder number of vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int pre(int v) {
      validateVertex(v);
      return pre[v];
    }

    /**
     * Returns the postorder number of vertex {@code v}.
     *
     * @param v the vertex
     * @return the postorder number of vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int post(int v) {
      validateVertex(v);
      return post[v];
    }

    /**
     * Returns the vertices in postorder.
     *
     * @return the vertices in postorder, as an iterable of vertices
     */
    public Iterable<Integer> post() {
      return postorder;
    }

    /**
     * Returns the vertices in preorder.
     *
     * @return the vertices in preorder, as an iterable of vertices
     */
    public Iterable<Integer> pre() {
      return preorder;
    }

    /**
     * Returns the vertices in reverse postorder.
     *
     * @return the vertices in reverse postorder, as an iterable of vertices
     */
    public Iterable<Integer> reversePost() {
      Stack<Integer> reverse = new Stack<Integer>();
      for (int v : postorder) {
        reverse.push(v);
      }
      return reverse;
    }


    // check that pre() and post() are consistent with pre(v) and post(v)
    private boolean check() {

      // check that post(v) is consistent with post()
      int r = 0;
      for (int v : post()) {
        if (post(v) != r) {
          // StdOut.println("post(v) and post() inconsistent");
          return false;
        }
        r++;
      }

      // check that pre(v) is consistent with pre()
      r = 0;
      for (int v : pre()) {
        if (pre(v) != r) {
          // StdOut.println("pre(v) and pre() inconsistent");
          return false;
        }
        r++;
      }

      return true;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
      int V = marked.length;
      if (v < 0 || v >= V) {
        throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
      }
    }

    /**
     * Unit tests the {@code DepthFirstOrder} data type.
     *
     * @param args the command-line arguments
     */
    // public static void main(String[] args) {
    //   In in = new In(args[0]);
    //   Digraph G = new Digraph(in);
    //
    //   DepthFirstOrder dfs = new DepthFirstOrder(G);
    //   StdOut.println("   v  pre post");
    //   StdOut.println("--------------");
    //   for (int v = 0; v < G.V(); v++) {
    //     StdOut.printf("%4d %4d %4d\n", v, dfs.pre(v), dfs.post(v));
    //   }
    //
    //   StdOut.print("Preorder:  ");
    //   for (int v : dfs.pre()) {
    //     StdOut.print(v + " ");
    //   }
    //   StdOut.println();
    //
    //   StdOut.print("Postorder: ");
    //   for (int v : dfs.post()) {
    //     StdOut.print(v + " ");
    //   }
    //   StdOut.println();
    //
    //   StdOut.print("Reverse postorder: ");
    //   for (int v : dfs.reversePost()) {
    //     StdOut.print(v + " ");
    //   }
    //   StdOut.println();
    //
    //
    // }

  }

  /**
   * @author yzq, yzq
   * @date 2020-11-22.
   */
  public static class Digraph {

    private static final String NEWLINE = System.getProperty("line.separator");

    private final int V;           // number of vertices in this digraph
    private int E;                 // number of edges in this digraph
    private Bag<Integer>[] adj;    // adj[v] = adjacency list for vertex v
    private int[] indegree;        // indegree[v] = indegree of vertex v

    /**
     * Initializes an empty digraph with <em>V</em> vertices.
     *
     * @param V the number of vertices
     * @throws IllegalArgumentException if {@code V < 0}
     */
    public Digraph(int V) {
      if (V < 0) {
        throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
      }
      this.V = V;
      this.E = 0;
      indegree = new int[V];
      adj = (Bag<Integer>[]) new Bag[V];
      for (int v = 0; v < V; v++) {
        adj[v] = new Bag<Integer>();
      }
    }

    /**
     * Initializes a digraph from the specified input stream.
     * The format is the number of vertices <em>V</em>,
     * followed by the number of edges <em>E</em>,
     * followed by <em>E</em> pairs of vertices, with each entry separated by whitespace.
     *
     * @param  in the input stream
     * @throws IllegalArgumentException if the endpoints of any edge are not in prescribed range
     * @throws IllegalArgumentException if the number of vertices or edges is negative
     * @throws IllegalArgumentException if the input stream is in the wrong format
     */
    // public Digraph(In in) {
    //   try {
    //     this.V = in.readInt();
    //     if (V < 0) throw new IllegalArgumentException("number of vertices in a Digraph must be nonnegative");
    //     indegree = new int[V];
    //     adj = (Bag<Integer>[]) new Bag[V];
    //     for (int v = 0; v < V; v++) {
    //       adj[v] = new Bag<Integer>();
    //     }
    //     int E = in.readInt();
    //     if (E < 0) throw new IllegalArgumentException("number of edges in a Digraph must be nonnegative");
    //     for (int i = 0; i < E; i++) {
    //       int v = in.readInt();
    //       int w = in.readInt();
    //       addEdge(v, w);
    //     }
    //   }
    //   catch (NoSuchElementException e) {
    //     throw new IllegalArgumentException("invalid input format in Digraph constructor", e);
    //   }
    // }

    /**
     * Initializes a new digraph that is a deep copy of the specified digraph.
     *
     * @param G the digraph to copy
     */
    public Digraph(Digraph G) {
      this(G.V());
      this.E = G.E();
      for (int v = 0; v < V; v++) {
        this.indegree[v] = G.indegree(v);
      }
      for (int v = 0; v < G.V(); v++) {
        // reverse so that adjacency list is in same order as original
        Stack<Integer> reverse = new Stack<Integer>();
        for (int w : G.adj[v]) {
          reverse.push(w);
        }
        for (int w : reverse) {
          adj[v].add(w);
        }
      }
    }

    /**
     * Returns the number of vertices in this digraph.
     *
     * @return the number of vertices in this digraph
     */
    public int V() {
      return V;
    }

    /**
     * Returns the number of edges in this digraph.
     *
     * @return the number of edges in this digraph
     */
    public int E() {
      return E;
    }


    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
      if (v < 0 || v >= V) {
        throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
      }
    }

    /**
     * Adds the directed edge v→w to this digraph.
     *
     * @param v the tail vertex
     * @param w the head vertex
     * @throws IllegalArgumentException unless both {@code 0 <= v < V} and {@code 0 <= w < V}
     */
    public void addEdge(int v, int w) {
      validateVertex(v);
      validateVertex(w);
      adj[v].add(w);
      indegree[w]++;
      E++;
    }

    /**
     * Returns the vertices adjacent from vertex {@code v} in this digraph.
     *
     * @param v the vertex
     * @return the vertices adjacent from vertex {@code v} in this digraph, as an iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<Integer> adj(int v) {
      validateVertex(v);
      return adj[v];
    }

    /**
     * Returns the number of directed edges incident from vertex {@code v}. This is known as the
     * <em>outdegree</em> of vertex {@code v}.
     *
     * @param v the vertex
     * @return the outdegree of vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int outdegree(int v) {
      validateVertex(v);
      return adj[v].size();
    }

    /**
     * Returns the number of directed edges incident to vertex {@code v}. This is known as the
     * <em>indegree</em> of vertex {@code v}.
     *
     * @param v the vertex
     * @return the indegree of vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int indegree(int v) {
      validateVertex(v);
      return indegree[v];
    }

    /**
     * Returns the reverse of the digraph.
     *
     * @return the reverse of the digraph
     */
    public Digraph reverse() {
      Digraph reverse = new Digraph(V);
      for (int v = 0; v < V; v++) {
        for (int w : adj(v)) {
          reverse.addEdge(w, v);
        }
      }
      return reverse;
    }

    /**
     * Returns a string representation of the graph.
     *
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     * followed by the <em>V</em> adjacency lists
     */
    public String toString() {
      StringBuilder s = new StringBuilder();
      s.append(V + " vertices, " + E + " edges " + NEWLINE);
      for (int v = 0; v < V; v++) {
        s.append(String.format("%d: ", v));
        for (int w : adj[v]) {
          s.append(String.format("%d ", w));
        }
        s.append(NEWLINE);
      }
      return s.toString();
    }

    /**
     * Unit tests the {@code Digraph} data type.
     *
     * @param args the command-line arguments
     */
    // public static void main(String[] args) {
    //   In in = new In(args[0]);
    //   Digraph G = new Digraph(in);
    //   StdOut.println(G);
    // }

  }

  /**
   * @author yzq, yzq
   * @date 2020-11-22.
   */
  public static class DirectedEdge {

    private final int v;
    private final int w;
    private final double weight;

    /**
     * Initializes a directed edge from vertex {@code v} to vertex {@code w} with the given {@code
     * weight}.
     *
     * @param v the tail vertex
     * @param w the head vertex
     * @param weight the weight of the directed edge
     * @throws IllegalArgumentException if either {@code v} or {@code w} is a negative integer
     * @throws IllegalArgumentException if {@code weight} is {@code NaN}
     */
    public DirectedEdge(int v, int w, double weight) {
      if (v < 0) {
        throw new IllegalArgumentException("Vertex names must be nonnegative integers");
      }
      if (w < 0) {
        throw new IllegalArgumentException("Vertex names must be nonnegative integers");
      }
      if (Double.isNaN(weight)) {
        throw new IllegalArgumentException("Weight is NaN");
      }
      this.v = v;
      this.w = w;
      this.weight = weight;
    }

    /**
     * Returns the tail vertex of the directed edge.
     *
     * @return the tail vertex of the directed edge
     */
    public int from() {
      return v;
    }

    /**
     * Returns the head vertex of the directed edge.
     *
     * @return the head vertex of the directed edge
     */
    public int to() {
      return w;
    }

    /**
     * Returns the weight of the directed edge.
     *
     * @return the weight of the directed edge
     */
    public double weight() {
      return weight;
    }

    /**
     * Returns a string representation of the directed edge.
     *
     * @return a string representation of the directed edge
     */
    public String toString() {
      return v + "->" + w + " " + String.format("%5.2f", weight);
    }

    /**
     * Unit tests the {@code DirectedEdge} data type.
     *
     * @param args the command-line arguments
     */
    // public static void main(String[] args) {
    //   DirectedEdge e = new DirectedEdge(12, 34, 5.67);
    //   StdOut.println(e);
    // }
  }

  /**
   * @author yzq, yzq
   * @date 2020-11-22.
   */
  public static class EdgeWeightedDigraph {

    private static final String NEWLINE = System.getProperty("line.separator");

    private final int V;                // number of vertices in this digraph
    private int E;                      // number of edges in this digraph
    private Bag<DirectedEdge>[] adj;    // adj[v] = adjacency list for vertex v
    private int[] indegree;             // indegree[v] = indegree of vertex v

    /**
     * Initializes an empty edge-weighted digraph with {@code V} vertices and 0 edges.
     *
     * @param V the number of vertices
     * @throws IllegalArgumentException if {@code V < 0}
     */
    public EdgeWeightedDigraph(int V) {
      if (V < 0) {
        throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
      }
      this.V = V;
      this.E = 0;
      this.indegree = new int[V];
      adj = (Bag<DirectedEdge>[]) new Bag[V];
      for (int v = 0; v < V; v++) {
        adj[v] = new Bag<DirectedEdge>();
      }
    }

    /**
     * Initializes a random edge-weighted digraph with {@code V} vertices and <em>E</em> edges.
     *
     * @param  V the number of vertices
     * @param  E the number of edges
     * @throws IllegalArgumentException if {@code V < 0}
     * @throws IllegalArgumentException if {@code E < 0}
     */
    // public EdgeWeightedDigraph(int V, int E) {
    //   this(V);
    //   if (E < 0) throw new IllegalArgumentException("Number of edges in a Digraph must be nonnegative");
    //   for (int i = 0; i < E; i++) {
    // int v = StdRandom.uniform(V);
    // int w = StdRandom.uniform(V);
    // double weight = 0.01 * StdRandom.uniform(100);
    // DirectedEdge e = new DirectedEdge(v, w, weight);
    // addEdge(e);
    // }
    // }

    /**
     * Initializes an edge-weighted digraph from the specified input stream.
     * The format is the number of vertices <em>V</em>,
     * followed by the number of edges <em>E</em>,
     * followed by <em>E</em> pairs of vertices and edge weights,
     * with each entry separated by whitespace.
     *
     * @param  in the input stream
     * @throws IllegalArgumentException if the endpoints of any edge are not in prescribed range
     * @throws IllegalArgumentException if the number of vertices or edges is negative
     */
    // public EdgeWeightedDigraph(In in) {
    //   this(in.readInt());
    //   int E = in.readInt();
    //   if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
    //   for (int i = 0; i < E; i++) {
    //     int v = in.readInt();
    //     int w = in.readInt();
    //     validateVertex(v);
    //     validateVertex(w);
    //     double weight = in.readDouble();
    //     addEdge(new DirectedEdge(v, w, weight));
    //   }
    // }

    /**
     * Initializes a new edge-weighted digraph that is a deep copy of {@code G}.
     *
     * @param G the edge-weighted digraph to copy
     */
    public EdgeWeightedDigraph(EdgeWeightedDigraph G) {
      this(G.V());
      this.E = G.E();
      for (int v = 0; v < G.V(); v++) {
        this.indegree[v] = G.indegree(v);
      }
      for (int v = 0; v < G.V(); v++) {
        // reverse so that adjacency list is in same order as original
        Stack<DirectedEdge> reverse = new Stack<DirectedEdge>();
        for (DirectedEdge e : G.adj[v]) {
          reverse.push(e);
        }
        for (DirectedEdge e : reverse) {
          adj[v].add(e);
        }
      }
    }

    /**
     * Returns the number of vertices in this edge-weighted digraph.
     *
     * @return the number of vertices in this edge-weighted digraph
     */
    public int V() {
      return V;
    }

    /**
     * Returns the number of edges in this edge-weighted digraph.
     *
     * @return the number of edges in this edge-weighted digraph
     */
    public int E() {
      return E;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
      if (v < 0 || v >= V) {
        throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
      }
    }

    /**
     * Adds the directed edge {@code e} to this edge-weighted digraph.
     *
     * @param e the edge
     * @throws IllegalArgumentException unless endpoints of edge are between {@code 0} and {@code
     * V-1}
     */
    public void addEdge(DirectedEdge e) {
      int v = e.from();
      int w = e.to();
      validateVertex(v);
      validateVertex(w);
      adj[v].add(e);
      indegree[w]++;
      E++;
    }


    /**
     * Returns the directed edges incident from vertex {@code v}.
     *
     * @param v the vertex
     * @return the directed edges incident from vertex {@code v} as an Iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<DirectedEdge> adj(int v) {
      validateVertex(v);
      return adj[v];
    }

    /**
     * Returns the number of directed edges incident from vertex {@code v}. This is known as the
     * <em>outdegree</em> of vertex {@code v}.
     *
     * @param v the vertex
     * @return the outdegree of vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int outdegree(int v) {
      validateVertex(v);
      return adj[v].size();
    }

    /**
     * Returns the number of directed edges incident to vertex {@code v}. This is known as the
     * <em>indegree</em> of vertex {@code v}.
     *
     * @param v the vertex
     * @return the indegree of vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int indegree(int v) {
      validateVertex(v);
      return indegree[v];
    }

    /**
     * Returns all directed edges in this edge-weighted digraph. To iterate over the edges in this
     * edge-weighted digraph, use foreach notation: {@code for (DirectedEdge e : G.edges())}.
     *
     * @return all edges in this edge-weighted digraph, as an iterable
     */
    public Iterable<DirectedEdge> edges() {
      Bag<DirectedEdge> list = new Bag<DirectedEdge>();
      for (int v = 0; v < V; v++) {
        for (DirectedEdge e : adj(v)) {
          list.add(e);
        }
      }
      return list;
    }

    /**
     * Returns a string representation of this edge-weighted digraph.
     *
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     * followed by the <em>V</em> adjacency lists of edges
     */
    public String toString() {
      StringBuilder s = new StringBuilder();
      s.append(V + " " + E + NEWLINE);
      for (int v = 0; v < V; v++) {
        s.append(v + ": ");
        for (DirectedEdge e : adj[v]) {
          s.append(e + "  ");
        }
        s.append(NEWLINE);
      }
      return s.toString();
    }

    /**
     * Unit tests the {@code EdgeWeightedDigraph} data type.
     *
     * @param args the command-line arguments
     */
    // public static void main(String[] args) {
    //   In in = new In(args[0]);
    //   EdgeWeightedDigraph G = new EdgeWeightedDigraph(in);
    //   StdOut.println(G);
    // }

  }

  /**
   * @author yzq, yzq
   * @date 2020-11-22.
   */
  public static class KosarajuSharirSCC {

    private boolean[] marked;     // marked[v] = has vertex v been visited?
    private int[] id;             // id[v] = id of strong component containing v
    private int count;            // number of strongly-connected components

    /**
     * Computes the strong components of the digraph {@code G}.
     *
     * @param G the digraph
     */
    public KosarajuSharirSCC(Digraph G) {

      // compute reverse postorder of reverse graph
      DepthFirstOrder dfs = new DepthFirstOrder(G.reverse());

      // run DFS on G, using reverse postorder to guide calculation
      marked = new boolean[G.V()];
      id = new int[G.V()];
      for (int v : dfs.reversePost()) {
        if (!marked[v]) {
          dfs(G, v);
          count++;
        }
      }

      // check that id[] gives strong components
      // assert check(G);
    }

    // DFS on graph G
    private void dfs(Digraph G, int v) {
      marked[v] = true;
      id[v] = count;
      for (int w : G.adj(v)) {
        if (!marked[w]) {
          dfs(G, w);
        }
      }
    }

    /**
     * Returns the number of strong components.
     *
     * @return the number of strong components
     */
    public int count() {
      return count;
    }

    /**
     * Are vertices {@code v} and {@code w} in the same strong component?
     *
     * @param v one vertex
     * @param w the other vertex
     * @return {@code true} if vertices {@code v} and {@code w} are in the same strong component,
     * and {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     * @throws IllegalArgumentException unless {@code 0 <= w < V}
     */
    public boolean stronglyConnected(int v, int w) {
      validateVertex(v);
      validateVertex(w);
      return id[v] == id[w];
    }

    /**
     * Returns the component id of the strong component containing vertex {@code v}.
     *
     * @param v the vertex
     * @return the component id of the strong component containing vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public int id(int v) {
      validateVertex(v);
      return id[v];
    }

    // does the id[] array contain the strongly connected components?
    // private boolean check(Digraph G) {
    //   TransitiveClosure tc = new TransitiveClosure(G);
    //   for (int v = 0; v < G.V(); v++) {
    //     for (int w = 0; w < G.V(); w++) {
    //       if (stronglyConnected(v, w) != (tc.reachable(v, w) && tc.reachable(w, v)))
    //         return false;
    //     }
    //   }
    //   return true;
    // }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
      int V = marked.length;
      if (v < 0 || v >= V) {
        throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
      }
    }

    /**
     * Unit tests the {@code KosarajuSharirSCC} data type.
     *
     * @param args the command-line arguments
     */
    // public static void main(String[] args) {
    //   In in = new In(args[0]);
    //   Digraph G = new Digraph(in);
    //   KosarajuSharirSCC scc = new KosarajuSharirSCC(G);
    //
    //   // number of connected components
    //   int m = scc.count();
    //   StdOut.println(m + " strong components");
    //
    //   // compute list of vertices in each strong component
    //   Queue<Integer>[] components = (Queue<Integer>[]) new Queue[m];
    //   for (int i = 0; i < m; i++) {
    //     components[i] = new Queue<Integer>();
    //   }
    //   for (int v = 0; v < G.V(); v++) {
    //     components[scc.id(v)].enqueue(v);
    //   }
    //
    //   // print results
    //   for (int i = 0; i < m; i++) {
    //     for (int v : components[i]) {
    //       StdOut.print(v + " ");
    //     }
    //     StdOut.println();
    //   }
    //
    // }

  }

  /**
   * @author yzq, yzq
   * @date 2020-11-22.
   */
  public static class Queue<Item> implements Iterable<Item> {

    private Node<Item> first;    // beginning of queue
    private Node<Item> last;     // end of queue
    private int n;               // number of elements on queue

    // helper linked list class
    private static class Node<Item> {

      private Item item;
      private Node<Item> next;
    }

    /**
     * Initializes an empty queue.
     */
    public Queue() {
      first = null;
      last = null;
      n = 0;
    }

    /**
     * Returns true if this queue is empty.
     *
     * @return {@code true} if this queue is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
      return first == null;
    }

    /**
     * Returns the number of items in this queue.
     *
     * @return the number of items in this queue
     */
    public int size() {
      return n;
    }

    /**
     * Returns the item least recently added to this queue.
     *
     * @return the item least recently added to this queue
     * @throws NoSuchElementException if this queue is empty
     */
    public Item peek() {
      if (isEmpty()) {
        throw new NoSuchElementException("Queue underflow");
      }
      return first.item;
    }

    /**
     * Adds the item to this queue.
     *
     * @param item the item to add
     */
    public void enqueue(Item item) {
      Node<Item> oldlast = last;
      last = new Node<Item>();
      last.item = item;
      last.next = null;
      if (isEmpty()) {
        first = last;
      } else {
        oldlast.next = last;
      }
      n++;
    }

    /**
     * Removes and returns the item on this queue that was least recently added.
     *
     * @return the item on this queue that was least recently added
     * @throws NoSuchElementException if this queue is empty
     */
    public Item dequeue() {
      if (isEmpty()) {
        throw new NoSuchElementException("Queue underflow");
      }
      Item item = first.item;
      first = first.next;
      n--;
      if (isEmpty()) {
        last = null;   // to avoid loitering
      }
      return item;
    }

    /**
     * Returns a string representation of this queue.
     *
     * @return the sequence of items in FIFO order, separated by spaces
     */
    public String toString() {
      StringBuilder s = new StringBuilder();
      for (Item item : this) {
        s.append(item);
        s.append(' ');
      }
      return s.toString();
    }

    /**
     * Returns an iterator that iterates over the items in this queue in FIFO order.
     *
     * @return an iterator that iterates over the items in this queue in FIFO order
     */
    public Iterator<Item> iterator() {
      return new ListIterator<Item>(first);
    }

    // an iterator, doesn't implement remove() since it's optional
    private class ListIterator<Item> implements Iterator<Item> {

      private Node<Item> current;

      public ListIterator(Node<Item> first) {
        current = first;
      }

      public boolean hasNext() {
        return current != null;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }

      public Item next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        Item item = current.item;
        current = current.next;
        return item;
      }
    }

    /**
     * Unit tests the {@code Queue} data type.
     *
     * @param args the command-line arguments
     */
    // public static void main(String[] args) {
    //   Queue<String> queue = new Queue<String>();
    //   while (!StdIn.isEmpty()) {
    //     String item = StdIn.readString();
    //     if (!item.equals("-"))
    //       queue.enqueue(item);
    //     else if (!queue.isEmpty())
    //       StdOut.print(queue.dequeue() + " ");
    //   }
    //   StdOut.println("(" + queue.size() + " left on queue)");
    // }
  }

  /**
   * @author yzq, yzq
   * @date 2020-11-22.
   */
  public static class Stack<Item> implements Iterable<Item> {

    private Node<Item> first;     // top of stack
    private int n;                // size of the stack

    // helper linked list class
    private static class Node<Item> {

      private Item item;
      private Node<Item> next;
    }

    /**
     * Initializes an empty stack.
     */
    public Stack() {
      first = null;
      n = 0;
    }

    /**
     * Returns true if this stack is empty.
     *
     * @return true if this stack is empty; false otherwise
     */
    public boolean isEmpty() {
      return first == null;
    }

    /**
     * Returns the number of items in this stack.
     *
     * @return the number of items in this stack
     */
    public int size() {
      return n;
    }

    /**
     * Adds the item to this stack.
     *
     * @param item the item to add
     */
    public void push(Item item) {
      Node<Item> oldfirst = first;
      first = new Node<Item>();
      first.item = item;
      first.next = oldfirst;
      n++;
    }

    /**
     * Removes and returns the item most recently added to this stack.
     *
     * @return the item most recently added
     * @throws NoSuchElementException if this stack is empty
     */
    public Item pop() {
      if (isEmpty()) {
        throw new NoSuchElementException("Stack underflow");
      }
      Item item = first.item;        // save item to return
      first = first.next;            // delete first node
      n--;
      return item;                   // return the saved item
    }


    /**
     * Returns (but does not remove) the item most recently added to this stack.
     *
     * @return the item most recently added to this stack
     * @throws NoSuchElementException if this stack is empty
     */
    public Item peek() {
      if (isEmpty()) {
        throw new NoSuchElementException("Stack underflow");
      }
      return first.item;
    }

    /**
     * Returns a string representation of this stack.
     *
     * @return the sequence of items in this stack in LIFO order, separated by spaces
     */
    public String toString() {
      StringBuilder s = new StringBuilder();
      for (Item item : this) {
        s.append(item);
        s.append(' ');
      }
      return s.toString();
    }


    /**
     * Returns an iterator to this stack that iterates through the items in LIFO order.
     *
     * @return an iterator to this stack that iterates through the items in LIFO order
     */
    public Iterator<Item> iterator() {
      return new ListIterator<Item>(first);
    }

    // an iterator, doesn't implement remove() since it's optional
    private class ListIterator<Item> implements Iterator<Item> {

      private Node<Item> current;

      public ListIterator(Node<Item> first) {
        current = first;
      }

      public boolean hasNext() {
        return current != null;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }

      public Item next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        Item item = current.item;
        current = current.next;
        return item;
      }
    }

    /**
     * Unit tests the {@code Stack} data type.
     *
     * @param args the command-line arguments
     */
    // public static void main(String[] args) {
    //   Stack<String> stack = new Stack<String>();
    //   while (!StdIn.isEmpty()) {
    //     String item = StdIn.readString();
    //     if (!item.equals("-"))
    //       stack.push(item);
    //     else if (!stack.isEmpty())
    //       StdOut.print(stack.pop() + " ");
    //   }
    //   StdOut.println("(" + stack.size() + " left on stack)");
    // }
  }
}
