package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {

    // ADDED
    int wordLen = 0;                                                            // Holds the length of the word that is to be guessed
    String wordPattern = "";                                                        // Holds the pattern of the hangman string
    Set<String> dictionaryWords = new TreeSet<String>();                          // Set of words in the dictionary

    SortedSet<Character> usedLetters = new TreeSet<Character>();                            // holds the letters that have been used so far
    Map<String, Set<String>> wordMap = new TreeMap<String, Set<String>>();      // Map

    // StartGame will initialized wordLen, wordPattern, dictionaryWords
    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        // Reset for a new game
        wordMap.clear();
        wordPattern = "";
        dictionaryWords.clear();

        Scanner scanner = null;

        wordLen = wordLength;
        wordPattern = instantiatePattern();

        try {
            scanner = new Scanner(dictionary);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }

        // Parse the dictionary file and put them into currentWords
        while(scanner.hasNext()){
            String word = scanner.next();
            // Only add words from the dictionary of specified length
            if (word.length() == wordLen){
                dictionaryWords.add(word);
            }
        }
        scanner.close();

        // If the input file is empty...
        if (dictionaryWords.isEmpty()){
            throw new EmptyDictionaryException();
        }
    }



    // Gets the guessed letters/ used letters
    @Override
    public SortedSet<Character> getGuessedLetters() {
        return usedLetters;
    }

    // Make a character guess
    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {

        guess = Character.toLowerCase(guess);

        // already guessed letter
        if (usedLetters.contains(guess)){
            throw new GuessAlreadyMadeException();
        }

        // add guess to used letters
        usedLetters.add(guess);

        buildWordMap(guess);                // Understand this better
        String key = chooseKey(guess);
        dictionaryWords = wordMap.get(key);     // update the dictionaryWords
        return dictionaryWords;
    }


    // Sets the default pattern with all dashes ---
    public String instantiatePattern(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < wordLen; i++){
            sb.append('-');
        }
        return sb.toString();
    }


    // Creates the wordPattern
    public String createWordPattern(String currentWord, char guess){
        StringBuilder sb = new StringBuilder("");

        // Make the word pattern out of the currentWord and guessed character
        for (int i = 0; i < currentWord.length(); i++){
            if (currentWord.charAt(i) == guess){
                sb.append(guess);
            }
            // keep the letters in wordPattern that were guessed correctly
            else if (wordPattern.charAt(i) != '-'){
                sb.append(wordPattern.charAt(i));
            }
            else {
                sb.append('-');
            }
        }
        return sb.toString();
    }


    // getter for wordPattern - this is the patter of the hangman string
    public String getWordPattern(){
        return wordPattern;
    }


    // Builds the wordMap, Maps pattern/word pattern grouping to the words that follow the pattern
    public void buildWordMap(char guess){
        wordMap.clear();

        for(String word : dictionaryWords){
            String pattern = createWordPattern(word, guess);
            if (wordMap.containsKey(pattern)){
                wordMap.get(pattern).add(word); // grab the matching word group/pattern and add the word in the dictionary that matched
            }
            // if te word pattern didnt exist, creat it in the wordMap
            else {
                Set<String> patternWordSet = new TreeSet<String>();
                patternWordSet.add(word);
                wordMap.put(pattern, patternWordSet);
            }
        }
    }

    //
    public String chooseKey(char guess){

        String wordGroupPattern = "";

        // Use the set with the largest size
        selectSetWithMaxSize();

        if (wordMap.size() > 1) {
            selectSetByFrequency(guess);
        }
        if (wordMap.size() > 1) {
            filterRight(guess);
        }

        for (String s : wordMap.keySet()){
            wordGroupPattern = s;
        }

        wordPattern = wordGroupPattern;
        return wordGroupPattern;
    }

    // filter to the largest set size in the wordMap
    public void selectSetWithMaxSize() {
        int maxSize = 0;

        // Go through the word map
        for (Map.Entry<String, Set<String>> entry : wordMap.entrySet()){
            if (maxSize < entry.getValue().size()){
                maxSize = entry.getValue().size();  // Grabs the number of entries into the value set
            }
        }
        Set<String> notMaxSizeSet = new TreeSet<String>();

        // Find the set with the maxValue/get rid of the ones that aren't the max size
        for (Map.Entry<String, Set<String>> entry : wordMap.entrySet()){
            if (entry.getValue().size() != maxSize){
//                wordMap.remove(entry);
                notMaxSizeSet.add(entry.getKey());
            }
        }
        wordMap.keySet().removeAll(notMaxSizeSet);
    }


    // THIS NEEDS HELP
    public void selectSetByFrequency(char guess){
        int frequency = 50;

        // go through word map Set
        for(Map.Entry<String, Set<String>> entry: wordMap.entrySet()){
            int freq = 0;
            for(int i = 0; i < entry.getKey().length(); i++){
                if(entry.getKey().charAt(i) == guess){
                    freq++;
                }
            }

            // will allow for largest frequency to be saved
            if(freq < frequency){
                frequency = freq;

            }
        }

        Set<String> lowFrequencySetRemovable = new TreeSet<String>();
        for(Map.Entry<String, Set<String>> entry: wordMap.entrySet()){
            int freq = 0;
            for(int i = 0; i < entry.getKey().length(); i++){
                if(entry.getKey().charAt(i) == guess){
                    freq++;
                }
            }
            if(freq != frequency){
//                wordMap.remove(entry);
                lowFrequencySetRemovable.add(entry.getKey());
            }
        }
        wordMap.keySet().removeAll(lowFrequencySetRemovable);
    }

    // rightmost guess character kept
    public void filterRight(char guess){

        StringBuilder sb = new StringBuilder();
        // FInd out how long the word
        for(int i = 0; i < wordLen; i++){
            sb.append('-');
        }
        String winnerString = sb.toString();

        for(Map.Entry<String, Set<String>> entry: wordMap.entrySet()) {
            for(int i = wordLen - 1; i >= 0; i--) {
                //Match, continue
                if( winnerString.charAt(i) == entry.getKey().charAt(i)){
                    continue;
                }
                else {
                    // Keep winner
                    if (winnerString.charAt(i) == '-'){
                        winnerString = entry.getKey();
                        break;
                    }
                    else {
                        break;
                    }
                }
            }
        }

        Set<String> deletionSet = new TreeSet<String>();
        // Keep only the winnerString in entry
        for(Map.Entry<String, Set<String>> entry: wordMap.entrySet()){
            if (entry.getKey() != winnerString) {
                deletionSet.add(entry.getKey());
//                wordMap.remove(entry);
            }
        }
        wordMap.keySet().removeAll(deletionSet);


    }
}
