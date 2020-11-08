package week2.a4;

import java.util.HashSet;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.Stack;

public class RescheduleExamsSearch {

  class Edge {

    int u, v;

    public Edge(int u, int v) {
      this.u = u;
      this.v = v;
    }
  }

  char[] idx2color = {'R', 'G', 'B'};
  int[] color2idx = new int[500];

  public RescheduleExamsSearch() {
    color2idx['R'] = 0;
    color2idx['G'] = 1;
    color2idx['B'] = 2;
  }


  char[] assignNewColors(int n, Edge[] edges, char[] colors) {

    Graph graph = new Graph(n);
    for (Edge edge : edges) {
      if (edge.u == edge.v) {
        return null;
      }
      graph.addEdge(edge.v - 1, edge.u - 1);
      graph.addEdge(edge.u - 1, edge.v - 1);
    }
    int[] plan = new int[n];
    for (int clr = 0; clr < 3; clr++) {
      if (color2idx[colors[0]] == clr) {
        continue;
      }
      plan[0] = clr;
      if (search(plan, 1, n, colors, graph)) {
        char[] res = new char[n];
        for (int i = 0; i < n; i++) {
          res[i] = idx2color[plan[i]];
        }
        return res;
      }
    }
    return null;
  }

  boolean checkFriend(int[] plan, int progress, int proposal, final Graph graph) {
    Set<Integer> adjs = graph.adjV(progress);
    if (adjs.contains(progress)) {
      return false;
    }
    if (progress > adjs.size()) {
      for (int adj : adjs) {
        if (adj < progress && plan[adj] == proposal) {
          return false;
        }
      }
    } else {
      for (int i = 0; i < progress; i++) {
        if (adjs.contains(i)) {
          if (plan[i] == proposal) {
            return false;
          }
        }
      }
    }
    return true;
  }

  boolean search(int[] plan, int progress, final int n, final char[] colors, final Graph graph) {
    if (progress >= n) {
      return true;
    }
    for (int clr = 0; clr < 3; clr++) {
      if (color2idx[colors[progress]] == clr) {
        continue;
      }

      if (!checkFriend(plan, progress, clr, graph)) {
        continue;
      }
      plan[progress] = clr;
      if (search(plan, progress + 1, n, colors, graph)) {
        return true;
      }
    }
    return false;
  }

  void run() {
    Scanner scanner = new Scanner(System.in);
    PrintWriter writer = new PrintWriter(System.out);

    int n = scanner.nextInt();
    int m = scanner.nextInt();
    scanner.nextLine();

    String colorsLine = scanner.nextLine();
    char[] colors = colorsLine.toCharArray();

    Edge[] edges = new Edge[m];
    for (int i = 0; i < m; i++) {
      int u = scanner.nextInt();
      int v = scanner.nextInt();
      edges[i] = new Edge(u, v);
    }

    char[] newColors = assignNewColors(n, edges, colors);

    if (newColors == null) {
      writer.println("Impossible");
    } else {
      writer.println(new String(newColors));
    }

    writer.close();
  }

  public static void main(String[] args) throws FileNotFoundException {
    new RescheduleExamsSearch().run();
  }

  /**
   * The {@code Graph} class represents an undirected graph of vertices named 0 through <em>V</em> –
   * 1. It supports the following two primary operations: add an edge to the graph, iterate over all
   * of the vertices adjacent to a vertex. It also provides methods for returning the number of
   * vertices <em>V</em> and the number of edges <em>E</em>. Parallel edges and self-loops are
   * permitted. By convention, a self-loop <em>v</em>-<em>v</em> appears in the adjacency list of
   * <em>v</em> twice and contributes two to the degree of <em>v</em>.
   * <p>
   * This implementation uses an adjacency-lists representation, which is a vertex-indexed array of
   * {@link Set} objects. All operations take constant time (in the worst case) except iterating
   * over the vertices adjacent to a given vertex, which takes time proportional to the number of
   * such vertices.
   * <p>
   * For additional documentation, see <a href="https://algs4.cs.princeton.edu/41graph">Section
   * 4.1</a> of <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
   *
   * @author Robert Sedgewick
   * @author Kevin Wayne
   */
  public static class Graph {

    private static final String NEWLINE = System.getProperty("line.separator");

    private final int V;
    private int E;
    private Set<Integer>[] adj;

    /**
     * Initializes an empty graph with {@code V} vertices and 0 edges. param V the number of
     * vertices
     *
     * @param V number of vertices
     * @throws IllegalArgumentException if {@code V < 0}
     */
    public Graph(int V) {
      if (V < 0) {
        throw new IllegalArgumentException("Number of vertices must be nonnegative");
      }
      this.V = V;
      this.E = 0;
      adj = (Set<Integer>[]) new Set[V];
      for (int v = 0; v < V; v++) {
        adj[v] = new HashSet<Integer>();
      }
    }

    /**
     * Initializes a graph from the specified input stream.
     * The format is the number of vertices <em>V</em>,
     * followed by the number of edges <em>E</em>,
     * followed by <em>E</em> pairs of vertices, with each entry separated by whitespace.
     *
     * @param  in the input stream
     * @throws IllegalArgumentException if the endpoints of any edge are not in prescribed range
     * @throws IllegalArgumentException if the number of vertices or edges is negative
     * @throws IllegalArgumentException if the input stream is in the wrong format
     */
    // public Graph(In in) {
    //   try {
    //     this.V = in.readInt();
    //     if (V < 0) throw new IllegalArgumentException("number of vertices in a Graph must be nonnegative");
    //     adj = (Bag<Integer>[]) new Bag[V];
    //     for (int v = 0; v < V; v++) {
    //       adj[v] = new Bag<Integer>();
    //     }
    //     int E = in.readInt();
    //     if (E < 0) throw new IllegalArgumentException("number of edges in a Graph must be nonnegative");
    //     for (int i = 0; i < E; i++) {
    //       int v = in.readInt();
    //       int w = in.readInt();
    //       validateVertex(v);
    //       validateVertex(w);
    //       addEdge(v, w);
    //     }
    //   }
    //   catch (NoSuchElementException e) {
    //     throw new IllegalArgumentException("invalid input format in Graph constructor", e);
    //   }
    // }


    /**
     * Initializes a new graph that is a deep copy of {@code G}.
     *
     * @param G the graph to copy
     */
    public Graph(Graph G) {
      this(G.V());
      this.E = G.E();
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
     * Returns the number of vertices in this graph.
     *
     * @return the number of vertices in this graph
     */
    public int V() {
      return V;
    }

    /**
     * Returns the number of edges in this graph.
     *
     * @return the number of edges in this graph
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
     * Adds the undirected edge v-w to this graph.
     *
     * @param v one vertex in the edge
     * @param w the other vertex in the edge
     * @throws IllegalArgumentException unless both {@code 0 <= v < V} and {@code 0 <= w < V}
     */
    public void addEdge(int v, int w) {
      validateVertex(v);
      validateVertex(w);
      E++;
      adj[v].add(w);
      adj[w].add(v);
    }


    /**
     * Returns the vertices adjacent to vertex {@code v}.
     *
     * @param v the vertex
     * @return the vertices adjacent to vertex {@code v}, as an iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<Integer> adj(int v) {
      validateVertex(v);
      return adj[v];
    }

    public Set<Integer> adjV(int v) {
      validateVertex(v);
      return adj[v];
    }

    /**
     * Returns the degree of vertex {@code v}.
     *
     * @param v the vertex
     * @return the degree of vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int degree(int v) {
      validateVertex(v);
      return adj[v].size();
    }


    /**
     * Returns a string representation of this graph.
     *
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     * followed by the <em>V</em> adjacency lists
     */
    public String toString() {
      StringBuilder s = new StringBuilder();
      s.append(V + " vertices, " + E + " edges " + NEWLINE);
      for (int v = 0; v < V; v++) {
        s.append(v + ": ");
        for (int w : adj[v]) {
          s.append(w + " ");
        }
        s.append(NEWLINE);
      }
      return s.toString();
    }

    /**
     * Unit tests the {@code Graph} data type.
     *
     * @param args the command-line arguments
     */
    // public static void main(String[] args) {
    //   In in = new In(args[0]);
    //   Graph G = new Graph(in);
    //   StdOut.println(G);
    // }

  }
}
