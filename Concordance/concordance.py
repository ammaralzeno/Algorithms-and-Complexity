import os, sys, shutil
from collections import defaultdict

CORPUS_FILE, RAW_INDEX_FILE = "korpus.txt", "rawindex.txt"
CONCORDANCE_DIR = "/var/tmp/concordance"
CHUNK_SIZE = 1000000 

def lazyHash(word):
    word = word.lower() # lowercase
    if len(word) < 3:
        word = word.ljust(3, 'x') # fyll med x om mindre än 3 bokstäver
    return ord(word[0]) * (30 * 30) + ord(word[1]) * 30 + ord(word[2]) # hash

def create_concordance(): # skapa concordance
    os.makedirs(CONCORDANCE_DIR, exist_ok=True) # skapa katalog om den inte finns
    chunk_count = 0
    total_lines_processed = 0

    with open(RAW_INDEX_FILE, 'r', encoding='latin-1') as f: # öppna filen
        while True:
            word_positions = defaultdict(list) # dictionary med listor
            
            
            lines = f.readlines(CHUNK_SIZE) # läs in CHUNK_SIZE rader
            if not lines:
                break # sluta om inga rader finns

            for line in lines:
                total_lines_processed += 1
                try:
                    word, position = line.strip().split() # dela upp raden i ord och position
                    word_lower = word.lower()
                    word_positions[lazyHash(word_lower)].append((word_lower, int(position))) # lägg till i dictionary
                except ValueError:
                    print(f"Skipping invalid line: {line.strip()}") # skriv ut felaktig rad

            for hash_key, positions in word_positions.items(): # loopa igenom dictionary
                index_file = f"{CONCORDANCE_DIR}/{hash_key}.idx" # skapa indexfil
                mode = 'a' if os.path.exists(index_file) else 'w' # öppna filen i append-läge om den finns, annars i write-läge
                
                with open(index_file, mode, encoding='latin-1') as index_f:  # Open index file
                    for word, position in sorted(positions): # loopa igenom listan
                        index_f.write(f"{word} {position}\n") # skriv till filen

            chunk_count += 1
            print(f"Del {chunk_count}, totala linjer: {total_lines_processed}")

    print("Concordance creation completed.")

def get_context(position, word, size=30): # hämta kontext
    word_length = len(word)
    with open(CORPUS_FILE, 'rb') as f: # öppna korpusfilen
        f.seek(max(0, position - size)) # sök till position
        before = f.read(position - f.tell()).decode('latin-1', errors='replace').replace('\n', ' ').rjust(size) # läs in kontext före ordet
        f.seek(position) 
        word_and_after = f.read(word_length + size).decode('latin-1', errors='replace').replace('\n', ' ') # läs in kontext efter ordet
        word = word_and_after[:word_length] # hämta ordet
        after = word_and_after[word_length:].ljust(size)[:size] # hämta resten av kontexten
    return f"{before}{word}{after}" # returnera kontexten

def search_word(word, limit=25): # sök efter ord
    word = word.lower() # lowercase 
    index_file = f"{CONCORDANCE_DIR}/{lazyHash(word)}.idx"
    if not os.path.exists(index_file):
        return [], 0

    results = []
    total_count = 0
    with open(index_file, 'r', encoding='latin-1') as f:
        for line in f:
            if line.startswith(word + ' '):
                total_count += 1
                if len(results) < limit:
                    results.append(int(line.split()[1]))
            elif total_count > 0:
                break  # We've passed all occurrences of the word

    return results, total_count  # Return positions and total count

def clear_concordance(): # rensa concordance
    if os.path.exists(CONCORDANCE_DIR):
        shutil.rmtree(CONCORDANCE_DIR)
        print(f"Alla filer i {CONCORDANCE_DIR} har blivit raderade.")
    else:
        print(f"Katalogen {CONCORDANCE_DIR} finns inte.")

def main():
    if len(sys.argv) < 2:
        print("python3 concordance.py <ord> | --clear | --build")
        return

    if sys.argv[1] == "--clear": # rensa concordance
        clear_concordance()
        return
    elif sys.argv[1] == "--build": # bygg concordance
        create_concordance()
        return

    search_word_input = sys.argv[1]
    positions, total_count = search_word(search_word_input) # sök efter ord
    
    print(f"Det finns {total_count} förekomster av ordet. Nedan visas de första 25:") # skriv ut antalet resultat
    
    for position in positions:
        print(get_context(position, search_word_input))

    if total_count > 25: # om antalet resultat är större än 25
        user_input = input("Det finns fler än 25 förekomster. Vill du skriva ut alla? (y/n): ")
        if user_input.lower() == 'y':
            all_positions, _ = search_word(search_word_input, limit=total_count)
            for position in all_positions[25:]:
                print(get_context(position, search_word_input))

if __name__ == "__main__":
    main()
