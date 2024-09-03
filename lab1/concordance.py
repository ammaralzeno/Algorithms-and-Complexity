import os
import sys
import shutil

desktop_path = os.path.join(os.path.expanduser("~"), "Desktop")

CORPUS_FILE = os.path.join(desktop_path, "korpus.txt")
RAW_INDEX_FILE = os.path.join(desktop_path, "rawindex.txt")
CONCORDANCE_DIR = "/var/tmp/concordance"
CHUNK_SIZE = 1000000   

def lazyHash(word):

    word = word.lower()
    
    if len(word) < 3:
        word = word.ljust(3, 'x')
 
    first = ord(word[0])
    second = ord(word[1])
    third = ord(word[2])

    hashValue = first * (30 * 30) + second * 30 + third

    return hashValue
ß
def create_concordance():
    if not os.path.exists(CONCORDANCE_DIR):
        os.makedirs(CONCORDANCE_DIR)

    print("Reading raw index file and creating hash files...")
    try:
        with open(RAW_INDEX_FILE, 'r', encoding='latin-1') as f:
            for i, line in enumerate(f):
                if i % 100000 == 0:
                    print(f"Processed {i} lines...")
                try:
                    word, position = line.strip().split()
                    hash_key = lazyHash(word)
                    with open(os.path.join(CONCORDANCE_DIR, f"{hash_key}.idx"), 'ab') as hash_file:
                        hash_file.write(f"{word.lower()} {position}\n".encode('utf-8'))
                except ValueError:
                    print(f"Skipping invalid line: {line.strip()}")
    except Exception as e:
        print(f"Error reading raw index file: {e}")
        return

    print("Concordance creation completed.")



def clear_concordance():
    """Delete all index files in the concordance directory."""
    if os.path.exists(CONCORDANCE_DIR):
        try:
            shutil.rmtree(CONCORDANCE_DIR)
            print(f"All index files in {CONCORDANCE_DIR} have been deleted.")
        except Exception as e:
            print(f"Error deleting concordance directory: {e}")
    else:
        print(f"The directory {CONCORDANCE_DIR} does not exist.")


def main():
    if len(sys.argv) < 2:
        print("       python3 concordance.py --clear")
        print("       python3 concordance.py --build")
        return

    if sys.argv[1] == "--clear":
        clear_concordance()
        return
    elif sys.argv[1] == "--build":
        create_concordance()
        return

if __name__ == "__main__":
    main()
