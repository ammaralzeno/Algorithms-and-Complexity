def lazy_hash(word):
    """Genererar ett hashvärde baserat på de tre första bokstäverna i ordet."""
    word = word.lower()
    
    if len(word) < 3:
        word = word.ljust(3, 'x')  # Fyller på med 'x' om ordet är kortare än 3 tecken

    first = ord(word[0])
    second = ord(word[1])
    third = ord(word[2])

    hash_value = first * (27 * 27) + second * 27 + third

    return hash_value

def konkordans(word, input_file, index_file, context_size=30):
    word = word.lower()
    word_positions = []

    with open(index_file, 'r', encoding='latin-1') as f:
        for line in f:
            entry_word, positions = line.split(' ', 1)  # Ändrade splitten för att hantera mellanslag i ord
            if entry_word == word:
                word_positions.extend(map(int, positions.split()))  # Samlar alla positioner istället för att bryta loopen
    
    if not word_positions:
        print(f"Inga förekomster av ordet '{word}' hittades.")
        return
    
    print(f"Det finns {len(word_positions)} förekomster av ordet.")
    
    if len(word_positions) > 25:
        user_input = input("Vill du skriva ut alla förekomster? (y/n): ")
        if user_input.lower() != 'y':
            word_positions = word_positions[:25]
    
    with open(input_file, 'r', encoding='latin-1') as f:
        text = f.read()
        for position in word_positions:
            start = max(0, position - context_size)
            end = min(len(text), position + len(word) + context_size)
            
            context = text[start:end]
            word_start = position - start
            word_end = word_start + len(word)
            
            before = context[:word_start].replace('\n', ' ')
            after = context[word_end:].replace('\n', ' ')
            
            print(f"...{before}{context[word_start:word_end]}{after}...\n")

if __name__ == "__main__":
    import sys
    if len(sys.argv) != 2:
        print("Usage: python3 konkordans.py <word>")
    else:
        konkordans(sys.argv[1], "korpus.txt", "rawindex.txt")