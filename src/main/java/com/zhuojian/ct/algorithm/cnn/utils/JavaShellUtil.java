package com.zhuojian.ct.algorithm.cnn.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jql on 2016/3/18.
 */


public class JavaShellUtil {

    private static final String separator = "@@@###@@@";

    private static final String shellPath = "/home/jql/test.sh";
    private static final String resultFile = "/home/jql/lung.rst";

    // 获取交互文件夹下的文件进行预测，并将结果从文件中读入，并解析成double类型的数组
    public static double[] execShellAndMatlab() throws Exception {

        Process process = Runtime.getRuntime().exec(shellPath);
        File rstFile = new File(resultFile);

        if (!rstFile.exists()) {
            List<String> error = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                error.add(line);
            }
            List<String> in = new ArrayList<>();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = reader.readLine()) != null) {
                in.add(line);
            }
            String msg = error.toString() + "\n-----\n" + in.toString();
            throw new Exception(msg);
        }

        BufferedReader reader = new BufferedReader(new FileReader(new File(resultFile)));
        String[] resultString = reader.readLine().trim().split(separator);
        double[] result = new double[resultString.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Double.parseDouble(resultString[i]);
        }
        return result;
    }

}
