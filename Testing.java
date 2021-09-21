/**
 * Viterbi algorithm and methods to test it
 *
 * @author Annabel Revers, Dartmouth CS 10, Spring 2020
 */

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Testing {

    Map<String, Map<String, Double>> observations;
    Map<String, Map<String, Double>> transitions;
    String start = "#";
    String best;

    public Testing(Map<String, Map<String, Double>> obsMap, Map<String, Map<String, Double>> transMap) {
        this.observations = obsMap;
        this.transitions = transMap;
    }


    /**
     * Viterbi algorithm
     */
    public ArrayList<String> viterbi(ArrayList<String> sentence) {

        // value for the probability of unseen words
        // use larger value for brown
        double neverSeen = -16;

        // declare double for adding scores
        Double nextScore;


        // set?
        // create an empty array and add start to it
        ArrayList<String> currStates = new ArrayList<>();
        currStates.add(start);

        // create an empty map to hold current scores
        Map<String, Double> currScores = new HashMap<>();
        currScores.put(start, 0.0);

        // create an array for backtracking
        ArrayList<HashMap<String, String>> backTrack = new ArrayList<>();

        for (int i = 0; i < sentence.size(); i++) {

            // create a new array to hold the next possible states
            ArrayList<String> nextStates = new ArrayList<>();
            // create a new map to hold the probability of the next possible states
            HashMap<String, Double> nextScoresMap = new HashMap<>();

            // create new map to add to the back track list and link current and next states
            HashMap<String, String> currBackTrack = new HashMap<>();
            backTrack.add(currBackTrack);

            // loop through each state in the list of current states
            for (String state : currStates) {

                if (transitions.containsKey(state)) {

                    // loop through the next possible states of the current state
                    for (String nextState : transitions.get(state).keySet()) {
                        // add that next possible state to the nextStatesLists
                        if (!nextStates.contains(nextState)) {
                            nextStates.add(nextState);
                        }

                        if (!observations.get(nextState).containsKey(sentence.get(i))) {
                            nextScore = currScores.get(state) + transitions.get(state).get(nextState) + neverSeen;

                        } else {
                            nextScore = currScores.get(state) + transitions.get(state).get(nextState) + observations.get(nextState).get(sentence.get(i));
                        }


                        if (!nextScoresMap.containsKey(nextState) || nextScore > nextScoresMap.get(nextState)) {
                            nextScoresMap.put(nextState, nextScore);
                            backTrack.get(i).put(nextState, state);

                        }

                    }
                }
            }
            currStates = nextStates;
            currScores = nextScoresMap;
        }

        Double largest = -1000.0;
        for (String state : currScores.keySet()) {
            if (currScores.get(state) > largest) {
                largest = currScores.get(state);
                best = state;
            }

        }

        ArrayList<String> finalPath = new ArrayList<>();
        String next = best;
        for (int i = backTrack.size() - 1; i >= 0; i--) {
            finalPath.add(0, next);
            String current = backTrack.get(i).get(next);
            next = current;

        }

        return finalPath;
    }

    /**
     * File-based test method that evaluates the viterbi algorithm's performance on a pair of test files
     */
    public void testFiles(File sentenceFile, File posFile) throws FileNotFoundException, IOException {

        BufferedReader sentenceReader = new BufferedReader(new FileReader(sentenceFile)); //file
        BufferedReader posReader = new BufferedReader(new FileReader(posFile)); //file

        String sentenceLine;
        String posLine;

        // these variables keep track of the total number of correct guesses and the total guesses
        Integer correct = 0;
        Integer total = 0;

        while ((sentenceLine = sentenceReader.readLine()) != null) {

            // get corresponding parts of speech for the sentence
            posLine = posReader.readLine();

            // split the sentence and add each word to the splitSentence String
            String[] splitSentence = sentenceLine.split(" ");
            // split the pos sentence and add each pos to the splitPOS String
            String[] splitPOS = posLine.split(" ");

            // create new array to pass in as parameter for the viterbi algorithm
            ArrayList<String> viterbiArray = new ArrayList<>();

            // create a new array to store the parts of speech for comparison
            ArrayList<String> posArray = new ArrayList<>();


            for (String pos : splitPOS) {
                posArray.add(pos);
            }


            for (String word : splitSentence) {
                word = word.toLowerCase();
                viterbiArray.add(word);
            }

            // call the viterbi method on the array
            ArrayList<String> viterbiPath = viterbi(viterbiArray);


            // for loop to compare the parts of speech viterbi guessed to the actual parts of speech of the sentence
            for (int i = 0; i < viterbiPath.size(); i++) {
                String vitPOS = viterbiPath.get(i);
                String realPOS = posArray.get(i);

                // increment the total number of guesses for each additional guess
                total++;

                // if the guess is correct, increment the correct guesses
                if (vitPOS.length() == realPOS.length()) {
                    int incorrect = 0;
                    for (int j = 0; j < vitPOS.length(); j++) {
                        int vitChar = (int) (vitPOS.charAt(j));
                        int posChar = (int) (realPOS.charAt(j));

                        if (vitChar != posChar) {
                            incorrect++;
                        }
                    }
                    if (incorrect == 0) {
                        correct++;
                    }
                }
            }

        }
        sentenceReader.close();
        posReader.close();

        System.out.println("The Viterbi algorithm correctly guessed " + correct + " tags out of " + total + " total tags.");

    }


    /**
     * Console-based test method that evaluates the viterbi algorithm's performance on a given sentence
     */

    public static void consoleTesting(Map<String,Map<String,Double>> obsMap, Map<String,Map<String,Double>> transMap) {

        //intro text
        System.out.println("Welcome to the console-based testing of the Viterbi algorithm.");

        // create scanner object
        Scanner myScanner = new Scanner(System.in);

        boolean run = true;

        while (run) {
            // get the sentence from the console
            String line;
            System.out.println("Enter a sentence:");
            line = myScanner.nextLine();
            line = line.toLowerCase();

            // COMMAND: "-1"
            // quit game
            if (line.equalsIgnoreCase("-1")) {
                System.out.println("You have quit. Thank you for testing");
                run = false;
            }
            else {
                // split sentence into words and put in an Arraylist
                String[] splitSentence = line.split(" ");

                // ArrayList for the words from the sentence
                ArrayList<String> viterbiArray = new ArrayList<>();

                // ArrayList for the parts of speech of the observed words
                //ArrayList<String> pos = new ArrayList<>();

                // add the words into the Array to be passed into the viterbi method
                for (String word : splitSentence) {
                    viterbiArray.add(word);
                }

                // create a testing object to be able to call the viterbi method on the array
                Testing consoleTesting = new Testing(obsMap, transMap);
                ArrayList<String> viterbiPath = consoleTesting.viterbi(viterbiArray);

                System.out.println(viterbiPath);
            }

        }

        myScanner.close();

    }


    public static void main(String[] args) {
        try {

            // get reference to training files
            String pathName2 = "brown-train-tags.txt";
            String pathName1 = "brown-train-sentences.txt";
            
            File trainSentences = new File(pathName1);
            File trainTags = new File(pathName2);

            // load the map of parts of speech observations and their word frequencies
            Map<String,Map<String,Integer>> observations = Training.loadObservations(trainTags, trainSentences);
            //System.out.println(observations);

            // load the map of parts of speech and their transition frequencies
            Map<String,Map<String,Integer>> transitions = Training.loadTransitions(trainTags, trainSentences);
            //System.out.println(transitions);

            // load the total number of observations for each part of speech
            Map<String,Integer> posTotals = Training.posTotals(observations);
            //System.out.println(posTotals);

            // load the probability maps for the word observations and transitions
            Map<String,Map<String,Double>> observationProb = Training.obsProbability(observations, posTotals);
            //System.out.println(observationProb);
            Map<String,Map<String,Double>> transProb = Training.transProbability(transitions, posTotals);
            //System.out.println(transProb);

            

            

            // test the consoleTest method
            consoleTesting(observationProb,transProb);

        }

        catch(Exception e) {
            e.printStackTrace();
        }

    }



}









