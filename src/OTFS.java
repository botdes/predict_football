/*
 * Copyright (c) 2018 Schibsted Media Group. All rights reserved
 */

public class OTFS {
    int [] otfs = new int[8];

    void add(int points) {
        for(int i =0; i <= points; i++) {
            otfs[i] ++;
        }
    }

    void print(double iterations) {
        System.out.printf("Raw OTFS:   %d %d %d %d", otfs[1], otfs[3], otfs[5], otfs[7]);
        System.out.printf("Persentage: %.2f %.2f %.2f %.2f", otfs[1]/iterations,
                          otfs[3]/iterations, otfs[5]/iterations, otfs[7] /iterations);
    }

    String getString(double iterations) {
        return String.format("Persentage: %.2f %.2f %.2f %.2f", otfs[1]/iterations,
                             otfs[3]/iterations, otfs[5]/iterations, otfs[7] /iterations);
    }
}
