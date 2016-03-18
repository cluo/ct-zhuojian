package com.zhuojian.ct.algorithm.cnn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataSet {
	private List<Record> records;
	private int lableIndex;

	private double maxLable = -1;

	public DataSet() {
		records = new ArrayList<>();
	}

	public int size() {
		return records.size();
	}

	public int getLableIndex() {
		return lableIndex;
	}

	public void append(Record record) {
		records.add(record);
	}

	public void clear() {
		records.clear();
	}


	public Iterator<Record> iter() {
		return records.iterator();
	}

	public double[] getAttrs(int index) {
		return records.get(index).getAttrs();
	}

	public Double getLable(int index) {
		return records.get(index).getLable();
	}

	public DataSet load(String filePath, String tag) {
		File file = new File(filePath);
		try {
			BufferedReader ina = new BufferedReader(new FileReader(file + ".a"));
			BufferedReader inb = new BufferedReader(new FileReader(file + ".b"));
			String line;
			while ((line = ina.readLine()) != null) {
				String[] ches = line.split(tag);
				if (ches.length == 0)
					continue;
				double[] data = new double[ches.length + 1];
				for (int i = 0; i < ches.length; i++)
					data[i] = Double.parseDouble(ches[i]);
				data[data.length - 1] = Double.parseDouble(inb.readLine());
				Record record = new Record(data);
				append(record);
			}
			ina.close();
			inb.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("records " + records.size());
		return this;
	}

	public DataSet load(String filePath) {
		//TODO:
		return null;
	}

	public Record getRecord(int index) {
		return records.get(index);
	}

}
