package com.zhuojian.ct.dicom;

import java.io.IOException;

/**
 * Created by jql on 2016/3/14.
 */
public interface DicomReader {
    int[][] readDicomImageData(String filePath) throws IOException;
    int[][] readTmp384Data(String filePath, int[] x, int[] y) throws Exception;
    int[][] readTmp128Data(String filePath, int[] x, int[] y) throws Exception;
}
