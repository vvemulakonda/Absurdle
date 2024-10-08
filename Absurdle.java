// Vivek Vemulakonda
// 05/09/2024
// CSE 122
// TA: Megana Kommareddy, Rucha Kher
//This program will play a game of Absurdle using the words given to it in other file
import java.util.*;
import java.io.*;

public class Absurdle  {
    public static final String GREEN = "🟩";
    public static final String YELLOW = "🟨";
    public static final String GRAY = "⬜";

    // [[ ALL OF MAIN PROVIDED ]]
    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.println("Welcome to the game of Absurdle.");

        System.out.print("What dictionary would you like to use? ");
        String dictName = console.next();

        System.out.print("What length word would you like to guess? ");
        int wordLength = console.nextInt();

        List<String> contents = loadFile(new Scanner(new File(dictName)));
        Set<String> words = pruneDictionary(contents, wordLength);

        List<String> guessedPatterns = new ArrayList<>();
        while (!isFinished(guessedPatterns)) {
            System.out.print("> ");
            String guess = console.next();
            String pattern = recordGuess(guess, words, wordLength);
            guessedPatterns.add(pattern);
            System.out.println(": " + pattern);
            System.out.println();
        }
        System.out.println("Absurdle " + guessedPatterns.size() + "/∞");
        System.out.println();
        printPatterns(guessedPatterns);
    }

    // [[ PROVIDED ]]
    // Prints out the given list of patterns.
    // - List<String> patterns: list of patterns from the game
    public static void printPatterns(List<String> patterns) {
        for (String pattern : patterns) {
            System.out.println(pattern);
        }
    }

    // [[ PROVIDED ]]
    // Returns true if the game is finished, meaning the user guessed the word. Returns
    // false otherwise.
    // - List<String> patterns: list of patterns from the game
    public static boolean isFinished(List<String> patterns) {
        if (patterns.isEmpty()) {
            return false;
        }
        String lastPattern = patterns.get(patterns.size() - 1);
        return !lastPattern.contains("⬜") && !lastPattern.contains("🟨");
    }

    // [[ PROVIDED ]]
    // Loads the contents of a given file Scanner into a List<String> and returns it.
    // - Scanner dictScan: contains file contents
    public static List<String> loadFile(Scanner dictScan) {
        List<String> contents = new ArrayList<>();
        while (dictScan.hasNext()) {
            contents.add(dictScan.next());
        }
        return contents;
    }

    //B:It looks through the dictionary of the users choice and finds words that are the same
    //length as what the user wants
    //E:Will throw IllegalArgumentException if word length is less than 1
    //R:Will return a set of words that contain the same amount of letters as desired word length
    //P:List<String> contents:represents dictionary, int wordLength:desired wordlength
    public static Set<String> pruneDictionary(List<String> contents, int wordLength) {
        if(wordLength<1){
            throw new IllegalArgumentException();
        }
        Set<String> dicWords= new TreeSet<>();
        for(String word : contents){
            if(word.length()==wordLength){
                dicWords.add(word);
            }
        }
        return dicWords;
    }

    //B: Finds the best pattern that applies to most words in the dictionary and changes the 
    //available words to ones in the pattern. This ensures that the set of words is refined 
    //to the largest subset that matches the user's guess pattern, maximizing the potential words
    // remaining for future guesses.
    //E:Will throw IllegalArgumentException if the set of words is 0 or the 
    //guess length isn't correct length 
    //R:Will return a string that diplays the correct pattern
    //P:String guess:user guess,Set<String> words:availabe words, 
    //int wordLength:desired wordlength
    public static String recordGuess(String guess, Set<String> words, int wordLength) {
        if(words.size()==0 || guess.length()!= wordLength){
            throw new IllegalArgumentException();
        }
        Map<String, TreeSet<String>> patternMap = new TreeMap<>();
        for(String word : words){
            String pattern= patternFor(word, guess);
            if(!patternMap.containsKey(pattern)){
                patternMap.put(pattern, new TreeSet<>());
            }
            patternMap.get(pattern).add(word);
        }
        String corrPattern=wantPattern(patternMap);
        words.clear();
        words.addAll(patternMap.get(corrPattern));
        
        return corrPattern;
    }

    //B:Will find the sequence of colors that has the most words availabe
    //R:Will return a string of the pattern that has the most words
    //P:Map<String, TreeSet<String>> patternMap: the possible words with patterns
    public static String wantPattern(Map<String, TreeSet<String>> patternMap){
        String corrPattern="";
        int largestSize=0;
        for(String pattern: patternMap.keySet()){
            TreeSet<String> words=patternMap.get(pattern);
            int size=words.size();
            if(size>largestSize){
                largestSize=size;
                corrPattern=pattern;
            }
        }
        return corrPattern;
    }

    //B:Will generate a string depicting the pattern that compares the user's guess to the
    //possible word
    //R:Will return a string depicting the pattern
    //P:String word: possible word, String guess:user guess
    public static String patternFor(String word, String guess) {
        String finalResult="";
        List<String> charList= new ArrayList<>();
        Map<Character, Integer> countChar= new TreeMap<>();
        for(int i=0; i<guess.length();i++){
            charList.add(i, guess.charAt(i)+ "");
        }
        for(int i=0; i<charList.size();i++){
            char letter = word.charAt(i);
            if(!countChar.containsKey(letter)){
                countChar.put(letter, 1);
            }else{
                countChar.put(letter, countChar.get(letter) + 1);
            }
        }
        for(int i=0; i<guess.length();i++){
            if(guess.charAt(i)==word.charAt(i)){
                charList.set(i,GREEN);
            }else{
                for(int j=0;j<guess.length();j++){
                    char letter=guess.charAt(j);
                    if(guess.charAt(j)==word.charAt(i) && countChar.get(letter)>=1
                    && charList.get(j)!= GREEN){
                        charList.set(j,YELLOW);
                        countChar.put(letter, (countChar.get(letter)-1));
                    }
                }
            }
        }
        for(int i=0;i<word.length();i++){
            if(charList.get(i)!= YELLOW && charList.get(i)!=GREEN){
                charList.set(i,GRAY);
            }
            finalResult+=charList.get(i);
        }
        return finalResult;
    }
}
