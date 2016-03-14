package com.zhuojian.ct.algorithm.cnn.DataPreProcess;

import com.zhuojian.ct.algorithm.cnn.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by jql on 2016/3/14.
 */
public class TrainCnn {

    public static void runCnn() {
        String modelName = "/dataset/dcm_model.cnn";

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
        CNN cnn = new CNN(builder, 20);

        try {
            Map<String, List<List<int[]>>> preDat = ReadDicoms.before();
        } catch (Exception e) {
            e.printStackTrace();
        }


        String fileName = "E:\\dataset\\train";
        DataSet dataSet = new DataSet();
        dataSet.load(fileName, ",");
        cnn.train(dataSet, 1);//

        cnn.saveModel(modelName);
        //CNN cnn = CNN.loadModel(modelName);

        DataSet testSet = new DataSet();
        testSet.load("E:\\dataset\\test", ",");
        cnn.predict(testSet, "E:\\dataset\\result");
        System.out.println("\n\n\n\n\n\n");
        try {
            System.out.println(new CalculatePrecision().getPrecision("E:\\dataset\\result", "E:\\dataset\\test.predict"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
