 
import java.util.ArrayList;
import java.util.List;

 public class ReduceToFlow {
    Kattio io;

    void readBipartiteGraph(int[] xAndY, int[] eCount, List<int[]> edges) {
        // Läs antal hörn i X och Y
        xAndY[0] = io.getInt();
        xAndY[1] = io.getInt();

        // Läs antal kanter
        eCount[0] = io.getInt();

        // Läs in kanterna
        for (int i = 0; i < eCount[0]; i++) {
            int u = io.getInt();
            int v = io.getInt();
            edges.add(new int[]{u, v});
        }
    }

    void writeFlowGraph(int X, int Y, List<int[]> edges) {
        int V = X + Y + 2; // Totalt antal hörn inklusive källa och sänka
        int s = V - 1;     // Källa (indexeras som sista hörnet)
        int t = V;         // Sänka (indexeras som sista + 1)

        List<String> flowEdges = new ArrayList<>();

        // Kanter från källa till alla X-hörn
        for (int i = 1; i <= X; i++) {
            flowEdges.add(s + " " + i + " 1");
        }

        // Kanter mellan X och Y enligt matchningskanterna
        for (int[] edge : edges) {
            flowEdges.add(edge[0] + " " + edge[1] + " 1");
        }

        // Kanter från alla Y-hörn till sänkan
        for (int i = X + 1; i <= X + Y; i++) {
            flowEdges.add(i + " " + t + " 1");
        }

        int totalEdges = flowEdges.size();

        // Skriv ut flödesproblemet
        io.println(V);
        io.println(s + " " + t);
        io.println(totalEdges);
        for (String edge : flowEdges) {
            io.println(edge);
        }
        io.flush();

    }

    void readMaxFlowSolution(int X, int Y, List<int[]> matching) {
        // Läs in flödeslösningen
        int V = io.getInt();
        int s = io.getInt();
        int t = io.getInt();
        int totalFlow = io.getInt();
        int E = io.getInt();

        for (int i = 0; i < E; i++) {
            int u = io.getInt();
            int v = io.getInt();
            int f = io.getInt();

            // Om flödet är 1 och kanten är mellan X och Y, inkludera i matchningen
            if (f > 0 && u >= 1 && u <= X && v >= X + 1 && v <= X + Y) {
                matching.add(new int[]{u, v});
            }
        }
    }

    void writeBipartiteMatching(int X, int Y, List<int[]> matching) {
        // Skriv ut matchningslösningen
        io.println(X + " " + Y);
        io.println(matching.size());
        for (int[] edge : matching) {
            io.println(edge[0] + " " + edge[1]);
        }
        io.flush();
    }

    ReduceToFlow() {
        io = new Kattio(System.in, System.out);

        int[] xAndY = new int[2];       // För att lagra antal hörn i X och Y
        int[] eCount = new int[1];      // För att lagra antal kanter
        List<int[]> edges = new ArrayList<>(); // Lista över kanter

        // Läs in matchningsproblemet
        readBipartiteGraph(xAndY, eCount, edges);

        int X = xAndY[0];
        int Y = xAndY[1];

        // Reducera till flödesproblem och skriv ut
        writeFlowGraph(X, Y, edges);

        // Läs in flödeslösningen
        List<int[]> matching = new ArrayList<>();
        readMaxFlowSolution(X, Y, matching);

        // Skriv ut matchningslösningen
        writeBipartiteMatching(X, Y, matching);

        io.close();
    }

    public static void main(String[] args) {
        new ReduceToFlow();
    }
}
