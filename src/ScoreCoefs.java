/*
 * Copyright (c) 2018 Schibsted Media Group. All rights reserved
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoreCoefs {

    public final Map<String, Double> win;
    public final Map<String, Double> draw;
    public final Map<String, Double> lose;
    public final List<String> winKeys;
    public final List<String> drawKeys;
    public final List<String> loseKeys;

    public ScoreCoefs(Map<String, Double> win, Map<String, Double> draw, Map<String, Double> lose) {
        this.win = win;
        this.draw = draw;
        this.lose = lose;
        winKeys = new ArrayList<>(win.keySet());
        drawKeys = new ArrayList<>(draw.keySet());
        loseKeys = new ArrayList<>(lose.keySet());
    }

    @Override
    public String toString() {
        return "ScoreCoefs{\n" +
               " win = " + getString(win) +
               ",\n draw= " + getString(draw) +
               ",\n lose= " + getString(lose) +
               "\n}";
    }

    private String getString(Map<String, Double> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(":").append(String.format("%.2f", entry.getValue())).append(", ");
        }
        return sb.toString();
    }
}
