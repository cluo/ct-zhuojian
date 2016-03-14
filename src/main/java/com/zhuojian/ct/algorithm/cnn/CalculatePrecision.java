package com.zhuojian.ct.algorithm.cnn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by jql on 2016/3/11.
 */
public class CalculatePrecision {
    public double getPrecision(String file1, String file2) throws IOException {
        BufferedReader reader1 = new BufferedReader(new FileReader(new File("E:\\dataset\\result")));
        BufferedReader reader2 = new BufferedReader(new FileReader(new File("E:\\dataset\\test.predict")));
        String line1;
        String line2;
        int right = 0;
        int error = 0;

        while ((line1 = reader1.readLine()) != null) {
            if ((line2 = reader2.readLine()) != null) {
                if (line1.equals(line2)) {
                    right ++;
                } else {
                    error ++;
                }
            } else {
                System.out.println("the number of target and result is not the same size.");
            }
        }
        System.out.println("right number and error number: " + right + " + " + error);
        double precision = (double) right / (right + error);
        return precision;
    }

    public static void main(String[] args) throws IOException {
        new CalculatePrecision().getPrecision("E:\\dataset\\result", "E:\\dataset\\test.predict");
    }
}
