package com.zhuojian.ct.algorithm.cnn.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by jql on 2016/3/18.
 */


public class JavaShellUtil {

    private static final String separator = "@#@#@";

    private static final String shellPath = "/home/jql/dicom/";
    private static final String resultFile = "/home/jql/dicom/DcmDir/";

    // 获取交互文件夹下的文件进行预测，并将结果从文件中读入，并解析成double类型的数组
    public static double[] execShellAndMatlab(String fileName) throws Exception {

        Process process = Runtime.getRuntime().exec("sh " + shellPath + "pred.sh " + fileName);
        process.waitFor(5, TimeUnit.SECONDS);

        File rstFile = new File(fileName + ".rst");

//        if (!rstFile.exists()) {
//            List<String> in = new ArrayList<>();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                in.add(line);
//            }
//            String msg = in.toString();
//            throw new Exception(msg);
//        }

        BufferedReader reader = new BufferedReader(new FileReader(new File(fileName + ".rst")));
        String[] resultString = reader.readLine().trim().split(separator);
        double[] result = new double[resultString.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Double.parseDouble(resultString[i]);
        }
        reader.close();
        return result;
    }

}
