import java.util.*;

public class labb4 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int V = sc.nextInt();
        int E = sc.nextInt();
        int m = sc.nextInt();

        // om m >= V, skriver vi ut minimal ja-instans då grafen är garanterat färgbar
        if (m >= V) {
            // minimal ja-instans: 3 roller, 2 scener, 3 skådespelare
            System.out.println("3");
            System.out.println("2");
            System.out.println("3");
            System.out.println("1 1");
            System.out.println("1 2");
            System.out.println("1 3");
            System.out.println("2 1 3");
            System.out.println("2 2 3");
            return;
        }

        int totalScenes = E + V + 1; //scener:  E-kant scener + V-hörn scener + 1 kopplingsscen
        int n = V + 2; // roller: V-hörn roller + roll för p1 + roll för p2
        int k = m + 2; // skådespelare: m-färger skådespelare + p1 + p2

        // skriv ut produktionen
        System.out.println(n);
        System.out.println(totalScenes);
        System.out.println(k);


        // typ 1 villkor: alla ursprungliga roller kan spelas av skådespelare 3 till k
        for (int i = 1; i <= V; i++) {
            System.out.print(k-2);
            for (int j = 3; j <= k; j++) {
                System.out.print(" " + j);
            }
            System.out.println();
        }
        
        // de två extra rollerna kan bara spelas av p1 och p2
        System.out.println("1 1");
        System.out.println("1 2");

        // typ 2 villkor: 
        // om det finns en kant mellan a och b, skriv ut 2 a b
        // om de 2 rollerna är i samma scen, måste de spelas av olika skådespelare (olika färger)
        int a, b;
        for (int i = 0; i < E; i++) {
            a = sc.nextInt();
            b = sc.nextInt();
            System.out.print("2 ");
            System.out.print(a);
            System.out.print(" ");
            System.out.println(b);
        }

        // hörnscener: koppla varje hörnroll till p2's roll
        // säkerställer att alla hörnroller visas i minst en scen
        for (int i = 1; i <= V; i++) {
            System.out.print("2 ");
            System.out.print(i);
            System.out.print(" ");
            System.out.println(n-1);
        }

        // sista kopplingsscen: säkerställer att p1 har också minst en scen
        System.out.print("2 ");
        System.out.print(n);
        System.out.print(" ");
        System.out.println(1);
    }
}


/*

Tidskomplexitet: O (V*m + E)



För ja-instansen:
Rollerna 1 till V kommer att spelas av skådespelare 3 till k.
Rollerna V+1 och V+2 kommer att spelas av skådespelare 1 och 2.

Då grafen är färgbar med m färger, kan vi besätta m skådespelare 
till rollerna 1 till V utan konflikter.

==> P1 och P2 får minst en roll var.
- Deras roller är i minst en scen var.
- Rollerna för P1 och P2 är inte i samma scen.
- Alla roller visas i minst en scen.
- Inga monologer.

För nej-instansen:
Instansen blir ej lösbar då det finns en kant mellan två noder av samma färg.
Det medför att det finns en scen som endast en skådespelare kan spela.

==> Då grafen ej är färgbar.

*/
