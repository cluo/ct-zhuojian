package com.zhuojian.ct.algorithm.cnn;

import edu.hitsz.c102c.cnn.Layer.Size;
import edu.hitsz.c102c.dataset.DataSet;
import edu.hitsz.c102c.util.ConcurenceRunner;
import edu.hitsz.c102c.util.TimedTest;
import edu.hitsz.c102c.util.TimedTest.TestTask;

import java.io.IOException;

public class RunCNN {

	public static void runCnn() {
		//����һ�����������
		String modelName = "E:\\dataset\\model.cnn";

		LayerBuilder builder = new LayerBuilder();
		builder.addLayer(Layer.buildInputLayer(new Size(28, 28)));
		builder.addLayer(Layer.buildConvLayer(6, new Size(5, 5)));
		builder.addLayer(Layer.buildSampLayer(new Size(2, 2)));
		builder.addLayer(Layer.buildConvLayer(12, new Size(5, 5)));
		builder.addLayer(Layer.buildSampLayer(new Size(2, 2)));
		builder.addLayer(Layer.buildOutputLayer(10));
		CNN cnn = new CNN(builder, 10);
		
		//�������ݼ�
		String fileName = "E:\\dataset\\train";
		DataSet dataSet = new DataSet();
		dataSet.load(fileName, ",");
		cnn.train(dataSet, 1);//

		cnn.saveModel(modelName);
		//Ԥ��
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

		new TimedTest(new TestTask() {

			@Override
			public void process() {
				runCnn();
			}
		}, 1).test();
		ConcurenceRunner.stop();

	}

}
