package com.zhuojian.ct.algorithm.cnn;

import java.io.IOException;

/**
 * ATTENTION!!!
 * 1. Record 中数据是按行组织的，不同于matlab的按列组织
 */
public class RunCNN {

	public static void runCnn() {
		String modelName = "E:\\dataset\\model.cnn";

		LayerBuilder builder = new LayerBuilder();
		builder.addLayer(Layer.buildInputLayer(new Layer.Size(28, 28)));
		builder.addLayer(Layer.buildConvLayer(6, new Layer.Size(5, 5)));
		builder.addLayer(Layer.buildSampLayer(new Layer.Size(2, 2)));
		builder.addLayer(Layer.buildConvLayer(12, new Layer.Size(5, 5)));
		builder.addLayer(Layer.buildSampLayer(new Layer.Size(2, 2)));
		builder.addLayer(Layer.buildOutputLayer(10));
		CNN cnn = new CNN(builder, 10);
		
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
