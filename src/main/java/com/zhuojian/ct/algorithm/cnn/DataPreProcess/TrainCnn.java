package com.zhuojian.ct.algorithm.cnn.DataPreProcess;

import com.zhuojian.ct.algorithm.cnn.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by jql on 2016/3/14.
 */
public class TrainCnn {

//    public static final String DCM_DIRECTORY = "E:\\javaDcmData\\dcm";
//    public static final String DJJ_TXT = "/djj.txt";
//    public static final String XJJ_TXT = "/xjj.txt";
    public static final String DCM_DIRECTORY = "/home/jql/dcm";
    public static final String DJJ_TXT = "/home/jql/conf/djj.txt";
    public static final String XJJ_TXT = "/home/jql/conf/xjj.txt";

    public static void runCnn() {
        String modelName = "/home/jql/dcm_model_2000.cnn";

        // 构建卷积神经网络
        LayerBuilder builder = new LayerBuilder();
        builder.addLayer(Layer.buildInputLayer(new Layer.Size(128, 128)));
        builder.addLayer(Layer.buildConvLayer(3, new Layer.Size(5, 5)));
        builder.addLayer(Layer.buildSampLayer(new Layer.Size(2, 2)));
        builder.addLayer(Layer.buildConvLayer(6, new Layer.Size(3, 3)));
        builder.addLayer(Layer.buildSampLayer(new Layer.Size(2, 2)));
        builder.addLayer(Layer.buildConvLayer(5, new Layer.Size(3, 3)));
        builder.addLayer(Layer.buildSampLayer(new Layer.Size(2, 2)));
        builder.addLayer(Layer.buildOutputLayer(3));
        CNN cnn = new CNN(builder, 80);

        try {
            Map<String, List<int[]>> trainAndTest = ReadDicoms.getTrainAndTest();
            List<int[]> train = trainAndTest.get("train");
            List<int[]> test = trainAndTest.get("test");
            long mm1 = System.currentTimeMillis();
			int epoch = 2000;
            cnn.train(train, epoch, DCM_DIRECTORY);
            long mm2 = System.currentTimeMillis();
            System.out.println(epoch + " epoch: " + ((mm2 - mm1) / 1000));

        } catch (Exception e) {
            e.printStackTrace();
        }

        cnn.saveModel(modelName);
        System.out.println("model is save in " + modelName);
//        CNN cnn = CNN.loadModel(modelName);

//        DataSet testSet = new DataSet();
//        testSet.load("E:\\dataset\\test", ",");
//        cnn.predict(testSet, "E:\\dataset\\result");
//        System.out.println("\n\n\n\n\n\n");
//        try {
//            System.out.println(new CalculatePrecision().getPrecision("E:\\dataset\\result", "E:\\dataset\\test.predict"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void main(String[] args) {

        new TimedTest(new TimedTest.TestTask() {

            @Override
            public void process() {
                runCnn();
            }
        }, 1).test();
        ConcurenceRunner.stop();

    }
}
