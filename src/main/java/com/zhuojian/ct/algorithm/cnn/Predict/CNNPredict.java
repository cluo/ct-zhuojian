package com.zhuojian.ct.algorithm.cnn.Predict;

import com.zhuojian.ct.algorithm.cnn.CNN;
import com.zhuojian.ct.algorithm.cnn.DataPreProcess.DcmConstant;
import com.zhuojian.ct.dicom.DicomReader;
import com.zhuojian.ct.dicom.DicomReaderImpl;

import java.io.File;

/**
 * Created by jql on 2016/3/16.
 */
public class CNNPredict {
    private DicomReader reader = new DicomReaderImpl();
    private CNN cnn = CNN.loadModel("model");

    public int getPred(File file) throws Exception {
        double[] data = reader.readTmp128DataInLine(file, DcmConstant.XRESIZE, DcmConstant.YRESIZE);
        int rst = cnn.predict(data);
        if (rst < 0)
            throw new Exception("predict error...");
        return rst;
    }
}
