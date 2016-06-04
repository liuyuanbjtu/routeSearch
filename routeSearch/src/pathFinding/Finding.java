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
		network.LoadFiledTable("Data\\�����������.txt");
		network.LoadTranferInfo("Data\\TransferStation.txt");
		network.LoadTimeTableall("Data\\time_table_all.txt");
		network.loadLineTrainLimit("Data\\�г���Ա.txt");
		/*network.LoadStationInfo(fileData[0]);
		network.LoadFiledTable(fileData[1]);
		network.LoadTranferInfo(fileData[2]);
		network.LoadTimeTableall(fileData[3]);
		network.loadLineTrainLimit("Data\\�г���Ա.txt");*/

		System.out.println("InitNetwork "+ (System.currentTimeMillis()-timeStart) +"ms");
		//FindAccessRoutesTest("15:26:20");
		//findPathTest(81298,"�͹�","������");
		//findPathTest("6:42:00","ĵ��԰","������վ");
		//findPathTimeSpan("21:47:04","23:05:04","������վ","ĵ��԰");
		//findAllPathTest("18:30:00");
		//findKroutesTest(0);
		//findKroutesTest(1,"2����","��ֱ��","��ˮ̶");
		//findlatestPath2();
		//FindAccessRouteKTest("21:20:25");
		//findlatestPath3();
		
		/*congest();
		findPathTest("8:00:00","������","����");
		findPathTest2("8:00:00","������","����");
		findPathTest3("8:00:00","������","����");*/
		findReasonableRoutes(0);
		//findReasonablePaths();
		System.out.println("ȫ����ʱ: "+ (System.currentTimeMillis()-timeFirst) +"ms");
	
	}

	
	public static void findAllPathTest(String timeStr)
	{
		Find2 find=new Find2();
	       
		System.out.print("��ʼ��������·��");
		long timeFirst = System.currentTimeMillis();
		find.setNetwork(network);
		find.initODInfo();
		find.k=1;
		find.findLatestRoutes();
		FindPath findpath=new FindPath();
		System.out.println("��ѯ��ʱ��"+(System.currentTimeMillis()-timeFirst)+"ms");
		
		long time=System.currentTimeMillis();
		findpath.setnetwork(network);
		findpath.setODInfo(find.ODInfos);		
		findpath.findAccessPathAll(timeStr);
		System.out.println("��ѯ��ʱ��"+(System.currentTimeMillis()-time)+"ms");
		time=System.currentTimeMillis();
	//	findpath.WriteAccessRoutes(timeStr,OriginalS, DestinationS,0);
		System.out.println("д����ʱ��"+(System.currentTimeMillis()-time)+"ms");
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
		System.out.print("��ʼ�������·��");
		if(bChanged==1)
		{
			find.network.SetBreak("1����", "����","�찲����");
			//find.SetBreak("1����", 1,"����","����");
			//find.network.BreakRelease("1����", "����","�찲����");
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
