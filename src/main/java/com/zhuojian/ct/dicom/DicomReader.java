package com.zhuojian.ct.dicom;

import java.io.File;
import java.io.IOException;

/**
 * Created by jql on 2016/3/14.
 */
public interface DicomReader {
    int[][] readDicomImageData(File file) throws IOException;
    int[][] readTmp384Data(File file, int[] x, int[] y) throws Exception;
    int[][] readTmp128Data(File file, int[] x, int[] y) throws Exception;
}
