/*
 * Copyright (c) 2018 Schibsted Media Group. All rights reserved
 */

import java.util.*;

public class Simulator {

    public static final int RESULT_THRESHOLD = 20;
    public static final int SCORE_THRESHOLD = 5;
    public static final int ITERATIONS = 100000;

    private ScoreCoefs coefScoreMaps;
    private List<Double> coefMatchResults;
    private List<Double> peopleMatchResults;
    private Map<String, Double> peopleScoresMap;

    private Random random = new Random();

    public Simulator(
            ScoreCoefs coefScoreMaps,
            List<Double> coefMatchResults,
            List<Double> peopleMatchResults,
            Map<String, Double> peopleScoresMap) {
        this.coefScoreMaps = coefScoreMaps;
        this.coefMatchResults = coefMatchResults;
        this.peopleMatchResults = peopleMatchResults;
        this.peopleScoresMap = peopleScoresMap;
    }

    public void simulate() {
        Map<String, Integer> totalScores = new HashMap<>();
        Map<Integer, Integer> totalResults = new HashMap<>();
        Map<String, OTFS> scoreDistribution = new HashMap<>();
        for(int iteration = 0; iteration < ITERATIONS; iteration++) {
            double currentMatchResultOffset = 0;
            double matchSimulationResult = random.nextDouble();
            int intMatchResult = G.WIN; //-1 win 0 draw,
            for (Double coefResult : coefMatchResults) {
                if (coefResult + currentMatchResultOffset >= matchSimulationResult) {
                    simulateScore(intMatchResult, totalScores, scoreDistribution);
                    totalResults.putIfAbsent(intMatchResult, 0);
                    totalResults.put(intMatchResult, totalResults.get(intMatchResult) + 1);
                    break;
                } else {
                    currentMatchResultOffset += coefResult;
                    intMatchResult++;
                }
            }

        }
        final ArrayList<Map.Entry<String, Integer>> sortedResults = new ArrayList<>(totalScores.entrySet());
        sortAndPrint(sortedResults, scoreDistribution);
        System.out.println();
        double totalSum = 0;
        for (Integer integer : totalResults.values()) {
            totalSum += integer;
        }
        for (Map.Entry<Integer, Integer> entry : totalResults.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue() + " " + (entry.getValue() / totalSum));
        }
    }

    private void simulateScore(int intMatchResult,
                               Map<String, Integer> totalResults,
                               Map<String, OTFS> scoreDistribution) {
        double scoreSimulationResult = random.nextDouble();
        double current = 0;
        final List<String> coefKeys;
        final Map<String, Double> coefScoreMap;
        if (intMatchResult == G.WIN){
            coefKeys = coefScoreMaps.winKeys;
            coefScoreMap = coefScoreMaps.win;
        } else if (intMatchResult == G.DRAW) {
            coefKeys = coefScoreMaps.drawKeys;
            coefScoreMap = coefScoreMaps.draw;
        } else {
            coefKeys = coefScoreMaps.loseKeys;
            coefScoreMap = coefScoreMaps.lose;

        }

        for (String actualScore : coefKeys) {
            final Double probability = coefScoreMap.get(actualScore);
            current += probability;
            if (scoreSimulationResult < current) {
//                System.out.println(actualScore);
                for (String score : G.SCORES) {
                    totalResults.putIfAbsent(score, 0);
                    scoreDistribution.putIfAbsent(score, new OTFS());
                    final int intScore = score(actualScore,
                                               score,
                                               peopleScoresMap.getOrDefault(score, 0.0),
                                               peopleMatchResults
                                              );
                    scoreDistribution.get(score).add(intScore);
                    totalResults.put(score, totalResults.get(score) + intScore);
                }
                break;
            }
        }
    }

    private  void sortAndPrint(ArrayList<Map.Entry<String, Integer>> sortedResults, Map<String, OTFS> scoreDistribution) {
        Collections.sort(sortedResults, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return Integer.compare(o1.getValue(), o2.getValue());
            }
        });
        for (Map.Entry<String, Integer> entry : sortedResults) {
            System.out.println(entry + " " + scoreDistribution.get(entry.getKey()).getString(ITERATIONS));
        }
    }

    private  int score(String actualScore, String score, Double percentage, List<Double> results) {
        if (Objects.equals(actualScore, score)) {
            int base = 3;
            if (percentage < SCORE_THRESHOLD) {
                base +=2;
            }
            int pos = Predict.getIntResult(actualScore) + 1;
//            System.out.println(pos);
            if (results.get(pos) < RESULT_THRESHOLD) {
                base +=2;
            }
            return base;
        } else {
            final int actualResult = Predict.getIntResult(actualScore);
            final int scoreResult = Predict.getIntResult(score);
            if (actualResult == scoreResult) {
                int base = 1;
                int pos = Predict.getIntResult(actualScore) + 1;
                if (results.get(pos) < RESULT_THRESHOLD) {
                    base +=2;
                }
                return base;
            } else {
                return 0;
            }
        }


    }

}
