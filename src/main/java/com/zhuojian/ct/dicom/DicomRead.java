package com.zhuojian.ct.dicom;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.stream.FileImageInputStream;

import fr.apteryx.imageio.dicom.DicomMetadata;
import fr.apteryx.imageio.dicom.DicomReader;

public class DicomRead {
	public static void main(String[] args) {
		try {
			 ImageIO.scanForPlugins();
		      File f = new File("/a.dcm");
		      Iterator readers = ImageIO.getImageReadersByFormatName("dicom");
		      DicomReader reader = (DicomReader)readers.next();
		      reader.addIIOReadWarningListener(new WarningListener());
		      reader.setInput(new FileImageInputStream(f));
		      DicomMetadata dmd = reader.getDicomMetadata();
		      int number=0;
		      BufferedImage bi_stored = reader.read(number);
		      BufferedImage bi = dmd.applyGrayscaleTransformations(bi_stored, number);
		      Raster raster=bi.getData();
		      int height=raster.getHeight();
		      int weight=raster.getWidth();
		      System.out.println(height);
		      System.out.println(weight);
		      DataBuffer dataBuffer=raster.getDataBuffer();
		      System.out.println(dataBuffer.getSize());
		      System.out.println(dataBuffer.getDataType());
		      System.out.println(dataBuffer.getNumBanks());
		      System.out.println(dataBuffer.getOffset());
		      int[][] data=new int[height][weight];
//		      FileOutputStream fileOut = new FileOutputStream("/home/zfh/data.dat");
		      PrintWriter printWriter=new PrintWriter("/home/zfh/data.dat");
		      for (int i = 0; i < height; i++) {
		    	  for (int j = 0; j < weight; j++) {
						data[i][j]=dataBuffer.getElem(i+j);
						printWriter.print(data[i][j]+1024+" ");
					}
		    	  printWriter.println();
			}
		      printWriter.flush();
		      printWriter.close();
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
	}
}
