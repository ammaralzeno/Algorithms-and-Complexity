 import java.util.*;

 public class BipartiteMatcher {
     Kattio io;
     int X, Y, E;
     List<int[]> edges;
     int V, s, t;
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
 
     void readBipartiteGraph() {
         // Läs antal hörn i X och Y
         X = io.getInt();
         Y = io.getInt();
 
         // Läs antal kanter
         E = io.getInt();
 
         edges = new ArrayList<>();
 
         // Läs in kanterna
         for (int i = 0; i < E; i++) {
             int u = io.getInt();
             int v = io.getInt();
             edges.add(new int[]{u, v});
         }
     }
 
     void buildFlowGraph() {
         V = X + Y + 2; // Totalt antal hörn inklusive källa och sänka
         s = V - 1;     // Källa (indexeras som sista hörnet)
         t = V;         // Sänka (indexeras som sista + 1)
 
         // Initiera grafen och adjacency listor
         graph = new ArrayList[V + 1];
         for (int i = 0; i <= V; i++) {
             graph[i] = new ArrayList<>();
         }
 
         // Kanter från källa till alla X-hörn
         for (int i = 1; i <= X; i++) {
             addEdge(s, i, 1);
         }
 
         // Kanter mellan X och Y enligt matchningskanterna
         for (int[] edge : edges) {
             addEdge(edge[0], edge[1], 1);
         }
 
         // Kanter från alla Y-hörn till sänkan
         for (int i = X + 1; i <= X + Y; i++) {
             addEdge(i, t, 1);
         }
     }
 
     // lägger till en kant mellan u och v med given kapacitet
     void addEdge(int u, int v, int capacity) { 
         Edge a = new Edge(v, graph[v].size(), capacity);
         Edge b = new Edge(u, graph[u].size(), 0);
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
             
             // Uppdatera flöde längs vägen
             // Eftersom alla kapaciteter är 1, kan vi bara lägga till 1 enhet flöde
             for (int v = t; v != s; v = parent[v]) {
                 int u = parent[v];
                 Edge e = graph[u].get(edgeIndex[v]);
                 e.flow = 1;
                 graph[e.to].get(e.rev).flow = -1;
             }
             
             totalFlow += 1;  // Varje augmenterande väg lägger till exakt 1 till flödet
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
 
     void extractMatching(int totalFlow) {
         List<int[]> matching = new ArrayList<>();
 
         // Gå igenom kanterna från X-hörnen till Y-hörnen för att extrahera matchningen
         for (int u = 1; u <= X; u++) {
             for (Edge e : graph[u]) {
                 if (e.flow > 0 && e.to >= X + 1 && e.to <= X + Y) {
                     matching.add(new int[]{u, e.to});
                 }
             }
         }
 
         // Skriv ut matchningslösningen
         io.println(X + " " + Y);
         io.println(matching.size());
         for (int[] edge : matching) {
             io.println(edge[0] + " " + edge[1]);
         }
         io.flush();

     }
 
     BipartiteMatcher() {
         io = new Kattio(System.in, System.out);
 
         // Läs in matchningsproblemet
         readBipartiteGraph();
 
         // Bygg flödesgrafen
         buildFlowGraph();
 
         // Beräkna maximalt flöde
         int totalFlow = maxFlow();
 
         // Extrahera matchningen och skriv ut lösningen
         extractMatching(totalFlow);
 
         io.close();
     }
 
     public static void main(String[] args) {
         new BipartiteMatcher();
     }
 }
 
