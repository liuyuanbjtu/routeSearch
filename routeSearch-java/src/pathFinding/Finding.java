package pathFinding;

import java.io.FileReader;
import java.io.IOException;
import java.util.ListIterator;
import java.util.Scanner;

import netWork.*;


public class Finding {

	public static Network network=new Network();
	public static String[] fileData=new String[6];
	public static void main(String[] args) {
		
		long timeFirst = System.currentTimeMillis();
		long timeStart=System.currentTimeMillis();
		

		network.LoadStationInfo("Data\\Stations.txt");
		network.LoadFiledTable("Data\\区间基本数据.txt");
		network.LoadTranferInfo("Data\\TransferStation.txt");
		network.LoadTimeTableall("Data\\time_table_all.txt");
		network.loadLineTrainLimit("Data\\列车定员.txt");
		/*network.LoadStationInfo(fileData[0]);
		network.LoadFiledTable(fileData[1]);
		network.LoadTranferInfo(fileData[2]);
		network.LoadTimeTableall(fileData[3]);
		network.loadLineTrainLimit("Data\\列车定员.txt");*/

		System.out.println("InitNetwork "+ (System.currentTimeMillis()-timeStart) +"ms");
		//FindAccessRoutesTest("15:26:20");
		//findPathTest(81298,"巴沟","大钟寺");
		//findPathTest("6:42:00","牡丹园","北京南站");
		//findPathTimeSpan("21:47:04","23:05:04","北京南站","牡丹园");
		//findAllPathTest("18:30:00");
		//findKroutesTest(0);
		//findKroutesTest(1,"2号线","西直门","积水潭");
		//findlatestPath2();
		//FindAccessRouteKTest("21:20:25");
		//findlatestPath3();
		
		/*congest();
		findPathTest("8:00:00","回龙观","东单");
		findPathTest2("8:00:00","回龙观","东单");
		findPathTest3("8:00:00","回龙观","东单");*/
		findReasonableRoutes(0);
		//findReasonablePaths();
		System.out.println("全部用时: "+ (System.currentTimeMillis()-timeFirst) +"ms");
	
	}

	
	public static void findAllPathTest(String timeStr)
	{
		Find2 find=new Find2();
	       
		System.out.print("开始计算最晚路径");
		long timeFirst = System.currentTimeMillis();
		find.setNetwork(network);
		find.initODInfo();
		find.k=1;
		find.findLatestRoutes();
		FindPath findpath=new FindPath();
		System.out.println("查询用时："+(System.currentTimeMillis()-timeFirst)+"ms");
		
		long time=System.currentTimeMillis();
		findpath.setnetwork(network);
		findpath.setODInfo(find.ODInfos);		
		findpath.findAccessPathAll(timeStr);
		System.out.println("查询用时："+(System.currentTimeMillis()-time)+"ms");
		time=System.currentTimeMillis();
	//	findpath.WriteAccessRoutes(timeStr,OriginalS, DestinationS,0);
		System.out.println("写入用时："+(System.currentTimeMillis()-time)+"ms");
	}
	public static void findReasonableRoutes(int bChanged)
	{
		FindRoute find=new FindRoute();
		long timeFirst = System.currentTimeMillis();
		long timeStart=System.currentTimeMillis();
		find.setNetwork(network);
		//find.initTopoNetwork();
		find.initODInfo();
		//System.out.print("InitNetwork "+ (System.currentTimeMillis()-timeFirst) +"ms");
		System.out.print("开始计算合理路径");
		if(bChanged==1)
		{
			find.network.SetBreak("1号线", "西单","天安门西");
			//find.SetBreak("1号线", 1,"西单","东单");
			//find.network.BreakRelease("1号线", "西单","天安门西");
		}
		timeFirst = System.currentTimeMillis();
		for(int i=0;i<3;i++)
		{
			//int i=0;
			
			find.findShortestRoutes(i, bChanged);
			
		}
		System.out.print("Finding :"+(System.currentTimeMillis()-timeFirst) +"ms");
		timeFirst = System.currentTimeMillis();
		find.ReleaseShortestRoute();
		System.out.print("shortest Release"+(System.currentTimeMillis()-timeFirst) +"ms");
		for(int i=0;i<3;i++)
		{
			timeFirst = System.currentTimeMillis();
			find.findReasonableRoute(i, bChanged);
			System.out.print("Finding Reasonable:"+i+":"+(System.currentTimeMillis()-timeFirst) +"ms");
			//find.WriteReaonableRoute(i,-1,0);
		}
		//System.gc();
		System.out.print("Find reasonable all "+ (System.currentTimeMillis()-timeStart) +"ms");
		timeFirst = System.currentTimeMillis();
		find.computerFastTime();
		for(int i=0;i<3;i++)
		{
			find.WriteReaonableRoute(i,-1,bChanged);		
		}
		System.out.print("Write reaonable all"+ (System.currentTimeMillis()-timeFirst) +"ms");
		
	}
	public static void findlatestPath3()
	{
		Find2 find=new Find2();
       find.k=Integer.parseInt(fileData[4]);
		long timeFirst = System.currentTimeMillis();
		find.setNetwork(network);
		find.initODInfo();
		find.findLatestRoutes();
		find.releaseTempRoute();
		timeFirst = System.currentTimeMillis();
		find.computePath();
		find.WriteLatesPath(1,fileData[5]);
		
		

	}


}
