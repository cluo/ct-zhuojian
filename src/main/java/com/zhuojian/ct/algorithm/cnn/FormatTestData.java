package com.zhuojian.ct.algorithm.cnn;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jql on 2016/3/11.
 */
public class FormatTestData {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("E:\\dataset\\train.format"))));
        List<String> data = new ArrayList<>();
        List<String> label = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            int index = line.lastIndexOf(",");
            data.add(line.substring(0, index));
            label.add(line.substring(index + 1, index + 2));
        }
        BufferedWriter writera = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("E:\\dataset\\train.a.format"))));
        BufferedWriter writerb = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("E:\\dataset\\train.b.format"))));
        for (int i = 0; i < data.size(); i ++) {
            writera.write(data.get(i) + "\n");
            writerb.write(label.get(i) + "\n");
        }
        writera.close();
        writerb.close();
        reader.close();
    }
}
