/**
 * Created by jql on 2016/3/11.
 */
package com.zhuojian.ct.algorithm.cnn;
import java.util.Arrays;

public class Record {
    private double[] attrs;
    private Double lable;

    public Record(double[] attrs, Double lable) {
        this.attrs = attrs;
        this.lable = lable;
    }

    public Record(double[] data) {
        lable = data[data.length - 1];
        attrs = Arrays.copyOfRange(data, 0, data.length - 1);
    }

    public double[] getAttrs() {
        return attrs;
    }
    public Double getLable() {
        return lable;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("attrs:\n");
        int size = (int) Math.sqrt(attrs.length);
        for (int i = 0; i < size; i ++) {
            for (int j = 0; j < size; j++) {
                sb.append(attrs[j + size * i]);
            }
        }
        sb.append("\nlabel:\t");
        sb.append(lable);
        return sb.toString();
    }

    // �������ж����Ʊ���
    public int[] getEncodeTarget(int n) {
        String binary = Integer.toBinaryString(lable.intValue());
        byte[] bytes = binary.getBytes();
        int[] encode = new int[n];
        int j = n;
        for (int i = bytes.length - 1; i >= 0; i--)
            encode[--j] = bytes[i] - '0';

        return encode;
    }

    public double[] getDoubleEncodeTarget(int n) {
        String binary = Integer.toBinaryString(lable.intValue());
        byte[] bytes = binary.getBytes();
        double[] encode = new double[n];
        int j = n;
        for (int i = bytes.length - 1; i >= 0; i--)
            encode[--j] = bytes[i] - '0';

        return encode;
    }

}
