from itertools import product

def reduce_3sat_to_fountain(clauses):

    variables = set(abs(literal) for clause in clauses for literal in clause)
    num_vars = len(variables) 
    num_clauses = len(clauses)
    n = num_vars + num_clauses
    k = 2


    holesAtLevel = [[] for _ in range(n)]  # n disks


    for idx, clause in enumerate(clauses):
        for literal in clause:
            var_index = abs(literal)
            y = 2 if literal > 0 else 1  # true for positive, false for negative
            holesAtLevel[idx].append((var_index, y))

    for var in range(1, num_vars + 1):
        disk_idx = num_clauses + var - 1
        # add two holes to represent the variable being True or False
        holesAtLevel[disk_idx].append((var, 1)) 
        holesAtLevel[disk_idx].append((var, 2))

    # add dummy holes to cover all x = num_vars + 1 to n
    # put all dummy holes in the last disk
    last_disk = n - 1
    for dummy_x in range(num_vars + 1, n + 1):
        holesAtLevel[last_disk].append((dummy_x, 1))  # Dummy hole

    # print the constructed fountain instance for verification
    print(f"n (Total Disks and Sliders) = {n}, k (Positions per Slider) = {k}")
    print("Holes at each level:")
    for idx, level in enumerate(holesAtLevel):
        print(f"Disk {idx + 1}: {level}")

    return n, k, holesAtLevel



clauses_yes1 = [
    [1, -2, 3],    # x₁ ∨ x₂ ∨ x₃
    [1, 5, -6],   # x₄ ∨ x₅ ∨ ¬x₆
    [1, -2, 3],   # x₁ ∨ ¬x₂ ∨ x₃
]

clauses_yes2 = [
    [-1, 2, 3],   # ¬x₁ ∨ x₂ ∨ x₃
    [1, 2, 3],    # x₁ ∨ x₂ ∨ x₃
    [-1, 2, -3],  # ¬x₁ ∨ x₂ ∨ ¬x₃
]

clauses_no1 = [
    [-1, -2, 3],    # x₁ ∨ x₂ ∨ x₃
    [-1, 2, -3], # ¬x₁ ∨ ¬x₂ ∨ ¬x₃
    [1, -2, -3],   # x₁ ∨ x₂ ∨ ¬x₃
    [1, 2, 3],  # ¬x₁ ∨ ¬x₂ ∨ x₃
]

clauses_no2 = [
    [1, 2, 3],    # x₁ ∨ x₂ ∨ x₃
    [-1, -2, 3],  # ¬x₁ ∨ ¬x₂ ∨ x₃
    [-1, 2, -3],  # ¬x₁ ∨ x₂ ∨ ¬x₃
    [1, -2, -3],  # x₁ ∨ ¬x₂ ∨ ¬x₃
    [-1, -2, -3], # ¬x₁ ∨ ¬x₂ ∨ ¬x₃
]

clauses_unsolvable = [
    [1, 2, 3],    # x₁ ∨ x₂ ∨ x₃
    [-1, 2, 3],
    [1, -2, 3],    # x₁ ∨ x₂ ∨ x₃
    [-1, -2, 3],
    [1, 2, -3],    # x₁ ∨ x₂ ∨ x₃
    [-1, 2, -3],
    [1, -2, -3],    # x₁ ∨ x₂ ∨ x₃
    [-1, -2, -3]  # ¬x₁ ∨ ¬x₂ ∨ x₄

]
    

reduce_3sat_to_fountain(clauses_yes1)
reduce_3sat_to_fountain(clauses_yes2)
reduce_3sat_to_fountain(clauses_no1)
reduce_3sat_to_fountain(clauses_no2)
reduce_3sat_to_fountain(clauses_unsolvable)


def verify_fountain_problem(n, k, holesAtLevel):
    total_settings = 2 ** n

    # iterate over all possible slider settings
    for assignment in range(total_settings):
        # determine the position of each slider based on the assignment
        positions = []
        for i in range(n):
            # positions are 1 or 2 (since k=2)
            # if the bit at position i is 1, slider i is at position 2; else at position 1
            if (assignment >> i) & 1:
                positions.append(2)
            else:
                positions.append(1)

        # assume the fountain is solvable with this assignment
        fountain_solvable = True

        # check each disk to see if there's at least one open hole
        for disk in holesAtLevel:
            disk_has_open_hole = False

            for hole in disk:
                x, y = hole  # x is the slider index (1-indexed), y is the hole position (1 or 2)
                slider_position = positions[x - 1]  # get the position of slider x

                if slider_position != y:
                    disk_has_open_hole = True
                    break  # no need to check more holes in this disk

            if not disk_has_open_hole:
                fountain_solvable = False
                break  # no need to check further disks

        if fountain_solvable:
            return True  # the fountain is solvable

    return False  # the fountain is unsolvable


result = verify_fountain_problem(*reduce_3sat_to_fountain(clauses_no2))
print(result)

