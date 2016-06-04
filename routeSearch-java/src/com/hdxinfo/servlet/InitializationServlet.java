package com.hdxinfo.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.hdxinfo.bean.BreakBean;
import com.hdxinfo.bean.Station;
import com.hdxinfo.service.RouteSearchService;

import netWork.Network;
import pathFinding.FindRoute;

public class InitializationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
		
		System.out.println("kaishijiazai-------------------");
		
		String dataPath = getServletContext().getRealPath("/");
		long timeStart = System.currentTimeMillis();
		Network network = new Network();
		/*network.LoadStationInfo(dataPath+ "\\WEB-INF\\classes\\com\\hdxinfo\\data\\Stations.txt");
		network.LoadFiledTable(dataPath+ "\\WEB-INF\\classes\\com\\hdxinfo\\data\\区间基本数据.txt");
		network.LoadTranferInfo(dataPath+ "\\WEB-INF\\classes\\com\\hdxinfo\\data\\TransferStation.txt");	
		network.LoadTimeTableall(dataPath+ "\\WEB-INF\\classes\\com\\hdxinfo\\data\\time_table_all.txt");
		network.loadLineTrainLimit(dataPath+ "\\WEB-INF\\classes\\com\\hdxinfo\\data\\列车定员.txt");
*/		network.LoadStationInfo(dataPath+ "\\WEB-INF\\classes\\com\\hdxinfo\\data\\Stations_0306.txt");
		network.LoadFiledTable(dataPath+ "\\WEB-INF\\classes\\com\\hdxinfo\\data\\区间基本数据_0306.txt");
		network.LoadTranferInfo(dataPath+ "\\WEB-INF\\classes\\com\\hdxinfo\\data\\TransferStation_0306.txt");	
		network.LoadTimeTableall(dataPath+ "\\WEB-INF\\classes\\com\\hdxinfo\\data\\time_table_all_0306.txt");
		network.loadLineTrainLimit(dataPath+ "\\WEB-INF\\classes\\com\\hdxinfo\\data\\列车定员_0306.txt");

		try {
			LoadTarget_lineInfo(dataPath+ "\\WEB-INF\\classes\\com\\hdxinfo\\data\\target_mainline", network);
			LoadTarget_stationInfo(dataPath+ "\\WEB-INF\\classes\\com\\hdxinfo\\data\\target_station", network);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("kaishi123-------------------");
		
		FindRoute find = new FindRoute();
		find.setNetwork(network);
		find.initODInfo();
		//find.findLatestRoutes();
		System.out.println("kaishi1234-------------------");
		for (int i = 0; i < 3; i++) {
			find.findShortestRoutes(i, 0);
		}
		System.out.println("kaishi12345-------------------");
		find.ReleaseShortestRoute();
		for (int i = 0; i < 3; i++) {
			find.findReasonableRoute(i, 0);
		}
		System.out.println("kaishi123456-------------------");
		find.routes_shrink2_6(0);
		find.routes_shrink(0);
		List<BreakBean> breakList = initBreak();
		
		int length = breakList.size();
		
		for(int i = 0; i < length; i++) {
			BreakBean bean1 = breakList.get(i);
			String startName = bean1.getSStation().getStationName();
			String sLineName = bean1.getSStation().getLineName();
			String endName = bean1.getEStation().getStationName();
			String eLineName = bean1.getEStation().getLineName();
			if(sLineName.equals(eLineName)) {
				find.network.SetBreak(sLineName, startName, endName);
			} else {
				find.network.SetBreak(sLineName, eLineName, startName, endName, 0, 0);
			}
		}
		
		for (int j = 0; j < 3; j++) {
			find.findShortestRoutes(j, 1);
		}
		find.ReleaseShortestRoute();
		for (int j = 0; j < 3; j++) {
			find.findReasonableRoute(j, 1);
		}
		find.routes_shrink2_6(1);
		find.routes_shrink(1);
		find.computerFastTime();

		System.out.println("jiazaijiesu--------------------");
		System.out.println("yongshi" + (System.currentTimeMillis() - timeStart)+ "ms");
		
		RouteSearchService.find = find;
		
		File file = new File(dataPath+ "\\WEB-INF\\classes\\com\\hdxinfo\\data\\Stations_0306.txt");
		
		RouteSearchService.obj = initStationName(file);
		
		RouteSearchService.breakList = breakList;
	}
	
	private List<BreakBean> initBreak() {
		List<BreakBean> breakList = new ArrayList<BreakBean>();
		BreakBean b1 = new BreakBean();
		BreakBean b2 = new BreakBean();
		BreakBean b3 = new BreakBean();
		BreakBean b4 = new BreakBean();
		BreakBean b5 = new BreakBean();
		BreakBean b6 = new BreakBean();
		BreakBean b7 = new BreakBean();
		BreakBean b8 = new BreakBean();
		
		Station s1 = new Station();
		
		s1.setStationName("雍和宫");
		s1.setLineName("2号线");
		
		Station s2 = new Station();
		
		s2.setStationName("安定门");
		s2.setLineName("2号线");
		
		Station s3 = new Station();
		
		s3.setStationName("鼓楼大街");
		s3.setLineName("2号线");
		
		Station s4 = new Station();
		
		s4.setStationName("积水潭");
		s4.setLineName("2号线");
		
		Station s5 = new Station();
		
		s5.setStationName("鼓楼大街");
		s5.setLineName("8号线");
		//中断区间：雍和宫-安定门-鼓楼大街-积水潭
		b1.setSStation(s1);
		b1.setEStation(s2);
		b1.setBreakStaStr("雍和宫-安定门");
		b1.setsTime(RouteSearchService.breakStartTime);
		b1.setETime(RouteSearchService.breakEndTime);
		b1.setBreakNo("02150216");
		
		b2.setSStation(s2);
		b2.setEStation(s3);
		b2.setBreakStaStr("安定门-鼓楼大街");
		b2.setsTime(RouteSearchService.breakStartTime);
		b2.setETime(RouteSearchService.breakEndTime);
		b2.setBreakNo("02160217");
		
		b3.setSStation(s5);
		b3.setEStation(s3);
		b3.setBreakStaStr("鼓楼大街-鼓楼大街");
		b3.setsTime(RouteSearchService.breakStartTime);
		b3.setETime(RouteSearchService.breakEndTime);
		b3.setBreakNo("02170835");
		
		b4.setSStation(s3);
		b4.setEStation(s4);
		b4.setBreakStaStr("鼓楼大街-积水潭");
		b4.setsTime(RouteSearchService.breakStartTime);
		b4.setETime(RouteSearchService.breakEndTime);
		b4.setBreakNo("02170218");
		
		//中断区间：雍和宫-安定门-鼓楼大街-积水潭
				b5.setSStation(s1);
				b5.setEStation(s2);
				b5.setBreakStaStr("雍和宫-安定门");
				b5.setsTime(RouteSearchService.breakStartTime1);
				b5.setETime(RouteSearchService.breakEndTime1);
				b5.setBreakNo("02150216");
				
				b6.setSStation(s2);
				b6.setEStation(s3);
				b6.setBreakStaStr("安定门-鼓楼大街");
				b6.setsTime(RouteSearchService.breakStartTime1);
				b6.setETime(RouteSearchService.breakEndTime1);
				b6.setBreakNo("02160217");
				
				b7.setSStation(s5);
				b7.setEStation(s3);
				b7.setBreakStaStr("鼓楼大街-鼓楼大街");
				b7.setsTime(RouteSearchService.breakStartTime1);
				b7.setETime(RouteSearchService.breakEndTime1);
				b7.setBreakNo("02170835");
				
				b8.setSStation(s3);
				b8.setEStation(s4);
				b8.setBreakStaStr("鼓楼大街-积水潭");
				b8.setsTime(RouteSearchService.breakStartTime1);
				b8.setETime(RouteSearchService.breakEndTime1);
				b8.setBreakNo("02170218");		
		
		
		breakList.add(b1);
		breakList.add(b2);
		breakList.add(b3);
		breakList.add(b4);
		breakList.add(b5);
		breakList.add(b6);
		breakList.add(b7);
		breakList.add(b8);
		return breakList;
	}
	
	public Map<String, String> initInternal(File file) {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}
	
	public Object[] initStationName(File file) {
		
		Object[] obj = new Object[17];
		List<String> all = new ArrayList<String>();
		List<String> line1 = new ArrayList<String>();
		List<String> line2 = new ArrayList<String>();
		List<String> line4 = new ArrayList<String>();
		List<String> line5 = new ArrayList<String>();
		List<String> line6 = new ArrayList<String>();
		List<String> line8 = new ArrayList<String>();
		List<String> line9 = new ArrayList<String>();
		List<String> line10 = new ArrayList<String>();
		List<String> line13 = new ArrayList<String>();
		List<String> line14 = new ArrayList<String>();
		List<String> line15 = new ArrayList<String>();
		List<String> line94 = new ArrayList<String>();
		List<String> line95 = new ArrayList<String>();
		List<String> line96 = new ArrayList<String>();
		List<String> line97 = new ArrayList<String>();
		List<String> line98 = new ArrayList<String>();
		
		
		try {
			FileInputStream fis = new FileInputStream(file); 
	    	InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); 
	    	BufferedReader br = new BufferedReader(isr); 
	    	
	    	br.readLine();
			br.readLine();
			String str;
			while ((str=br.readLine())!=null) {
				
				
				
				String[] stationData = str.split("\t");
				
				String lineNumStr = stationData[0];
				String stationName = stationData[3];
				
				all.add(stationName);
				
				if ("1".equals(lineNumStr))
					line1.add(stationName);
				else if ("2".equals(lineNumStr))
					line2.add(stationName);
				else if ("4".equals(lineNumStr))
					line4.add(stationName);
				else if ("5".equals(lineNumStr))
					line5.add(stationName);
				else if ("6".equals(lineNumStr))
					line6.add(stationName);
				else if ("8".equals(lineNumStr))
					line8.add(stationName);
				else if ("9".equals(lineNumStr))
					line9.add(stationName);
				else if ("10".equals(lineNumStr))
					line10.add(stationName);
				else if ("13".equals(lineNumStr))
					line13.add(stationName);
				else if ("14".equals(lineNumStr))
					line14.add(stationName);
				else if ("15".equals(lineNumStr))
					line15.add(stationName);
				else if ("94".equals(lineNumStr))
					line94.add(stationName);
				else if ("95".equals(lineNumStr))
					line95.add(stationName);
				else if ("96".equals(lineNumStr))
					line96.add(stationName);
				else if ("97".equals(lineNumStr))
					line97.add(stationName);
				else if ("98".equals(lineNumStr))
					line98.add(stationName);
			}
			
			fis.close();
            isr.close();
            br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		obj[0] = all;
		obj[1] = line1;
		obj[2] = line2;
		obj[3] = line4;
		obj[4] = line5;
		obj[5] = line6;
		obj[6] = line8;
		obj[7] = line9;
		obj[8] = line10;
		obj[9] = line13;
		obj[10] = line14;
		obj[11] = line15;
		obj[12] = line94;
		obj[13] = line95;
		obj[14] = line96;
		obj[15] = line97;
		obj[16] = line98;
		
		return obj;
	}
	
	/**
	 * 功能：读取蒋老师客流数据中Target_lineInfo.txt文件 创建人：刘源 创建时间：2016-03-29
	 * 
	 * @param filepath
	 * @param network
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void LoadTarget_lineInfo(String filepath, Network network)
			throws FileNotFoundException, IOException {
		try {
			File file = new File(filepath);
			if (!file.isDirectory()) {
//				System.out.println("这是一个文件");
				System.out.println("path=" + file.getPath());
				System.out.println("absolutepath=" + file.getAbsolutePath());
				System.out.println("name=" + file.getName());
			} else if (file.isDirectory()) {
				// System.out.println("这是一个文件夹");
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filepath + "\\" + filelist[i]);
					if (!readfile.isDirectory()) {
						// System.out.println("path=" + readfile.getPath());
						// System.out.println("absolutepath=" +
						// readfile.getAbsolutePath());
						System.out.println("name=" + readfile.getName());
						network.LoadTarget_lineInfo(filepath,
								readfile.getName());
					} else if (readfile.isDirectory()) {
						LoadTarget_lineInfo(filepath + "\\" + filelist[i],
								network);
					}
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("readfile()   Exception:" + e.getMessage());
		}
	}
	
	/**
	 * 读取蒋老师客流数据中Target_stationInfo.txt文件 
	 * 创建人：刘源 创建时间：2016-03-29
	 * @param filepath
	 * @param network
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void LoadTarget_stationInfo(String filepath, Network network)
			throws FileNotFoundException, IOException {
		try {
			File file = new File(filepath);
			if (!file.isDirectory()) {
				System.out.println("这是一个文件");
				System.out.println("path=" + file.getPath());
				System.out.println("absolutepath=" + file.getAbsolutePath());
				System.out.println("name=" + file.getName());
			} else if (file.isDirectory()) {
				System.out.println("这是一个文件夹");
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filepath + "\\" + filelist[i]);
					if (!readfile.isDirectory()) {
						// System.out.println("path=" + readfile.getPath());
						// System.out.println("absolutepath=" +
						// readfile.getAbsolutePath());
						System.out.println("name=" + readfile.getName());
						network.LoadTarget_stationInfo(filepath,
								readfile.getName());
					} else if (readfile.isDirectory()) {
						LoadTarget_stationInfo(filepath + "\\" + filelist[i],
								network);
					}
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("readfile()   Exception:" + e.getMessage());
		}
	}
}
