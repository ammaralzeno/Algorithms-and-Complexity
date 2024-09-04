import os, sys, shutil
from collections import defaultdict

desktop_path = os.path.join(os.path.expanduser("~"), "Desktop")

CORPUS_FILE = os.path.join(desktop_path, "korpus.txt")
RAW_INDEX_FILE = os.path.join(desktop_path, "rawindex.txt")
CONCORDANCE_DIR = "/var/tmp/concordance"

def lazyHash(word):
    word = word.lower()                                                     
    if len(word) < 3:
        word = word.ljust(3, 'x')                                           # fyll med x om mindre än 3 bokstäver
    return ord(word[0]) * (30 * 30) + ord(word[1]) * 30 + ord(word[2])      # hash

def create_concordance():
    os.makedirs(CONCORDANCE_DIR, exist_ok=True)                             # skapa katalog om den inte finns
    word_positions = defaultdict(list)                                      # dictionary med listor

    with open(RAW_INDEX_FILE, 'r', encoding='latin-1') as f:                # öppna filen
        for i, line in enumerate(f):                                        # loopa igenom filen
            if i % 1000000 == 0: print(f"Processed {i} lines...")
            try:
                word, position = line.strip().split()                       # dela upp raden med ord och position
                word_positions[lazyHash(word)].append((word.lower(), int(position))) # lägg till i dictionary
            except ValueError:
                print(f"Skipping invalid line: {line.strip()}")             # skippa ogiltiga rader

    for hash_key, positions in word_positions.items():                      # skapa indexfiler
        with open(f"{CONCORDANCE_DIR}/{hash_key}.idx", 'w', encoding='latin-1') as f:
            for word, position in sorted(positions):                        # skriv till fil
                f.write(f"{word} {position}\n")

    print("Concordance creation completed.")

def get_context(position, word, size=30):                                   # hämta kontext
    word_length = len(word)
    with open(CORPUS_FILE, 'rb') as f:                                      # öppna korpusfilen
        f.seek(max(0, position - size))                                     # sök till position
        before = f.read(position - f.tell()).decode('latin-1', errors='replace').replace('\n', ' ').rjust(size) # läs in kontext före ordet
        f.seek(position)
        word_and_after = f.read(word_length + size).decode('latin-1', errors='replace').replace('\n', ' ') # läs in kontext efter ordet
        word = word_and_after[:word_length]                                 # hämta ordet
        after = word_and_after[word_length:].ljust(size)[:size]             # hämta resten av kontexten
    return f"{before}{word}{after}"                                         # returnera kontexten

def search_word(word):                                                      # sök efter ord / linjärsökning
    word = word.lower()                                                     
    index_file = f"{CONCORDANCE_DIR}/{lazyHash(word)}.idx"                  # hitta indexfilen
    if not os.path.exists(index_file):                                      # om filen inte finns
        return []

    results = []                                                            # lista för resultat
    with open(index_file, 'r', encoding='latin-1') as f:                    # öppna filen
        lines = f.readlines()                                               # läs in filen
        start = next((i for i, line in enumerate(lines) if line.startswith(word + ' ')), -1) # hitta startpositionen
        if start != -1:                                                     # om startpositionen finns
            for line in lines[start:]:                                      # loopa igenom filen
                if not line.startswith(word + ' '): break                   # om raden inte börjar med ordet
                results.append(get_context(int(line.split()[1]), word))

    print(f"Found {len(results)} occurrences in {index_file}")              # skriv ut antalet resultat
    return results                                                      

def clear_concordance():                                                    # rensa concordance
    if os.path.exists(CONCORDANCE_DIR):
        shutil.rmtree(CONCORDANCE_DIR)
        print(f"All index files in {CONCORDANCE_DIR} have been deleted.")
    else:
        print(f"The directory {CONCORDANCE_DIR} does not exist.")

def main():
    if len(sys.argv) < 2:
        print("Usage: python3 concordance.py <word> | --clear | --build")
        return

    if sys.argv[1] == "--clear":                                            # rensa concordance
        clear_concordance()
        return
    elif sys.argv[1] == "--build":                                          # bygg concordance
        create_concordance()
        return

    search_word_input = sys.argv[1]
    results = search_word(search_word_input)                                # sök efter ord
    
    print(f"There are {len(results)} occurrences of the word.")             # skriv ut antalet resultat
    
    if len(results) > 25:                                                   # om antalet resultat är större än 25
        user_input = input("There are more than 25 occurrences. Do you want to print them all? (y/n): ")
        if user_input.lower() != 'y':
            results = results[:25]
    
    for result in results:
        print(result)

if __name__ == "__main__":
    main()
