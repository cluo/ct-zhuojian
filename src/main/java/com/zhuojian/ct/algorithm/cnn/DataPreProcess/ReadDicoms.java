package com.zhuojian.ct.algorithm.cnn.DataPreProcess;

import com.zhuojian.ct.dicom.DicomReader;
import com.zhuojian.ct.dicom.DicomReaderImpl;

import java.io.*;
import java.util.*;

/**
 * Created by jql on 2016/3/14.
 */
public class ReadDicoms {
    private static final int XSTART = 100;
    private static final int XEND = 483 + 1;
    private static final int YSTART = 67;
    private static final int YEND = 450 + 1;

    private static final int[] XRESIZE = {XSTART, XEND};
    private static final int[] YRESIZE = {YSTART, YEND};

    private static final int MIN_START_DCM = 30;
    private static final int MAX_END_DCM = 120;

    public List<List<int[][]>> readDcms(String dirName, String indexPath) throws Exception {
        File parentDir = new File(dirName);
        if (!parentDir.isDirectory()) {
            throw new IllegalArgumentException("dirName must be a directory name:" + dirName);
        }
        File[] subDirList = parentDir.listFiles();
        if (subDirList.length <= 0) {
            return null;
        }
        DicomReader reader = new DicomReaderImpl();
        List<List<int[][]>> rst = new ArrayList<>();
        for (File dir : subDirList) {
            List<String> fileList = getFileList(dir, "dcm");
            System.out.println("read dcm size: " + fileList.size());
            List<int[][]> list = new ArrayList<>(subDirList.length);
            for (int i = 0; i < fileList.size(); i ++) {
                int[][] data = reader.readTmp128Data(new File(fileList.get(i)), XRESIZE, YRESIZE);
                list.add(data);
            }
            rst.add(list);
        }
        return rst;
    }

    /**
     *
     * @param dir 目录
     * @param type 文件类型
     * @return
     */
    public List<String> getFileList(File dir, String type) {
        List<String> result = new ArrayList<>();
        if (!dir.isDirectory()) {
            result.add(dir.getAbsolutePath());
        } else {
            File[] dirList = dir.listFiles(file -> {
                if (file.isFile() && file.getName().endsWith("." + type)) {
                    return true;
                } else {
                    return false;
                }
            });
            for (int i = 0; i < dirList.length; i++) {
                result.add(dirList[i].getAbsolutePath());
            }
        }
        return result;
    }

//    private List<int[]> getBzIndex(String path) {
//        List<int[]> list = new ArrayList<>();
//        return list;
//    }

    private static Map<String, List<List<int[]>>> before() throws Exception {
//        // 读取所有dicom数据，并转化成 128 * 128 大小的图片数据
//        BufferedReader reader = new BufferedReader(new FileReader(new File("/dcm.conf")));
//        String filePathDjj = reader.readLine();
//        String filePathXjj = reader.readLine();
//        reader.close();
//        ReadDicoms readDicoms = new ReadDicoms();
//        List<List<int[][]>> djj = readDicoms.readDcms(filePathDjj, "/djj.txt");
//        System.out.println("==========================");
//        List<List<int[][]>> xjj = readDicoms.readDcms(filePathXjj, "/xjj.txt");

        // 将图片分成3组：正常、大结节、小结节；标签分别是：0/1/2

        // 计算大结节切片数量
        // List 中的数组有2个元素，
        // 第一个是病灶的起始图片序号，
        // 第二个是病灶的结束图片序号,
        // 第三个是一个CT的切片数量
        List<int[]> cou = pre();
        int djjNum = 0;
        for (int i = 0; i < cou.get(0).length - 1; i +=2) {
            djjNum += cou.get(0)[i + 1] - cou.get(0)[i] + 1;
        }
        System.out.println("all djj NUM is " + djjNum);

        //计算小结节切片数量
        int xjjNum = 0;
        for (int i = 0; i < cou.get(1).length; i +=2) {
            xjjNum += cou.get(1)[i + 1] - cou.get(1)[i] + 1;
        }
        System.out.println("all xjj NUM is " + xjjNum);

        // 计算大结节CT中正常切片的数量
        int djjNormNum = 0;
        for (int i = 0; i < cou.get(2).length; i ++ ) {
            djjNormNum +=cou.get(2)[i];
        }
        djjNormNum = djjNormNum - djjNum;
        System.out.println("the number of djj normal data: " + djjNormNum );

        // 计算小结节CT中正常切片的数量
        int xjjNormNum = 0;
        for (int i = 0; i < cou.get(3).length - 1; i ++) {
            xjjNormNum += cou.get(3)[i];
        }
        xjjNormNum = xjjNormNum - xjjNum;
        System.out.println("the number of xjj normal data: " + xjjNormNum);

        // 数据：
        // 大结节 351 = 245 + 106
        // 小结节 248 = 173 + 75
        // 正常   451 = 282 + 169 （djj 200, xjj 250)

        // 大结节和小结节的病灶图片 训练和测试进行映射
        int djjTrainNum = (int) (djjNum * 0.7);
        int xjjTrainNum = (int) (xjjNum * 0.7);
        System.out.println("train djj data number is djj_number*0.7: " + djjTrainNum);
        System.out.println("train xjj data number is xjj_number*0.7: " + xjjTrainNum);

        // from 1 表示大结节的文件夹下的数据
        // from 2 表示小结节的文件夹下的数据
        Map<Integer, int[]> djjMap = remap(cou.get(0), 0);
        // 对大结节数据打乱后，抽取训练和测试数据
        List<List<int[]>> djjTrainAndTest = getShuffleData(djjMap, djjTrainNum);
        Map<Integer, int[]> xjjMap = remap(cou.get(1), 1);
        // 对小结节数据打乱后，抽取训练和测试数据
        List<List<int[]>> xjjTrainAndTest = getShuffleData(xjjMap, xjjTrainNum);

        // 大结节和小结节的正常图片 进行映射
        // from 1 表示大结节的文件夹下的数据
        // from 2 表示小结节的文件夹下的数据
        Map<Integer, int[]> djjNormMap = normRemap(cou.get(0), cou.get(2), 0);
        List<List<int[]>> djjNormTrainAndTest = getShuffleNormData(djjNormMap, 224, 93);
        Map<Integer, int[]> xjjNormMap = normRemap(cou.get(1), cou.get(3), 1);
        List<List<int[]>> xjjNormTrainAndTest = getShuffleNormData(xjjNormMap, 158, 66);

        Map<String, List<List<int[]>>> rst = new HashMap<>(8);
        rst.put("djj", djjTrainAndTest);
        rst.put("xjj", xjjTrainAndTest);
        rst.put("djjNorm", djjNormTrainAndTest);
        rst.put("xjjNorm", xjjNormTrainAndTest);
        return rst;

    }

    public static Map<String, List<int[]>> getTrainAndTest() throws Exception {
        Map<String, List<List<int[]>>> bef = before();
        List<List<int[]>> djjBz = bef.get("djj");
        List<List<int[]>> xjjBz = bef.get("xjj");
        List<List<int[]>> djjNorm = bef.get("djjNorm");
        List<List<int[]>> xjjNorm = bef.get("xjjNorm");

        List<int[]> train = new ArrayList<>();
        System.out.println("djj train image number: " + djjBz.get(0).size());
        train.addAll(djjBz.get(0));
        System.out.println("xjj train image number: " + xjjBz.get(0).size());
        train.addAll(xjjBz.get(0));
        System.out.println("djj normal train image number: " + djjNorm.get(0).size());
        train.addAll(djjNorm.get(0));
        System.out.println("xjj normal train image number: " + xjjNorm.get(0).size());
        train.addAll(xjjNorm.get(0));
        System.out.println("shuffle train data.");
        Collections.shuffle(train);


        List<int[]> test = new ArrayList<>();
        System.out.println("djj test image number: " + djjBz.get(1).size());
        test.addAll(djjBz.get(1));
        System.out.println("xjj test image number: " + xjjBz.get(1).size());
        test.addAll(xjjBz.get(1));
        System.out.println("djj normal test image number: " + djjNorm.get(1).size());
        test.addAll(djjNorm.get(1));
        System.out.println("djj normal image number: " + xjjNorm.get(1).size());
        test.addAll(xjjNorm.get(1));

        Map<String, List<int[]>> rst = new HashMap<>(2);
        rst.put("train", train);
        rst.put("test", test);
        return rst;
    }

    private static List<int[]> pre() throws IOException {
        BufferedReader reader;
        String line;

        reader = new BufferedReader(new FileReader(new File(TrainCnn.DJJ_TXT)));
        List<String> djjBzCoor = new ArrayList<>();
        List<String> djjNum = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            djjBzCoor.add(data[0]);
            djjBzCoor.add(data[1]);
            djjNum.add(data[2]);
        }
        reader.close();

        reader = new BufferedReader(new FileReader(new File(TrainCnn.XJJ_TXT)));
        List<String> xjjBzCoor = new ArrayList<>();
        List<String> xjjNum = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            xjjBzCoor.add(data[0]);
            xjjBzCoor.add(data[1]);
            xjjNum.add(data[2]);
        }
        reader.close();

        System.out.println("the number of djj: " + djjBzCoor.size() / 2);
        System.out.println("the number of xjj: " + xjjBzCoor.size() / 2);

        int[] djjBzCo = new int[djjBzCoor.size()];
        for (int i = 0; i < djjBzCo.length; i ++) {
            djjBzCo[i] = Integer.parseInt(djjBzCoor.get(i));
        }
        int[] djjNums = new int[djjNum.size()];
        for (int i = 0; i < djjNums.length; i ++) {
            djjNums[i] = Integer.parseInt(djjNum.get(i));
        }
        int[] xjjBzCo = new int[xjjBzCoor.size()];
        for (int i = 0; i < xjjBzCo.length; i ++) {
            xjjBzCo[i] = Integer.parseInt(xjjBzCoor.get(i));
        }
        int[] xjjNums = new int[xjjNum.size()];
        for (int i = 0; i < xjjNum.size(); i ++) {
            xjjNums[i] = Integer.parseInt(xjjNum.get(i));
        }


        List<int[]> rst = new ArrayList<>(4);
        rst.add(djjBzCo);
        rst.add(xjjBzCo);
        rst.add(djjNums);
        rst.add(xjjNums);
        return rst;
    }

    private static Map<Integer, int[]> remap(int[] bz, int from) {
        Map<Integer, int[]> map = new HashMap<>(bz.length / 2);
        int count = 0;
        for (int i = 0; i < bz.length; i += 2) {
            for (int j =0; j < (bz[i + 1] - bz[i] + 1); j ++) {
                int[] dat = new int[4];
                dat[0] = i / 2 + 1;
                dat[1] = bz[i] + j;
                dat[2] = from + 1;
                dat[3] = from;  //0表示大结节，1表示小结节
                map.put(count ++, dat);
            }
        }
        return map;
    }

    private static Map<Integer, int[]> normRemap(int[] index, int[] maxNum, int from) {
        if (index.length != maxNum.length * 2) {
            throw new IllegalArgumentException("length unmatched, please check your parameters.");
        }
        Map<Integer, int[]> map = new HashMap<>(4096);
        int count = 0;
        for (int i = 0; i < index.length; i +=2) {
            for (int j = 0; j < maxNum[i / 2]; j ++) {
                if (j > MIN_START_DCM && j < MAX_END_DCM) {
                    if (j < index[i] || j > index[i + 1]) {
                        int[] dat = new int[4];
                        dat[0] = i / 2 + 1;     //属于第几张CT
                        dat[1] = j;        //属于某张CT的第几个切片
                        dat[2] = from + 1;
                        dat[3] = 2;     //表示正常图片
                        map.put(count ++, dat);
                    }
                } else {
                    // 低于MIN_START_DCM 或者高于MAX_END_DCM的所有切片都删掉
                }
            }
        }
        return map;
    }

    /**
     *
     * @param map
     * @param num2 train image number
     * @return a list of two data, the first is the train and the second is the test, shuffle before return
     */
    private static List<List<int[]>> getShuffleData(Map<Integer, int[]> map, int num2) {
        List<Integer> shuffleNum = new ArrayList<>(map.size());
        for (int i = 0; i < map.size(); i ++) {
            shuffleNum.add(i);
        }
        Collections.shuffle(shuffleNum);

        List<int[]> trainDataSuffix = new ArrayList<>(num2);
        List<int[]> testDataSuffix = new ArrayList<>(map.size() - num2);
        for (int i = 0; i < num2; i ++) {
            trainDataSuffix.add(map.get(shuffleNum.get(i)));
        }
        for (int i = num2; i < map.size(); i ++) {
            testDataSuffix.add(map.get(shuffleNum.get(i)));
        }
        List<List<int[]>> rst = new ArrayList<>(2);
        rst.add(trainDataSuffix);
        rst.add(testDataSuffix);
        return rst;
    }

    /**
     *
     * @param map
     * @param num1  number of train data
     * @param num2  number of test data
     * @return
     */
    private static List<List<int[]>> getShuffleNormData(Map<Integer, int[]> map, int num1, int num2) {
        List<Integer> shuffleNum = new ArrayList<>(map.size());
        for (int i = 0; i < map.size(); i ++) {
            shuffleNum.add(i);
        }
        Collections.shuffle(shuffleNum);

        List<int[]> trainDataSuffix = new ArrayList<>(num1);
        List<int[]> testDataSuffix = new ArrayList<>(num2);
        for (int i = 0; i < num1; i ++) {
            trainDataSuffix.add(map.get(shuffleNum.get(i)));
        }
        for (int i = 0; i < num2; i ++) {
            testDataSuffix.add(map.get(shuffleNum.get(i + num1)));
        }
        List<List<int[]>> rst = new ArrayList<>(2);
        rst.add(trainDataSuffix);
        rst.add(testDataSuffix);
        return rst;
    }

    // test case
    public static void main(String[] args) throws Exception {
        getTrainAndTest();
    }
}
