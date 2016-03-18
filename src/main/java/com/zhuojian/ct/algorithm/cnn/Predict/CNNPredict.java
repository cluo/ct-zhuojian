package com.zhuojian.ct.algorithm.cnn.Predict;

import com.zhuojian.ct.algorithm.cnn.CNN;
import com.zhuojian.ct.algorithm.cnn.DataPreProcess.DcmConstant;
import com.zhuojian.ct.algorithm.cnn.utils.JavaShellUtil;
import com.zhuojian.ct.dicom.DicomReader;
import com.zhuojian.ct.dicom.DicomReaderImpl;

import java.io.File;

/**
 * Created by jql on 2016/3/16.
 */
public class CNNPredict {
    // 进程交互的目录
    private static final String DcmDir = "/home/jql/dicom/DcmDir";
    static {
        File dir = new File(DcmDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }



    private DicomReader reader = new DicomReaderImpl();
    private CNN cnn = CNN.loadModel("model");

    public int getPred(File file) throws Exception {
        double[] data = reader.readTmp128DataInLine(file, DcmConstant.XRESIZE, DcmConstant.YRESIZE);
        int rst = cnn.predict(data);
        if (rst < 0)
            throw new Exception("predict error...");
        return rst;
    }

    public int getPred(String uuid) throws Exception {
        if (!fileService(uuid))
            throw new Exception("file Service 服务执行失败。");

        double[] result = JavaShellUtil.execShellAndMatlab();
        double large = result[0];
        double small = result[1];
        double normal = result[2];
        if (large > small && large > normal)
            return 0;
        else if (small > large && small > normal)
            return 1;
        else
            return 2;
    }

    // 将数据库中的文件存放到进程交互的文件夹DcmDir下，成功则返回 true；否则返回 false。
    private boolean fileService(String uuid) {
        return true;
    }
}
