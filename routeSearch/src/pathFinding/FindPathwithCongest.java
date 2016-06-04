
package pathFinding;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.awt.*;
import java.awt.Event.*;

import netWork.*;

public class FindPathwithCongest {
	int K=2;
	int Type=3;
	private double refUp=600; 
	private double refUpRate=1.2; 
	public List<Node> nodesSetB= new ArrayList<Node>();

	public double refwalktime=5;
	public double refTransferTime=300;
	public ODInfo[][] ODInfoAll=null;
	public double feerate=60;
	public ODPathInfo[][] ODInfos;
	public Network network=null;
	public Route AccessRoutesK[][]=null;
	private Station stationBeFinding=null;
	private Station OriginalStations[]=null;
	private int countOriginal=0;
	public Node nodes[][];
	private String timeStr;
	private String OriginalS;
	private String DestinationS;
	public FindPathwithCongest()
	{
		
	}
	
	public void setK(int k)
	{
		this.K=k;
		AccessRoutesK=new Route[Type][K];
	}
	public void setnetwork(Network networkref)
	{
		network=networkref;
	}
	public void setODInfo(ODInfo aODInfos[][])
	{
		this.ODInfoAll=aODInfos;
	}
	public void init(String OriginalS,String DestinationS)
	{
		this.DestinationS=DestinationS;
		this.OriginalS=OriginalS;
		 OriginalStations=this.network.GetStationsByStationName(OriginalS);
		int stationNum=network.stations.length;
		int count=OriginalStations.length;
		ODInfos=new ODPathInfo[count][stationNum];
		for(int i=0;i<count;i++)
		{
			for(int j=0;j<stationNum;j++)
			{
				ODInfos[i][j]=new ODPathInfo();
				ODInfos[i][j].stationOrigin=OriginalStations[i];
				ODInfos[i][j].stationDestination=network.stations[j];
				ODInfos[i][j].AccessRoutesK=new Route[Type][2*K];
				for(int ref1=0;ref1<Type;ref1++)
					for(int ref2=0;ref2<2*K;ref2++)
					{
						ODInfos[i][j].AccessRoutesK[ref1][ref2]=null;
					}
			}
			
		}
		nodes=new Node[stationNum][2*K];
		for(int i=0;i<stationNum;i++)
			for(int ref=0;ref<2*K;ref++)
			this.nodes[i][ref]=null;
		if(AccessRoutesK==null)
			AccessRoutesK=new Route[Type][K];
	

	}
	
    public void findAccessPathAll(String time, int bChanged)
    {
    	
    	
    	int stationNum=network.stations.length;
    	
    	for(int indexO=0;indexO<stationNum;indexO++)
    	{
    		long timeStart=System.currentTimeMillis();
    		for(int indexD=0;indexD<stationNum;indexD++)
		{
    			String timeStr="20:00:00";
    			String OriginalS=network.stations[indexO].stationName;
    			String DestinationS=network.stations[indexD].stationName;
    			if(!OriginalS.equals(DestinationS))
    			{
    			computeForwardPaths(time,network.stations[indexO].stationName, network.stations[indexD].stationName, bChanged);
    		
    		
    			}
    			
    			
		}
    	System.out.println("find 1-All:"+indexO+"\t"+network.stations[indexO].type+"\t"+(System.currentTimeMillis()-timeStart) +"\tms");
    	}

    	
    }
    public void Release()
    {
    	this.AccessRoutesK=null;
    	this.nodes=null;
    
    	this.ODInfos=null;
    	this.OriginalStations=null;
    	
    }
    public  void computeForwardPaths(long time,String OriginalS,String DestinationS, int bChanged)
    {

    	String timestr="";
    	timestr+=time;
    	
    	
		init(OriginalS,DestinationS);
		for(int type=0;type<3;type++)
		{
			computeForwardPath(time,OriginalS,DestinationS,type, bChanged);
		}
	    updateAccessPathK(OriginalS,DestinationS);
	  
	   
	    
	    
	   

    }
    public  void computeForwardPaths(String timeStr,String OriginalS,String DestinationS,int bChanged)
    {
		long time=0;
    	try
        {
    	DateFormat dateFormat=DateFormat.getTimeInstance();
        Date date=dateFormat.parse(timeStr);
    	time=date.getHours()*3600+date.getMinutes()*60+date.getSeconds();
        }
    	catch (ParseException e)
        {
        	e.printStackTrace();
        }
    	
    	
		init(OriginalS,DestinationS);
		for(int type=0;type<3;type++)
		{
			computeForwardPath(time,OriginalS,DestinationS,type, bChanged);
		}
	    updateAccessPathK(OriginalS,DestinationS);

    }
    public  void computeForwardPaths(String timeStr, String timeStr2,String OriginalS,String DestinationS,int bChanged)
    {
		long time=0;
		long time2=0;
    	try
        {
    	DateFormat dateFormat=DateFormat.getTimeInstance();
        Date date=dateFormat.parse(timeStr);
    	time=date.getHours()*3600+date.getMinutes()*60+date.getSeconds();
    	date=dateFormat.parse(timeStr2);
    	time2=date.getHours()*3600+date.getMinutes()*60+date.getSeconds();
        }
    	catch (ParseException e)
        {
        	e.printStackTrace();
        }
    	
    	
    	do
    	{
    		this.computeForwardPaths(time-1, OriginalS, DestinationS, bChanged);
    		this.WriteAccessRoutes(timeStr, OriginalS, DestinationS, 1);
    		StopInfo stopinfo=this.AccessRoutesK[0][0].nodes.get(1).stopInfo.previousStopInfo;
    		StopInfo stopinfo1=stopinfo.currentStation.getStopinfoAfterTime(1,stopinfo.departureTime);
    		StopInfo stopinfo2=stopinfo.currentStation.getStopinfoAfterTime(2,stopinfo.departureTime);
    		if(stopinfo1==null&&stopinfo2!=null)
    			time=stopinfo2.departureTime;
    		if(stopinfo1!=null&&stopinfo2==null)
    			time=stopinfo1.departureTime;
    		if(stopinfo1!=null&&stopinfo2!=null)
    		time=Math.min(stopinfo1.departureTime, stopinfo2.departureTime);
    	}while(time <= time2   );
    	
    	
		
    }
    public void updatePaths(Route[][] routes, int index1,int index2,String OriginalS,String DestinationS,long time)
	{
		List<Route> routesTemp=new ArrayList<Route>();
		double refCost=0;
		double cost=0;
		for(int ref1=0;ref1<index1;ref1++)
		{	int iref=0;
			if(routes[ref1][iref]==null)
			{
				System.out.println("error:"+ref1+"O:"+OriginalS+"D:"+DestinationS+"time:"+time);
				
				
				continue;
			}
			refCost=routes[ref1][iref].costInfos[2];
		  
			  
			for(int ref=0;ref<index2;ref++)
			{
				if(routes[ref1][ref]==null)
					continue;
				cost=routes[ref1][ref].costInfos[2];
				  
					 
				if(cost/refCost>refUpRate||((cost-refCost)>this.refUp))
					routes[ref1][ref]=null;
			}
		}
	}
    private int routeEqual(Route route1, Route route2)
    {
    	if(route1==null&&route2!=null)
    		return 0;
    	if(route1!=null&&route2==null)
    		return 0;
    	
    	int compare=this.compareCostInfo(2, route1.costInfos,route2.costInfos);
    	if(compare!=0)
    		return 0;
    	if(route1.staionNum!=route2.staionNum)
    		return 0;
    	else
    	{
    		for(int i=0;i<route1.staionNum;i++)
    		{
    			if(route1.nodes.get(i).station!=route2.nodes.get(i).station)
    				return 0;
    		}
    	
    	}
    	
    	return 1;
    }
    public void updateAccessPathK(String OriginalS,String DestinationS)
    
    
    {

    	int len1=OriginalStations.length;
    	Station DestinationStations[]=network.GetStationsByStationName(DestinationS);
    	int len2=DestinationStations.length;
    	int indexD=0;
    	
    	
    		for(int type=0;type<3;type++)
    			for(int kref=0;kref<K;kref++)
			{
    			
			}
   
   
    	Route route1=null,route2=null;
    	for(int type=0;type<Type;type++)
		{
	    	for(int i=0;i<len1;i++)
	    	{
	    		for(int j=0;j<len2;j++)
	    		{
	    			indexD=DestinationStations[j].odNo;
	    			for(int kref1=0;kref1<2*K;kref1++)
					{
	    				
		    			route1=ODInfos[i][indexD].AccessRoutesK[type][kref1];
		    			if(route1==null)
		    				continue;
		    			for(int kref=0;kref<K;kref++)
						{
		    			  if(AccessRoutesK[type][kref]==null)
		    				  {
		    				  AccessRoutesK[type][kref]=route1;
		    				  break;
		    				  }
		    			int  compare=compareCostInfo(type,  AccessRoutesK[type][kref].costInfos, route1.costInfos);
		    			int compare2=0;
		    			if(kref-1>=0)
		    				compare2=this.routeEqual(route1, AccessRoutesK[type][kref-1]);
		    			
		    			
		    			  if(compare==1&&compare2==0)
		    			  {
		    				  for(int ref=K-1;ref>kref;ref--)
		    				  {
		    					  AccessRoutesK[type][ref]=AccessRoutesK[type][ref-1];
		    				  }
		    				  AccessRoutesK[type][kref]=route1;
		    				  break;
		    			  } 
						}
					}
	    		}
	    	}
	    
	       	
		}
    	
    	
    	
   }
  
	public  void computeForwardPath(long time,String OriginalS,String DestinationS,int type,int bChanged)
    {
    	 
    	for(countOriginal=0;countOriginal<OriginalStations.length;countOriginal++)
    	{
    	stationBeFinding=OriginalStations[countOriginal];
    	
    	
    	nodesSetB.removeAll(nodesSetB);
    	for(int j=0;j<2*K;j++)
		{
			for(int i=0;i<network.stations.length;i++)
			{
		

				this.nodes[i][j]=null;
			}
		}
    	int routeIndex=0;
		
		Node node=new Node();
		node.station=stationBeFinding;
		node.arriveTime=time;
		node.relationFather=-1;
	    this.stationNextToAccessNode(node,type,time, bChanged);
	    		    while(!nodesSetB.isEmpty())
			{
				node=nodesSetB.get(0);
				
					
				
				nodesSetB.remove(0);
				nodesSetB.toArray();
				int binsert=insertAccessRoutes( node, stationBeFinding,type); 
				
				
				if(node.station.stationName.equals(DestinationS))
					{
					if(binsert==1)
					{routeIndex++;
					if(routeIndex==K)
						break;
					}
					}
				else
					stationNextToAccessNode(node,type,time,bChanged);
	
			}
    	}
   
    }
 
	   private void insertB(Node node, int type)
	    {
	    	
	    	
			Station station=node.station;
			
			if(station.stationName.equals(stationBeFinding.stationName))
				return ;
			int index=this.network.GetStationsByStationName(DestinationS)[0].odNo;
			
			if(!node.station.stationName.equals(DestinationS)
					&&node.arriveTime>this.ODInfoAll[node.station.odNo][index].latestTime)
			{
				
				return;
			}
			int direction=node.direction;
			ListIterator<Node> iter=null;
			Node nodeOld=null;
			
			int indexNow=node.station.odNo;
			int refK=0;
			 index=0;
			int refC=0;
			int refstatus=0;
			Route route=null;
			
			
			
			
			if(refK==K)
				return;
			
			
			
		
			
			int indexO=countOriginal;
			int indexD=node.nodeFather.station.odNo;
			route=ODInfos[indexO][indexD].AccessRoutesK[type][node.nodeFather.routeIndex];
			if(route!=null)
			{
			iter=route.nodes.listIterator();
			while(iter.hasNext())
			{
				nodeOld=iter.next();
				if(node.station==nodeOld.station)
				{
							return;
							
				}
						
			}
			}
			
			
			
			
			int reftype=0;
			int compare=0;
			int binsert=0;
			int bDel=0;
			int index1=0;
			int i=0;
			int bBreak=0;
			Node beDelNode=null;
			int bMove=1;
			
			if(node.direction!=0)
			{
				reftype=node.direction-1;
			}
			
			
			
			for(int refcount=0;refcount<2;refcount++)
			{
				
				
			for(i=0;i<K;i++)
			{
				index=i+K*reftype;
				
					
				if(this.nodes[indexNow][index]==null)
				{
					binsert=1;
					refK=i;
					bMove=0;
					break;
				}
				compare=this.compareCost(type, this.nodes[indexNow][index], node);
				if(compare==1)
				{
					binsert=1;
					refK=i;
					break;
				}
				else
					continue;
			}
			if(binsert==1)
			{	
				if(bMove==1)
				{
					for( i=K-1;i>=refK;i--)
					
					{
					index=i+K*reftype;
					if(i==K-1)
					{
						if(this.nodes[indexNow][index]!=null)
							{
							beDelNode=this.nodes[indexNow][index];
							bDel=1;
							}
					}
					if(i>refK)
					{
					if(this.nodes[indexNow][index-1]==null)
						continue;
					this.nodes[indexNow][index]=this.nodes[indexNow][index-1];
					this.nodes[indexNow][index].routeIndex=index;
					}
					}
				}
				this.nodes[indexNow][refK+K*reftype]=node;
				node.routeIndex=refK+K*reftype;
			}
			index1=0;
			i=0;
			bBreak=0;
			
			
			if(bDel==1)
			{
				
				
				index=0;
				index1=this.nodesSetB.size()-1;
				
				i=0;
				while(true)
				{
					nodeOld=nodesSetB.get(index);
					bBreak=0;
					while(this.compareCost(type, nodeOld, beDelNode)==0)
					{
						if(nodeOld==beDelNode)
							{nodesSetB.remove(index);
							nodesSetB.toArray();
							bBreak=1;
							break;
							}
						index++;
						nodeOld=nodesSetB.get(index);
					}
					if(bBreak==1)
						break;
					nodeOld=nodesSetB.get(index1);
					while(this.compareCost(type, nodeOld, beDelNode)==0)
					{
						if(nodeOld==beDelNode)
						{nodesSetB.remove(index1);
							nodesSetB.toArray();
							bBreak=1;
							break;
							}
						index1--;
						nodeOld=nodesSetB.get(index1);
					}
					if(bBreak==1)
						break;
				i=(index1-index)/2+index;
				nodeOld=nodesSetB.get(i);
				if(this.compareCost(type, nodeOld, beDelNode)==0)
				{
					index=index1=i;
					nodeOld=nodesSetB.get(index);
					while(this.compareCost(type, nodeOld, beDelNode)==0)
					{
						if(nodeOld==beDelNode)
							
							{nodesSetB.remove(index);
							nodesSetB.toArray();
							bBreak=1;
							break;
							}
						index++;
						nodeOld=nodesSetB.get(index);
					}
					if(bBreak==1)
						break;
					nodeOld=nodesSetB.get(index1);
					while(this.compareCost(type, nodeOld, beDelNode)==0)
					{
						if(nodeOld==beDelNode)
							{nodesSetB.remove(index1);
							nodesSetB.toArray();
							bBreak=1;
							break;
							}
						index1--;
						nodeOld=nodesSetB.get(index1);
					}
					if(bBreak==1)
						break;
				}
				if(this.compareCost(type, nodeOld, beDelNode)==1)
					index1=i;
				else
					index=i;
			}
				if(bBreak!=1)
					System.out.println("can't find delnode");
			}
			if(node.direction!=0)
				break;
			if(node.direction==0&&binsert!=1)
				reftype++;
			else
				break;
			}
			
			
			
			if(binsert==1)
			{
				if(nodesSetB.isEmpty())
				{
					nodesSetB.add(node);
					nodesSetB.toArray();
					return;
				}
				
				index=0;
				index1=this.nodesSetB.size()-1;
				nodeOld=nodesSetB.get(index);
				
				if(this.compareCost(type, nodeOld, node)!=-1)
				{
					nodesSetB.add(index, node);
					nodesSetB.toArray();
					return  ;
				}
				nodeOld=nodesSetB.get(index1);
				if(this.compareCost(type, nodeOld, node)!=1)
				{
					nodesSetB.add(node);
					nodesSetB.toArray();
					return  ;
				}
				while(true)
				{
				nodeOld=nodesSetB.get(index);
				if(this.compareCost(type, nodeOld, node)==0)
				{
					nodesSetB.add(index+1, node);
					nodesSetB.toArray();
					return;
				}
				i=(index1-index)/2+index;
				nodeOld=nodesSetB.get(i);
				if(this.compareCost(type, nodeOld, node)==1)
					index1=i;
				else
					index=i;
				if(index1-index<=1)
				{
					nodesSetB.add(index+1, node);
					nodesSetB.toArray();
					return;
					
				}
				
				}
		
			}

		
	    }
	    public int insertAccessRoutes(Node node, Station station,int type)
	    {

			
			Node nodeFather=node.nodeFather;
			Route route;
			Route routeRef;
			Station stationFather=nodeFather.station;
			int indexD;
			int kPos=node.routeIndex;
			int indexO=countOriginal;
			if(station==stationFather)
			{
				route=new Route();
				route.nodes.add(nodeFather);
				route.nodes.add(node);
				route.nodes.toArray();
				indexD=node.station.odNo;
				route.costInfos=node.costInfos;
				route.staionNum=2;
				if(ODInfos[indexO][indexD].AccessRoutesK[type][kPos]==null)
				{
					ODInfos[indexO][indexD].AccessRoutesK[type][kPos]=route;
					return 1;
				}

				
				return 0;
			}
			indexD=stationFather.odNo;
			int indexDnew=node.station.odNo;
			int bRepeat=0;
			ListIterator<Route> iter1=null;
			routeRef=ODInfos[indexO][indexD].AccessRoutesK[type][nodeFather.routeIndex];
			route=new Route();
			route.nodes.addAll(routeRef.nodes);
			route.nodes.add(node);
			route.nodes.toArray();
			route.costInfos=node.costInfos;
			route.staionNum=(short)(routeRef.staionNum+1);
			
			if(ODInfos[indexO][indexDnew].AccessRoutesK[type][kPos]==null)
			{
				ODInfos[indexO][indexDnew].AccessRoutesK[type][kPos]=route;
				return 1;
			}
			
			return 0;
	   }
		
	    private void stationNextToAccessNode(Node node,int type,long time, int bChanged)
		{
			if(node==null||node.station==null)
				return;
			
			Field field=null;
			StopInfo stopinfos[]=new StopInfo[2] ;
			Station station=node.station;
			Node nodeFather=node;
			if(node.relationFather==-1||(node.station.type==1&&node.station.transferType==0))
				
			{
				if(node.relationFather==-1||
						(node.relationFather==1&&(node.station.type==1&&node.station.transferType==0))
						||(node.station.stationName.equals("三元桥")&&node.nodeFather.station.staiondir.equals("T2航站楼"))
						)
				{
				for(int i=0;i<2;i++)
				{
					
					field=station.fields[i];
					
						
						
					StopInfo stopinfo=station.getStopinfoAfterTime(2-i, node.arriveTime);
					if(stopinfo==null)
						{
						
						continue;
						}
					for(int j=0;j<2;j++)
					{
					stopinfos[j]=null;
					}
					stopinfos=stopinfo.getValidNextStopInfo();
					for(int j=0;j<2;j++)
					{
					stopinfo=stopinfos[j];
					if(stopinfo==null)
						continue;
					field=station.fields[2-stopinfo.trainInfo.direction];
					Node nodeNew=new Node();
					nodeNew.station=stopinfo.currentStation;
					nodeNew.direction=stopinfo.trainInfo.direction;
					nodeNew.nodeFather=nodeFather;
					nodeNew.relationFather=0;
					nodeNew.stopInfo=stopinfo;
					nodeNew.arriveTime=stopinfo.arrivalTime;
					nodeNew.costInfos[1]=node.costInfos[1];
					nodeNew.costInfos[0]=stopinfo.arrivalTime-time;
					
					int typeRef=1;
					if(bChanged==1)
						typeRef=3;
					double congestRef=network.computeCongestRef(field, stopinfo.arrivalTime, typeRef);
					nodeNew.costInfos[2]=congestRef*(nodeNew.costInfos[0]-node.costInfos[0])+node.costInfos[2];
					nodeNew.costInfos[3]=node.costInfos[3];
					if(field.fee>node.costInfos[3])
					{
						nodeNew.costInfos[3]=node.costInfos[3]+field.fee;
						nodeNew.costInfos[2]+=field.fee*this.feerate;
					}
					this.insertB(nodeNew,type);
					}
				}
				}
				if(node.relationFather==0)
				{
					int direction=node.direction;
					field=station.fields[2-direction];
					
						
						
					
					
						
					for(int j=0;j<2;j++)
					{
					stopinfos[j]=null;
					}
					stopinfos=node.stopInfo.getValidNextStopInfo();
					for(int j=0;j<2;j++)
					{
					StopInfo stopinfo=stopinfos[j];
					if(stopinfo==null)
						continue;
					field=station.fields[2-stopinfo.trainInfo.direction];
					Node nodeNew=new Node();
					nodeNew.station=stopinfo.currentStation;
					nodeNew.direction=stopinfo.trainInfo.direction;
					nodeNew.nodeFather=nodeFather;
					nodeNew.relationFather=0;
					nodeNew.stopInfo=stopinfo;
					nodeNew.arriveTime=stopinfo.arrivalTime;
					nodeNew.costInfos[1]=node.costInfos[1];
					nodeNew.costInfos[0]=stopinfo.arrivalTime-time;
					
					int typeRef=2;
					if(bChanged==1)
						typeRef=3;
					double congestRef=network.computeCongestRef(field, stopinfo.arrivalTime, typeRef);
					nodeNew.costInfos[2]=congestRef*(nodeNew.costInfos[0]-node.costInfos[0])+node.costInfos[2];
					nodeNew.costInfos[3]=node.costInfos[3];
					if(field.fee>node.costInfos[3])
					{
						nodeNew.costInfos[3]=node.costInfos[3]+field.fee;
						nodeNew.costInfos[2]+=field.fee*this.feerate;
					}
					this.insertB(nodeNew,type);
					}
				}
				TransSta transferStation=null;
				if(node.relationFather!=-1&&station.type==1)
				{
					transferStation=node.station.transferStation;
					if(transferStation==null)
						return;
					for(StaTransCon transferConnection:transferStation.stationTransferCons)
					{
						if(transferConnection.Stationpre==station)
						{
							
								
							
								Node nodeNew=new Node();
								nodeNew.station=transferConnection.StationNext;
								nodeNew.nodeFather=node;
								nodeNew.costToFather=transferConnection.walkTime;
								nodeNew.relationFather=1;
								nodeNew.arriveTime=node.arriveTime+transferConnection.walkTime;
								nodeNew.costInfos[0]=node.costInfos[0]+transferConnection.walkTime;
								nodeNew.costInfos[1]=1+node.costInfos[1];
								nodeNew.costInfos[2]=node.costInfos[2]+this.refTransferTime+transferConnection.walkTime*refwalktime;
								nodeNew.costInfos[3]=node.costInfos[3];
								if(transferConnection.fee>node.costInfos[3])
								{
									nodeNew.costInfos[3]=node.costInfos[3]+transferConnection.fee;
									nodeNew.costInfos[2]+=transferConnection.fee*this.feerate;
								}

								nodeNew.direction=transferConnection.direction2;

								this.insertB(nodeNew, type);
							}
						}
							
					}
				}
			
	  
	    
			StopInfo stopinfoTemp=null;
			if(node.relationFather!=-1&&
					((node.station.transferType!=0)||
							node.station.type==0))
			{
				int direction=node.direction;
			
				
				int typeRef=1;
				for(int j=0;j<2;j++)
				{
				stopinfos[j]=null;
				}
				if(node.relationFather==1)
				{
					stopinfoTemp=station.getStopinfoAfterTime(direction, node.arriveTime-1);
					if(stopinfoTemp!=null)
					{
						stopinfos=stopinfoTemp.getValidNextStopInfo();
					}
					
				}
				else
				{
					stopinfos=node.stopInfo.getValidNextStopInfo();
					typeRef=2;
				}
				for(StopInfo stopinfo : stopinfos)
				{
					if(stopinfo!=null)
					{
						
						field=station.fields[2-stopinfo.trainInfo.direction];
						Node nodeNew=new Node();
						nodeNew.station=stopinfo.currentStation;
						nodeNew.direction=stopinfo.trainInfo.direction;
						nodeNew.nodeFather=nodeFather;
						nodeNew.relationFather=0;
						nodeNew.stopInfo=stopinfo;
						nodeNew.arriveTime=stopinfo.arrivalTime;
						nodeNew.costInfos[1]=node.costInfos[1];
						nodeNew.costInfos[0]=stopinfo.arrivalTime-time;
						
						
						if(bChanged==1)
							typeRef=3;
						double congestRef=network.computeCongestRef(field, stopinfo.arrivalTime, typeRef);
						nodeNew.costInfos[2]=congestRef*(nodeNew.costInfos[0]-node.costInfos[0])+node.costInfos[2];
						nodeNew.costInfos[2]=nodeNew.costInfos[0]+node.costInfos[2]-node.costInfos[0];
						nodeNew.costInfos[3]=node.costInfos[3];
						if(field.fee>node.costInfos[3])
						{
							nodeNew.costInfos[3]=node.costInfos[3]+field.fee;
							nodeNew.costInfos[2]+=field.fee*this.feerate;
						}
						this.insertB(nodeNew,type);
											
					}
			}
			
		
			TransSta transferStation=null;
			if(station.type==1)
			{
				transferStation=node.station.transferStation;
				if(transferStation==null)
					return;
				for(StaTransCon transferConnection:transferStation.stationTransferCons)
				{
					if(transferConnection.Stationpre==station)
					{
						
					
						
						if(node.direction==transferConnection.direction1)
						{
							Node nodeNew=new Node();
							nodeNew.station=transferConnection.StationNext;
							nodeNew.nodeFather=node;
							nodeNew.costToFather=transferConnection.walkTime;
							nodeNew.relationFather=1;
							nodeNew.arriveTime=node.arriveTime+transferConnection.walkTime;
							nodeNew.costInfos[0]=node.costInfos[0]+transferConnection.walkTime;
							nodeNew.costInfos[1]=1+node.costInfos[1];
							nodeNew.costInfos[2]=node.costInfos[2]+this.refTransferTime+transferConnection.walkTime*refwalktime;
							nodeNew.costInfos[3]=node.costInfos[3];
							if(transferConnection.fee>node.costInfos[3])
							{
								nodeNew.costInfos[3]=node.costInfos[3]+transferConnection.fee;
								nodeNew.costInfos[2]+=transferConnection.fee*this.feerate;
							}
							nodeNew.direction=transferConnection.direction2;

							this.insertB(nodeNew, type);
						}
					}
						
				}
			}
			}
		}

	    private int compareCost(int type,Node nodeOld, Node nodeNew)
	    {
	    	int returni=0;
	    	if(type==2)
	    	{
	    		if(nodeOld.costInfos[type]>nodeNew.costInfos[type])
	    			return 1;
	    		if(nodeOld.costInfos[type]<nodeNew.costInfos[type])
	    			return -1;
	    	}
	    	if(type==1)
	    	{
	    		if((nodeOld.costInfos[1]>nodeNew.costInfos[1])
	    				||((nodeOld.costInfos[1]==nodeNew.costInfos[1])
	    						&&nodeOld.costInfos[2]>nodeNew.costInfos[2]))
	    			return 1;
	    		if((nodeOld.costInfos[1]<nodeNew.costInfos[1])
	    				||((nodeOld.costInfos[1]==nodeNew.costInfos[1])
	    						&&nodeOld.costInfos[2]<nodeNew.costInfos[2]))
	    			return -1;
	    	}
	    	if(type==0)
	    	{
	    		if((nodeOld.costInfos[0]>nodeNew.costInfos[0])
	    				||((nodeOld.costInfos[0]==nodeNew.costInfos[0])
	    						&&nodeOld.costInfos[2]>nodeNew.costInfos[2]))
	    			return 1;
	    		if((nodeOld.costInfos[0]<nodeNew.costInfos[0])
	    				||((nodeOld.costInfos[0]==nodeNew.costInfos[0])
	    						&&nodeOld.costInfos[2]<nodeNew.costInfos[2]))
	    			return -1;
	    	}
	    	return 0;
	    }
	    private int compareCostInfo(int type,double costInfo1[], double costInfo2[])
	    {
	    	int returni=0;
	    	if(type==2)
	    	{
	    		if(costInfo1[type]>costInfo2[type])
	    			return 1;
	    		if(costInfo1[type]<costInfo2[type])
	    			return -1;
	    	}
	    	if(type==1)
	    	{
	    		if((costInfo1[1]>costInfo2[1])
	    				||((costInfo1[1]==costInfo2[1])
	    						&&costInfo1[0]>costInfo2[0]))
	    			return 1;
	    		if((costInfo1[1]<costInfo2[1])
	    				||((costInfo1[1]==costInfo2[1])
	    						&&costInfo1[0]<costInfo2[0]))
	    			return -1;
	    	}
	    	if(type==0)
	    	{
	    		if((costInfo1[0]>costInfo2[0])
	    				||((costInfo1[0]==costInfo2[0])
	    						&&costInfo1[2]>costInfo2[2]))
	    			return 1;
	    		if((costInfo1[0]<costInfo2[0])
	    				||((costInfo1[0]==costInfo2[0])
	    						&&costInfo1[2]<costInfo2[2]))
	    			return -1;
	    	}
	    	return 0;
	    }

	    public void WriteAccessRoutes(String time,String stationO, String stationD,int style)
		{

				try{
					String filePath="";
						filePath="Data\\AccessRoutesingle_Congest.txt";
					FileWriter fileWriter;
					if(style==0)
				fileWriter=new FileWriter(filePath);
					else
						fileWriter=new FileWriter(filePath,true);
				FileWriter writeTemp=new FileWriter("Data\\error-Resonable.txt");
				fileWriter.write("时间:"+time+"\n");
				fileWriter.write("起站名称\t到站名称\t可达性\t类型\t序号\t时间成本\t换乘次数\t综合\t途经线路\t途经换乘站\t换乘车站列表\t途径车站数量\t途经车站");
				DateTransfer date=null;
				String access="";
				String typeStr="";
				String lineList=""; String transferList=""; String stationList="";
				String costdistance="";double costcom=0; int transfertime=0;int iNo=0;String transferStations="";
				int stationNum=0;
				Route route=null;
				int count=0;
				ListIterator<Route> iter1=null;
				ListIterator<Station> iters=null;
				Station station=null;
				String strTemp="";
				Station s=null;
				int start=0;
				date=new  DateTransfer();
						for(int type=0;type<3;type++)
						{
							for(int k=0;k<K;k++)
							{
								
							route=AccessRoutesK[type][k];
							transfertime=0;
							costcom=0;
							costdistance="";
							if(route==null)
							{
								access="0";
								continue;
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
											
											 strTemp="";
											 
											strTemp+=time;
											stationList+=station.stationName+"(到达："+strTemp+")";
											
										}
										if(node.relationFather==0)
										{

											{
												StopInfo stopinfo=node.stopInfo.getValidPreviousStopInfo(s);
												date.time=(long)stopinfo.departureTime;
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
												Field field=stopinfo.currentStation.fields[2-stopinfo.trainInfo.direction];
												String congest="";
												if(field==null)
													System.out.println("field null");
												congest+=field.congestions[(int)(date.time%(24*3600)/300)];
												stationList+="("+strTemp+"C:"+congest+")"+station.stationName;
											}
											if(node.station.stationName.equals(stationD))
											{
												date.time=(long)node.arriveTime;
												 date.timeToDate();
												 strTemp="";
												 if(date.day==1)
													 strTemp+="第二天";
												strTemp+=date.hour+":"+date.minute+":"+date.second;

												stationList+="(终到)";
											}
											
											
										
										}
										
										
										stationNum++;
										
									}
									s=node.station;	
								}
							}
								
								
								fileWriter.write("\n"+stationO+"\t"+stationD
										+"\t"+access+"\t"+type+"\t"+k+"\t"+costdistance+"\t"+transfertime+"\t"+costcom+"\t"+lineList+"\t"+transferList+"\t"+transferStations+"\t"+stationNum+"\t"+stationList);
							
								
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
	    public void  writesigle(String time,String stationO, String stationD)
	    {


			try{
				String filePath="";
					filePath="Data\\path\\"+stationO+"到"+stationD+".txt";
				FileWriter fileWriter;
				
			fileWriter=new FileWriter(filePath);
			fileWriter.write("时间:"+time+"\n");
			fileWriter.write("起站名称\t到站名称\t可达性\t类型\t序号\t时间成本\t换乘次数\t综合\t途经线路\t途经换乘站\t换乘车站列表\t途径车站数量\t途经车站");
			DateTransfer date=null;
			String access="";
			String typeStr="";
			String lineList=""; String transferList=""; String stationList="";
			String costdistance="";double costcom=0; int transfertime=0;int iNo=0;String transferStations="";
			int stationNum=0;
			Route route=null;
			int count=0;
			ListIterator<Route> iter1=null;
			ListIterator<Station> iters=null;
			Station station=null;
			String strTemp="";
			Station s=null;
			int start=0;
			date=new  DateTransfer();
					for(int type=0;type<3;type++)
					{
						for(int k=0;k<K;k++)
						{
							
						route=AccessRoutesK[type][k];
						transfertime=0;
						costcom=0;
						costdistance="";
						if(route==null)
						{
							access="0";
							continue;
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
										transferList+=station.stationName+"(换乘："+strTemp+")";
										stationList+="(换乘："+strTemp+")"+station.stationName;
										if(node.station.stationName.equals(stationD))
										{
											date.time=(long)node.arriveTime;
											 date.timeToDate();
											 strTemp="";
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
										
										 strTemp="";
										strTemp+=time;
										stationList+=station.stationName+"(到达："+strTemp+")";
										
									}
									if(node.relationFather==0)
									{
										{
											date.time=(long)node.stopInfo.getValidPreviousStopInfo(s).departureTime;
											 date.timeToDate();
											 strTemp="";
											strTemp+=date.hour+":"+date.minute+":"+date.second;
											strTemp+="-";
											date.time=(long)node.stopInfo.arrivalTime;
											 date.timeToDate();
											strTemp+=date.hour+":"+date.minute+":"+date.second;
											stationList+="("+strTemp+")"+station.stationName;
										}
										if(node.station.stationName.equals(stationD))
										{
											date.time=(long)node.arriveTime;
											 date.timeToDate();
											 strTemp="";
											strTemp+=date.hour+":"+date.minute+":"+date.second;
											stationList+="(终到："+strTemp+")";
										}
										
										
									}
									
									
									stationNum++;
									
								}
								s=node.station;	
							}
						}
							
							
							fileWriter.write("\n"+stationO+"\t"+stationD
									+"\t"+access+"\t"+type+"\t"+k+"\t"+costdistance+"\t"+transfertime+"\t"+costcom+"\t"+lineList+"\t"+transferList+"\t"+transferStations+"\t"+stationNum+"\t"+stationList);
						
							
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
