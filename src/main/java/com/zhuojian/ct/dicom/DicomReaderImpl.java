package com.zhuojian.ct.dicom;

import fr.apteryx.imageio.dicom.DicomMetadata;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by jql on 2016/3/14.
 */
public class DicomReaderImpl implements DicomReader {
    @Override
    public int[][] readDicomImageData(String filePath) throws IOException {
        File f = new File(filePath);
        Iterator readers = ImageIO.getImageReadersByFormatName("dicom");
        fr.apteryx.imageio.dicom.DicomReader reader = (fr.apteryx.imageio.dicom.DicomReader) readers.next();
        reader.addIIOReadWarningListener(new WarningListener());
            reader.setInput(new FileImageInputStream(f));
            DicomMetadata dmd = reader.getDicomMetadata();
            int number = 0;
            BufferedImage bi_stored = reader.read(number);
            BufferedImage bi = dmd.applyGrayscaleTransformations(bi_stored, number);
            Raster raster = bi.getData();
            int height = raster.getHeight();
            int weight = raster.getWidth();
            DataBuffer dataBuffer = raster.getDataBuffer();
            int [][] data = new int[height][weight];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < weight; j++) {
                    data[i][j] = dataBuffer.getElem(i + j);
                }
            }
        return data;
    }

    @Override
    public int[][] readTmp384Data(String filePath, int[] x, int[] y) throws IOException {
        if (x.length != 2 || y.length != 2) {
            throw new IllegalArgumentException("x/y coordinate length must be two dimension.");
        }
        if ((x[1] - x[0]) != 384 || (y[1] - y[0]) != 384) {
            throw new IllegalArgumentException("the start of x/y must be 384 in this method");
        }
        int[][] origin = readDicomImageData(filePath);
        int[][] result = new int[384][384];
        for (int xLabel = x[0]; xLabel < x[1]; xLabel ++ ) {
            for (int yLabel = y[0]; yLabel < y[1]; yLabel ++) {
                result[yLabel - y[0]][xLabel - x[0]] = origin[xLabel][yLabel];
            }
        }
        return result;
    }

    @Override
    public int[][] readTmp128Data(String filePath, int[] x, int[] y) throws IOException {
        if (x.length != 2 || y.length != 2) {
            throw new IllegalArgumentException("x/y coordinate length must be two dimension.");
        }
        if ((x[1] - x[0]) != 128 || (y[1] - y[0]) != 128) {
            throw new IllegalArgumentException("the start of x/y must be 128 in this method");
        }
        int[][] origin = readDicomImageData(filePath);
        int[][] result = new int[128][128];
        for (int xLabel = x[0]; xLabel < x[1]; xLabel += 3 ) {
            for (int yLabel = y[0]; yLabel < y[1]; yLabel +=3) {
                result[yLabel - y[0]][xLabel - x[0]] = origin[xLabel][yLabel];
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        int[] x = {1, 385};
        int[] y= {2, 386};
        DicomReader reader = new DicomReaderImpl();
        reader.readDicomImageData("/a.dcm");
        reader.readTmp384Data("/a.dcm", x, y);
        reader.readTmp128Data("/a.dcm", x, y);
    }
}
