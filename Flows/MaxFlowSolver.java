import java.util.*;
import java.io.*;
 
 public class MaxFlowSolver {
     Kattio io;
     int V, E, s, t;
     List<Edge>[] graph;
 
     class Edge {
         int to, rev;
         int capacity;
         int flow;
 
         Edge(int to, int rev, int capacity) {
             this.to = to;
             this.rev = rev;
             this.capacity = capacity;
             this.flow = 0;
         }
     }
 
     void readFlowGraph() {
         // Läs in antal hörn
         V = io.getInt();
         // Läs in källa och sänka
         s = io.getInt();
         t = io.getInt();
         // Läs in antal kanter
         E = io.getInt();
 
         // Initiera grafen och adjacency listor
         graph = new ArrayList[V + 1];
         for (int i = 0; i <= V; i++) {
             graph[i] = new ArrayList<>();
         }
 
         // Läs in kanterna
         for (int i = 0; i < E; i++) {
             int u = io.getInt();
             int v = io.getInt();
             int c = io.getInt();
 
             // Lägg till kanten i grafen
             addEdge(u, v, c);
         }
     }
 
     void addEdge(int u, int v, int capacity) {
         Edge a = new Edge(v, graph[v].size(), capacity);
         Edge b = new Edge(u, graph[u].size(), 0); // Omvänd kant med kapacitet 0
         graph[u].add(a);
         graph[v].add(b);
     }
 
     int maxFlow() {
         int totalFlow = 0;
         while (true) {
             int[] parent = new int[V + 1];
             int[] edgeIndex = new int[V + 1];
             Arrays.fill(parent, -1);
             
             // Hitta augmenterande väg med BFS
             if (!bfs(parent, edgeIndex)) {
                 break;
             }
             
             // Hitta minsta residualkapaciteten längs vägen
             int pathFlow = Integer.MAX_VALUE;
             for (int v = t; v != s; v = parent[v]) {
                 int u = parent[v];
                 Edge e = graph[u].get(edgeIndex[v]);
                 pathFlow = Math.min(pathFlow, e.capacity - e.flow);
             }
             
             // Uppdatera flöde längs vägen
             for (int v = t; v != s; v = parent[v]) {
                 int u = parent[v];
                 Edge e = graph[u].get(edgeIndex[v]);
                 e.flow += pathFlow;
                 graph[e.to].get(e.rev).flow -= pathFlow;
             }
             
             totalFlow += pathFlow;
         }
         return totalFlow;
     }
 
     boolean bfs(int[] parent, int[] edgeIndex) {
         Queue<Integer> queue = new ArrayDeque<>();
         boolean[] visited = new boolean[V + 1];
         
         // Lägg till källan i kön och markera den som besökt
         queue.add(s);
         visited[s] = true;
         
         while (!queue.isEmpty()) {
             int u = queue.poll();
             
             // Gå igenom alla grannar till u och 
             // lägg till dem i kön om de inte har besökts
             for (int i = 0; i < graph[u].size(); i++) {
                 Edge e = graph[u].get(i);
                 if (!visited[e.to] && e.flow < e.capacity) {
                     visited[e.to] = true;
                     parent[e.to] = u;
                     edgeIndex[e.to] = i;
                     queue.add(e.to);
                     
                     if (e.to == t) {
                         return true;
                     }
                 }
             }
         }
         
         return false;
     }
 
     void writeFlowSolution(int totalFlow) {
         // Räkna antalet kanter med positivt flöde
         int flowEdges = 0;
         for (int u = 1; u <= V; u++) {
             for (Edge e : graph[u]) {
                 if (e.flow > 0) {
                     flowEdges++;
                 }
             }
         }
 
         // Skriv ut lösningen
         io.println(V);
         io.println(s + " " + t + " " + totalFlow);
         io.println(flowEdges);
 
         for (int u = 1; u <= V; u++) {
             for (Edge e : graph[u]) {
                 if (e.flow > 0) {
                     io.println(u + " " + e.to + " " + e.flow);
                 }
             }
         }
         io.flush();
     }
 
     MaxFlowSolver() {
         io = new Kattio(System.in, System.out);
 
         // Läs in flödesgrafen
         readFlowGraph();
 
         // Beräkna maximalt flöde
         int totalFlow = maxFlow();
 
         // Skriv ut flödeslösningen
         writeFlowSolution(totalFlow);
 
         io.close();
     }
 
     public static void main(String[] args) {
         new MaxFlowSolver();
     }
 }
 