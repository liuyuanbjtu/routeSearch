package pathFinding;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import netWork.*;

public class FindRoute extends Find{
	private List<Node> nodesSetB= new LinkedList<Node>();


	private List<Node> nodesSetA=new LinkedList<Node>();
	
	
	public int refTimespan=0;
	public int refiStation=3; 
	public Station stationBeFinding=null;
	public double refwalktime=2;
	public double reffield0=0.002;
	public double reffield=0.002;
	public double refTransferTime0=5;
	public double refTransferTime=3;
	public double[] randomCost={0.1, 0.1, 0.1};
	public int[] randomCostDis={10,10,10};
	
	
	
	
	public void initODInfo()
	{
		int stationNum=network.stations.length;
		ODInfos=new ODInfo[stationNum][stationNum];
		for(int i=0;i<stationNum;i++)
		{
			for(int j=0;j<stationNum;j++)
			{
				ODInfos[i][j]=new ODInfo();
				ODInfos[i][j].stationOrigin=network.stations[i];
				ODInfos[i][j].stationDestination=network.stations[j];

			}
		}
	}
	
	
	public void findShortestRoutes(int type, int bChanged)
	{
		for(Station station : network.stations)
		{
		
			
			
			stationBeFinding=station;
			
			nodesSetB.removeAll(nodesSetB);
			nodesSetA.removeAll(nodesSetA);
			//初始化，所有车站都不在nodeSetA和nodeSetB里面，所有车站的成本都是最大。
			for(Station s:network.stations)
			{
				s.inSetA=false;
				s.inSetB=false;
				for(int i=0;i<3;i++)
					s.cost[i]=Float.MAX_VALUE;
			}
			
			
			Node node=new Node();
			node.station=stationBeFinding;
			node.direction=0;
			node.relationFather=-1;
			node.costInfos[0]=node.costInfos[2]=stationBeFinding.enterTime;
		    stationNextToNode(node,type,bChanged,0);
		    while(!nodesSetB.isEmpty())
			{
				node=nodesSetB.get(0);
				nodesSetB.remove(0);
				nodesSetA.add(node);
				node.station.inSetA=true;
				insertShortestRoutes(node,stationBeFinding,type,bChanged);
				stationNextToNode(node,type,bChanged,0);

			}
			
		}
		
	}
	public void ReleaseShortestRoute()
	{
		int stationNum=network.stations.length;
		for(int i=0;i<stationNum;i++)
		{
			for(int j=0;j<stationNum;j++)
			{
				for(int k=0;k<3;k++)
				{
					
					
						ODInfos[i][j].shortestRoutesTemp[k].nodes.removeAll(ODInfos[i][j].shortestRoutesTemp[k].nodes);
						
										
					
				}
	

			}
		}
		
	}

	public void ReleaseReasonableRoute(int type, int iCount, int bChanged)
	{
		int stationNum=network.stations.length;
		ListIterator<Route> iter=null;
		ListIterator<Node> iter2=null;
		Node node=null;
		Route route=null;
		for(int i=100*iCount;i<Math.min(100*(iCount+1),stationNum);i++)
		{
			for(int j=0;j<stationNum;j++)
			{
				
					
					
				if(bChanged==0)
				{
					iter=ODInfos[i][j].ReasonableRoutesRef[type].routes.listIterator();
					
						
				}
				if(bChanged==1)
				{
					iter=ODInfos[i][j].ReasonableRoutes[type].routes.listIterator();
				}
				while(iter.hasNext())
				{
					route=iter.next();
					route.nodes.removeAll(route.nodes);
					iter2=route.nodes.listIterator();
					while(iter2.hasNext())
					{
						node=iter2.next();
						node.costInfos=null;
						
				}
					
					
				}
				
	

			}
		}
		
	}
	

	public void SetBreakTransfer(String linenameStart, int directionStart, String sStart, String lineEnd, int directionEnd,String sEnd)
	{
		;
	}

	public void findReasonableRoute(int type, int bChanged)
	{
		//type表示计算成本的方式
	
		for(int i=0;i< network.stations.length;i++)
		{
			stationBeFinding=network.stations[i];
			nodesSetB.removeAll(nodesSetB);
			int indexO=stationBeFinding.odNo;
			//创建一个节点node，作为起始节点；
			Node node=new Node();
			node.costInfos[0]=node.costInfos[2]=stationBeFinding.enterTime;
			node.station=stationBeFinding;
			node.relationFather=-1;
			//总而言之，stationNextToNode函数做了几件事：
			//1、给出了路径成本计算的方法：通过创建节点node的下一个节点nodeNew来依次计算路径的成本；
			//2、首先，对于所有的车站node（这里西直门车站可以看作3个node：2号线的西直门，4号线的西直门和13号线的西直门），nodeNew是根据区间field信息确定；
		    //然后，特别对于换乘站node,通过换乘关系来确定nodeNew；
		    stationNextToNode(node,type,bChanged,1);
		    while(!nodesSetB.isEmpty())
			{
				node=nodesSetB.get(0);
				nodesSetB.remove(0);
				//该函数生成可达路径：起点站为stationBeFinding，终点站为node.station
				this.insertReasonableRoutes(node,stationBeFinding,type,bChanged);
				//用该函数寻找node的下一个车站，并放到集合nodeSetB中
				stationNextToNode(node,type,bChanged,1);

			}
			
		}
	}
	private int insertBReasonable(Node node,int type,int bChanged)
	{
		if(node.station==this.stationBeFinding)
			return -1;
		ListIterator<Node> iter=null;
		ListIterator<Route> iter1=null;
		
		Node nodeOld=null;
		int indexO=stationBeFinding.odNo;
		int indexD=node.station.odNo;
		double costref[]=new double[3];
		if(bChanged==0)
			costref=this.ODInfos[indexO][indexD].costRef;
		if(bChanged==1)
			costref=this.ODInfos[indexO][indexD].costInfos;
		
		if(type==0||type==2)
		{
			
			if(node.costInfos[type]>costref[type]*(1+this.randomCost[type])||node.costInfos[type]>costref[type]+this.randomCostDis[type]*60)
			return -1;

		}
		if(type==1)
		{
			double costdistance=ODInfos[indexO][indexD].shortestRoutesTemp[1].costInfos[0];
			if(node.costInfos[type]>costref[type])
				return -1;
			if(node.costInfos[type]==costref[type]&&(node.costInfos[0]>costdistance*(1+this.randomCost[1])||node.costInfos[0]>costdistance+this.randomCostDis[type]*60))
				return -1;
		}
		
		indexD=node.nodeFather.station.odNo;
		Route routeRef=null;
		if(bChanged==0)
			iter1=ODInfos[indexO][indexD].ReasonableRoutesRef[type].routes.listIterator();
		if(bChanged==1)
			iter1=ODInfos[indexO][indexD].ReasonableRoutes[type].routes.listIterator();
			while(iter1.hasNext())
			{
				routeRef=iter1.next();
				if(routeRef.nodes.get(routeRef.nodes.size()-1)==node.nodeFather)
				{
						iter=routeRef.nodes.listIterator();
						while(iter.hasNext())
						{
							nodeOld=iter.next();
							if(node.station==nodeOld.station)
							{
								return -1;
							}
						}
				}
			}
		nodesSetB.add(node);
		return 1;
		
	}
	private int insertB(Node node,int type,int bChanged)
	{
		ListIterator<Node> iter=null;
		iter=nodesSetA.listIterator();
		Node nodeOld=null;
		
		if(node.station==this.stationBeFinding||node.station.inSetA)
			return -1;
		if(type==0||type==2)
		{ 
			if(node.station.inSetB&&node.costInfos[type]>=node.station.cost[type])
				return -1;
			if(node.station.inSetB)
			{
				iter=nodesSetB.listIterator();
				while(iter.hasNext())
				{
					nodeOld=iter.next();
					if(node.station==nodeOld.station)
					{
						iter.remove();
							break;
					}
				}
			}
			
			node.station.cost=node.costInfos;
			node.station.inSetB=true;
			if(nodesSetB.isEmpty())
			{
				nodesSetB.add(node);
				return 1;
			}
			
	
			
			if(node.costInfos[type]<=nodesSetB.get(0).costInfos[type])
			{
				nodesSetB.add(0,node);
				return 1;
			}
			if(node.costInfos[type]>=nodesSetB.get(nodesSetB.size()-1).costInfos[type])
			{
				nodesSetB.add(node);
				return 1;
			}
			
			
			iter=nodesSetB.listIterator();
			
			
			 
			while(iter.hasNext())
			{
				nodeOld=iter.next();
				if(nodeOld.costInfos[type]>=node.costInfos[type])
				{ 
					iter.previous();
					iter.add(node);
				  		return 1;
				}
				
			}
		}
		if(type==1)
		{
			if(node.station.inSetB&&node.costInfos[type]>node.station.cost[type])
				return -1;
			if(node.station.inSetB&&node.costInfos[type]==node.station.cost[type]&&node.costInfos[0]>node.station.cost[0])
				return -1;
			if(node.station.inSetB)
			{
				iter=nodesSetB.listIterator();
				while(iter.hasNext())
				{
					nodeOld=iter.next();
					if(node.station==nodeOld.station)
					{
						iter.remove();
							break;
					}
				}
			}
			
			node.station.cost=node.costInfos;
			node.station.inSetB=true;
			if(nodesSetB.isEmpty())
			{
				nodesSetB.add(node);
				return 1;
			}
			
	
			
			if((node.costInfos[type]<nodesSetB.get(0).costInfos[type])
					||(node.costInfos[type]==nodesSetB.get(0).costInfos[type]&&node.costInfos[0]<=nodesSetB.get(0).costInfos[0]))
			{
				nodesSetB.add(0,node);
				return 1;
			}
			if((node.costInfos[type]>nodesSetB.get(nodesSetB.size()-1).costInfos[type])
					||(node.costInfos[type]==nodesSetB.get(nodesSetB.size()-1).costInfos[type]&&node.costInfos[0]>=nodesSetB.get(nodesSetB.size()-1).costInfos[0]))
			{
				nodesSetB.add(node);
				return 1;
			}
			
			
			iter=nodesSetB.listIterator();
			
			
			 
			while(iter.hasNext())
			{
				nodeOld=iter.next();
				if((nodeOld.costInfos[type]>node.costInfos[type])
						||(nodeOld.costInfos[type]==node.costInfos[type]&&nodeOld.costInfos[0]>=node.costInfos[0]))
				{ 
					iter.previous();
					iter.add(node);
				  		return 1;
				}
				
			}
		}
		return 0;
	}
	private void insertReasonableRoutes(Node node, Station station, int type, int bChanged)
	{
		
		Node nodeFather=node.nodeFather;
		Route route;
		Route routeRef;
		Station stationFather=nodeFather.station;
		int indexD;
		int indexO=station.odNo;
		if(station==stationFather)
		{
			route=new Route();
			route.nodes.add(nodeFather);
			route.nodes.add(node);
			indexD=node.station.odNo;
			
				
			route.costInfos=node.costInfos;
			route.staionNum=2;

			
			ODInfos[indexO][indexD].addReasonableRoute(type,route, bChanged);
			route.ODObj=ODInfos[indexO][indexD];
			return;
		}
		indexD=stationFather.odNo;
		int indexDnew=node.station.odNo;
		int bRepeat=0;
		ListIterator<Route> iter1=null;
		if(bChanged==0)
			iter1=ODInfos[indexO][indexD].ReasonableRoutesRef[type].routes.listIterator();
		if(bChanged==1)
			iter1=ODInfos[indexO][indexD].ReasonableRoutes[type].routes.listIterator();
		while(iter1.hasNext())
		{
			routeRef=iter1.next();
			
			if(routeRef.nodes.get(routeRef.nodes.size()-1)==nodeFather)
			{
				route=new Route();
				
				route.nodes.addAll(routeRef.nodes);
				
				
				
		
				route.nodes.add(node);
	
				route.costInfos=node.costInfos;
				route.staionNum=(short)(routeRef.staionNum+1);
				ODInfos[indexO][indexDnew].addReasonableRoute(type,route, bChanged);
				route.ODObj=ODInfos[indexO][indexDnew];
				
						
			}
			
		}
	
	}
	private void insertShortestRoutes(Node node, Station station, int type, int bChanged)
	{
		Node nodeFather=node.nodeFather;
		Route shortestR=new Route();
		Route shortestRRef=null;
		Station stationFather=nodeFather.station;
		int indexD;
		int indexO=station.odNo;
		if(station==stationFather)
		{
			
			shortestR.nodes.add(nodeFather);
			shortestR.nodes.add(node);
			shortestR.staionNum=2;			
		}
		else
		{
			indexD=stationFather.odNo;
			shortestRRef=ODInfos[indexO][indexD].shortestRoutesTemp[type];
			shortestR.nodes.addAll(shortestRRef.nodes);
			shortestR.nodes.add(node);
			shortestR.staionNum=(short)(shortestRRef.staionNum+1);
	
		}
		for(int i=0;i<shortestR.costInfos.length;i++)
		{
		shortestR.costInfos[i]=node.costInfos[i];
		}
	
		indexD=node.station.odNo;
		this.ODInfos[indexO][indexD].shortestRoutesTemp[type]=shortestR;
		if(bChanged==0)
		{
			ODInfos[indexO][indexD].costRef[type]=node.costInfos[type];
		}
		if(bChanged==1)
		{
			ODInfos[indexO][indexD].costInfos[type]=node.costInfos[type];
		}
	}
	private void stationNextToNode(Node node, int type,int bChanged,int style)
	{
		if(node==null||node.station==null)
			return;
		//node.station就是stationBeFinding（起点站）
		Station station=node.station;
		Field field=null;
		//通过该循环可以在setB中插入node节点的下一个节点
		for(int i=0;i<2;i++)
		{
			field=station.fields[i];
			//当station是起点站，可能出现station.fields[0]/station.fields[1]==null的情况
			if(field==null)
				continue;
			if(bChanged==1&&!field.bValid)
				continue;
			Node nodeNew=new Node();
			nodeNew.station=field.stationNext;
			nodeNew.direction=field.direction;
			nodeNew.nodeFather=node;
			//第二种计算方式：后一个节点的成本等于前一个节点的成本
			nodeNew.costInfos[1]=node.costInfos[1];
			nodeNew.relationFather=0;
			
			nodeNew.costInfos[0]=node.costInfos[0]+field.runTime;
			nodeNew.costInfos[2]=node.costInfos[2]+field.runTime*field.obsrate;
			nodeNew.costInfos[3]=node.costInfos[3]+field.runTime;
			if(field.fee>node.costInfos[3])
			{
				//nodeNew.costInfos[3]=node.costInfos[3]+field.fee;
				nodeNew.costInfos[2]+=field.fee;
			}
				if(style==0)
					this.insertB(nodeNew,type,bChanged);
				if(style==1)
					this.insertBReasonable(nodeNew, type,bChanged);		
		}
		TransSta transferStation=null;
		//如果该车站是换乘站
		if(station.type==1)
		{
			transferStation=node.station.transferStation;
			if(transferStation==null)
				return;
			for(StaTransCon transferConnection:transferStation.stationTransferCons)
			{
				if(transferConnection.Stationpre==station)
				{
					if(bChanged==1&&!transferConnection.bValid)
						continue;
					if(this.stationBeFinding.line.lineNo!=98&&node.station.line.lineNo==98
					&&transferConnection.StationNext.line.lineNo!=98) 
				continue;
					if(transferConnection.direction1==node.direction||transferConnection.direction1==0)
					{
						Node nodeNew=new Node();
						nodeNew.station=transferConnection.StationNext;
						nodeNew.nodeFather=node;
						nodeNew.costToFather=transferConnection.walkTime;
						nodeNew.relationFather=1;
						//nodeNew.costInfos[3]=node.costInfos[3];
						nodeNew.costInfos[0]=node.costInfos[0]+this.refTransferTime0+0.5*this.refTimespan+60;
						nodeNew.costInfos[1]=1+node.costInfos[1];
						nodeNew.costInfos[2]=node.costInfos[2]+this.refTransferTime+2*this.refTimespan+60+transferConnection.walkTime*refwalktime*transferConnection.obsrate;
						/*if(transferConnection.fee>nodeNew.costInfos[3])
						{
							nodeNew.costInfos[3]+=transferConnection.fee;
							nodeNew.costInfos[2]+=transferConnection.fee;
						}*/
						nodeNew.direction=transferConnection.direction2;

							if(style==0)
								this.insertB(nodeNew,type,bChanged);
							if(style==1)
								this.insertBReasonable(nodeNew, type,bChanged);
					}
				}
					
			}
		}
	}
	
	public void WriteShortestRoute()
	{
		
		try{
			String filePath="";
				filePath="Data\\Shortest.txt";
			FileWriter fileWriter=new FileWriter(filePath);
			FileWriter writeTemp=new FileWriter("Data\\error-Shortest.txt");
		DateTransfer date=new  DateTransfer();
		date.time=System.currentTimeMillis()/1000000;   
		 date.timeToDate();
		fileWriter.write("ʱ��"+date.hour+":"+date.minute+":"+date.second+"\n");
		fileWriter.write("��վ������·\t��վ���\t��վ������·\t��վ���\t����\t����ɱ�\t�ۺϳɱ�\t���˴���\t;����·\t;������վ\t;����վ����\t;����վ");
		String stationO="";
		String stationD=""; String lineO=""; String lineD=""; String lineList=""; String transferList=""; String stationList="";
		double costdistance=0;double costcom=0; double transfertime=0;
		int stationNum=0;
		Route route=null;

		for(int i=0;i<network.stations.length;i++)
		{
			for(int j=0;j<network.stations.length;j++)
			{
				if(i==j)
					continue;
				stationO=ODInfos[i][j].stationOrigin.stationName;
				lineO=ODInfos[i][j].stationOrigin.line.lineName;
				stationD=ODInfos[i][j].stationDestination.stationName;
				lineD=ODInfos[i][j].stationDestination.line.lineName;
				
				for(int k=0;k<3;k++)
				{
					route =ODInfos[i][j].shortestRoutesTemp[k];
					if(route==null)
					{
						
						
						
						continue;
					}
					costdistance=route.costInfos[0];
					transfertime=route.costInfos[1];
					costcom=route.costInfos[2];
					String stationNameOld="";
				
					stationList="";
					lineList="";
					transferList="";
					stationNameOld="";
					stationNum=0;
					int b=0;
					ListIterator<Node> iter=null;
					iter=route.nodes.listIterator();
					int m=0;
					while(iter.hasNext())
					{
						Node node=iter.next();
						Station station=node.station;

						if(m==0)
						{
							lineList+=station.line.lineName;
							m++;}
						if(node.relationFather==1)
						{
							
							if(stationNameOld!=station.stationName)
							{
								if(!transferList.equals(""))
								transferList+="-";
								transferList+=station.stationName;
								
								
								stationNameOld=station.stationName;
								
								lineList+="-"+node.station.line.lineName;
							}

								
						}
						else
						{
							
							if(b!=0)
								stationList+="-";
							stationList+=station.stationName;
							b=1;
							
							stationNum++;
							
						}
							
					}
						fileWriter.write("\n"+lineO+"\t"+stationO+"\t"+lineD+"\t"+stationD+"\t"+k+"\t"+costdistance+"\t"+costcom+"\t"+transfertime+"\t"+lineList+"\t"+transferList+"\t"+stationNum+"\t"+stationList);
				}

				
			}
		}
		 fileWriter.flush();
		 fileWriter.close();
		
			 writeTemp.flush();
			 writeTemp.close();
		
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}
	public void computerFastTime()
	{
		for(int i=0;i<network.stations.length;i++)
		{
			for(int j=0;j<network.stations.length;j++)
			{
				if(i==j)
					continue;
				Routes routes=ODInfos[i][j].ReasonableRoutesRef[0];
					
				ListIterator<Route> iter1 =routes.routes.listIterator();
				int reffastTimeCost=Integer.MAX_VALUE;
				while(iter1.hasNext())
				{
					Route route=iter1.next();
					
				  int costdistance=(int)(route.costInfos[0]-route.costInfos[1]*(this.refTimespan*2+60));
				  if(costdistance<reffastTimeCost)
					  reffastTimeCost=costdistance;
				}
				ODInfos[i][j].fastTimeCost=reffastTimeCost;
			}
		}
	}
		public void WriteReaonableRoute(int type, int iCount, int style)
		{
			
			try{
				String filePath="";
				if(style==0)
					filePath="Data\\ReasonableRef.txt";
				if(style==1)
					filePath="Data\\Reasonable.txt";
				FileWriter fileWriter;
				//如果icount<0,则重新写入文件，如果icount>0，则在文件中追加内容。
				if(type==0&&iCount<=0)
			fileWriter=new FileWriter(filePath);
				else
				fileWriter=new FileWriter(filePath,true);
					
			FileWriter writeTemp=new FileWriter("Data\\error-Resonable.txt");
//			DateTransfer date=new  DateTransfer();
//			date.time=System.currentTimeMillis()/1000000;   
//			 date.timeToDate();
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("HH:mm:ss");
			String time=format.format(date);
//			fileWriter.write("查询时刻"+date.hour+":"+date.minute+":"+date.second+"\n");
			fileWriter.write("查询时刻"+time);
			fileWriter.write("起始线路\t起点站\t终点线路\t终点站\t最快用时\t成本计算方式\t路径编号\t距离（0）\t距离（2）\t换乘时间（1）\t途径线路\t换乘列表\t换乘车站\t车站数量\t车站列表");
			
			String stationO="";
			String stationD=""; String lineO=""; String lineD=""; String lineList=""; String transferList=""; String stationList="";
			int fastTime=0;
			double costdistance=0;double costcom=0; double transfertime=0;int iNo=0;String transferStations="";
			int stationNum=0;
			Route route=null;
			int count=0;
			ListIterator<Route> iter1=null;
			ListIterator<Station> iters=null;
			Station station=null;
			Routes routes=null;
			int start=0;
			int end=network.stations.length;
			if(iCount>=0)
			{
				start=iCount*100;
				end=Math.min((iCount+1)*100,network.stations.length);
			}
			for(int i=start;i<end;i++)
			{
				for(int j=0;j<network.stations.length;j++)
				{
					if(i==j)
						continue;
					
					stationO=ODInfos[i][j].stationOrigin.stationName;
					lineO=ODInfos[i][j].stationOrigin.line.lineName;
					stationD=ODInfos[i][j].stationDestination.stationName;
					lineD=ODInfos[i][j].stationDestination.line.lineName;
					
					
						if(style==0)
						{
							
							
							routes=ODInfos[i][j].ReasonableRoutesRef[type];
						}
						if(style==1)
						{
							
							routes=ODInfos[i][j].ReasonableRoutes[type];
						}
						
						iter1=routes.routes.listIterator();
					
					iNo=0;
					fastTime=ODInfos[i][j].fastTimeCost/60;
					//遍历每一条路径
					while(iter1.hasNext())
					{
						iNo++;
						route=iter1.next();
						
					costdistance=route.costInfos[0]/60;
					transfertime=route.costInfos[1];
					costcom=route.costInfos[2]/60;
					String stationNameOld="";
					
						stationList="";
						lineList="";
						transferList="";
						stationNameOld="";
						transferStations="";
						stationNum=0;
						int b=0;
						ListIterator<Node> iter=null;
						iter=route.nodes.listIterator();
						int m=0;
						//遍历该路径上每一个点
						while(iter.hasNext())
						{
							Node node=iter.next();
							station=node.station;

							//起点站
							if(m==0)
							{
								lineList+=station.line.lineName;
								m++;}
							//换乘站，如西直门(relationFather=0)-西直门(relationFather=1)
							if(node.relationFather==1)
							{
								//
								if(stationNameOld!=station.stationName)
								{
									//如果transferList是空，就不用加上-，如果不是空，就加上-
									if(!transferList.equals(""))
									transferList+="-";
									
									transferList+=station.stationName;
									
									stationNameOld=station.stationName;
									
									lineList+="-"+node.station.line.lineName;
								}
									
							}
							else
							{
								
								if(b!=0)
									stationList+="-";
								stationList+=station.stationName;
								b=1;
								
								stationNum++;
								
							}
								
						}
							fileWriter.write("\n"+lineO+"\t"+stationO+"\t"+lineD+"\t"+stationD+"\t"+fastTime+"\t"+type+"\t"+iNo+"\t"+costdistance+"\t"+costcom+"\t"+transfertime+"\t"+lineList+"\t"+transferList+"\t"+transferStations+"\t"+stationNum+"\t"+stationList);
					}
					if(iNo==0)
					{
						System.out.println("null:"+stationO+"-"+stationO);
						
						
					}


					}
				}
			
			 fileWriter.flush();
			 fileWriter.close();
			 writeTemp.flush();
			writeTemp.close();
			
			}
			catch(IOException e)
			{	
				e.printStackTrace();
			}

		}
	
	
		public void computelatestTime(String stationO, String stationD,int beChanged)
		{
			if(stationO.equals(stationD))
				return;
			
			Station stationsO[]=this.network.GetStationsByStationName(stationO);
			Station stationsD[]=this.network.GetStationsByStationName(stationD);
			ListIterator<Route> iter1=null;
			ListIterator<Node> iter2=null;
			Node node=null;
			Route route=null;
			int iO=0;
			int iD=0;
			
			int i,j,k;
			long timeTemp=0;
			long time=0;
			int direction;
			for(i=0;i<stationsO.length;i++)
			{
				iO=stationsO[i].odNo;
				for(j=0;j<stationsD.length;j++)
				{
			
					iD=stationsD[j].odNo;
					for(k=0;k<3;k++)
					{
						if(beChanged==0)
							iter1=this.ODInfos[iO][iD].ReasonableRoutesRef[k].routes.listIterator();
						if(beChanged==1)
							iter1=this.ODInfos[iO][iD].ReasonableRoutes[k].routes.listIterator();
						while(iter1.hasNext())
						{
							route=iter1.next();
							iter2=route.nodes.listIterator();
							while(iter2.hasNext())
							{
								iter2.next();
							}
							
							
							
							
							
							while(iter2.hasPrevious())
							{
								node=iter2.previous();
								if(node.relationFather==1)
								 continue;
								if(node.relationFather==0)
									break;
							}
							node.stopInfo=node.station.getlaststopInfo(node.direction);
							
							
							
							long timeOld=0;
							while(iter2.hasPrevious())
							{  
								
								if(node.relationFather==1)
								{
									if(node.stopInfo==null)
										System.out.println("O:"+stationsO[i].stationNo+"D:"+stationsD[j].stationNo+"node:"+node.station.stationNo);
									time=node.stopInfo.departureTime-node.costToFather;
									
									if(node.nodeFather.station==stationsO[i])
									{
										route.time=time;
										break;
									}
									direction=node.nodeFather.direction;
									if(direction==0)
										{
										direction=this.network.setDirection(node.nodeFather.nodeFather.station, node.nodeFather.station);
										if(direction==0)
											{
													node=iter2.previous();
													time=time-node.nodeFather.costToFather;
													if(node.nodeFather.station==stationsO[i])
													{
														route.time=time;
														break;
													}
											}
											}
									node.nodeFather.stopInfo=node.nodeFather.station.getStopinfoBeforeTime(direction, time);
									
								}
								if(node.relationFather==0)
								{
									if(node.stopInfo==null)
										System.out.println("error: O:"+stationsO[i].stationNo+"D:"+stationsD[j].stationNo+"node:"+node.station.stationNo);
									
									node.nodeFather.stopInfo=node.stopInfo.getValidPreviousStopInfo(node.nodeFather.station);
									
									if(node.nodeFather.station==stationsO[i])
									{
										route.time=node.nodeFather.stopInfo.departureTime;
										break;
									}
									
									
								}
								node=iter2.previous();
							}
						}
						
					}
				}
			}
		}
		public void computeLatestTimes(  int beChanged)
		{
			int len=network.stations.length;
			Station stations1[];
			Station stations2[];
			int indexO,indexD;
			String stationO,stationD;
			for(int i=0;i<len;i++)
				for(int j=0;j<len;j++)
				{
					ODInfos[i][j].bwrite=false;
				}
				
				for(int i=0;i<len;i++)
				{
					for(int j=0;j<len;j++)
					{
						
						if(ODInfos[i][j].bwrite)
							continue;
						stationO=ODInfos[i][j].stationOrigin.stationName;
						stationD=ODInfos[i][j].stationDestination.stationName;
						stations1=network.GetStationsByStationName(stationO);
						stations2=network.GetStationsByStationName(stationD);
						for(indexO=0;indexO<stations1.length;indexO++)
						{
							for(indexD=0;indexD<stations2.length;indexD++)
							{
								int iO=stations1[indexO].odNo;
								int iD=stations2[indexD].odNo;
								ODInfos[iO][iD].bwrite=true;
							}
						}
						
							this.computelatestTime(stationO, stationD, beChanged);
						
					
					}
					
				}
				for(int i=0;i<len;i++)
					for(int j=0;j<len;j++)
					{
						ODInfos[i][j].bwrite=false;
					}
		}
		public void writelatesTimes(int type,int style)
		{
			

			
			try{
				String filePath="";
				if(type==0)
					filePath="Data\\Result.txt";
				if(type==1)
					filePath="Data\\ResultbyName.txt";
			FileWriter fileWriter=new FileWriter(filePath,true);
			DateTransfer date=new  DateTransfer();
			
			
			
				
			String stationO="";
			String stationD=""; String lineO=""; String lineD=""; String lineList=""; String transferList=""; String stationList="";
			String latestTime="";String indexStr="";
			int stationNum=0;
			Station stations1[];
			Station stations2[];
			int indexO,indexD;
			Routes routes=null;
			Station station=null;
			Route route=null;
			Node node=null;
			for(int i=0;i<network.stations.length;i++)
			{
				for(int j=0;j<network.stations.length;j++)
				{

					ODInfos[i][j].bwrite=false;
				}
			}
			for(int i=0;i<network.stations.length;i++)
			{
				for(int j=0;j<network.stations.length;j++)
				{
					if(type==1&&ODInfos[i][j].bwrite)
						continue;
					stationO=ODInfos[i][j].stationOrigin.stationName;
					lineO=ODInfos[i][j].stationOrigin.line.lineName;
					stationD=ODInfos[i][j].stationDestination.stationName;
					lineD=ODInfos[i][j].stationDestination.line.lineName;
					if(type==1)
					{
						stationO=ODInfos[i][j].stationOrigin.stationName;
						stationD=ODInfos[i][j].stationDestination.stationName;
						if(ODInfos[i][j].stationOrigin.type==1||ODInfos[i][j].stationDestination.type==1)
						{
							stations1=network.GetStationsByStationName(stationO);
							stations2=network.GetStationsByStationName(stationD);
							for(indexO=0;indexO<stations1.length;indexO++)
								for(indexD=0;indexD<stations2.length;indexD++)
								{
									ODInfos[stations1[indexO].odNo][stations2[indexD].odNo].bwrite=true;
								}
						}
					}
					int index=0;
					String stationNameOld="";
					
					
					for(int n=0;n<3;n++)
					{
					if(style==0)
						routes=ODInfos[i][j].ReasonableRoutesRef[n];
					if(style==1)
						routes=ODInfos[i][j].ReasonableRoutes[n];
					for(int k=0;k<routes.routes.size();k++)
					{
						index++;
						indexStr="R"+n+":"+index;
						
						route=routes.routes.get(k);
						date=new  DateTransfer();
						 date.time=route.time;
						
						 date.timeToDate();
						 latestTime="";
						if(date.day==1)
							latestTime+="�ڶ���";
						latestTime+=date.hour+":"+date.minute+":"+date.second;
						
						stationList="";
						lineList="";
						transferList="";
						stationNameOld="";
						stationNum=0;
						int b=0;
						ListIterator<Node> iter=null;
						iter=route.nodes.listIterator();
						int m=0;
						while(iter.hasNext())
						{
							node=iter.next();
							station=node.station;

							if(m==0)
							{
								lineList+=station.line.lineName;
								m++;}
							if(node.relationFather==1)
							{
								
								if(stationNameOld!=station.stationName)
								{
									if(!transferList.equals(""))
									transferList+="-";
									transferList+=station.stationName;
									
									
									stationNameOld=station.stationName;
									
									lineList+="-"+node.station.line.lineName;
								}

									
							}
							else
							{
								
								if(b!=0)
									stationList+="-";
								stationList+=station.stationName;
								b=1;
								
								stationNum++;
								
							}
						
								
						}
						if(type==0)
							fileWriter.write("\n"+lineO+"\t"+stationO+"\t"+lineD+"\t"+stationD+"\t"+latestTime+"\t"+indexStr+"\t"+lineList+"\t"+transferList+"\t"+stationNum+"\t"+stationList);
						if(type==1)
							fileWriter.write("\n"+stationO+"\t"+stationD+"\t"+latestTime+"\t"+indexStr+"\t"+lineList+"\t"+transferList+"\t"+stationNum+"\t"+stationList);
					}
					
					}}
			}
			 fileWriter.flush();
			 fileWriter.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}

		
		}
		public void computeForwardpath(String timeStart, String stationO, String stationD,int beChanged)
		{
			if(stationO.equals(stationD))
				return;
			
			Station stationsO[]=this.network.GetStationsByStationName(stationO);
			Station stationsD[]=this.network.GetStationsByStationName(stationD);
			ListIterator<Route> iter1=null;
			ListIterator<Node> iter2=null;
			Node node=null;
			Route route=null;
			int iO=0;
			int iD=0;
			
			int i,j,k;
			long timeTemp=0;
			long time=0;
			int direction;
			for(i=0;i<stationsO.length;i++)
			{
				iO=stationsO[i].odNo;
				for(j=0;j<stationsD.length;j++)
				{
			
					iD=stationsD[j].odNo;
					for(k=0;k<3;k++)
					{
						if(beChanged==0)
							iter1=this.ODInfos[iO][iD].ReasonableRoutesRef[k].routes.listIterator();
						if(beChanged==1)
							iter1=this.ODInfos[iO][iD].ReasonableRoutes[k].routes.listIterator();
						while(iter1.hasNext())
						{
							route=iter1.next();
							iter2=route.nodes.listIterator();
							
							
							
							
							
							
							while(iter2.hasNext())
							{
								node=iter2.previous();
								if(node.relationFather==1)
								 continue;
								if(node.relationFather==0)
									break;
							}
							node.stopInfo=node.station.getlaststopInfo(node.direction);
							
							
							
							long timeOld=0;
							while(iter2.hasPrevious())
							{  
								
								if(node.relationFather==1)
								{
									if(node.stopInfo==null)
										System.out.println("O:"+stationsO[i].stationNo+"D:"+stationsD[j].stationNo+"node:"+node.station.stationNo);
									time=node.stopInfo.departureTime-node.costToFather;
									
									if(node.nodeFather.station==stationsO[i])
									{
										route.time=time;
										break;
									}
									direction=node.nodeFather.direction;
									if(direction==0)
										{
										direction=this.network.setDirection(node.nodeFather.nodeFather.station, node.nodeFather.station);
										if(direction==0)
											{
													node=iter2.previous();
													time=time-node.nodeFather.costToFather;
													if(node.nodeFather.station==stationsO[i])
													{
														route.time=time;
														break;
													}
											}
											}
									node.nodeFather.stopInfo=node.nodeFather.station.getStopinfoAfterTime(direction, time);
									
								}
								if(node.relationFather==0)
								{
									if(node.stopInfo==null)
										System.out.println("error: O:"+stationsO[i].stationNo+"D:"+stationsD[j].stationNo+"node:"+node.station.stationNo);
									
									node.nodeFather.stopInfo=node.stopInfo.getValidPreviousStopInfo(node.nodeFather.station);
									
									if(node.nodeFather.station==stationsO[i])
									{
										route.time=node.nodeFather.stopInfo.departureTime;
										break;
									}
									
									
								}
								node=iter2.previous();
							}
						}
						
					}
				}
			}
		}
		public void computeforwardpaths(  String timeSart, int beChanged)
		{
			int len=network.stations.length;
			Station stations1[];
			Station stations2[];
			int indexO,indexD;
			String stationO,stationD;
			for(int i=0;i<len;i++)
				for(int j=0;j<len;j++)
				{
					ODInfos[i][j].bwrite=false;
				}
				
				for(int i=0;i<len;i++)
				{
					for(int j=0;j<len;j++)
					{
						
						if(ODInfos[i][j].bwrite)
							continue;
						stationO=ODInfos[i][j].stationOrigin.stationName;
						stationD=ODInfos[i][j].stationDestination.stationName;
						stations1=network.GetStationsByStationName(stationO);
						stations2=network.GetStationsByStationName(stationD);
						for(indexO=0;indexO<stations1.length;indexO++)
						{
							for(indexD=0;indexD<stations2.length;indexD++)
							{
								int iO=stations1[indexO].odNo;
								int iD=stations2[indexD].odNo;
								ODInfos[iO][iD].bwrite=true;
							}
						}
						
							this.computeForwardpath(timeSart, stationO, stationD, beChanged);
						
					
					}
					
				}
				for(int i=0;i<len;i++)
					for(int j=0;j<len;j++)
					{
						ODInfos[i][j].bwrite=false;
					}
		}
		
		public void computeBackwardpath(String timeEnd, String stationO, String stationD,int beChanged)
		{
			if(stationO.equals(stationD))
				return;
			
			Station stationsO[]=this.network.GetStationsByStationName(stationO);
			Station stationsD[]=this.network.GetStationsByStationName(stationD);
			ListIterator<Route> iter1=null;
			ListIterator<Node> iter2=null;
			Node node=null;
			Route route=null;
			int iO=0;
			int iD=0;
			
			int i,j,k;
			long timeTemp=0;
			long time=0;
			long time0=0;
			try
	        {
	    	DateFormat dateFormat=DateFormat.getTimeInstance();
	        Date date=dateFormat.parse(timeEnd);
	        time0=date.getHours()*3600+date.getMinutes()*60+date.getSeconds();
	        }
	    	catch (ParseException e)
	        {
	        	e.printStackTrace();
	        }
			int direction;
			for(i=0;i<stationsO.length;i++)
			{
				iO=stationsO[i].odNo;
				for(j=0;j<stationsD.length;j++)
				{
			
					iD=stationsD[j].odNo;
					for(k=0;k<3;k++)
					{
						if(beChanged==0)
							{iter1=this.ODInfos[iO][iD].ReasonableRoutesRef[k].routes.listIterator();
							
							}
						if(beChanged==1)
							iter1=this.ODInfos[iO][iD].ReasonableRoutes[k].routes.listIterator();
						
						while(iter1.hasNext())
						{
							route=iter1.next();
							if(route.nodes.size()<2)
								continue;
							time=time0;
							iter2=route.nodes.listIterator();
							Node nodepre=null;
							while(iter2.hasNext())
							{nodepre=node;
							node=iter2.next();
							}
							
							
							
							
							
							while(iter2.hasPrevious())
							{
								node=iter2.previous();
								
								if(node.relationFather==1)
								{
									node.stopInfo=new StopInfo();
									node.stopInfo.arrivalTime=time;
									time-=node.costToFather;
									
								 continue;
								}
								if(node.relationFather==0)
									break;
								
							}
							direction=this.network.setDirection(node.nodeFather.station, node.station);
							node.stopInfo=node.station.getStopinfoBeforeTime(direction, time);
							
							
							long timeOld=0;
							while(iter2.hasPrevious())
							{  
								if(node.relationFather==1)
								{
									if(node.stopInfo==null)
										System.out.println("O:"+stationsO[i].stationNo+"D:"+stationsD[j].stationNo+"node:"+node.station.stationNo);
									time=node.stopInfo.departureTime-node.costToFather;
									node.nodeFather.stopInfo=new StopInfo();
									node.nodeFather.stopInfo.arrivalTime=time;
									if(node.nodeFather.station==stationsO[i])
									{
										node.nodeFather.stopInfo=new StopInfo();
										node.nodeFather.stopInfo.arrivalTime=node.nodeFather.stopInfo.departureTime=time;
										route.time=time;
										break;
									}
									direction=node.nodeFather.direction;
									int bfind=0;
									if(direction==0)
										direction=1;
									while(direction==0)
										{
										node.nodeFather.stopInfo=new StopInfo();
										node.nodeFather.stopInfo.departureTime=time;
										node=iter2.previous();
										
										time=time-node.costToFather;
										node.nodeFather.stopInfo=new StopInfo();
										node.nodeFather.stopInfo.arrivalTime=time;
										if(node.nodeFather.station==stationsO[i])
										{
											node.nodeFather.stopInfo=new StopInfo();
											node.nodeFather.stopInfo.departureTime=time;
											
											route.time=time;
											bfind=1;
											break;
										}
										direction=this.network.setDirection(node.nodeFather.nodeFather.station, node.nodeFather.station);
										
											
											}
									if(bfind==1)
										break;
									node.nodeFather.stopInfo=node.nodeFather.station.getStopinfoBeforeTime(direction, time);
									
								}
								if(node.relationFather==0)
								{
									if(node.stopInfo==null)
										System.out.println("error: O:"+stationsO[i].stationNo+"D:"+stationsD[j].stationNo+"node:"+node.station.stationNo);
									
									node.nodeFather.stopInfo=node.stopInfo.getValidPreviousStopInfo(node.nodeFather.station);
									
									if(node.nodeFather.station==stationsO[i])
									{
										if(node.nodeFather.stopInfo==null)
										{
											System.out.println("error: O:"+stationsO[i].stationNo+"D:"+stationsD[j].stationNo+
													"node:"+node.station.stationNo
													+"nodeFather:"+node.nodeFather.station.stationNo);
										}
										route.time=node.nodeFather.stopInfo.departureTime;
										break;
									}
								}
								node=iter2.previous();
							}
						}
						
					}
				}
			}
		}
		public void computeBackwardpaths(  String timeEnd, int beChanged)
		{
			int len=network.stations.length;
			Station stations1[];
			Station stations2[];
			int indexO,indexD;
			String stationO,stationD;
			for(int i=0;i<len;i++)
				for(int j=0;j<len;j++)
				{
					ODInfos[i][j].bwrite=false;
				}
				
				for(int i=0;i<len;i++)
				{
					for(int j=0;j<len;j++)
					{
						
						if(ODInfos[i][j].bwrite)
							continue;
						stationO=ODInfos[i][j].stationOrigin.stationName;
						stationD=ODInfos[i][j].stationDestination.stationName;
						stations1=network.GetStationsByStationName(stationO);
						stations2=network.GetStationsByStationName(stationD);
						for(indexO=0;indexO<stations1.length;indexO++)
						{
							for(indexD=0;indexD<stations2.length;indexD++)
							{
								int iO=stations1[indexO].odNo;
								int iD=stations2[indexD].odNo;
								ODInfos[iO][iD].bwrite=true;
							}
						}
						
  							this.computeBackwardpath(timeEnd,stationO, stationD, beChanged);
						
					
					}
					
				}
				for(int i=0;i<len;i++)
					for(int j=0;j<len;j++)
					{
						ODInfos[i][j].bwrite=false;
					}
		}	

public void writeBackePaths(String time)
{
	try{
		String filePath="";
			filePath="Data\\BackwardPaths.txt";
		FileWriter fileWriter;
	fileWriter=new FileWriter(filePath);
	FileWriter writeTemp=new FileWriter("Data\\error-Resonable.txt");
	fileWriter.write("ʱ��:"+time+"\n");
	fileWriter.write("��վ������·\t��վ���\t��վ������·\t��վ���\t�ɴ���\t����\t���\tʱ��ɱ�\t���˴���\t�ۺ�\t;����·\t;������վ\t���˳�վ�б�\t;����վ����\t;����վ");
	DateTransfer date=null;
	String stationO="";
	String access="";
	String typeStr="";
	String stationD=""; String lineO=""; String lineD=""; String lineList=""; String transferList=""; String stationList="";
	String costdistance="";double costcom=0; int transfertime=0;String transferStations="";
	int stationNum=0;
	Route route=null;
	Station station=null;
	String strTemp="";
	Station s=null;
	ListIterator<Route> iter1=null;
	int len=network.stations.length;
	date=new  DateTransfer();
	for(int indexO=0;indexO<len;indexO++)
	{
	for(int j=0;j<len;j++)
	{
		if(indexO==j)
				continue;
			stationO=ODInfos[indexO][j].stationOrigin.stationName;
			lineO=ODInfos[indexO][j].stationOrigin.line.lineName;
			stationD=ODInfos[indexO][j].stationDestination.stationName;
			lineD=ODInfos[indexO][j].stationDestination.line.lineName;
		
			for(int type=0;type<3;type++)
			{
				
				iter1=ODInfos[indexO][j].ReasonableRoutesRef[type].routes.listIterator();
			
			int iNo=0;
			while(iter1.hasNext())
			{
				iNo++;
				route=iter1.next();
			transfertime=0;
				costcom=0;
				costdistance="";
				if(route==null)
				{
					access="0";
					
				}
				else
				{
					access="1";
						transfertime=(int)route.costInfos[1];
						costcom=route.costInfos[2];
							
							 date.time=(long)route.costInfos[0];
							
							 date.timeToDate();
							costdistance+=date.hour+":"+date.minute+":"+date.second;
				}
				
				String stationNameOld="";
				stationList="";
				lineList="";
				transferList="";
				stationNameOld="";
				transferStations="";
				typeStr="";
				typeStr+=type;
				stationNum=0;
				int b=0;
				ListIterator<Node> iter=null;
				if(route!=null)
				{
				iter=route.nodes.listIterator();
				int m=0;
				while(iter.hasNext())
					{
						Node node=iter.next();
						station=node.station;

						if(m==0)
						{
							lineList+=station.line.lineName;
							m++;}
						if(node.relationFather==1)
						{
							
							
							{
								if(!transferList.equals(""))
								transferList+="-";
								date.time=(long)node.costToFather;
								 date.timeToDate();
								 strTemp="";
								strTemp+=date.hour+":"+date.minute+":"+date.second;
								transferList+=station.stationName+"(���ˣ�"+strTemp+")";
								stationList+="(���ˣ�"+strTemp+")"+station.stationName;
								if(node.station==network.stations[j])
								{
									date.time=(long)node.arriveTime;
									 date.timeToDate();
									 strTemp="";
									strTemp+=date.hour+":"+date.minute+":"+date.second;
									stationList+=station.stationName+"(�յ���"+strTemp+")";
								}
									
								stationNameOld=station.stationName;
								lineList+="-"+node.station.line.lineName;
							}
						}
						else
						{
							
							
							if(node.relationFather==-1)
							{
								
								 strTemp="";
								strTemp+=time;
								stationList+=station.stationName+"(���"+strTemp+")";
								
							}
							if(node.relationFather==0)
							{
								{
									
									date.time=(long)node.stopInfo.arrivalTime;
									 date.timeToDate();
									strTemp+=date.hour+":"+date.minute+":"+date.second;
									stationList+=strTemp+")"+station.stationName;
								}
								if(node.station==network.stations[j])
								{
									date.time=(long)node.arriveTime;
									 date.timeToDate();
									 strTemp="";
									strTemp+=date.hour+":"+date.minute+":"+date.second;
									stationList+="(�յ���"+strTemp+")";
								}
								date.time=(long)node.stopInfo.departureTime;
								 date.timeToDate();
								 strTemp="";
								strTemp+=date.hour+":"+date.minute+":"+date.second;
								strTemp+="-";
								stationList+="("+strTemp;
								
							}
							
							
							stationNum++;
							
						}
						s=node.station;	
					}
				}
					
					
					fileWriter.write("\n"+lineO+"\t"+stationO+"\t"+lineD+"\t"+stationD
							+"\t"+access+"\t"+type+"\t"+iNo+"\t"+costdistance+"\t"+transfertime+"\t"+costcom+"\t"+lineList+"\t"+transferList+"\t"+transferStations+"\t"+stationNum+"\t"+stationList);
				
					
			}
			}
		}
	}
	 fileWriter.flush();
	 fileWriter.close();
	 writeTemp.flush();
	writeTemp.close();
	
	
	}
catch(IOException e)
{	
	e.printStackTrace();
}

}
public void routes_shrink2_6(int type)//
{
	int length = this.network.stations.length;
	for(int i = 0 ; i < length ; i++)
	{
		Station sO=network.stations[i];
		for(int j = 0 ; j < length ; j++)
		{				
			Station sD=network.stations[j];
			 if(sO.stationName.equals(sD.stationName))//ͬһ����վ���������ֱ��2����-4���ߣ�
				 continue;//
			
			Routes routesArray[] = null;
			if(type==0)
				routesArray=this.ODInfos[i][j].ReasonableRoutesRef;
			else
				routesArray= this.ODInfos[i][j].ReasonableRoutes;
			for(int itype=0;itype<3;itype++)
			{
			 Routes routes = routesArray[itype];
			
			if(routes==null)
				continue;
			 if(routes.routes.size()==0)
				 continue;
		{
			 ListIterator<Route> it = null;
			 it=routes.routes.listIterator();
			 int count1=0,count2=0,count0=0;
			  while(it.hasNext())
			  {
				  Route route = it.next();
				  Node nodepre=null,nodeNext=null;
				  if(route==null)
					  continue;
				  int size=route.nodes.size();
				 
				  //����·��·���޸ģ��Գ�ʼ���˵�·������Ӧ������������뵽 ���˺��O��D�У��޳���ʼ���˵�·����
				  nodepre=route.nodes.get(0);
				  nodeNext=route.nodes.get(1);
				  //int direction=this.network.getDirectionbyStations(nodepre.station, nodeNext.station);
				  //if(direction==1)
				  {
					  count1++;
					  if(count1>=3)
						  it.remove();
				  }
				 /* if(direction==2)
				  {
					  count2++;
					  if(count2>=3)
						  it.remove();
				  }*/
				 /* if(direction==0)
				  {
					  count0++;
					  if(count0>=3)
						  it.remove();
				  }*/
			//	  if(count1>=3&&count2>=3)
				//	  break;
				 
				  
			  }
			  routes.routes.toArray();
		}
		}
		}
	}
	
}
public void routes_shrink(int type)
//����·��·���޸ģ����޳���ʼ���˵�·����
//�Ե��ﻻ�˵�·�����޳�·��
{
	int length = this.network.stations.length;
	for(int i = 0 ; i < length ; i++)
	{
		Station sO=network.stations[i];
		for(int j = 0 ; j < length ; j++)
		{				
			Station sD=network.stations[j];
			 if(sO.stationName.equals(sD.stationName))//ͬһ����վ���������ֱ��2����-4���ߣ�
				 continue;//
			 Routes routesArray[] = null;
			 if(type==0)
					routesArray=this.ODInfos[i][j].ReasonableRoutesRef;
				else
					routesArray= this.ODInfos[i][j].ReasonableRoutes;
				for(int itype=0;itype<3;itype++)
				{
				 Routes routes = routesArray[itype];
			
			if(routes==null)
				continue;
			 if(routes.routes.size()==0)
				 continue;
		{
			 ListIterator<Route> it = null;
			 it=routes.routes.listIterator();
			// int count=0;
			  while(it.hasNext())
			  {
				  Route route = it.next();
				  Node nodepre=null,nodeNext=null;
				  if(route==null)
					  continue;
				  int size=route.nodes.size();
				 
				  //����·��·���޸ģ��Գ�ʼ���˵�·������Ӧ������������뵽 ���˺��O��D�У��޳���ʼ���˵�·����
				  nodepre=route.nodes.get(0);
				  nodeNext=route.nodes.get(1);
				  
				 // route.index-=count;
				  if(nodeNext.relationFather==1)//����
				  {
					 if(route.nodes.get(2).relationFather==1)
						 nodeNext=route.nodes.get(2);
					
					it.remove();
					//count++;
					continue;
				  }
				  
				//�Ե��ﻻ�˵�·����������Ļ���D���뵽����ǰ��OD���У��޳�·��					  
				  nodepre=route.nodes.get(size-2);
				  nodeNext=route.nodes.get(size-1);  
				  if(nodeNext.relationFather==1)//����
				  {
					  Station O1=nodepre.station;
					  Station O2=nodeNext.station;
					
					 
					  it.remove();
				//	  count++;
					  continue;
				  }
			  }
		}
				}
		}
	}
	
}
}
