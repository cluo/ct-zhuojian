package com.zhuojian.ct.dicom;

/**
 * Created by jql on 2016/3/14.
 */
public interface DicomReader {
    int[][] readDicomImageData(String filePath);
    int[][] readTmp384Data(String filePath, int[] x, int[] y);
    int[][] readTmp128Data(String filePath, int[] x, int[] y);
}
