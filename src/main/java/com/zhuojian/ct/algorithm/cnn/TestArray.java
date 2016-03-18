package com.zhuojian.ct.algorithm.cnn;


/**
 * ����Ԫ��ֱ�ӷ���������ͨ���������������Ч�ʣ� ���ۣ�������ʽ���ʲ�û�н����ٶ�
 * 
 * @author jiqunpeng
 * 
 *         ����ʱ�䣺2014-7-9 ����3:18:30
 */
public class TestArray {
	double[][] data;

	public TestArray(int m, int n) {
		data = new double[m][n];
	}

	public void set(int x, int y, double value) {
		data[x][y] = value;
	}

	private void useOrigin() {
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[0].length; j++)
				data[i][j] = i * j;
	}

	private void useFunc() {
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[0].length; j++)
				set(i, j, i * j);
	}


}
