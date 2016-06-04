package com.hdxinfo.service;

import java.io.File;
import java.io.FileWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;

import javax.xml.crypto.Data;

import netWork.*;
import oracle.jdbc.driver.OracleTypes;
import pathFinding.FindRoute;
import pathFinding.Node;
import pathFinding.ODInfo;
import pathFinding.Route;
import pathFinding.Routes;

import com.hdxinfo.bean.BreakBean;
import com.hdxinfo.bean.RouteBean;
import com.hdxinfo.util.DbManager;

public class RouteSearchService {
	static int m_refplan=Integer.MAX_VALUE;
	public static FindRoute find = new FindRoute();
	
	public static Object[] obj;
	
	public static Map<String, String> internalMap;
	
	public static List<BreakBean> breakList;
	
//	public static String breakStartTime = "2015-12-27 15:22:00";
//	public static String breakEndTime = "2015-12-27 18:40:00";
	
	public static String breakStartTime = "2015-03-26 07:22:00";
	public static String breakEndTime = "2015-03-26 07:57:00";
	
	public static String breakStartTime1 = "2015-03-27 07:22:00";
	public static String breakEndTime1 = "2015-03-27 07:57:00";
	
//	public static String breakStartTime1 = "2015-03-27 07:30:00";
//	public static String breakEndTime1 = "2015-03-27 07:57:00";
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	public Object[] searchRoute(String sName, String eName, String stime) throws ParseException {
		m_refplan=Integer.MAX_VALUE;
		Object[] obj = new Object[3];
		
//		stime = "2015-03-26 7:30:00";
		
		try {
			
//			for(int cost = 0; cost < 3; cost++)
			for(int p = 0; p < 3; p++) {
				
				if(p==0){
					stime = "2015-03-25 7:30:00";
				}else if(p==1){
					stime = "2015-03-26 7:30:00";
				}else if(p==2){
					stime = "2015-03-27 7:30:00";
				}
				
				List<BreakBean> breakList = queryBreak(stime);
				int length = breakList.size();
				
				List<Route> normalRoutes = null;
				List<Route> backupRoutes = null;
				
				int cost = 0;
				
				if (length == 0) {
					normalRoutes = findReasonableRoutes(sName, eName, cost, 0);
//					travelTime = getTravelTime(normalRoutes);
//					getTravelTime(normalRoutes);
				} 
				else {
					normalRoutes = findReasonableRoutes(sName, eName, cost, 0);
					backupRoutes = findReasonableRoutes(sName, eName, cost, 1);
					computeBreak(normalRoutes,backupRoutes,stime,cost,length);
				}
				
				//得到normalRoutes路径集中的最短路径的时间成本m_refplan
				if(p==0)
				{
					Iterator<Route> routeIt = normalRoutes.iterator();
					while(routeIt.hasNext()) {
					Route route = routeIt.next();
					int temp=(int)route.ODObj.ReasonableRoutesRef[0].routes.get(0).costInfos[0];
					if(m_refplan>temp)
						m_refplan=temp;
					}
				}
				Iterator<Route> routeIt = normalRoutes.iterator();
				
				List<RouteBean> list = new ArrayList<RouteBean>();
				
				
//				int p=0;
				//查询时间
				Date date = sdf.parse(stime);
				int time = date.getHours() * 3600 + date.getMinutes() * 60
						+ date.getSeconds();
				int index_t = time/300;
				
				int numRoute = 0;
				while(routeIt.hasNext()) {
					RouteBean bean = new RouteBean();
					//经过线路的名称
					String routeStr = "";
					//
					String oldTransfer = "";
					//线路
					List<String> lineList = new ArrayList<String>();
					//换乘车站
					List<String> transferList = new ArrayList<String>();
					//所有车站名称
					List<String> allStationNameList = new ArrayList<String>();
					//车站数量
					int sNumPerLine = 0;
					
					//将路径集中的路径存放到route中
					Route route = routeIt.next();
					
					//计算路径满意度,存放在Route类中int costStatisfaction[24*12] 
					route.costSatisfaction = new double[3][24*12];
					for(int m=0;m<3;m++)
					computecostSat(route, m, index_t);
					
					List<Node> nodeList = route.nodes;
					int nLength = nodeList.size();
					for(int i = 0; i < nLength; i++) {
						/*if(lineList.size() != 0 && lineList.size()%3 == 0 ) {
							routeStr = routeStr + "\n";
						}*/
						Node node = nodeList.get(i);
						Station s = node.station;
						
						if(i == 0) {
							lineList.add(s.line.lineName);
							routeStr = routeStr + s.line.lineName;
							transferList.add(s.stationName);
						}
						
						if (node.relationFather == 1) {
							if(sNumPerLine == 0) continue;
							sNumPerLine = 0;
							if(oldTransfer.equals(s.stationName)) continue;
							oldTransfer = s.stationName;
							transferList.add(s.stationName);
							lineList.add(s.line.lineName);
							routeStr = routeStr + "->" + s.line.lineName;
						} else {
							allStationNameList.add(s.stationName);
							sNumPerLine++;
						}
						
						if(i == (nLength - 1)) {
							transferList.add(s.stationName);
						}
					}
					
					String[] routeStrItem = routeStr.split("->");
					String newRouteStr = "";
					for(int i=0;i<routeStrItem.length;i++){
						if(routeStrItem[i].equals("4号线(大兴线)")){
							routeStrItem[i]="4号线";
						}
						
						if(i==0){
							newRouteStr=routeStrItem[i];
						}
						else if(i!=0&&i%3!=0){
							newRouteStr=newRouteStr+" -> "+routeStrItem[i];
						}
						else{
							newRouteStr=newRouteStr+"\n"+"-> "+routeStrItem[i];
						}
					}
					if(sNumPerLine == 0) continue;
					bean.setRouteStr(newRouteStr);
//					System.out.println(newRouteStr);
					bean.setLineArr(lineList);
					bean.setTransferArr(transferList);
					bean.setAllStationArr(allStationNameList);
					
					if(p==0){
						bean.setTotalTime(route.travelTime);
					}else if(p==1||p==2){
						//增加额外等待时间
						route.travelTimeBreak = route.travelTime + route.waitTime[index_t];
						bean.setTotalTime(route.travelTimeBreak);
					}
					
					route.travelTime = 0;
					bean.setWaitTime(route.waitTime[index_t]);
					bean.setCostSatisfaction(route.costSatisfaction[p][index_t]);
//					bean.setCostSatArray(route.costSatisfaction,p);
					bean.setCostSatArray(route.costSatisfaction);
//					System.out.println(route.costSatisfaction[p][index_t-1]);
					
					list.add(bean);
					
					numRoute++;
				}
				
				list = sortList(list);
				obj[p] = list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*String outputPath = "C:\\Users\\LiuYuan\\Workspaces\\routeSearch\\routeSearch\\Data\\output.txt";
		try {
			FileWriter fr = new FileWriter(outputPath);
			for(int n=0;n<3;n++){
				fr.write("第"+n+"种情况"+"\n");
				List<RouteBean> testobj = (ArrayList<RouteBean>)obj[n];
				for(int i=0;i<testobj.size();i++){
					RouteBean rb = testobj.get(i);
					fr.write(rb.getRouteStr()+"\n");
					if(rb != null){
						double[] cost = rb.getCostSatArray();
						for(int m=0;m<cost.length;m++){
							if(cost[m]!=0){
								fr.write(String.valueOf((double)Math.round(100*cost[m])/100.0)+"\t");
							}
						}
					}
					fr.write("\n");
				}
				
			}
			fr.flush();
			fr.close();
		} catch (Exception e) {
			// TODO: handle exception
		}*/
		return obj;
	}
	
	//鏌ヨ鍖洪棿缂栧彿
	public String queryInterval(String routeIntervalStr) {
	//	return "\'10011003\'";//,\'10031005\',\'10051007\'";//,\'10071009\'";
		//return "\'10011003\',\'10031005\'";//,\'10051007\'";//,\'10071009\',\'10111013\'";
				/*if(j != allStationArr.length - 1)
				{
					routeIntervalStr = routeIntervalStr + allStationArr[j] + "-" + allStationArr[j + 1] + ",";
				}
				else
				{
					routeIntervalStr = routeIntervalStr + allStationArr[j] + "-" + allStationArr[j + 1];
				}*/
				String  strReturn="";
				String  stationstr;
				String[] stationsstr;//涓�杞︾珯
				String[] stationsstrs;//杞︾珯瀵规暟缁�
				stationsstrs=routeIntervalStr.split(",");//灏嗗瓧绗︿覆鐢ㄩ�鍙峰垎寮�垚瀛楃涓叉暟缁�
				
				for(int i=0;i<stationsstrs.length;i++)
				{
					strReturn+="\'";
					stationsstr=stationsstrs[i].split("-");//姣忎釜鏁扮粍鐢�鍒嗗紑,姣忎竴涓瓧绗︿覆str锛屾牴鎹畇tr璇诲彇杞︾珯锛�
					Station[] sStations = find.network.GetStationsByStationName(stationsstr[0]);
					Station[] eStations = find.network.GetStationsByStationName(stationsstr[1]);
					Station station=null;
					Line line1=null,line2=null;
					for(int j=0;j<sStations.length;j++)
					{
						line1=sStations[j].line;
						int bfinded=0;
						for(int k=0;k<eStations.length;k++)
						{
							line2=eStations[k].line;
							if(line1==line2)//涓や釜杞︾珯绾胯矾鐩稿悓鐨勪负瀹為檯绾胯矾锛�
							{
								bfinded=1;
								break;
							}
						}
						if(bfinded==1)
						{
							station=sStations[j];
							break;
						}
					}
				
				
				int direction=0;
				String str1=stationsstr[0];
				String str2=stationsstr[1];
				direction=find.network.getDirectionbyStations(line1.lineName,str1,str2);//鏍规嵁绾胯矾鍚嶇О鍜岃溅绔欏悕绉拌幏寰楁柟鍚�
				Field fieldobj=station.fields[2-direction];//鏍规嵁杞︾珯鍜屾柟鍚戣幏寰楀尯闂达紝
				if(line1.lineNo<10)
					strReturn+=0;//濡傛灉绾胯矾缂栧彿灏忎簬10锛屽尯闂村瓧绗︿覆涔嬪墠+0
				strReturn+=fieldobj.fieldNo+"\'";//鐢熸垚鏍煎紡涓殑瀛楃涓�
				if(i<stationsstrs.length-1)//濡傛灉杞︾珯鏈粨鏉熷垯鍦ㄥ瓧绗︿覆缁撳熬+,
					strReturn+=",";
				}
				return strReturn;
	}
	
	
	public String hello() {
		return "hello";
	}
	
	//查询车站名称
	public Object[] queryAllStations() {
		return obj;
	}
	
	public List<BreakBean> queryBreak(String time) throws ParseException {
		//time =2016-05-26 08:45:11 
		Date date = sdf.parse(time);
		List<BreakBean> list = new ArrayList<BreakBean>();
		for (BreakBean bean : breakList) {
			//中断起始时间：bean.getsTime() = 2015-03-26 07:22:00
			Date sDate = sdf.parse(bean.getsTime());
			Date eDate = sdf.parse(bean.getETime());
			//判断此时time是否在路网出现中断的时间内[sDate,eDate]内
			if (date.getTime() >= sDate.getTime() && date.getTime() <= eDate.getTime()) {
				list.add(bean);
			}
		}
		return list;
	}
	
	/**
	 * 功能：对给定List进行排序
	 * 创建人：牛艳东
	 * 创建时间：2014-02-14
	 */
	public static List<Route> oldsortList(List<Route> list, int cost) {
		
        int lLength = list.size();
		
		//对得到路径集根据成本进行排序
		for(int i = 0; i < lLength; i++) {
			
			int minIndex = i;
			
			for(int j = i + 1; j < lLength; j++) {
				double iCost = 0;
				double jCost = 0;
				Route r1 = list.get(minIndex);
				Route r2 = list.get(j);
				if (cost == 0) {
//					iCost = r1.costInfos[cost] + r1.waitTime;
//					jCost = r2.costInfos[cost]+ r2.waitTime;
					iCost = r1.costInfos[cost];
					jCost = r2.costInfos[cost];
				} else if (cost == 1) {
					iCost = list.get(minIndex).costInfos[cost];
					jCost = list.get(j).costInfos[cost];
				} else if (cost == 2) {
//					iCost = r1.costInfos[cost] + r1.waitTime;
//					jCost = r2.costInfos[cost]+ r2.waitTime;
					iCost = r1.costInfos[cost];
					jCost = r2.costInfos[cost];
				}
				
				if(iCost > jCost) {
					minIndex = j;
				}
			}
			
			if(i != minIndex) {
				Route temp = list.get(i);
				list.set(i, list.get(minIndex));
				list.set(minIndex, temp);
			}
		}
		
		return list;
	}
	
	/**
	 * 功能：对给定List进行排序
	 * 创建人：刘源
	 * 创建时间：2016-05-07
	 */
	public static List<RouteBean> sortList(List<RouteBean> list) {
		
        int lLength = list.size();
		
		//对得到路径集根据成本进行排序
		for(int i = 0; i < lLength; i++) {
			
			int maxIndex = i;
			
			for(int j = i + 1; j < lLength; j++) {
				double iCost = 0;
				double jCost = 0;
				RouteBean r1 = list.get(maxIndex);
				RouteBean r2 = list.get(j);
				
				iCost = r1.getCostSatisfaction();
				jCost = r2.getCostSatisfaction();
				
				if(iCost < jCost) {
					maxIndex = j;
				}
			}
			
			if(i != maxIndex) {
				RouteBean temp = list.get(i);
				list.set(i, list.get(maxIndex));
				list.set(maxIndex, temp);
			}
		}
		//如果路径集大于5条，取前五条显示
		if(list.size() > 5) {
			list = list.subList(0, 5);
		}
		return list;
	}
	
	/**
	 * 功能：查找非突发事件下合理路径并按成本进行排序，取前五条
	 * 输入：
	 * 输出：
	 * 创建人：牛艳东
	 * 创建时间：2014-02-13
	 */
	public static List<Route> findReasonableRoutes(String sStationName, String eStationName, int cost, int flag) {
		
		List<Route> list = new ArrayList<Route>();
		
		Station[] sStations = find.network.GetStationsByStationName(sStationName);
		Station[] eStations = find.network.GetStationsByStationName(eStationName);
		int sLength = sStations.length;
		int eLength = eStations.length;
		Routes routes = null;
		
		//获取所有路径集
		for(int i = 0; i < sLength; i++) {
			for(int j = 0; j < eLength; j++) {
				if(flag == 0) {
					routes = find.ODInfos[sStations[i].odNo][eStations[j].odNo].ReasonableRoutesRef[cost];
				} else if(flag == 1) {
					routes = find.ODInfos[sStations[i].odNo][eStations[j].odNo].ReasonableRoutes[cost];
				}
				list.addAll(routes.routes);
			}
		}
		
		list = oldsortList(list, cost);
		
		//如果路径集大于5条，取前五条显示
		if(list.size() > 5) {
			list = list.subList(0, 5);
		}
		return list;
	}
	
	/**
	 * 功能：计算一条路径的分时满意度,仿真时间是从5:00-9:10
	 * 输入：一条路径Route route，系统时刻index_t=time/300
	 * 输出：该路径的满意度costSatisfaction，存放在Route类中int costStatisfaction[24*12] 创建人：刘源
	 * 创建时间：2016-03-06
	 */
	private static void oldComputecostSat(Route route, int p) {
		// 对时间进行处理
		// int index_t = time/300;
		// 对路径进行处理
		/*if(route.ODObj.ReasonableRoutesRef.length==0)
			System.out.println("erro 1");
		if(route.ODObj.ReasonableRoutesRef[0].routes.size()==0)
			System.out.println("erro 2");
		if(route.ODObj.ReasonableRoutesRef[0].routes.get(0).costInfos.length==0)
			System.out.println("erro 3");
		int refplan=(int)route.ODObj.ReasonableRoutesRef[0].routes.get(0).costInfos[0]+180;*/
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		route.costSatisfaction = new double[3][24*12];
		try {
			//仿真起始时间
			String sTime = "06:00:00";
			Date sDate = simpleDateFormat.parse(sTime);
			int sIndex_t = (sDate.getHours() * 3600 + sDate.getMinutes() * 60
					+ sDate.getSeconds())/300;
			//仿真结束时间
			String eTime = "9:10:10";
			Date eDate = simpleDateFormat.parse(eTime);
			int eIndex_t = (eDate.getHours() * 3600 + eDate.getMinutes() * 60
					+ eDate.getSeconds())/300;
		//定义一个时间表示乘客到达车站的时间
		double arriveTime = 0; 
			//index_t表示乘客的出发时间
		
		List<Field> fields = new ArrayList<Field>();
		TransSta transferStations = new TransSta();
		
		getNewRoute(route,fields,transferStations);
		
		for(int index_t=sIndex_t;index_t<eIndex_t;index_t++){
			/*// 首先确定路径起点站以及换乘站
			Station stationO = null;
			Station station = null;
			// TransSta transferStations = null;
			// StaTransCon transferStation = null;
			TransSta transferStations = new TransSta();
			stationO = route.nodes.get(0).station;
			List<Field> fields = new ArrayList<Field>();

			ListIterator<Node> iter = null;
			iter = route.nodes.listIterator();
		
			Node nodeO = iter.next();
			while (iter.hasNext()) {
			Node node = iter.next();
			station = node.station;
			Station stationPre = node.nodeFather.station;
			
			if (node.relationFather == 1) {
				StaTransCon transferStation = new StaTransCon();
				transferStation.StationNext = station;
				transferStation.Stationpre = stationPre;
				transferStations.stationTransferCons.add(transferStation);
			}

			if (node.nodeFather != null
					&& stationPre.stationName != station.stationName) {
				Field field = new Field();
				field = getFieldByStations(stationPre, station);
				fields.add(field);
			}
		}
		route.fields = fields;
		route.transferStations = transferStations;*/
			
		

		// 计算该路径的满意度
		ListIterator<Field> iter1 = fields.listIterator();
		int m = 0;
		double sat_in = 0; // 进站候车满意度
		double sat_tran = 0; // 换乘候车满意度
		double congestionSat = 0;// 区间拥挤满意度
		
		double waitSat = 0;//候车满意度
		double runSat = 0;//行程满意度
		double transferSat = 0;//换乘满意度
		
		double runTime = 0;//行程总时间
		// 换乘站个数
		int size_tran = transferStations.stationTransferCons.size();
		//换乘满意度
		transferSat = getTransfeSat(size_tran);
		// 区间个数
		int size_field = fields.size();
		
		while (iter1.hasNext()) {
			Field field = iter1.next();
			if (field.staionPre != field.stationNext) {
				// 起点站需要加上进站候车时间
				if (m == 0) {
					sat_in = getWaitSat(field.getOnWaittime[p][index_t]);
					congestionSat = getCongestionSat(field.congestionRefs[p][index_t]);
					//分时进站上车集散时间
					runTime = field.getOnTime[p][index_t];
					m++;
				} else {
					// 获取区间拥挤满意度
					congestionSat = congestionSat + getCongestionSat(field.congestionRefs[p][index_t]);
					
				}

				StaTransCon transferStation = null;
				// 遍历换乘站的节点
				for (int i = 0; i < size_tran; i++) {
					transferStation = transferStations.stationTransferCons
							.get(i);
					if (field.staionPre == transferStation.StationNext) {
						sat_tran = sat_tran + getWaitSat(field.tranInWaittime[p][index_t]);
						runTime = runTime + field.tranInTime[p][index_t];
					}
				}
			}
		}
		
		congestionSat = congestionSat / size_field;
		//换乘候车满意度=（换乘站1候车满意度+换乘站2候车满意度+..）/换乘站数量
		if(size_tran==0){
			sat_tran = 10;
		}else{
			sat_tran = sat_tran / size_tran;
		}
		//候车满意度=进站候车满意度+（换乘站1候车满意度+换乘站2候车满意度+..）/换乘站数量
		waitSat = (sat_in+sat_tran)/2;
		
		if(p==0){
			route.travelTime = route.costInfos[0];
			runTime = runTime+route.costInfos[3];
		}else if(p==1){
			//增加额外等待时间
			route.travelTime = route.costInfos[0]+route.waitTime[index_t];
			runTime = runTime + route.waitTime[index_t] + route.costInfos[3];
		}else{
			//增加额外等待时间
			route.travelTime = route.costInfos[0]+route.waitTime[index_t];
			runTime = runTime + route.waitTime[index_t] + route.costInfos[3];
		}
		if((runTime-m_refplan)<0){
			runTime = 0;
		}else{
			runTime = runTime - m_refplan;
		}
		runSat = getTripSat(runTime);
		
		/*if(p==1&&index_t==100){
			System.out.println("路径：\n");
			System.out.println("waitSat:"+waitSat+"\n");
			System.out.println("runSat:"+runSat+"\n");
			System.out.println("transferSat:"+transferSat+"\n");
			System.out.println("congestionSat:"+congestionSat+"\n");
		}*/
		
		if(waitSat==-1){
			route.costSatisfaction[p][index_t] = (2.9*runSat + 2.5*transferSat + 2.5*congestionSat)/(2.9+2.5+2.5);
		}else if(waitSat!=10){
			route.costSatisfaction[p][index_t] = (2.2*waitSat + 2.9*runSat + 2.5*transferSat + 2.5*congestionSat)/(2.2+2.9+2.5+2.5);
		}
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 功能：计算一条路径的分时满意度,仿真时间是从5:00-9:10
	 * 输入：一条路径Route route，系统时刻index_t=time/300
	 * 输出：该路径的满意度costSatisfaction，存放在Route类中int costStatisfaction[24*12] 创建人：刘源
	 * 创建时间：2016-06-03
	 */
	private static void computecostSat(Route route, int p ,int stime) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		
		try {
			//仿真起始时间
			String sTime = "06:10:00";
			Date sDate = simpleDateFormat.parse(sTime);
			int sIndex_t = (sDate.getHours() * 3600 + sDate.getMinutes() * 60
					+ sDate.getSeconds())/300;
			//仿真结束时间
			String eTime = "9:10:00";
			Date eDate = simpleDateFormat.parse(eTime);
			int eIndex_t = (eDate.getHours() * 3600 + eDate.getMinutes() * 60
					+ eDate.getSeconds())/300;
			
			//突发事件开始时间
			Date sBreakDate = sdf.parse(breakStartTime);
			int sBreakTime = sBreakDate.getHours() * 3600 + sBreakDate.getMinutes() * 60
					+ sBreakDate.getSeconds();
			int s_BreakIndex_t = sBreakTime/300;
			
			//突发事件结束时间
			Date eBreakDate = sdf.parse(breakEndTime);
			int eBreakTime = eBreakDate.getHours() * 3600 + eBreakDate.getMinutes() * 60
					+ eBreakDate.getSeconds();
			int e_BreakIndex_t = eBreakTime/300;
			
			//如果nodeCost=-1,表示该路径没有中断区间，如果发生中断，表示从该路径起点到中断区间第一个点所花费的时间。
			Node nodeBreak = getBreakNodeCost(route);
			
			//遍历查询时刻
			for(int index_t=sIndex_t;index_t<eIndex_t;index_t++){
				
				//计算额外等待时间
				
				if(nodeBreak != null){
					double nodeCost = nodeBreak.costInfos[0];
					//到达中断区间的时间
					double tempBreak = nodeCost/60;
					int runningt = (int)Math.ceil(tempBreak/5);
					int arrivetTime = index_t + runningt;
					
					if(arrivetTime>=s_BreakIndex_t&&arrivetTime<=e_BreakIndex_t){
						//需要额外等待的时间(min)
						double extraWaitTime =  (eBreakTime - (index_t*300) - nodeCost)/60;
						
						int tempt = index_t;
						for(int tBreak =s_BreakIndex_t;tBreak<e_BreakIndex_t;tBreak++){
							route.waitTime[tempt] = extraWaitTime;
							tempt++;
							extraWaitTime = extraWaitTime - 5;
							if(extraWaitTime<=0){
								extraWaitTime = 0;
							}
						}
					}
				
				}
				
				
				double runTime = 0;//行程总时间
				double waitTime = 0;//总候车时间
				
				double congestionSat = 0;// 区间拥挤满意度
				//乘客到达每一个点的时间
				int t = index_t;
				double detaT = 0;
				
				ListIterator<Node> iter = route.nodes.listIterator();
				Node nodePre = iter.next();
				
				if(nodePre.equals(nodeBreak)&&p!=0){
					runTime = runTime + route.waitTime[index_t]*60;
					waitTime = waitTime + route.waitTime[index_t]*60;
				}
				
				int m =0;
				int numField = 0;
				double sum = 0;
				//遍历route上的每一个区间
				while(iter.hasNext()){
					Node node = iter.next();
					
					if(node.equals(nodeBreak)&&p!=0){
						runTime = runTime + route.waitTime[index_t]*60;
						waitTime = waitTime + route.waitTime[index_t]*60;
					}
					
					Field field = getFieldByNodes(nodePre,node);
					//判断是否起点站
					if(m==0){
						//进站时间认为设定2分钟；
						runTime = runTime + field.getOnTime[p][t] + field.getOnWaittime[p][t];
						waitTime = waitTime + field.getOnWaittime[p][t];
						detaT = runTime;
						congestionSat = getCongestionSat(field.congestionRefs[p][t]);
						m++;
						numField++;
					}else{
						//判断是否换乘站
						double tempTrans = 0;
						if(nodePre.station.stationName.equals(node.station.stationName)){
							TransSta transferStation = node.station.transferStation;
							for(StaTransCon transferConnection:transferStation.stationTransferCons){
								if(transferConnection.Stationpre==nodePre.station&&transferConnection.StationNext==node.station){
									tempTrans = transferConnection.walkTime;
								}
							}
//							runTime = runTime +	field.tranInWaittime[p][t] + field.tranInTime[p][t] ;
//							waitTime = waitTime + field.tranInWaittime[p][t];
//							detaT = field.tranInWaittime[p][t] + field.tranInTime[p][t];
							runTime = runTime +	tempTrans ;
							waitTime = waitTime + tempTrans;
							detaT = tempTrans;
							numField++;
						}else{
							runTime = runTime + (node.costInfos[3]-nodePre.costInfos[3]);
							detaT = (node.costInfos[3]-nodePre.costInfos[3]);
							congestionSat = congestionSat + getCongestionSat(field.congestionRefs[p][t]);
							numField++;
						}
					}
					
					//更新t
					double temp = detaT/60;
					int temp1 = (int)Math.round(temp/5);
					
					if(temp1 == 0){
						sum = sum +temp;
						if(sum > 2.5){
							t = t + (int)Math.round(sum/5);
							sum = 0;
						}
					}else{
						t = t + temp1;
					}
					
					//更新点
					nodePre = node;
				}
				
				//候车满意度
				double waitSat = 0;
				waitSat = getWaitSat(waitTime);
				
				//换乘满意度
				double transferSat = 0;
				
				List<Field> fields = new ArrayList<Field>();
				TransSta transferStations = new TransSta();
				
				getNewRoute(route,fields,transferStations);
				// 换乘站个数
				int size_tran = transferStations.stationTransferCons.size();
				//换乘满意度
				transferSat = getTransfeSat(size_tran);
				
				// 区间个数
				int size_field = fields.size();
				//拥挤满意度
//				System.out.println(size_field == numField);两者相等
				congestionSat = congestionSat / (size_field-size_tran);
				
				//行程满意度
				double runSat = 0;
				
				if(index_t == stime)
				route.travelTime = runTime;
				
				
				if((runTime-m_refplan)<0){
					runTime = 0;
				}else{
					runTime = runTime - m_refplan;
				}
				runSat = getTripSat(runTime);
				
				
				/*if(index_t==89&&p==1){
					
					if(transferSat == 10){
						System.out.println("---"+p);
						System.out.println(transferSat);
//					System.out.println("路径：\n");
					System.out.println("waitSat:"+waitSat+"\t"+waitTime+"\n");
					System.out.println("runSat:"+runSat+"\n");
					System.out.println("transferSat:"+transferSat+"\n");
					System.out.println("congestionSat:"+congestionSat+"\n");
					}
				}*/
				
//				if(waitSat==10){
//					route.costSatisfaction[p][index_t] = (2.9*runSat + 2.5*transferSat + 2.5*congestionSat)/(2.9+2.5+2.5);
//				}else{
					route.costSatisfaction[p][index_t] = (2.2*waitSat + 2.9*runSat + 2.5*transferSat + 2.5*congestionSat)/(2.2+2.9+2.5+2.5);
//				}
			}
			} catch (Exception e) {
				// TODO: handle exception
			}
	}
	
	public static Field getFieldByStations(Station s1, Station s2) {
		Field field = new Field();
		if (s1.fields[0] != null && s2 == s1.fields[0].stationNext) {
			field = s1.fields[0];
		} else if (s1.fields[1] != null && s2 == s1.fields[1].stationNext) {
			field = s1.fields[1];
		}
		return field;
	}
	
	/**
	 * 功能：分段函数——时间 
	 * 输入：进站候车时间或换乘候车时间（单位：秒）
	 *  输出：相应的满意度指数(double类型) 
	 *  创建人：刘源
	 * 创建时间：2016-04-20
	 */
	public static double getWaitSat(double waittime) {
		double[] bTime = new double[]{0,2.5,4.0,4.7,5.5,6.9,7.8,8.1,9.2,9.6,10.7};
		double satisfaction = 0;
		double time = waittime / 60;
		if (time > bTime[0] && time <= bTime[1]) {
			satisfaction = 10;
		} else if (time > bTime[1] && time <= bTime[2]) {
			satisfaction = (10*(bTime[2]-time)+9*(time-bTime[1]))/(bTime[2]-bTime[1]);
		} else if (time > bTime[2] && time <= bTime[3]) {
			satisfaction = (9*(bTime[3]-time)+8*(time-bTime[2]))/(bTime[3]-bTime[2]);
		} else if (time > bTime[3] && time <= bTime[4]) {
			satisfaction = (8*(bTime[4]-time)+7*(time-bTime[3]))/(bTime[4]-bTime[3]);
		} else if (time > bTime[4] && time <= bTime[5]) {
			satisfaction = (7*(bTime[5]-time)+6*(time-bTime[4]))/(bTime[5]-bTime[4]);
		} else if (time > bTime[5] && time <= bTime[6]) {
			satisfaction = (6*(bTime[6]-time)+5*(time-bTime[5]))/(bTime[6]-bTime[5]);
		} else if (time > bTime[6] && time <= bTime[7]) {
			satisfaction = (5*(bTime[7]-time)+4*(time-bTime[6]))/(bTime[7]-bTime[6]);
		} else if (time > bTime[7] && time <= bTime[8]) {
			satisfaction = (4*(bTime[8]-time)+3*(time-bTime[7]))/(bTime[8]-bTime[7]);
		} else if (time > bTime[8] && time <= bTime[9]) {
			satisfaction = (3*(bTime[9]-time)+2*(time-bTime[8]))/(bTime[9]-bTime[8]);
		} else if (time > bTime[9] && time <= bTime[10]) {
			satisfaction = (2*(bTime[10]-time)+1*(time-bTime[9]))/(bTime[10]-bTime[9]);
		} else if (time > bTime[10]) {
			satisfaction = 1;
		} 
		else {
//			System.out.println("候车时间time小于等于0");
//			satisfaction = -1;
			satisfaction = 10;
		}
		return satisfaction;
	}

	/**
	 * 功能：分段函数——时间 
	 * 输入：行程总时间（单位：秒）
	 *  输出：相应的满意度指数(double类型) 
	 *  创建人：刘源
	 * 创建时间：2016-04-20
	 */
	public static double getTripSat(double runningtime) {
		double[] bTime = new double[]{0,8.4,9.1,11.8,13.0,15.2,16.3,19.3,22.4,24.6,25.5};
		double satisfaction = 0;
		double time = runningtime / 60;
		if (time > bTime[0] && time <= bTime[1]) {
			satisfaction = 10;
		} else if (time > bTime[1] && time <= bTime[2]) {
			satisfaction = (10*(bTime[2]-time)+9*(time-bTime[1]))/(bTime[2]-bTime[1]);
		} else if (time > bTime[2] && time <= bTime[3]) {
			satisfaction = (9*(bTime[3]-time)+8*(time-bTime[2]))/(bTime[3]-bTime[2]);
		} else if (time > bTime[3] && time <= bTime[4]) {
			satisfaction = (8*(bTime[4]-time)+7*(time-bTime[3]))/(bTime[4]-bTime[3]);
		} else if (time > bTime[4] && time <= bTime[5]) {
			satisfaction = (7*(bTime[5]-time)+6*(time-bTime[4]))/(bTime[5]-bTime[4]);
		} else if (time > bTime[5] && time <= bTime[6]) {
			satisfaction = (6*(bTime[6]-time)+5*(time-bTime[5]))/(bTime[6]-bTime[5]);
		} else if (time > bTime[6] && time <= bTime[7]) {
			satisfaction = (5*(bTime[7]-time)+4*(time-bTime[6]))/(bTime[7]-bTime[6]);
		} else if (time > bTime[7] && time <= bTime[8]) {
			satisfaction = (4*(bTime[8]-time)+3*(time-bTime[7]))/(bTime[8]-bTime[7]);
		} else if (time > bTime[8] && time <= bTime[9]) {
			satisfaction = (3*(bTime[9]-time)+2*(time-bTime[8]))/(bTime[9]-bTime[8]);
		} else if (time > bTime[9] && time <= bTime[10]) {
			satisfaction = (2*(bTime[10]-time)+1*(time-bTime[9]))/(bTime[10]-bTime[9]);
		} else if (time > bTime[10]) {
			satisfaction = 1;
		} 
		else {
//			System.out.println("行程时间time小于等于0");
			satisfaction = 10;
		}
		return satisfaction;
	} 
	
	/**
	 * 功能：分段函数——满载率 
	 * 输入：断面满载率（如89.33）
	 *  输出：相应的满意度指数(int类型) 
	 *  创建人：刘源 创建时间：2016-03-13
	 */
	public static double getCongestionSat(double congestion) {
		double satisfaction = 0;
		if (congestion > 0 && congestion < 30) {
			satisfaction = 10;
		} else if (congestion >= 30 && congestion < 60) {
			satisfaction = 9;
		} else if (congestion >= 60 && congestion < 80) {
			satisfaction = 7;
		} else if (congestion >= 80 && congestion < 100) {
			satisfaction = 5;
		} else if (congestion >= 100 && congestion < 120) {
			satisfaction = 3;
		} else if (congestion >= 120) {
			satisfaction = 1;
		} else {
//			System.out.println("congetstion小于等于0");
			satisfaction = 10;
		}
		return satisfaction;
	}
	
	/**
	 * 功能：分段函数——换乘次数 
	 * 输入：换乘次数
	 *  输出：相应的满意度指数(double类型) 
	 *  创建人：刘源 
	 *  创建时间：2016-04-20
	 */
	public static double getTransfeSat(int transferTime) {
		double satisfaction = 0;
		if (transferTime == 0) {
			satisfaction = 10;
		} else if (transferTime == 1) {
			satisfaction = 10;
		} else if (transferTime == 2) {
			satisfaction = 8;
		} else if (transferTime == 3) {
			satisfaction = 5;
		} else if (transferTime > 3) {
			satisfaction = 1;
		}  
		else {
			System.out.println("换乘次数小于0");
		}
		return satisfaction;
	}
	/**
	 *  功能：中断情况下的计算路径的行程总时间、额外等待时间
	 *  输入：路径集
	 *  输出： 路径集
	 *  创建人：刘源 
	 *  创建时间：2016-05-26
	 */
	public void computeBreak(List<Route> normalRoutes,List<Route> backupRoutes,String stime,int cost,int length){

		try {
//			normalRoutes = findReasonableRoutes(sName, eName, cost, 0);
			ListIterator<Route> iterator = normalRoutes.listIterator();
			int flagInfo = 0;
			while(iterator.hasNext()) {
				
				Route route1 = (Route) iterator.next();
				
				
				//如果nodeCost=-1,表示该路径没有中断区间，如果发生中断，表示从该路径起点到中断区间第一个点所花费的时间。
//				double nodeCost = getBreakNodeCost(route1);
				Node nodeBreak = getBreakNodeCost(route1);
//				double nodeCost = getBreakNodeCost(route1).costInfos[0];
				
				/*List<Node> nodeList = route1.nodes;
				int nodeListLength = nodeList.size();
				List<String> strNodeList = new ArrayList<String>();
				
				//输出String类型的路径,输入nodeList,输出strNodeList,如“雍和宫-积水潭，积水潭-东直门”
				getRouteStr(nodeList,strNodeList);
				
				List<Integer> nodeIndex = new ArrayList<Integer>();
				Map<String,BreakBean> breakMap = new HashMap<String,BreakBean>();
				for(int i = 0; i < length; i++) {
					BreakBean bean1 = breakList.get(i);
					String breakStaStr = bean1.getBreakStaStr();
					breakMap.put(breakStaStr, bean1);
					if(strNodeList.contains(breakStaStr)) {
						nodeIndex.add(strNodeList.indexOf(breakStaStr));
					}
				}
				
				if(nodeIndex.size() > 0) {
					//循环记录下的index，找出最小index
					int minIndex = 0;
					for(int i = 1; i < nodeIndex.size(); i++) {
						//minIndex = getMinNum(nodeIndex.get(i), nodeIndex.get(i + 1));
						if(nodeIndex.get(minIndex) > nodeIndex.get(i)) {
							minIndex = i;
						}
					}
					//minIndex表示该路径上中断区间的第一个车站
					//如查询“东直门”--“西直门”，中断区间“雍和宫”--“积水潭”，node为雍和宫
					Node node = nodeList.get(nodeIndex.get(minIndex));
					//nodeCost为从东直门到node雍和宫的时间（秒）
					double nodeCost = node.costInfos[0];*/
					
				if(nodeBreak.equals(null) == false){
			/*		//用户查询时间，即当前时间
					Date sDate = sdf.parse(stime);
					int time = sDate.getHours() * 3600 + sDate.getMinutes() * 60
							+ sDate.getSeconds();
					int index_t = time/300;
					long sDateLong = sDate.getTime();
					//获取突发事件终止时间
//					BreakBean bean2 = breakMap.get(strNodeList.get(nodeIndex.get(minIndex)));
//					Date eDate = sdf.parse(bean2.getETime());
					Date eDate = sdf.parse(breakEndTime);
					int etime = eDate.getHours() * 3600 + eDate.getMinutes() * 60
							+ eDate.getSeconds();
					int e_index_t = etime/300;
					long eDateLong = eDate.getTime();//突发事件终止时间，sDateLong 查询时间
					//计算额外等待时间
					
						//额外等待时间 = 突发事件的结束时间-当前时间-从起点站到中断区间的第一个车站的时间
					double waitTime =  (eDateLong - (sDateLong + (nodeCost * 1000)))/1000;
						if(waitTime > 0) {
							for(int t=index_t;t<e_index_t;t++){
								route1.waitTime[t] = waitTime;
							}
						}*/
						flagInfo++;
					}
			}
			
			if(flagInfo != 0) {
				//获得备选路径集
//				backupRoutes = findReasonableRoutes(sName, eName, cost, 1);
				normalRoutes.addAll(backupRoutes);
				for(int i = 0; i < normalRoutes.size(); i++) {
					Route route4 = normalRoutes.get(i);
					List<Node> nodeList = route4.nodes;
					int nodeListLength = nodeList.size();
					String str = "";
					for(int j = 0; j < nodeListLength; j++) {
						Node node = nodeList.get(j);
						str = str + node.station.stationName + " ";
					}
				}
				//循环去除重复路径集（根据花费时间和所有的Node进行比较）
				for(int k = 0; k < normalRoutes.size(); k++) {
					Route r = normalRoutes.get(k);
					double rCost = r.costInfos[0];
					List<Node> nList = r.nodes;
					List<String> iList = new ArrayList<String>();
					ListIterator li = nList.listIterator();
					//while循环得到了路径r上所有点的名称
					while(li.hasNext()) {
						Node n = (Node) li.next();
						iList.add(n.station.stationName);
					}
					for(int m = k + 1; m < normalRoutes.size(); m++) {
						Route r1 = normalRoutes.get(m);
						double r1Cost = r1.costInfos[0];
						List<Node> nList1 = r1.nodes;
						//如果r和r1的路径成本相同，就从normalRoutes中去掉r1
						if(rCost == r1Cost) {
							normalRoutes.remove(m);
						} else {
							List<String> iList1 = new ArrayList<String>();
							ListIterator li1 = nList1.listIterator();
							//while循环得到路径r1上所有点的名称
							while(li1.hasNext()) {
								Node n = (Node) li1.next();
								iList1.add(n.station.stationName);
							}
							//如果路径r中包括路径r1的所有点，就从normalRoutes中去掉r1
							if(iList.containsAll(iList1)) {
								normalRoutes.remove(m);
								m--;
							}
						}
					}
				}
//				normalRoutes = oldsortList(normalRoutes, cost);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
/*	public List getTravelTime(List<Route> normalRoutes){
		List<Double> travelTime = new ArrayList();
		Iterator<Route> routeIt = normalRoutes.iterator();
		while(routeIt.hasNext()) {
			Route route = routeIt.next();
			travelTime.add(route.costInfos[0]);
		}
		return travelTime;
	}*/
	
/*	public void getTravelTime(List<Route> normalRoutes){
		List<Double> travelTime = new ArrayList();
		Iterator<Route> routeIt = normalRoutes.iterator();
		while(routeIt.hasNext()) {
			Route route = routeIt.next();
			route.travelTime = route.costInfos[0];
		}
	}*/
	
	public static void getRouteStr(List<Node> nodeList,List<String> snoList){
		int nodeListLength = nodeList.size();
		for(int i = 0; i < nodeListLength - 1; i++) {
			Node node1 = nodeList.get(i);
			Node node2 = nodeList.get(i + 1);
			String sStationName = node1.station.stationName;
			String eStationName = node2.station.stationName;
			if(!sStationName.equals(eStationName)) {
				snoList.add(sStationName + "-" + eStationName);
			}
		}
	}
	/**
	 * 输入：路径route
	 * 输出：该中断区段在该路径上的第一个车站节点
	 * 时间：06-03
	 * @param route
	 * @return
	 */
	private static Node getBreakNodeCost(Route route){
		
		List<Node> nodeList = route.nodes;
		List<String> strNodeList = new ArrayList<String>();
		
		//输出String类型的路径,输入nodeList,输出strNodeList,如“雍和宫-积水潭，积水潭-东直门”
		getRouteStr(nodeList,strNodeList);
		
		List<Integer> nodeIndex = new ArrayList<Integer>();
		
		for(int i = 0; i < breakList.size(); i++) {
			BreakBean bean1 = breakList.get(i);
			String breakStaStr = bean1.getBreakStaStr();
			if(strNodeList.contains(breakStaStr)) {
				nodeIndex.add(strNodeList.indexOf(breakStaStr));
			}
		}
		
		if(nodeIndex.size() > 0) {
			//循环记录下的index，找出最小index
			int minIndex = 0;
			for(int i = 1; i < nodeIndex.size(); i++) {
				//minIndex = getMinNum(nodeIndex.get(i), nodeIndex.get(i + 1));
				if(nodeIndex.get(minIndex) > nodeIndex.get(i)) {
					minIndex = i;
				}
			}
		//minIndex表示该路径上中断区间的第一个车站
		//如查询“东直门”--“西直门”，中断区间“雍和宫”--“积水潭”，node为雍和宫
		Node node = nodeList.get(nodeIndex.get(minIndex));
		
		return node;
		//nodeCost为从东直门到node雍和宫的时间（秒）
//		double nodeCost = node.costInfos[0];
//		return nodeCost;
		}
		else{
			return null;
		}
	}
	
	/**
	 * 输入：路径route
	 * 输出：另一条路径 route1，新的区间和换乘站信息
	 * 时间：06-03
	 */
	private static void getNewRoute(Route route,List<Field> fields,TransSta transferStations){
//		Route newRoute = new Route();
		
		Station stationO = null;
		Station station = null;
		// TransSta transferStations = null;
		// StaTransCon transferStation = null;
//		TransSta transferStations = new TransSta();
		stationO = route.nodes.get(0).station;
//		List<Field> fields = new ArrayList<Field>();

		ListIterator<Node> iter = route.nodes.listIterator();
		//因为后面要用到node.nodeFather,所以先排除第一个车站
		Node nodeO = iter.next();
		while (iter.hasNext()) {
		Node node = iter.next();
		station = node.station;
		Station stationPre = node.nodeFather.station;
		//换乘站
		if (node.relationFather == 1) {
			StaTransCon transferStation = new StaTransCon();
			transferStation.StationNext = station;
			transferStation.Stationpre = stationPre;
			transferStations.stationTransferCons.add(transferStation);
		}
		//非换乘站
		if (node.nodeFather != null
				&& stationPre.stationName != station.stationName) {
			Field field = new Field();
			field = getFieldByStations(stationPre, station);
			fields.add(field);
		}
	}
//	newRoute.fields = fields;
//	newRoute.transferStations = transferStations;
		
//		return newRoute;
	}
	
	/**
	 * 功能：根据两个节点得到区间
	 * 时间：06-03
	 */
	private static Field getFieldByNodes(Node nodePre, Node node){
		Field field = new Field();
		Station s1 = nodePre.station;
		Station s2 = node.station;
		if (s1.fields[0] != null && s2 == s1.fields[0].stationNext) {
			field = s1.fields[0];
		} else if (s1.fields[1] != null && s2 == s1.fields[1].stationNext) {
			field = s1.fields[1];
		}
		return field;
		
	}
}
