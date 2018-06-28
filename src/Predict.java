/*
 * Copyright (c) 2018 Schibsted Media Group. All rights reserved
 */

import java.util.*;

public class Predict {

    public static void main(String[] args) {
        String predictResult = "[10.88%]\n" +
                               "\t[25.71%]\n" +
                               "\t[63.41%]\n";

        String predictScores = "0 - 1\t [19.1%]\n" +
                               "0 - 2\t [17.5%]\n" +
                               "1 - 2\t [15.4%]\n" +
                               "1 - 1\t [15.28%]\n" +
                               "0 - 0\t [7.33%]\n" +
                               "1 - 3\t [5.4%]\n" +
                               "1 - 0\t [4.94%]\n" +
                               "0 - 3\t [4.2%]\n" +
                               "2 - 1\t [3.61%]";

        String betResult = "37/10\n" +
                           "Draw141/50\n" +
                           "Tunisia To Win17/20";
        
        String betCoefs = "(1 - 0) 51/4\t\n" +
                          "(0 - 0) 51/4\t\n" +
                          "(0 - 1) 38/5\n" +
                          "(2 - 0) 24/1\t\n" +
                          "(1 - 1) 23/4\t\n" +
                          "(0 - 2) 41/5\n" +
                          "(2 - 1) 51/4\t\n" +
                          "(2 - 2) 12/1\t\n" +
                          "(1 - 2) 36/5\n" +
                          "(3 - 0) 57/1\t\n" +
                          "(3 - 3) 52/1\t\n" +
                          "(0 - 3) 55/4\n" +
                          "(3 - 1) 37/1\t\n" +
                          "(1 - 3) 49/4\n" +
                          "(3 - 2) 37/1\t\n" +
                          "(2 - 3) 22/1\n" +
                          "(0 - 4) 14/1\n" +
                          "(1 - 4) 25/2\n" +
                          "(2 - 4) 24/1\n" +
                          "(0 - 5) 47/1\n" +
                          "(1 - 5) 42/1\n";


        final List<Double> results = buildResultsList(predictResult);
        final Map<String, Double> scores = buildScoresMap(predictScores);
        final List<Double> coefResults = buildMatchCoef(betResult);
        final ScoreCoefs scoreCoefs = buildScoreCoef(betCoefs);
        new Simulator(scoreCoefs, coefResults, results, scores).simulate();
        
    }

    private static List<Double> buildResultsList(String predictScores) {
        List<Double> resultList = new ArrayList<>();
        final String[] results = predictScores.split("\n");
        for (String result : results) {
            String percent = result.replaceAll("[^\\d\\\\.]", "");
            resultList.add(Double.parseDouble(percent));
        }
        return resultList;
    }

    private static Map<String, Double> buildScoresMap(String predictScores) {
        Map<String, Double> map = new HashMap<>();
        final String[] games = predictScores.split("\n");
        for (String game : games) {
            if(!game.trim().isEmpty()) {
                String[] tokens = game.replaceAll(" - ", "-").split("\t");
                String score = tokens[0];
                String percent = tokens[1].replaceAll("[^\\d\\\\.]", "");
                map.put(score, Double.parseDouble(percent));
            }
        }
        return map;
    }




    public static int getIntResult(String score) {
        final String[] scoreSplit = score.split("-");
        final int compare = Character.compare(scoreSplit[0].charAt(0), scoreSplit[1].charAt(0));
        return - Integer.compare(compare, 0);
    }

    private static ScoreCoefs buildScoreCoef(String betCoefs) {
        Map<String, Double> win = new HashMap<>();
        Map<String, Double> draw = new HashMap<>();
        Map<String, Double> lose = new HashMap<>();
        double winsum = 0;
        double drawsum = 0;
        double losesum = 0;


        for (String coef : betCoefs.split("\n")) {
            String[] tokens = coef.replaceAll(" - ", "-").split(" ");
            String score = tokens[0].replaceAll("[)(]", "");
            Double doubleCoef = parseCoefficient(tokens[1]);
//            System.out.printf("%s %.2f\n", score, doubleCoef);
            switch (getIntResult(score)){
                case -1: win.put(score, doubleCoef); winsum+=doubleCoef;
                    break;
                case 0: draw.put(score, doubleCoef); drawsum +=doubleCoef;
                    break;
                case 1: lose.put(score, doubleCoef); losesum += doubleCoef;
                    break;
            }
        }
        System.out.println("Score Coefficients Raw : " + new ScoreCoefs(win, draw, lose));
        final double total = winsum + drawsum + losesum;
        normalise(win, winsum);
        normalise(draw, drawsum);
        normalise(lose, losesum);

        System.out.printf("winsum=%.2f drawsum=%.2f losesum=%2f total=%2f\n", winsum, drawsum, losesum, total);
        System.out.printf("Match Coefs from score:  %.2f %.2f %.2f\n", winsum / total, drawsum/total, losesum/total);
        final ScoreCoefs scoreCoefs = new ScoreCoefs(win, draw, lose);
        System.out.println("Score Coefficients Norm: " + scoreCoefs);
        return scoreCoefs;
    }

    public static Double parseCoefficient(String token) {
        String[] dividers = token.trim().split("/");
        final double first = Double.parseDouble(dividers[0]);
        final double second = Double.parseDouble(dividers[1]);
        return second / (second + first);
    }

    private static void normalise(Map<String, Double> map, double sum) {
        for (String s : map.keySet()) {
            map.put(s, map.get(s) / sum);
        }

    }

    private static List<Double> buildMatchCoef(String betResults) {
        final String[] lines = betResults.split("\n");
        List<Double> results = new ArrayList<>();
        for (String line : lines) {
            String percent = line.replaceAll("[^\\d\\\\./]", "");
            results.add(parseCoefficient(percent));
        }
        double sum = 0;
        for (Double result : results) {
            sum += result;
        }
        final ArrayList<Double> normalisedResults = new ArrayList<>();
        for (Double result : results) {
            normalisedResults.add(result / sum);
        }
        System.out.println("Match Coefficients Raw : " + results);
        System.out.println("total = " + sum);
        System.out.println("Match Coefficients Norm: " + normalisedResults);
        return normalisedResults;
    }
}
