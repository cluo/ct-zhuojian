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

    public static void main(String[] args) throws Exception {
//        // 读取所有dicom数据，并转化成 128 * 128 大小的图片数据
//        BufferedReader reader = new BufferedReader(new FileReader(new File("/dcm.conf")));
//        String filePathDjj = reader.readLine();
//        String filePathXjj = reader.readLine();
//        reader.close();
//        ReadDicoms readDicoms = new ReadDicoms();
//        List<List<int[][]>> djj = readDicoms.readDcms(filePathDjj, "/djj.txt");
//        List<List<int[][]>> xjj = readDicoms.readDcms(filePathXjj, "/xjj.txt");

//        // 将图片分成3组：正常、大结节、小结节；标签分别是：0/1/2
        List<int[]> cou = pre();
        int djjNum = 0;
        for (int i = 0; i < cou.get(0).length - 1; i +=2) {
            djjNum += cou.get(0)[i + 1] - cou.get(0)[i] + 1;
        }
        System.out.println("all djj NUM is " + djjNum);
        int xjjNum = 0;
        for (int i = 0; i < cou.get(1).length - 1; i +=2) {
            xjjNum += cou.get(1)[i + 1] - cou.get(1)[i] + 1;
        }
        System.out.println("all xjj NUM is " + xjjNum);

        int djjTrainNum = (int) (djjNum * 0.7);
        int xjjTrainNum = (int) (xjjNum * 0.7);
        System.out.println("train djj data number is djj_number*0.7 " + djjTrainNum);
        System.out.println("train xjj data number is xjj_number*0.7 " + xjjTrainNum);

        Map<Integer, int[]> djjMap = remap(cou.get(0));
        List<List<int[]>> djjTrainAndTest = getShuffleData(djjMap, djjNum, djjTrainNum);

        Map<Integer, int[]> xjjMap = remap(cou.get(1));
        List<List<int[]>> xjjTrainAndTest = getShuffleData(xjjMap, xjjNum, xjjTrainNum);

    }

    private static List<int[]> pre() throws IOException {
        BufferedReader reader;
        String line;

        reader = new BufferedReader(new FileReader(new File("/djj.txt")));
        List<String> djjBzCoor = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            djjBzCoor.add(data[0]);
            djjBzCoor.add(data[1]);
        }
        reader.close();

        reader = new BufferedReader(new FileReader(new File("/xjj.txt")));
        List<String> xjjBzCoor = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            xjjBzCoor.add(data[0]);
            xjjBzCoor.add(data[1]);
        }
        reader.close();

        System.out.println("the number of djj: " + djjBzCoor.size() / 2);
        System.out.println("the number of djj: " + xjjBzCoor.size() / 2);
        int[] djjBzCo = new int[djjBzCoor.size()];
        for (int i = 0; i < djjBzCo.length; i ++) {
            djjBzCo[i] = Integer.parseInt(djjBzCoor.get(i));
        }
        int[] xjjBzCo = new int[xjjBzCoor.size()];
        for (int i = 0; i < xjjBzCo.length; i ++) {
            xjjBzCo[i] = Integer.parseInt(xjjBzCoor.get(i));
        }
        List<int[]> rst = new ArrayList<>(2);
        rst.add(djjBzCo);
        rst.add(xjjBzCo);
        return rst;
    }

    private static Map<Integer, int[]> remap(int[] bz) {
        Map<Integer, int[]> map = new HashMap<>(bz.length / 2);
        int count = 0;
        for (int i = 0; i < bz.length; i += 2) {
            for (int j =0; j < bz[i + 1] - bz[i + 0] + 1; j ++) {
                int[] dat = new int[2];
                dat[0] = i / 2;
                dat[1] = bz[i] + j;
                map.put(count ++, dat);
            }
        }
        return map;
    }
    private static List<List<int[]>> getShuffleData(Map<Integer, int[]> map, int num1, int num2) {
        List<Integer> shuffleNum = new ArrayList<>(num1);
        for (int i = 0; i < num1; i ++) {
            shuffleNum.add(i);
        }
        Collections.shuffle(shuffleNum);

        List<int[]> trainDataSuffix = new ArrayList<>(num2);
        List<int[]> testDataSuffix = new ArrayList<>(num1 - num2);
        for (int i = 0; i < num2; i ++) {
            trainDataSuffix.add(map.get(shuffleNum.get(i)));
        }
        for (int i = num2; i < num1; i ++) {
            testDataSuffix.add(map.get(shuffleNum.get(i)));
        }
        List<List<int[]>> rst = new ArrayList<>(2);
        rst.add(trainDataSuffix);
        rst.add(testDataSuffix);
        return rst;
    }
}
