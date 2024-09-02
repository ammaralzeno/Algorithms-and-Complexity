def konkordans(word, input_file, index_file, context_size=30):
    word = word.lower()
    word_positions = []
    
    with open(index_file, 'r', encoding='latin-1') as f:
        for line in f:
            entry_word, positions = line.split(':')
            if entry_word == word:
                word_positions = list(map(int, positions.split(',')))
                break
    
    if not word_positions:
        print(f"Inga förekomster av ordet '{word}' hittades.")
        return
    
    print(f"Det finns {len(word_positions)} förekomster av ordet.")
    
    if len(word_positions) > 25:
        user_input = input("Do you want to print all occurrences? (y/n): ")
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
        konkordans(sys.argv[1], "korpus.txt", "/var/tmp/konkordans.txt")
