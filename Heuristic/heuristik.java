import java.util.*;
import java.util.stream.Collectors;

public class Labb5 {
    public static void main(String[] args) {
        new Labb5().run();
    }

    void run() {
        Scanner sc = new Scanner(System.in);
        // läsa in input
        int n = Integer.parseInt(sc.nextLine()); // antalet roller
        int s = Integer.parseInt(sc.nextLine()); // antalet scener
        int k = Integer.parseInt(sc.nextLine()); // antal skådisar

        // mappar rollerna med sina möjliga skådisar som kan spela denna roll
        // hash map används för O(1) lookup
        Map<Integer, Set<Integer>> rolePossibleActors = new HashMap<>();

        for (int role = 1; role <= n; role++) {
            String[] parts = sc.nextLine().split(" ");

            Set<Integer> possibleActors = new HashSet<>();
            for (int i = 1; i < parts.length; i++) {
                possibleActors.add(Integer.parseInt(parts[i]));
            }
            rolePossibleActors.put(role, possibleActors);
        }

        // läser in scener och dess roller 
        // görs mha lista av mängder som representerar roller i en scen
        List<Set<Integer>> sceneRoles = new ArrayList<>();

        for (int i = 0; i < s; i++) {
            String[] parts = sc.nextLine().split(" ");
            // mappar rollerna i en scen till en mängd
            Set<Integer> rolesInScene = new HashSet<>();
            for (int j = 1; j < parts.length; j++) {
                rolesInScene.add(Integer.parseInt(parts[j]));
            }
            sceneRoles.add(rolesInScene);
        }

        // tilldela roller
        AssignmentResult result = assignRoles(n, s, k, rolePossibleActors, sceneRoles);
        if (result == null) {
            System.out.println("ingen korrekt rolltilldelning kan hittas");
            return;
        }

        // outputa med rätt format
        System.out.println(result.usedActors.size());
        for (Integer actor : result.usedActors) {
            Set<Integer> roles = result.actorRoles.get(actor);
            System.out.print(actor + " " + roles.size());
            for (Integer role : roles) {
                System.out.print(" " + role);
            }
            System.out.println();
        }
    }

    // skapar en klass för att lagra resultatet av tilldelningen
    class AssignmentResult {
        Map<Integer, Integer> assignedRoles;
        Map<Integer, Set<Integer>> actorRoles;
        Set<Integer> usedActors;

        AssignmentResult(Map<Integer, Integer> assignedRoles, Map<Integer, Set<Integer>> actorRoles,
                Set<Integer> usedActors) {
            this.assignedRoles = assignedRoles;
            this.actorRoles = actorRoles;
            this.usedActors = usedActors;
        }
    }

    // tilldelnings resultat
    AssignmentResult assignRoles(int n, int s, int k, Map<Integer, Set<Integer>> rolePossibleActors,
            List<Set<Integer>> sceneRoles) {

        
        Map<Integer, Integer> assignedRoles = new HashMap<>(); // mappar roll till skådis
        Map<Integer, Set<Integer>> actorRoles = new HashMap<>(); // mappar skådis till ett sett av roller
        Set<Integer> usedActors = new HashSet<>(); // mängd avskådisar som är tilldelade roller

        // tilldela divorna
        int[] divas = { 1, 2 };
        // skapar listor över roller som diva 1 och diva 2 är behöriga att spela
        List<Integer> possibleRolesDiva1 = new ArrayList<>();
        List<Integer> possibleRolesDiva2 = new ArrayList<>();

        // loopar igenom alla roller och lägger till de som divorna är behöriga att spela
        for (Map.Entry<Integer, Set<Integer>> entry : rolePossibleActors.entrySet()) {
            if (entry.getValue().contains(divas[0])) {
                possibleRolesDiva1.add(entry.getKey());
            }
            if (entry.getValue().contains(divas[1])) {
                possibleRolesDiva2.add(entry.getKey());
            }
        }

        // om ingen roll finns för någon av divorna
        if (possibleRolesDiva1.isEmpty() || possibleRolesDiva2.isEmpty()) {
            return null;
        }

        // försök hitta roller till divorna som inte är med i samma scen
        boolean foundAssignment = false;
        int assignedRoleDiva1 = -1;
        int assignedRoleDiva2 = -1;

        // loopar igenom alla möjliga roller för divorna
        for (int role1 : possibleRolesDiva1) {
            for (int role2 : possibleRolesDiva2) {
                boolean rolesConflict = false;
                for (Set<Integer> scene : sceneRoles) {
                    if (scene.contains(role1) && scene.contains(role2)) {
                        rolesConflict = true;
                        break;
                    }
                }
                // om ingen konflikt hittas
                if (!rolesConflict) {
                    // hittade roller för divorna som inte är med i samma scen
                    assignedRoleDiva1 = role1;
                    assignedRoleDiva2 = role2;
                    foundAssignment = true;
                    break;
                }
            }
            // om en tilldelning hittas av rollerna till divorna
            if (foundAssignment) {
                break;
            }
        }

        // om ingen tilldelning hittas av rollerna till divorna
        if (!foundAssignment) {
            return null;
        }

        assignedRoles.put(assignedRoleDiva1, divas[0]); // tilldela rollen till diva1
        actorRoles.computeIfAbsent(divas[0], x -> new HashSet<>()).add(assignedRoleDiva1); // skapa en mängd om den inte finns och lägg till rollen
        usedActors.add(divas[0]); // lägg till diva1 i mängden av använda skådisar
        rolePossibleActors.remove(assignedRoleDiva1); // ta bort rollen från möjliga roller för framtida tilldelningar

        // tilldela andra rollen till diva2
        assignedRoles.put(assignedRoleDiva2, divas[1]);
        actorRoles.computeIfAbsent(divas[1], x -> new HashSet<>()).add(assignedRoleDiva2);
        usedActors.add(divas[1]);
        rolePossibleActors.remove(assignedRoleDiva2);

        // tilldela resten av rollerna med en greedy algoritm, sedan förbättrar vi med lokalsökning
        List<Integer> unassignedRoles = new ArrayList<>(rolePossibleActors.keySet());
        int superskadespelareNum = k + 1;

        for (Integer role : unassignedRoles) {
            Set<Integer> possibleActors = new HashSet<>(rolePossibleActors.get(role));
            possibleActors.remove(divas[0]);
            possibleActors.remove(divas[1]);
            boolean assigned = false;

            // slumpmässigt tilldela rollen till en skådis
            List<Integer> randomActors = new ArrayList<>(possibleActors);
            for (Integer actor : randomActors) {
                // kontrollera om tilldelningen är giltig (ingen konflikt)
                if (isValidAssignment(role, actor, assignedRoles, sceneRoles)) {
                    assignRole(role, actor, assignedRoles, actorRoles, usedActors);
                    assigned = true;
                    break;
                }
            }

            // om ingen tilldelning hittas, tilldela rollen till en superskådespelare
            if (!assigned) {
                int actor = superskadespelareNum++;
                assignRole(role, actor, assignedRoles, actorRoles, usedActors);
            }
        }

// #####################################################################################################
// ########################################lokalsökningsheuristik#######################################
// #####################################################################################################

        int maxIterations = 100;
        int currentIteration = 0;

        while (currentIteration < maxIterations) {
            boolean improved = false;
            
            // hämta alla superskådespelare
            List<Integer> superActors = usedActors.stream()
                .filter(actor -> actor >= k + 1)
                .collect(Collectors.toList());
            
            // loopa igenom alla superskådespelare
            for (Integer superActor : superActors) {
                // hämta rollen som superskådespelaren har
                Integer role = actorRoles.get(superActor).iterator().next();
                // hämta alla skådisar som kan spela rollen
                Set<Integer> possibleActors = new HashSet<>(rolePossibleActors.getOrDefault(role, new HashSet<>()));
                possibleActors.remove(divas[0]);
                possibleActors.remove(divas[1]);

                // försök hitta en vanlig skådespelare som kan ta rollen
                for (Integer regularActor : possibleActors) {
                    if (isValidAssignment(role, regularActor, assignedRoles, sceneRoles)) {
                        // ta bort superskådespelaren helt
                        actorRoles.remove(superActor);
                        usedActors.remove(superActor);
                        
                        // tilldela rollen till en vanlig skådis
                        assignRole(role, regularActor, assignedRoles, actorRoles, usedActors);
                        improved = true;
                        break;
                    }
                }
            }
            
            if (!improved) {
                break;  // om ingen förbättring gjordes stoppar vi
            }
            currentIteration++;
        }


// #####################################################################################################
// #####################################################################################################

        return new AssignmentResult(assignedRoles, actorRoles, usedActors);
    }

    // kontrollerar om tilldelningen är giltig
    private boolean isValidAssignment(Integer role, Integer actor, 
            Map<Integer, Integer> assignedRoles, List<Set<Integer>> sceneRoles) {
        // loopar igenom alla scener
        for (Set<Integer> scene : sceneRoles) {
            // om scenen innehåller rollen
            if (scene.contains(role)) {
                // loopar igenom alla roller i scenen
                for (Integer otherRole : scene) {
                    // 1: kontrollera om andra roller i scenen har blivit tilldelade än (om nej: ingen konflikt)
                    // 2: kontrollera om denna skådisen redan har en annan roll i scenen
                    if (!otherRole.equals(role) && assignedRoles.get(otherRole) != null
                            && assignedRoles.get(otherRole).equals(actor)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void assignRole(Integer role, Integer actor, 
            Map<Integer, Integer> assignedRoles,
            Map<Integer, Set<Integer>> actorRoles,
            Set<Integer> usedActors) {
        assignedRoles.put(role, actor);
        actorRoles.computeIfAbsent(actor, x -> new HashSet<>()).add(role);
        usedActors.add(actor);
    }

}
