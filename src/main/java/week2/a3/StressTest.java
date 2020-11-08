package week2.a3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author yzq, yzq@leyantech.com
 * @date 2020-11-23.
 */
public class StressTest {

  public static void main(String[] args) {
    for (int i = 0; i < 100; i++) {
      int[][] graph = makeRandomGraph();
      System.out.println(i);
      SchoolBus.Answer answer1 = SchoolBus.solveSchoolBus(graph);
      System.out.println("solution return");
      SchoolBusBase.Answer answer2 = SchoolBusBase.solveSchoolBus(graph);
      System.out.println("base return");
      if (!compare(answer1, answer2)) {
        printGraph(graph);
        System.out.println("----------------------------");
        SchoolBus.printAnswer(answer1);
        printPath(graph, answer1.path);
        System.out.println("----------------------------");
        SchoolBusBase.printAnswer(answer2);
        printPath(graph, answer2.path);
        System.out.println("----------------------------");
        break;
      }
    }
  }

  static void printPath(int[][] graph, List<Integer> path) {
    for (int i = 1; i < path.size(); i++) {
      int from = path.get(i - 1) - 1;
      int to = path.get(i) - 1;
      System.out.print(graph[from][to] + " ");
    }
    int from = path.get(0) - 1;
    int to = path.get(path.size() - 1) - 1;
    System.out.println(graph[from][to]);
  }

  static void printGraph(int[][] graph) {
    for (int i = 0; i < graph.length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        System.out.printf("%4d ", graph[i][j]);
      }
      System.out.println();
    }
  }

  static int[][] makeRandomGraph() {
    Random random = new Random(42);
    int n = 5;
    int edges = 20 + (Math.abs(random.nextInt()) % 60);
    int[][] graph = new int[n][n];
    int count = 0;
    while (true) {
      if (count >= edges) {
        return graph;
      }

      int x = Math.abs(random.nextInt()) % n;
      int y = Math.abs(random.nextInt()) % n;
      if (x == y) {
        continue;
      }

      int weight = Math.abs(random.nextInt()) % 100 + 10;
      graph[x][y] = weight;
      graph[y][x] = weight;
      count++;
    }
  }


  static boolean compare(SchoolBus.Answer answer1, SchoolBusBase.Answer answer2) {
    if (answer1.weight != answer2.weight) {
      System.out.println("111111");
      return false;
    }
    if (answer1.path.size() != answer2.path.size()) {
      System.out.println("222222222");
      return false;
    }
    if (compareCycle(answer1.path, answer2.path)) {
      System.out.println("333333333");
      return true;
    }
    List<Integer> x = reverse(answer1.path);
    return compareCycle(x, answer2.path);
  }

  static List<Integer> reverse(List<Integer> a1) {
    List<Integer> x = IntStream.range(0, a1.size()).boxed()
        .map(idx -> a1.get(a1.size() - 1 - idx)).collect(
            Collectors.toList());
    return x;
  }

  static boolean compareCycle(List<Integer> a1, List<Integer> a2) {
    List<Integer> c1 = new ArrayList<>(a1);
    List<Integer> c2 = new ArrayList<>(a2);
    if (c1.size() != c2.size()) {
      return false;
    }
    int rotate = c1.size();
    while (rotate > 0) {
      int head = c1.get(0);
      c1 = c1.subList(1, c1.size());
      c1.add(head);
      if (c1.equals(c2)) {
        return true;
      }
      rotate -= 1;
    }
    return false;
  }

  // public static void main(String[] args) {
  //   List<Integer> c1 = Arrays.asList(1, 5, 2, 3, 4);
  //   List<Integer> c2 = Arrays.asList(4, 3, 2, 5, 1);
  //   System.out.println(compareCycle(c1, c2));
  //   System.out.println(compareCycle(reverse(c1), c2));
  // }
}
