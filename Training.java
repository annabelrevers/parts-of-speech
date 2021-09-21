/**
 * Training.java
 * 
 * This file contains the Training class, which
 * 
 * Annabel Revers, May 2020
 */

import java.io.*;
import java.util.*;

public class Training {
    
    /**************** loadObservations ****************/
    /*
     * Creates a map with parts of speech as keys and a map of observation words + their frequencies as values
     */
    public static Map<String,Map<String,Integer>> loadObservations(File posFile, File sentenceFile) throws FileNotFoundException, IOException {

        BufferedReader posReader = new BufferedReader(new FileReader(posFile)); //file
        BufferedReader sentenceReader = new BufferedReader(new FileReader(sentenceFile)); //file

        String line1;
        String line2;

        Map<String, Map<String,Integer>> tagMap = new HashMap<>();

        while ((line1 = posReader.readLine()) != null) {

            line2 = sentenceReader.readLine();

            String[] myLine1 = line1.split(" ");
            String[] myLine2 = line2.split(" ");


            for (int i = 0; i < myLine1.length; i++) {


                String pos = myLine1[i];
                String word = myLine2[i];
                word = word.toLowerCase();

                if (tagMap.containsKey(pos)) {

                    if (tagMap.get(pos).containsKey(word)) {
                        int freq = tagMap.get(pos).get(word);
                        tagMap.get(pos).put(word, freq + 1);
                    } else {
                        tagMap.get(pos).put(word, 1);
                    }
                } else {
                    tagMap.put(pos, new HashMap<>());
                    tagMap.get(pos).put(word, 1);

                }
            }

        }

        posReader.close();
        sentenceReader.close();


        return tagMap;
    }




    /**
     * Creates a map of transitions with observed parts of speech as keys and a map of subsequent parts of speech and
     * their frequencies as values.
     */
    public static Map<String,Map<String,Integer>> loadTransitions(File posFile, File sentenceFile) throws FileNotFoundException, IOException {

        BufferedReader posReader = new BufferedReader(new FileReader(posFile)); //file

        String line;


        //word, next word, frequency
        Map<String, Map<String, Integer>> transMap = new HashMap<>();

        transMap.put("#", new HashMap<>());

        while ((line = posReader.readLine()) != null) {

            String[] myLine = line.split(" ");

            for (int i = 0; i < myLine.length - 1; i++) {

                String curr = myLine[i];
                String next = myLine[i + 1];

                if (i == 0) {
                    if (transMap.get("#").containsKey(curr)) {
                        int freq = transMap.get("#").get(curr);
                        freq++;
                        transMap.get("#").put(curr, freq);
                    } else {
                        transMap.get("#").put(curr, 1);
                    }
                }

                if (transMap.containsKey(curr)) {

                    if (transMap.get(curr).containsKey(next)) {
                        int freq = transMap.get(curr).get(next);
                        freq++;
                        transMap.get(curr).put(next, freq);
                    } else {
                        transMap.get(curr).put(next, 1);
                    }
                } else {
                    transMap.put(curr, new HashMap<>());
                    transMap.get(curr).put(next, 1);
                }
            }
        }
        posReader.close();

        return transMap;
    }

    /**
     * Calculates the total number of observations for each part of speech
     */
    public static Map<String,Integer> posTotals(Map<String,Map<String,Integer>> observationMap) {
        Map<String,Integer> posTotals = new HashMap<>();

        for (String pos : observationMap.keySet()) {
            int i = 0;
            for (String word : observationMap.get(pos).keySet()) {
                int freq = observationMap.get(pos).get(word);
                i += freq;
            }
            posTotals.put(pos, i);
        }

        return posTotals;
    }


    /**
     * Calculates probabilities based on observations
     */
    public static Map<String,Map<String,Double>> obsProbability(Map<String,Map<String, Integer>> myMap, Map<String,Integer> posTotals) {

        Map<String,Map<String,Double>> probabilityMap = new HashMap<>();

        for (String pos : myMap.keySet()) {
            probabilityMap.put(pos, new HashMap<>());
            for (String word : myMap.get(pos).keySet()) {

                double numberOfElements = posTotals.get(pos);
                double freq = myMap.get(pos).get(word);
                double calc = Math.log((freq / numberOfElements));
                probabilityMap.get(pos).put(word, calc);
            }
        }
        return probabilityMap;
    }

    /**
     * Calculates probabilities based on transitions
     */
    public static Map<String,Map<String,Double>> transProbability(Map<String,Map<String, Integer>> myMap, Map<String,Integer> posTotals) {

        Map<String,Map<String,Double>> probabilityMap = new HashMap<>();
        double numberOfElements;

        for (String pos : myMap.keySet()) {
            probabilityMap.put(pos, new HashMap<>());
            for (String trans : myMap.get(pos).keySet()) {
                if (pos == "#") {
                    int i = 0;
                    for (String hashTrans : myMap.get("#").keySet()) {
                        int freq = myMap.get("#").get(hashTrans);
                        i += freq;
                    }
                    numberOfElements = i;
                }

                else {
                    numberOfElements = posTotals.get(pos);

                }

                double freq = myMap.get(pos).get(trans);
                double calc = Math.log((freq / numberOfElements));
                probabilityMap.get(pos).put(trans, calc);
            }
        }
        return probabilityMap;
    }
}
