package com.zhuojian.ct.algorithm.cnn.DataPreProcess;

/**
 * Created by jql on 2016/3/15.
 */
public class DcmConstant {
    public static final int XSTART = 100;
    public static final int XEND = 483 + 1;
    public static final int YSTART = 67;
    public static final int YEND = 450 + 1;

    public static final int[] XRESIZE = {XSTART, XEND};
    public static final int[] YRESIZE = {YSTART, YEND};

    public static final int LARGER_NODULE = 0;
    public static final int SMALL_NODULE = 1;
    public static final int NORMAL_NODULE = 2;

}
