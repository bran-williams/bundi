package com.branwilliams.bundi.engine;

public class FloydWarshall {

    final static int INFINITY = 8767868;

    public static int[][] floydWarshall(int graph[][], int V) {
        int dist[][] = new int[V][V];
        int i, j, k;

        /* Initialize the solution matrix same as input graph matrix.
           Or we can say the initial values of shortest distances
           are based on shortest paths considering no intermediate
           vertex. */
        for (i = 0; i < V; i++)
            for (j = 0; j < V; j++)
                dist[i][j] = graph[i][j];

        /* Add all vertices one by one to the set of intermediate
           vertices.
          ---> Before start of an iteration, we have shortest
               distances between all pairs of vertices such that
               the shortest distances consider only the vertices in
               set {0, 1, 2, .. k-1} as intermediate vertices.
          ----> After the end of an iteration, vertex no. k is added
                to the set of intermediate vertices and the set
                becomes {0, 1, 2, .. k} */
        int changeCount = 0;
        for (i = 0; i < V; i++) {
            // Pick all vertices as source one by one
            for (j = 0; j < V; j++) {
                // Pick all vertices as destination for the
                // above picked source
                for (k = 0; k < V; k++) {
                    // If vertex k is on the shortest path from
                    // i to j, then update the value of dist[i][j]
                    if (dist[j][i] + dist[i][k] < dist[j][k]) {
                        dist[j][k] = dist[j][i] + dist[i][k];

                        // print the progess
                        System.out.println("change " + changeCount + ":");
                        printGraph(dist, V);
                        changeCount++;
                        System.out.println("************************");
                    }
                }
            }
        }

       return dist;
    }

    public static void printGraph(int dist[][], int V) {
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (dist[i][j] == INFINITY) {
                    System.out.print("INF ");
                } else {
                    System.out.print(dist[i][j] + "   ");
                }
            }
            System.out.print("\n");
        }
    }

    public static void testGraph(int[][] graph, int V) {
        System.out.println("************************");

        System.out.println("Graph before Floyd-Warshall:");
        printGraph(graph, V);
        System.out.println("************************");

        int soln[][] = floydWarshall(graph, V);
        System.out.println("Graph after Floyd-Warshall:");
        printGraph(soln, V);
        System.out.println("************************");
    }

    // Driver program to test above function
    public static void main(String[] args) {
        int graph[][] = {
                {0, 5, INFINITY, 10, INFINITY, INFINITY},
                {INFINITY, 0, 3, INFINITY, INFINITY, 2},
                {INFINITY, INFINITY, 0, 1, 0, INFINITY},
                {INFINITY, INFINITY, INFINITY, 0, INFINITY, 4},
                {INFINITY, 3, INFINITY, 0, INFINITY, INFINITY},
                {6, INFINITY, INFINITY, 0, INFINITY, 8}
        };
        int V = 6;
        testGraph(graph, V);

        graph = new int[][] {
                {0, 5, INFINITY, 8},
                {INFINITY, 0, 3, INFINITY},
                {7, INFINITY, 0, INFINITY},
                {INFINITY, INFINITY, 0, 3}
        };
        V = 4;
        testGraph(graph, V);
    }
}
