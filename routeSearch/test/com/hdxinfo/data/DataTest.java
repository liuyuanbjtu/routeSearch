package com.hdxinfo.data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import org.junit.Test;

public class DataTest {
//	@Test
//	public void testStationsData() {
//		try {
//			Scanner s = new Scanner(new FileReader("E:\\WorkSpace\\routeSearch\\src\\com\\hdxinfo\\data\\Stations.txt"));
//			while(s.hasNext()) {
//				String str = s.nextLine();
//				System.out.println(str);
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
	
	@Test
	public void testReadIntervalTxt() {
		try {
			Scanner s = new Scanner(new FileReader("E:\\WorkSpace\\routeSearch\\src\\com\\hdxinfo\\data\\区间基本数据.txt"));
			s.nextLine();
			while(s.hasNext()) {
				String str = s.nextLine();
				String[] sArr = str.split("\t");
				if(Integer.parseInt(sArr[4]) == 449) {
					System.out.println(sArr[4]);
				}
				if(Integer.parseInt(sArr[5]) == 450) {
					System.out.println(sArr[5]);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
