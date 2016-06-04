package pathFinding;

import java.io.FileWriter;
import java.io.IOException;


import java.util.*;

import netWork.*;

public class Find2 {

	
	private List<NodeT> nodeTsSetB= new ArrayList<NodeT>();
	private NodeT nodeTsSetA[][];
	public ODInfo[][] ODInfos;
	public Network network=null;
	public int k=0;
	private Compare compare=new Compare();

	private Station stationBeFinding=null;
	public void setNetwork(Network anetwork)
	{
		network=anetwork;
	}
	public void WriteTempLatesRoute(int type)
	{
		
		try{
			String filePath="";
			if(type==0)
				filePath="Data\\Result.txt";
			if(type==1)
				filePath="Data\\ResultbyName.txt";
		FileWriter fileWriter=new FileWriter(filePath);
		DateTransfer date=new  DateTransfer();
		date.time=System.currentTimeMillis()/1000000;   
		 date.timeToDate();
		fileWriter.write("时间"+date.hour+":"+date.minute+":"+date.second+"\n");
		if(type==0)
		fileWriter.write("起站所在线路\t起站名称\t到站所在线路\t到站名称\t最晚出发时间\t路径序号\t途经线路\t途经换乘站\t途径车站数量\t途经车站");
		if(type==1)
			fileWriter.write("起站名称\t到站名称\t最晚出发时间\t路径序号\t途经线路\t途经换乘站\t途径车站数量\t途经车站");
		String stationO="";
		String stationD=""; String lineO=""; String lineD=""; String lineList=""; String transferList=""; String stationList="";
		String latestTime="";
		int stationNum=0;
		Station stations1[];
		Station stations2[];
		int indexO,indexD;
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
				
				
				for(int k=0;k<ODInfos[i][j].TempNodeList.size();k++)
				{
					index++;
					
					NodeLatestRoute nodelist=ODInfos[i][j].TempNodeList.get(k);
					date=new  DateTransfer();
					 date.time=nodelist.Nodes.get(0).stopInfo.departureTime;
					
					 date.timeToDate();
					 latestTime="";
					if(date.day==1)
						latestTime+="第二天";
					latestTime+=date.hour+":"+date.minute+":"+date.second;
					
					stationList="";
					lineList="";
					transferList="";
					stationNameOld="";
					stationNum=0;
					int b=0;
					for(int m=0;m<nodelist.Nodes.size();m++)
					{
						Station station=nodelist.Nodes.get(m).station;

						if(m==0)
							lineList+=station.line.lineName;
						if(nodelist.Nodes.get(m).relationFather==1)
						{
							
							if(stationNameOld!=station.stationName)
							{
								if(!transferList.equals(""))
								transferList+="-";
								transferList+=station.stationName;
								
								
								stationNameOld=station.stationName;
								
								lineList+="-"+nodelist.Nodes.get(m+1).station.line.lineName;
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
						fileWriter.write("\n"+lineO+"\t"+stationO+"\t"+lineD+"\t"+stationD+"\t"+latestTime+"\t"+index+"\t"+lineList+"\t"+transferList+"\t"+stationNum+"\t"+stationList);
					if(type==1)
						fileWriter.write("\n"+stationO+"\t"+stationD+"\t"+latestTime+"\t"+index+"\t"+lineList+"\t"+transferList+"\t"+stationNum+"\t"+stationList);
				}

				
			}
		}
		 fileWriter.flush();
		 fileWriter.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}
		
	public void WriteLatesRoute(int type)
	{
		
		try{
			String filePath="";
			if(type==0)
				filePath="Data\\Result.txt";
			if(type==1)
				filePath="Data\\ResultbyName-2.txt";
		FileWriter fileWriter=new FileWriter(filePath);
		DateTransfer date=new  DateTransfer();
		date.time=System.currentTimeMillis()/1000000;   
		 date.timeToDate();
		if(type==0)
		fileWriter.write("起站所在线路\t起站名称\t到站所在线路\t到站名称\t最晚出发时间\t路径序号\t途经线路\t途经换乘站\t途径车站数量\t途经车站");
		if(type==1)
			fileWriter.write("起站名称\t到站名称\t最晚出发时间\t路径序号\t途经线路\t途经换乘站\t途径车站数量\t途经车站");
		String stationO="";
		String stationD=""; String lineO=""; String lineD=""; String lineList=""; String transferList=""; String stationList="";
		String latestTime="";
		int stationNum=0;
		Station stations1[];
		Station stations2[];
		int indexO,indexD;
		for(int i=0;i<network.stations.length;i++)
		{
			for(int j=0;j<network.stations.length;j++)
			{
				if(ODInfos[i][j].latestRoutes==null)
				{
					//if(!ODInfos[i][j].stationDestination.stationName.equals(ODInfos[i][j].stationOrigin.stationName))
						//System.out.println("无最晚记录i="+i+"j="+j);
					continue;
				}
				if(type==1&&ODInfos[i][j].bwrite)
					continue;
				stationO=ODInfos[i][j].stationOrigin.stationName;
				lineO=ODInfos[i][j].stationOrigin.line.lineName;
				stationD=ODInfos[i][j].stationDestination.stationName;
				lineD=ODInfos[i][j].stationDestination.line.lineName;
				date=new  DateTransfer();
				 date.time=ODInfos[i][j].latestTime;
				
				 date.timeToDate();
				 latestTime="";
				if(date.day==1)
					latestTime+="第二天";
				latestTime+=date.hour+":"+date.minute+":"+date.second;
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
				
				for(int k=0;k<ODInfos[i][j].latestRoutes.size();k++)
				
				{
					index++;
					NodeLatestRoute nodelist=ODInfos[i][j].latestRoutes.get(k);
					stationList="";
					lineList="";
					transferList="";
					stationNameOld="";
					stationNum=0;
					int b=0;
					for(int m=0;m<nodelist.Nodes.size();m++)
					{
						Station station=nodelist.Nodes.get(m).station;
						if(b!=0)
							{
							stationList+="-";
							
							}
						b=1;
						stationList+=station.stationName;
						if(station.type==1)
						{
							stationList+="("+station.line.lineName+")";
						}
						if(m==0)
							lineList+=station.line.lineName;
						if(nodelist.Nodes.get(m).relationFather==1)
						{
							
							
							{
								if(!transferList.equals(""))
								transferList+="-";
								transferList+=station.stationName;
								
								
								stationNameOld=station.stationName;
								
								lineList+="-"+nodelist.Nodes.get(m+1).station.line.lineName;
							}

								
						}
						else
						{
							
							
								
							
							
							
							stationNum++;
							
						}
							
					}
					if(type==0)
						fileWriter.write("\n"+lineO+"\t"+stationO+"\t"+lineD+"\t"+stationD+"\t"+latestTime+"\t"+index+"\t"+lineList+"\t"+transferList+"\t"+stationNum+"\t"+stationList);
					if(type==1)
						fileWriter.write("\n"+stationO+"\t"+stationD+"\t"+latestTime+"\t"+index+"\t"+lineList+"\t"+transferList+"\t"+stationNum+"\t"+stationList);
				}

				
			}
		}
		 fileWriter.flush();
		 fileWriter.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}
	

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


	private void findLatestRoute()
	{
		nodeTsSetA=new NodeT[network.stations.length][2];
		int i,j;
		int count=0;
		long timeFirst=System.currentTimeMillis();
		for(Station station : network.stations)
		{
			timeFirst=System.currentTimeMillis();
						
		stationBeFinding=station;
		
			nodeTsSetB.removeAll(nodeTsSetB);
			
			for(i=0;i<network.stations.length;i++)
			{
			for(j=0;j<2;j++)
				{
				nodeTsSetA[i][j]=null;
				}
			}
			
			
			NodeT node=new NodeT();
			node.station=stationBeFinding;
			node.direction=0;
			
			
	
		    stationNextToLatestNode(node,0);
			while(!nodeTsSetB.isEmpty())

			{
				node=nodeTsSetB.get(0);
				nodeTsSetB.remove(0);
				i=node.station.odNo;
				j=2-node.stopInfo.trainInfo.direction;
				if(nodeTsSetA[i][j]==null)
				{
				
				int binserted=insertTempRoutes(node,stationBeFinding,0);
				if(binserted==1)
				{
					nodeTsSetA[i][j]=node;
					
				stationNextToNode(node,0);
				if(node.station.type==1)
				{
					
					transferToNode(node,node.station.transferStation,0);
				}
				}
				}
			}

		}
		
	}
	public void releaseTempRoute()
	{
		
		
		int len=this.network.stations.length;
		NodeT node=null;
		for(int i=0;i<len;i++)
			for(int j=0;j<len;j++)
			{
			
				if(!this.ODInfos[i][j].TempNodeList.isEmpty())
				{
					if(!this.ODInfos[i][j].TempNodeList.get(0).Nodes.isEmpty())
					{node=ODInfos[i][j].TempNodeList.get(0).Nodes.get(0);
					int direction=node.stopInfo.trainInfo.direction;
					this.ODInfos[i][j].nodeTRef[2-direction]=node;
					}
					if(this.ODInfos[i][j].TempNodeList.size()>=2)
					{node=ODInfos[i][j].TempNodeList.get(1).Nodes.get(0);
					int direction=node.stopInfo.trainInfo.direction;
					this.ODInfos[i][j].nodeTRef[2-direction]=node;
					}
					this.ODInfos[i][j].TempNodeList.removeAll(ODInfos[i][j].TempNodeList);
					
				}
			}
	}
	public void findLatestRoutes()
	{
		findLatestRoute();
		
		this.updateLatestTime();
	
		
		
		
		
		
		
		
		
		
		
	}
	private int insertTempRoutes(NodeT node, Station station, int type)
	{
		
		int bReturn=0;
		long time=System.currentTimeMillis();
		NodeT nodeFather=node.nodeFather;
		NodeLatestRoute nodeList;
		NodeLatestRoute nodeListRef;
		Station stationFather=nodeFather.station;
		int indexO;
		int indexD=station.odNo;
		if(station==stationFather)
		{
			nodeList=new NodeLatestRoute();
			nodeList.Nodes.add(node);
			nodeList.Nodes.add(nodeFather);
			indexO=node.station.odNo;
			this.ODInfos[indexO][indexD].TempNodeList.add(nodeList);
			long c=System.currentTimeMillis()-time;
			if(c>1)
				System.out.println("提前"+ODInfos[indexO][indexD].TempNodeList.size()+":"+c);
			return 1;
		}
		indexO=stationFather.odNo;
		int indexOnew=node.station.odNo;
		int index=ODInfos[indexO][indexD].TempNodeList.size()-1;
		int bRepeat=0;
		ListIterator<NodeLatestRoute> iter1=null;
		iter1=ODInfos[indexO][indexD].TempNodeList.listIterator();
		
		
		StopInfo stopinfos[]=null;
		time=System.currentTimeMillis();
		while(iter1.hasNext())
		{
			
			nodeListRef=iter1.next();
		   
			bRepeat=0;
			
			{
				if(nodeListRef.Nodes.
						get(0)==nodeFather)
				{
					nodeList=new NodeLatestRoute();
					nodeList.Nodes.add(node);
					nodeList.Nodes.addAll(nodeListRef.Nodes);
					
					ODInfos[indexOnew][indexD].TempNodeList.add(nodeList);
					bReturn=1;
				}
					continue;
			}

			
		}
		return bReturn;
		
	}
	public void updateLatestTime()
	{

		int indexO,indexD;
		long latestTime=0;
		long latestTime2=0;
		long timeRef=0;

		for(int j=0;j<network.stations.length;j++)
		{
			for(int i=0;i<network.stations.length;i++)
			{
				
					latestTime=0;
					for(int k=0;k<ODInfos[i][j].TempNodeList.size();k++)
					{
						timeRef=ODInfos[i][j].TempNodeList.get(k).Nodes.get(0).time;
						if(latestTime<timeRef)
						{
							latestTime=timeRef;
							indexO=i;
							indexD=j;
						}
					}
					for(int k=0;k<ODInfos[i][j].TempNodeList.size();k++)
					{
						NodeLatestRoute nodeList=ODInfos[i][j].TempNodeList.get(k);
						timeRef=nodeList.Nodes.get(0).time;
						
						if(latestTime>timeRef)
						{
							ODInfos[i][j].TempNodeList.remove(k);
							k--;
							continue;
						}
						if(nodeList.Nodes.get(nodeList.Nodes.size()-2).relationFather==1)
						{
							ODInfos[i][j].TempNodeList.remove(k);
							k--;
						}
					}
					ODInfos[i][j].latestTime=ODInfos[i][j].latestTimeODS=latestTime;
					if(ODInfos[i][j].stationDestination.type==1)
					{
						Station[] stations2=network.GetStationsByStationName(ODInfos[i][j].stationDestination.stationName);
						for(Station station2: stations2)
						{
							indexD=station2.odNo;
							
							
							if(ODInfos[i][j].latestTimeODS<ODInfos[i][indexD].latestTime)
								ODInfos[i][j].latestTimeODS=ODInfos[i][indexD].latestTime;
						}
						for(Station station2: stations2)
						{
							indexD=station2.odNo;
							
							
							ODInfos[i][indexD].latestTime=ODInfos[i][j].latestTimeODS;
						}
					}
					}
				}
	 
		
		{for(int j=0;j<network.stations.length;j++)
		{
			for(int i=0;i<network.stations.length;i++)
			{
				
			}
		}
		}
		int iTransfer=Integer.MAX_VALUE;
		for(int j=0;j<network.stations.length;j++)
		{
			for(int i=0;i<network.stations.length;i++)
			{
				
				if(ODInfos[i][j].latestRoutes==null)
						{
					
					
					
					{
						latestTime=ODInfos[i][j].latestTime;
						latestTime2=0;
					}
					
					

						Station[] stations=network.GetStationsByStationName(ODInfos[i][j].stationOrigin.stationName);
						Station[] stations2=network.GetStationsByStationName(ODInfos[i][j].stationDestination.stationName);
							
							for(Station station: stations)
							{
								for(Station station2: stations2)
								{
									indexO=station.odNo;
									indexD=station2.odNo;
									
									
									if(!ODInfos[indexO][indexD].TempNodeList.isEmpty())
									{
									timeRef=ODInfos[indexO][indexD].TempNodeList.get(0).Nodes.get(0).time;
									{if(latestTime<timeRef)
										latestTime=timeRef;
										}
									
									}
								}
							}
						
						
					
						
							
							
							for(Station station: stations)
							{
								for(Station station2: stations2)
								{
								indexO=station.odNo;
								indexD=station2.odNo;
								
								
									ODInfos[indexO][indexD].latestTime=latestTime;
								
								
								}
							}
						}
			}
			}
	
	}
	public void updateLatestRoutes()
	{
		int indexO,indexD;
		long latestTime=0;
		long latestTime2=0;
		long timeRef=0;

		for(int j=0;j<network.stations.length;j++)
		{
			for(int i=0;i<network.stations.length;i++)
			{
				
					latestTime=0;
					for(int k=0;k<ODInfos[i][j].TempNodeList.size();k++)
					{
						timeRef=ODInfos[i][j].TempNodeList.get(k).Nodes.get(0).time;
						if(latestTime<timeRef)
						{
							latestTime=timeRef;
							indexO=i;
							indexD=j;
						}
					}
					for(int k=0;k<ODInfos[i][j].TempNodeList.size();k++)
					{
						NodeLatestRoute nodeList=ODInfos[i][j].TempNodeList.get(k);
						timeRef=nodeList.Nodes.get(0).time;
						
						if(latestTime>timeRef)
						{
							ODInfos[i][j].TempNodeList.remove(k);
							k--;
							continue;
						}
						if(nodeList.Nodes.get(nodeList.Nodes.size()-2).relationFather==1)
						{
							ODInfos[i][j].TempNodeList.remove(k);
							k--;
						}
					}
					
					
					ODInfos[i][j].latestTime=latestTime;

					}
				}
		
		for(int j=0;j<network.stations.length;j++)
		{
			for(int i=0;i<network.stations.length;i++)
			{
				if(ODInfos[i][j].stationDestination.type==1)
				{
					Station[] stations2=network.GetStationsByStationName(ODInfos[i][j].stationDestination.stationName);
					for(Station station2: stations2)
					{
						indexD=station2.odNo;
						
						
						if(ODInfos[i][j].latestTimeODS<ODInfos[i][indexD].latestTime)
							ODInfos[i][j].latestTimeODS=ODInfos[i][indexD].latestTime;
					}
					for(Station station2: stations2)
					{
						indexD=station2.odNo;
						
						
						ODInfos[i][indexD].latestTimeODS=ODInfos[i][j].latestTimeODS;
					}
				}
			}
		}
		int iTransfer=Integer.MAX_VALUE;
		for(int j=0;j<network.stations.length;j++)
		{
			for(int i=0;i<network.stations.length;i++)
			{
				
				if(ODInfos[i][j].latestRoutes==null)
						{
					
					
					if(ODInfos[i][j].stationOrigin.line.lineNo!=98&&
							ODInfos[i][j].stationDestination.line.lineNo!=98)
					{
						latestTime=ODInfos[i][j].latestTime;
						latestTime2=0;
					}
					else
					{
						latestTime2=ODInfos[i][j].latestTime;
						latestTime=0;
					}
					

					
					
						Station[] stations=network.GetStationsByStationName(ODInfos[i][j].stationOrigin.stationName);
						Station[] stations2=network.GetStationsByStationName(ODInfos[i][j].stationDestination.stationName);
						
						
							
							for(Station station: stations)
							{
								for(Station station2: stations2)
								{
									indexO=station.odNo;
									indexD=station2.odNo;
									
									
									if(!ODInfos[indexO][indexD].TempNodeList.isEmpty())
									{
									timeRef=ODInfos[indexO][indexD].TempNodeList.get(0).Nodes.get(0).time;
									if(ODInfos[indexO][indexD].stationOrigin.line.lineNo!=98&&
											ODInfos[indexO][indexD].stationDestination.line.lineNo!=98)
										{if(latestTime<timeRef)
										latestTime=timeRef;
										}
									else
									{
										if(latestTime2<timeRef)
											latestTime2=timeRef;
									}
									}
								}
							}
							
							
							
							if(latestTime==latestTime2&&latestTime>0)
							{
								for(Station station: stations)
								{
									for(Station station2: stations2)
									{
										indexO=station.odNo;
										indexD=station2.odNo;
									
										if(ODInfos[indexO][indexD].stationOrigin.line.lineNo==98||
												ODInfos[indexO][indexD].stationDestination.line.lineNo==98)
										{
											ODInfos[indexO][indexD].TempNodeList.removeAll(ODInfos[indexO][indexD].TempNodeList);
											ODInfos[indexO][indexD].latestTime=0;
										}
									}
							}
							}
							if(latestTime==0)
								latestTime=latestTime2;
							
							for(Station station: stations)
							{
								for(Station station2: stations2)
								{
									indexO=station.odNo;
									indexD=station2.odNo;
									if(!ODInfos[indexO][indexD].TempNodeList.isEmpty()
											&&ODInfos[indexO][indexD].latestTime==latestTime)
									{
										if(ODInfos[i][j].latestRoutes==null)
											ODInfos[i][j].latestRoutes=new LinkedList<NodeLatestRoute>();
										
											ODInfos[i][j].latestRoutes.addAll(ODInfos[indexO][indexD].TempNodeList);	
									}
								}
							}
						
							
							
							if(ODInfos[i][j].latestRoutes==null)
								continue;
							iTransfer=Integer.MAX_VALUE;
							for(int index=0;index<ODInfos[i][j].latestRoutes.size();index++)
							{
								for(int index1=0;index1<ODInfos[i][j].latestRoutes.get(index).Nodes.size();index1++)
								{
									if(ODInfos[i][j].latestRoutes.get(index).Nodes.get(index1).relationFather==1)
										ODInfos[i][j].latestRoutes.get(index).iTransfer++;
								}
								if(iTransfer>ODInfos[i][j].latestRoutes.get(index).iTransfer)
									iTransfer=ODInfos[i][j].latestRoutes.get(index).iTransfer;
							}
							ODInfos[i][j].costInfos[1]=iTransfer;
							for(int index=0;index<ODInfos[i][j].latestRoutes.size();index++)
								for(int index1=0;index1<ODInfos[i][j].latestRoutes.size();index1++)	
							{
								if(index==index1)
									continue;
								if(!ODInfos[i][j].latestRoutes.get(index).bReasonabl)
									continue;
								if(ODInfos[i][j].latestRoutes.get(index1).Nodes.size()<ODInfos[i][j].latestRoutes.get(index).Nodes.size()
										&&(ODInfos[i][j].latestRoutes.get(index1).iTransfer<=ODInfos[i][j].latestRoutes.get(index).iTransfer))
									
										{
											ODInfos[i][j].latestRoutes.get(index).bReasonabl=false;
										
										}
								
							}
							for(int index=0;index<ODInfos[i][j].latestRoutes.size();index++)
							{
								if(!ODInfos[i][j].latestRoutes.get(index).bReasonabl)
								{
									ODInfos[i][j].latestRoutes.remove(index);
									index--;
								
								}
							}
							
							
							for(Station station: stations)
							{
								for(Station station2: stations2)
								{
								indexO=station.odNo;
								indexD=station2.odNo;
								
								
									ODInfos[indexO][indexD].latestTime=latestTime;
									ODInfos[indexO][indexD].latestRoutes=ODInfos[i][j].latestRoutes;
								
								}
							}
						}
			}
			}
	}
	
	private void stationNextToLatestNode(NodeT node,int type)
	{
		
		Station station=node.station;
		StopInfo stopinfos[]=new StopInfo[2];
		StopInfo prestopinfos[]=new StopInfo[2];
		stopinfos=station.getlaststopInfos();
		for(StopInfo stopinfo : stopinfos)
		{
			if(stopinfo!=null)
			{
				prestopinfos=stopinfo.getValidPreviousStopInfo();
				for(StopInfo prestopinfo :prestopinfos)
				{
				if(prestopinfo!=null)
				{
					station=prestopinfo.currentStation;
					NodeT nodenew=new NodeT();
					nodenew.station=station;
					
					nodenew.direction=prestopinfo.trainInfo.direction;
					nodenew.nodeFather=node;
					nodenew.relationFather=0;
					nodenew.time=prestopinfo.departureTime;
					
					nodenew.stopInfo=prestopinfo;
					if(type==0)
						this.insertBT(nodenew);
					if(type==1)
						this.insertBTS(nodenew);
				}
				}
			}
			
		}

		
	}
	private void stationNextToNode(NodeT node, int type)
	{
		if(node==null||node.stopInfo==null)
			return;
		
		
		
		StopInfo stopinfos[]=node.stopInfo.getValidPreviousStopInfo();
			
		for(StopInfo stopinfo : stopinfos)
		{
			if(stopinfo!=null)
			{
				NodeT nodenew=new NodeT();
				nodenew.station=stopinfo.currentStation;
				nodenew.direction=stopinfo.trainInfo.direction;
				nodenew.nodeFather=node;
				nodenew.relationFather=0;
				nodenew.stopInfo=stopinfo;
				nodenew.time=stopinfo.departureTime;
				
				
				if(type==0)
					this.insertBT(nodenew);
				if(type==1)
					this.insertBTS(nodenew);
				
			}
		}
		
	}

	private void transferToNode(NodeT node, TransSta transferStation,int type)
	{
		
		if(node==null||node.stopInfo==null)
			return;
		if(transferStation==null)
			return;
		for(int i=0;i<transferStation.stationTransferCons.size();i++)
		{
			
			 StaTransCon transferConnection=transferStation.stationTransferCons.get(i);
			 if(transferConnection.StationNext!=node.station)
				 continue;
			 if(transferConnection.direction2!=0&&transferConnection.direction2!=node.stopInfo.trainInfo.direction)
				 continue;
			
			 NodeT nodeNew=new NodeT();
			 nodeNew.station=transferConnection.Stationpre;
			 nodeNew.nodeFather=node;

			 nodeNew.time=node.stopInfo.departureTime-transferConnection.walkTime;
			 nodeNew.relationFather=1;
			if(transferConnection.direction1!=0)
				nodeNew.direction=transferConnection.direction1;
			 if(transferConnection.direction1==0)
			 {
				 nodeNew.direction=1;
				 NodeT nodeNew2=new NodeT();
				 nodeNew2.station=transferConnection.Stationpre;
				 nodeNew2.nodeFather=node;
				 nodeNew2.time=node.stopInfo.departureTime-transferConnection.walkTime;
				 nodeNew2.relationFather=1;
				nodeNew2.direction=2;
				
				
				nodeNew2.stopInfo=nodeNew2.station.getStopinfoBeforeTime(nodeNew2.direction,nodeNew2.time);
				
					
				if(nodeNew2.stopInfo!=null)
				 {
					
					if(type==0)
						this.insertBT(nodeNew2);
					if(type==1)
						this.insertBTS(nodeNew2);
					
				 }
			 }
			
			
			 nodeNew.stopInfo=nodeNew.station.getStopinfoBeforeTime(nodeNew.direction,nodeNew.time);
			 
			 
					
			 
			if(nodeNew.stopInfo!=null)
			 
			 {
				
				if(type==0)
					this.insertBT(nodeNew);
				if(type==1)
					this.insertBTS(nodeNew);
				
				

			 }
		}
		
	}

	private void insertBT(NodeT node)
	{
		Station station=node.station;
		
		if(station.stationName.equals(stationBeFinding.stationName))
			return ;
		int direction=node.direction;
		ListIterator<NodeT> iter=null;
	
		NodeT nodeOld=null;
		
		int i=node.station.odNo;
		int j=2-node.stopInfo.trainInfo.direction;
		nodeOld=this.nodeTsSetA[i][j];
		StopInfo sNow[]=null;
		StopInfo sOld[]=null;

		
		
		if(nodeOld!=null)
		{
			
			
			
				
					return;
				
				
			
		}

		
		
		
		int indexO=node.nodeFather.station.odNo;
		int indexD=this.stationBeFinding.odNo;

		int index=ODInfos[indexO][indexD].TempNodeList.size()-1;
		int bRepeat=0;
		ListIterator<NodeLatestRoute> iter1=null;
		iter1=ODInfos[indexO][indexD].TempNodeList.listIterator();
		
		
		StopInfo stopinfos[]=null;
		NodeLatestRoute nodeListRef=null;
	
		while(iter1.hasNext())
		{
			
			nodeListRef=iter1.next();
			if(nodeListRef.Nodes.get(0)==node.nodeFather)
			{
			
				ListIterator<NodeT> iter2=null;
				iter2=nodeListRef.Nodes.listIterator();
				nodeOld=null;
				while(iter2.hasNext())
				{
					nodeOld=iter2.next();
					if(node.station==nodeOld.station)
					{
						return;
						
					}
					
				}
				break;
			} 
			
		}
				

		
		

		index=0;
	
		iter=nodeTsSetB.listIterator();
		while(iter.hasNext())
		{
			nodeOld=iter.next();

	
				
			if(nodeOld.station==node.station&&nodeOld.direction==node.direction
					&&nodeOld.time>=node.time)
			{
				  
				return ;
			}
			if(nodeOld.station==node.station&&nodeOld.direction==node.direction
					&&nodeOld.time<node.time
					)
			{

				iter.remove();
				nodeTsSetB.toArray();
				
			}
			
	
		}
		if(nodeTsSetB.isEmpty())
		{
			
			nodeTsSetB.add(node);
			
			return ;
		}
		
		index=0;
		
		int index1=this.nodeTsSetB.size()-1;
		nodeOld=(NodeT)nodeTsSetB.get(index);
		if(node.time>=nodeOld.time)
		{
			nodeTsSetB.add(index, node);
			nodeTsSetB.toArray();
			return  ;
		}
		nodeOld=nodeTsSetB.get(index1);
		if(node.time<=nodeOld.time)
		{
			nodeTsSetB.add(node);
			nodeTsSetB.toArray();
			return  ;
		}
		while(true)
		{
		nodeOld=nodeTsSetB.get(index);
		if(node.time==nodeOld.time)
		{
			nodeTsSetB.add(index+1, node);
			nodeTsSetB.toArray();
			return;
		}
		i=(index1-index)/2+index;
		nodeOld=(NodeT)nodeTsSetB.get(i);
		if(node.time<nodeOld.time)
			index=i;
		else
			index1=i;
		if(index1-index<=1)
		{
			nodeTsSetB.add(index+1, node);
			nodeTsSetB.toArray();
			return;
			
		}
		}
		
		
	


	}
	private void insertBTS(NodeT node)
	{
		
		Station station=node.station;
		if(station.stationName.equals(stationBeFinding.stationName))
			return ;
		
		int direction=node.direction;
		int indexO=node.nodeFather.station.odNo;
		int indexD=this.stationBeFinding.odNo;

		int index=ODInfos[indexO][indexD].TempNodeList.size()-1;
		int bRepeat=0;
		ListIterator<NodeLatestRoute> iter1=null;
		iter1=ODInfos[indexO][indexD].TempNodeList.listIterator();
		
		
		StopInfo stopinfos[]=null;
		NodeLatestRoute nodeListRef=null;
		NodeT nodeOld=null;
	
		while(iter1.hasNext())
		{
			
			nodeListRef=iter1.next();
			if(nodeListRef.Nodes.get(0)==node.nodeFather)
			{
			
				ListIterator<NodeT> iter2=null;
				iter2=nodeListRef.Nodes.listIterator();
				nodeOld=null;
				while(iter2.hasNext())
				{
					nodeOld=iter2.next();
					if(node.station==nodeOld.station)
					{
						return;
						
					}
					
				}
				break;
			} 
			
		}
				
		ListIterator<NodeT> iter=null;
	
		
		int being=0;
		int i=node.station.odNo;
		int j=node.stopInfo.trainInfo.direction-1;
		StopInfo sNow[]=null;
		StopInfo sOld[]=null;
		NodeT nodeRef=null;
		indexD=this.stationBeFinding.odNo;
		
		nodeRef=ODInfos[i][indexD].nodeTRef[2-direction];
		if(ODInfos[i][indexD].nodeTRef[2-direction]==null)
			return;
		if(ODInfos[i][indexD].nodeTRef[2-direction]!=null)
		{if(node.stopInfo==this.ODInfos[i][indexD].nodeTRef[2-direction].stopInfo)
				being=1;
		else
		{
			sNow=node.stopInfo.getValidPreviousStopInfo();
			sOld=ODInfos[i][indexD].nodeTRef[2-direction].stopInfo.getValidPreviousStopInfo();
			if(sNow[0]!=null&&sOld[0]!=null)
			{	if(sNow[0]==sOld[0])
				being=1;				
			}
			else
			{
				
				if(sNow[1]!=null&&sOld[1]!=null)
				{	if(sNow[1]==sOld[1])
					being=1;				
				}
			}
		}
			}
		if(being==0)
			return;

		
	
			nodeTsSetB.add(node);
			
			return  ;

	}

	public void computePath()
	{  
		
		
		
	
		

		int len=network.stations.length;
		
		
		long latesttime=0;
		
		String StationOstr="",StationDstr="";
		
		
		for(int i=0;i<len;i++)
		{
			
			for(int j=0;j<len;j++)
			{
				
				ODInfos[i][j].bwrite=false;
				
			}
		}
		for(int i=0;i<len;i++)
		{
			//System.out.println("i"+i);
			for(int j=0;j<len;j++)
			{
				Route routesTemp[];
				if(ODInfos[i][j].bwrite)
					continue;
			
				 StationOstr="";
				 StationDstr="";
				StationOstr+=this.ODInfos[i][j].stationOrigin.stationName;
				StationDstr+=this.ODInfos[i][j].stationDestination.stationName;
				if(StationOstr.equals(StationDstr))
					continue;
				latesttime=this.ODInfos[i][j].latestTime-1;
				
				
				
				FindPath find=new FindPath();
				find.setK(this.k);
				find.setnetwork(network);
				find.setODInfo(ODInfos);
				
				
				find.computeForwardPaths(latesttime, StationOstr, StationDstr);
				find.updatePaths(find.AccessRoutesK, find.Type, find.K,StationOstr,StationDstr,latesttime);
				
				
				for(int type=0;type<3;type++)
				{//if(find.AccessRoutesK[type][0]==null)
					//System.out.println(StationOstr+":"+StationDstr+"-"+latesttime+"i:"+i+"j:"+j);
					}
				ODInfos[i][j].latestPaths=updatePaths(find.AccessRoutesK, find.Type,find.K);
								
				
				if(ODInfos[i][j].stationOrigin.type==1||ODInfos[i][j].stationDestination.type==1)
				{
					Station[] stations=network.GetStationsByStationName(ODInfos[i][j].stationOrigin.stationName);
					Station[] stations2=network.GetStationsByStationName(ODInfos[i][j].stationDestination.stationName);
						for(Station station: stations)
						{
							for(Station station2: stations2)
							{
								int indexO=station.odNo;
								int indexD=station2.odNo;
								
								
								ODInfos[indexO][indexD].bwrite=true;
							}
						}
				}
			}
		}
	}
	public Route[] updatePaths(Route[][] routes, int index1,int index2)
	{
		List<Route> routesTemp=new ArrayList<Route>();
		Route routesReturn [] =null;
		double refCost=0;
		double cost=0;
		
		for(int ref1=0;ref1<index1;ref1++)
		for(int ref=0;ref<index2;ref++)
		{
			if(routes[ref1][ref]==null)
				continue;
		 refCost=routes[ref1][ref].costInfos[2];
		for(int i=ref1+1;i<index1;i++)
		{
			for(int j=0;j<index2;j++)
			{
				if(routes[i][j]!=null&&compare.routeEqual(routes[i][j],routes[ref1][ref])==1)
				
					routes[i][j]=null;
					
			}
			
		}
		}
		
		
			for(int j=0;j<index2;j++)
			{
				for(int i=0;i<index1;i++)
				{
				if(routes[i][j]!=null)
					routesTemp.add(routes[i][j]);
			}
			}
			routesTemp.toArray();
			routesReturn=new Route[routesTemp.size()];
			for(int i=0;i<routesTemp.size();i++)
			{
				routesReturn[i]=routesTemp.get(i);
			}
		return routesReturn;
	}
	 

	public void writeLatesPathTime()
	{
		
		try{
			String filePath="";
				filePath="Data\\ResultbyName-time.txt";
		FileWriter fileWriter=new FileWriter(filePath);
		for(int i=0;i<network.stations.length;i++)
		{
			for(int j=0;j<network.stations.length;j++)
			{
				ODInfos[i][j].bwrite=false;
			}
		}
		DateTransfer date=new  DateTransfer();
		date.time=System.currentTimeMillis()/1000;   
		 date.timeToDate();
		 fileWriter.write("起站名称\t到站名称\t最晚出发时间\t序号\t时间成本\t换乘次数\t综合\t途经线路\t途经换乘站\t换乘车站列表\t途径车站数量\t途经车站");
			String stationO="";
		String stationD=""; 
		String lineO=""; String lineD=""; 
		String lineList=""; String transferList=""; String stationList="";
		String costdistance="";double costcom=0; int transfertime=0;int iNo=0;String transferStations="";
	String latestTime="";
		int stationNum=0;
		Station station=null;
		String strTemp="";
		String typeStr="";
		Station s=null;

		Station stations1[];
		Station stations2[];
		int indexO,indexD;
		for(int i=0;i<network.stations.length;i++)
		{
			for(int j=0;j<network.stations.length;j++)
			{
				if(ODInfos[i][j].stationDestination.stationName.equals(ODInfos[i][j].stationOrigin.stationName))
					continue;
				if(ODInfos[i][j].bwrite)
					continue;
				if(ODInfos[i][j].latestPaths==null)
					{//System.out.println("无最晚记录i="+i+"j="+j);
				continue;
					}
				if(ODInfos[i][j].latestPaths.length==0)
				{
					
						//System.out.println("无最晚记录i="+i+"j="+j);
					continue;
				}
			
				stationO=ODInfos[i][j].stationOrigin.stationName;
				lineO=ODInfos[i][j].stationOrigin.line.lineName;
				stationD=ODInfos[i][j].stationDestination.stationName;
				lineD=ODInfos[i][j].stationDestination.line.lineName;
				date=new  DateTransfer();
				 date.time=ODInfos[i][j].latestTime;
				
				 date.timeToDate();
				 latestTime="";
				if(date.day==1)
					latestTime+="第二天";
				latestTime+=date.hour+":"+date.minute+":"+date.second;
				
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
				
				for(int k=0;k<ODInfos[i][j].latestPaths.length;k++)
				
				{
					index++;
					Route route=ODInfos[i][j].latestPaths[k];
					transfertime=(int)route.costInfos[1];
					costcom=route.costInfos[2];
					date.time=(long)route.costInfos[0];
					date.timeToDate();
					costdistance="";
					costdistance+=date.hour+":"+date.minute+":"+date.second;
					stationNameOld="";
					stationList="";
					lineList="";
					transferList="";
					stationNameOld="";
					transferStations="";
					stationNum=0;
					int b=0;
					
					ListIterator<Node> iter=route.nodes.listIterator();
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
									 if(date.day==1)
										 strTemp+="第二天";
									strTemp+=date.hour+":"+date.minute+":"+date.second;
									transferList+=station.stationName+"(换乘："+strTemp+")";
									stationList+="(换乘："+strTemp+")"+station.stationName;
									if(node.station.stationName.equals(stationD))
									{
										date.time=(long)node.arriveTime;
										 date.timeToDate();
										 strTemp="";
										 if(date.day==1)
											 strTemp+="第二天";
										strTemp+=date.hour+":"+date.minute+":"+date.second;
										stationList+=station.stationName+"(终到："+strTemp+")";
									}
									
									 
									
									
									stationNameOld=station.stationName;
									lineList+="-"+node.station.line.lineName;
								}
							}
							else
							{
								
								
								if(node.relationFather==-1)
								{
									
								
									stationList+=station.stationName;
									
								}
								if(node.relationFather==0)
								{
									{
										date.time=(long)node.stopInfo.getValidPreviousStopInfo(s).departureTime;
										 date.timeToDate();
										 strTemp="";
										 if(date.day==1)
											 strTemp+="第二天";
										strTemp+=date.hour+":"+date.minute+":"+date.second;
										strTemp+="-";
										date.time=(long)node.stopInfo.arrivalTime;
										 date.timeToDate();
										 if(date.day==1)
											 strTemp+="第二天";
										strTemp+=date.hour+":"+date.minute+":"+date.second;
										stationList+="("+strTemp+")"+station.stationName;
									}
									if(node.station.stationName.equals(stationD))
									{
										date.time=(long)node.arriveTime;
										 date.timeToDate();
										 strTemp="";
										 if(date.day==1)
											 strTemp+="第二天";
										strTemp+=date.hour+":"+date.minute+":"+date.second;
										stationList+="(终到："+strTemp+")";
									}
									
									
								}
								
								
								stationNum++;
								
							}
							s=node.station;	
						}
					
					
				
					fileWriter.write("\n"+stationO+"\t"+stationD
							+"\t"+latestTime+"\t"+k+"\t"+costdistance+"\t"+transfertime+"\t"+costcom+"\t"+lineList+"\t"+transferList+"\t"+transferStations+"\t"+stationNum+"\t"+stationList);

				}

				
			}
		}
		 fileWriter.flush();
		 fileWriter.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		
	}
	public void WriteLatesPath(int type,String filepath)
	{
		
	
	try{
		String filePath="";

			filePath=filepath;
	FileWriter fileWriter=new FileWriter(filePath);
	for(int i=0;i<network.stations.length;i++)
	{
		for(int j=0;j<network.stations.length;j++)
		{
			ODInfos[i][j].bwrite=false;
		}
	}
	DateTransfer date=new  DateTransfer();
	date.time=System.currentTimeMillis()/1000;   
	 date.timeToDate();
	if(type==0)
	fileWriter.write("起站所在线路\t起站名称\t到站所在线路\t到站名称\t最晚出发时间\t路径序号\t途经线路\t途经换乘站\t途径车站数量\t途经车站");
	if(type==1)
		fileWriter.write("起站名称\t到站名称\t最晚出发时间\t路径序号\tcost\t途经线路\t途经换乘站\t途径车站数量\t途经车站");
	String stationO="";
	String stationD=""; String lineO=""; String lineD=""; String lineList=""; String transferList=""; String stationList="";
	String latestTime="";
	int stationNum=0;

	Station stations1[];
	Station stations2[];
	int indexO,indexD;
	for(int i=0;i<network.stations.length;i++)
	{
		for(int j=0;j<network.stations.length;j++)
		{
			if(ODInfos[i][j].stationDestination.stationName.equals(ODInfos[i][j].stationOrigin.stationName))
				continue;
			if(type==1&&ODInfos[i][j].bwrite)
				continue;
			if(ODInfos[i][j].latestPaths==null)
				{//System.out.println("无最晚记录i="+i+"j="+j);
			continue;
				}
			if(ODInfos[i][j].latestPaths.length==0)
			{
				
					//System.out.println("无最晚记录i="+i+"j="+j);
				continue;
			}
		
			stationO=ODInfos[i][j].stationOrigin.stationName;
			lineO=ODInfos[i][j].stationOrigin.line.lineName;
			stationD=ODInfos[i][j].stationDestination.stationName;
			lineD=ODInfos[i][j].stationDestination.line.lineName;
			date=new  DateTransfer();
			 date.time=ODInfos[i][j].latestTime;
			
			 date.timeToDate();
			 latestTime="";
			if(date.day==1)
				latestTime+="第二天";
			latestTime+=date.hour+":"+date.minute+":"+date.second;
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
			
			for(int k=0;k<ODInfos[i][j].latestPaths.length;k++)
			
			{
				index++;
				Route nodelist=ODInfos[i][j].latestPaths[k];
				stationList="";
				lineList="";
				transferList="";
				stationNameOld="";
				stationNum=0;
				int b=0;
				for(int m=0;m<nodelist.nodes.size();m++)
				{
					Station station=nodelist.nodes.get(m).station;
					if(b!=0)
						{
						stationList+="-";
						
						}
					b=1;
					stationList+=station.stationName;
					if(station.type==1)
					{
						stationList+="("+station.line.lineName+")";
					}
					if(m==0)
						lineList+=station.line.lineName;
					if(nodelist.nodes.get(m).relationFather==1)
					{
						
						
						{
							if(!transferList.equals(""))
							transferList+="-";
							transferList+=station.stationName;
							
							
							stationNameOld=station.stationName;
							
							lineList+="-"+nodelist.nodes.get(m+1).station.line.lineName;
						}

							
					}
					else
					{
						
						
							
						
						
						
						stationNum++;
						
					}
						
				}
				if(type==0)
					fileWriter.write("\n"+lineO+"\t"+stationO+"\t"+lineD+"\t"+stationD+"\t"+latestTime+"\t"+index+"\t"+lineList+"\t"+transferList+"\t"+stationNum+"\t"+stationList);
				if(type==1)
					fileWriter.write("\n"+stationO+"\t"+stationD+"\t"+latestTime+"\t"+index+"\t"+nodelist.costInfos[2]+"\t"+lineList+"\t"+transferList+"\t"+stationNum+"\t"+stationList);
			}

			
		}
	}
	 fileWriter.flush();
	 fileWriter.close();
	}
	catch(IOException e)
	{
		e.printStackTrace();
	}

	}
}

