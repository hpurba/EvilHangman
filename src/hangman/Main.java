package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main{
    public static void main(String[]args) throws IOException, EmptyDictionaryException {

        // initialize
        String dictionaryName = "";
        int wordLength = 0;
        int guesses = 0;

        // We need the correct number of arguments: dictionaryName, wordLength, and numGuesses
        if(args.length == 3){
            dictionaryName = args[0];
            wordLength = Integer.parseInt(args[1]);
            guesses = Integer.parseInt(args[2]);
        }
        else{
            System.out.print("Try again with: java Main dictionary.txt wordLength numGuesses");
        }

        EvilHangmanGame hangmanGame = new EvilHangmanGame();   // instantiates a new EvilHangman Game
        hangmanGame.startGame(new File(dictionaryName), wordLength);    // start the game by giving the hangmanGame object dictionary file and word length
        playHangmanGame(hangmanGame, guesses);   // To run the game, use hangmanGame object with numGuesses
    }

    // Takes in the started Hangman game. Also passed in are the number of guesses
    public static void playHangmanGame(EvilHangmanGame hangmanGame, int guesses){

        SortedSet<Character> usedLetters = hangmanGame.getGuessedLetters();
        System.out.println("Used letters: " + usedLetters);
        String pattern = hangmanGame.instantiatePattern();

        // Function to print the status of the game
        printGameStatus(guesses, usedLetters, pattern);

        // Get user input [must be a character]!
        Scanner playerInput = new Scanner(System.in);
        String inputChar = "";  // user inputs a word?
        char charGuess = 0;     // input char is the charGuess

        while(guesses > 0){
            Set<String> resultingDictionary = new TreeSet<String>();
            inputChar = playerInput.nextLine();

            if (inputChar.isEmpty()){
                System.out.print("Invalid input\n\n");
                // Reprompt
                // Function to print the status of the game
                printGameStatus(guesses, usedLetters, pattern);
                continue;
            }

            // maybe try a while loop until the input is valid
            // Character entered is invalid
            if(!Character.isLetter(inputChar.charAt(0)) || Character.isWhitespace(inputChar.charAt(0))){    // inputChar.length() != 1 ||
                System.out.print("Invalid input\n\n");
                // Reprompt
                // Function to print the status of the game
                printGameStatus(guesses, usedLetters, pattern);
                continue;
            }
            // if the string entered is empty
            if (inputChar.length() == 0){
                System.out.print("Invalid input\n\n");
                // Reprompt
                // Function to print the status of the game
                printGameStatus(guesses, usedLetters, pattern);
                continue;
            }
            // User inputs a string of letters
            if (inputChar.length() > 1){
                System.out.print("Invalid input\n\n");
                printGameStatus(guesses, usedLetters, pattern);
                continue;
            }


            // VALID INPUT
            // set the guess
            charGuess = inputChar.charAt(0);    // grabs the user input character/ guessed character
            try{
                resultingDictionary = hangmanGame.makeGuess(charGuess);
                pattern = hangmanGame.getWordPattern();

                if(pattern.contains(String.valueOf(charGuess))){
                    int numchars = 0;
                    for(int i = 0; i < pattern.length(); i++){
                        if(pattern.charAt(i) == charGuess)
                            numchars++;
                    }
                    guesses++;
                    printResponse(true, charGuess, numchars);
                } else {
                    printResponse(false, charGuess, 0);
                }
            } catch (GuessAlreadyMadeException e){
                System.out.println("You already used that letter.\n");
                printGameStatus(guesses, usedLetters, pattern);
                continue;
            }

            int count = 0;
            for(int i = 0; i < pattern.length(); i++){
                if(pattern.charAt(i) != '-')
                    count++;
            }
            if(count == pattern.length()){
                printWinCase(resultingDictionary.toString());
                break;
            }
            guesses --;
            if(guesses == 0){
                String word = resultingDictionary.iterator().next();
                printLoseCase(word);
            }
            printGameStatus(guesses, usedLetters, pattern);
        }


        if(playerInput != null){
            playerInput.close();
        }

    }


    // Prints the status of the game
    public static void printGameStatus(int guesses, SortedSet<Character> usedLetters, String wordPattern){   // word pattern shows what letters are guessed correctly with context of dashes

        // More than 1 guesses left
        if (guesses > 1){
            System.out.println("You have " + guesses + " guesses left");
        }
        // only one guess left
        else {
            System.out.println("You have " + guesses + " guess left");
        }

        // Build String of letters used/guessed so far
        StringBuilder sb = new StringBuilder();
        for (Character letter: usedLetters){
            sb.append(letter);
            sb.append(" ");
        }

        System.out.println("Used letters: " + sb.toString());
        System.out.println("Word: " + wordPattern);
        System.out.print("Enter guess: ");
    }


    // Print the guess response, yes or no
    public static void printResponse(boolean isContained, char guess, int numbers){

        if(isContained) {
            System.out.println("Yes, there is " + numbers + " " + guess + "\n");
        }
        else {
            System.out.println("Sorry, there are no " + guess + "'s\n");
        }
        // might need to change this because of multiple letters present of a correct guess
    }


    // Print the Win case
    public static void printWinCase(String correct){
        System.out.println("You win! " + correct);
        System.exit(0);
    }

    // Print the lose case
    public static void printLoseCase(String correct){
        System.out.println("You lose!");
        System.out.println("The word was: " + correct);
        System.exit(0);
    }
}