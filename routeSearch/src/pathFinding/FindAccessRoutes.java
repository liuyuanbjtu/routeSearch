package pathFinding;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

import netWork.*;


public class FindAccessRoutes extends FindRoute
{
	public int K=3;
	int Type=3;
	public double feerate=60;
	private double refUp=500; 
	private double refUpRate=1.2; 
	
	
	public List<Node> nodesSetB= new ArrayList<Node>();
	public double refwalktime=5;
	public double refTransferTime=300;
	private Node nodes[][];
	public FindAccessRoutes() {
		
		
	}

	public void init()
	{
		int stationNum=network.stations.length;
		
		for(int i=0;i<stationNum;i++)
		{
			for(int j=0;j<stationNum;j++)
			{
				
				
				
				ODInfos[i][j].AccessRoutesK=new Route[Type][2*K];
				for(int type=0;type<Type;type++)
					for(int k=0;k<K*2;k++)
					{
						ODInfos[i][j].AccessRoutesK[type][k]=null;
					}
			}
		
		}
		nodes=new Node[stationNum][2*K];
		for(int i=0;i<stationNum;i++)
			for(int ref=0;ref<2*K;ref++)
			this.nodes[i][ref]=null;
		
	}
	
    public void findAccessRouteAll(String time,int type)
    {
    	long time1=0;
    	try
        {
    	DateFormat dateFormat=DateFormat.getTimeInstance();
        Date date=dateFormat.parse(time);
    	time1=date.getHours()*3600+date.getMinutes()*60+date.getSeconds();
        }
    	catch (ParseException e)
        {
        	e.printStackTrace();
        }
    	for(int indexO=0;indexO<network.stations.length;indexO++)
		{
    		findAccessRotues(time1,indexO, type);
		}
    	
    }
    public void findAccessRotues(long time,int indexO,int type)
    {
    	
    	stationBeFinding=network.stations[indexO];
    	
    	
    	
    	nodesSetB.removeAll(nodesSetB);
    	for(int j=0;j<2*K;j++)
		{
			for(int i=0;i<network.stations.length;i++)
			{
	
			nodes[i][j]=null;
			}
		}
		
		Node node=new Node();
		node.station=stationBeFinding;
		node.arriveTime=time;
		node.relationFather=-1;
	    this.stationNextToAccessNode(node,type,time);
	    
	    
		    while(!nodesSetB.isEmpty())
			{
				node=nodesSetB.get(0);
				nodesSetB.remove(0);
				nodesSetB.toArray();
				
					
				
				int iref=insertAccessRoutes( node, stationBeFinding,type); 
				
				if(iref==1)
					stationNextToAccessNode(node,type,time);
	
			}
		    updateAccessRoute(indexO,type);
	    
    }

    
    private void insertB(Node node, int type)
    {
    	
    	
		Station station=node.station;
		
		if(station==stationBeFinding)
			return ;
		int direction=node.direction;
		ListIterator<Node> iter=null;
		Node nodeOld=null;
		
		int indexNow=node.station.odNo;
		int refK=0;
		int index=0;
		int refC=0;
		int refstatus=0;
		Route route=null;
		
		
		
		
		if(refK==K)
			return;
		
		
		
	
		
		int indexO=this.stationBeFinding.odNo;
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
			
				
			if(nodes[indexNow][index]==null)
			{
				binsert=1;
				refK=i;
				bMove=0;
				break;
			}
			compare=this.compareCost(type, nodes[indexNow][index], node);
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
					if(nodes[indexNow][index]!=null)
						{
						beDelNode=nodes[indexNow][index];
						bDel=1;
						}
				}
				if(i>refK)
				{
				if(nodes[indexNow][index-1]==null)
					continue;
				nodes[indexNow][index]=nodes[indexNow][index-1];
				nodes[indexNow][index].routeIndex=index;
				}
				}
			}
			nodes[indexNow][refK+K*reftype]=node;
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
    private int insertAccessRoutes(Node node, Station station,int type)
    {

		
		Node nodeFather=node.nodeFather;
		Route route;
		Route routeRef;
		Station stationFather=nodeFather.station;
		int indexD;
		int kPos=node.routeIndex;
		int indexO=station.odNo;
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
				route.ODObj=ODInfos[indexO][indexD];
				return 1;
			}
			else
				System.out.println("erro1");

			
		return 0;
		}
		indexD=stationFather.odNo;
		int indexDnew=node.station.odNo;
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
			route.ODObj=ODInfos[indexO][indexDnew];
			return 1;
		}
		else
			System.out.println("erro2");
		return 0;

   }
	
    
    private void stationNextToAccessNode(Node node,int type,long time)
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
				if(node.relationFather!=-1&&field!=null&&node.nodeFather.station==field.stationNext)
					continue;
					
				StopInfo stopinfo=station.getStopinfoAfterTime(2-i, node.arriveTime);
				if(stopinfo==null)
					continue;
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
			if(node.relationFather!=-1&&station.type==1)
			{
				transferStation=node.station.transferStation;
				if(transferStation==null)
					return;
				for(StaTransCon transferConnection:transferStation.stationTransferCons)
				{
					if(transferConnection.Stationpre==station)
					{
						if(node.relationFather!=-1
								&&node.nodeFather.station==transferConnection.StationNext)
							continue;
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
		
			
			for(int j=0;j<2;j++)
			{
			stopinfos[j]=null;
			}
			if(node.relationFather==1)
			{
				stopinfoTemp=station.getStopinfoAfterTime(direction, node.arriveTime);
				if(stopinfoTemp!=null)
				{
					stopinfos=stopinfoTemp.getValidNextStopInfo();
				}
				
			}
			else
				stopinfos=node.stopInfo.getValidNextStopInfo();
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
					
					if(node.relationFather!=-1&&node.nodeFather.station==transferConnection.StationNext)
						continue;
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
    	if(type==0||type==2)
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
    						&&nodeOld.costInfos[0]>nodeNew.costInfos[0]))
    			return 1;
    		if((nodeOld.costInfos[1]<nodeNew.costInfos[1])
    				||((nodeOld.costInfos[1]==nodeNew.costInfos[1])
    						&&nodeOld.costInfos[0]<nodeNew.costInfos[0]))
    			return -1;
    	}
    	return 0;
    }
    private int compareCostInfo(int type,double costInfo1[], double costInfo2[])
    {
    	if(type==0||type==2)
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
    	return 0;
    }
 
    private void updateAccessRoute(int indexO,int type)
    {
    	int len=network.stations.length;
    	Route routes[]=null;
    	Route route1=null,route2=null;
    	int kref1=0,kref2=K,compare=0;
    	for(int indexD=0;indexD<len;indexD++)
		{
    		routes=new Route[K];
			for(int iref=0;iref<K;iref++)
			{
				routes[iref]=null;
			}
			kref1=0;kref2=K;

			for(int kref=0;kref<K;kref++)
			{
				route1=ODInfos[indexO][indexD].AccessRoutesK[type][kref1];
				route2=ODInfos[indexO][indexD].AccessRoutesK[type][kref2];
				if(route1==null&&route2!=null)
				{
					routes[kref]=route2;
					kref2++;
					continue;
				}
				if(route1!=null&&route2==null)
				{
					routes[kref]=route1;
					kref1++;
					continue;
				}
				if(route1==null&&route2==null)
				{
					break;
				}
				
				
				compare=compareCostInfo(type, route1.costInfos, route2.costInfos);
				if(compare==-1)
				{
					routes[kref]=route1;
					kref1++;
				}
				if(compare==0)
				{
					routes[kref]=route1;
					kref++;
					if(kref==K)
						break;
					routes[kref]=route2;
					kref1++;
					kref2++;
					
				}
				if(compare==1)
				{
					routes[kref]=route2;
					kref2++;
				}
			}
			for(int iref=0;iref<K;iref++)
			ODInfos[indexO][indexD].AccessRoutesK[type][iref]=routes[iref];
			}
    }
    private void updateAccessRoutes()
    {
    	int len=network.stations.length;
    	Route routes[]=null;
    	Route route1=null,route2=null;
    	int kref1=0,kref2=K,compare=0;
    	for(int i=0;i<len;i++)
    	{
    		for(int j=0;j<len;j++)
    		{
    			
    			{
    				for(int type=0;type<3;type++)
    				{
    					routes=new Route[K];
    					for(int iref=0;iref<K;iref++)
    					{
    						routes[iref]=null;
    					}
    					kref1=0;kref2=K;
    					for(int kref=0;kref<K;kref++)
    					{
    						route1=ODInfos[i][j].AccessRoutesK[type][kref1];
    						route2=ODInfos[i][j].AccessRoutesK[type][kref2];
    						if(route1==null&&route2!=null)
    						{
    							routes[kref]=route2;
    							kref2++;
    							continue;
    						}
    						if(route1!=null&&route2==null)
    						{
    							routes[kref]=route1;
    							kref1++;
    							continue;
    						}
    						if(route1==null&&route2==null)
    						{
    							break;
    						}
    						
    						
    						compare=compareCostInfo(type, route1.costInfos, route2.costInfos);
    						if(compare==-1)
    						{
    							routes[kref]=route1;
    							kref1++;
    						}
    						if(compare==0)
    						{
    							routes[kref]=route1;
    							kref++;
    							if(kref==K)
    								break;
    							routes[kref]=route2;
    							kref1++;
    							kref2++;
    							
    						}
    						if(compare==1)
    						{
    							routes[kref]=route2;
    							kref2++;
    						}
    					}
    					for(int iref=0;iref<K;iref++)
    					ODInfos[i][j].AccessRoutesK[type][iref]=routes[iref];
    				}
    			}
    		}
    	}
    }
    public void WriteAccessRoutes(String time)
	{

			try{
				String filePath="";
					filePath="Data\\AccessRoutes.txt";
				FileWriter fileWriter;
			fileWriter=new FileWriter(filePath);
			FileWriter writeTemp=new FileWriter("Data\\error-Resonable.txt");
			fileWriter.write("时间:"+time+"\n");
			fileWriter.write("起站所在线路\t起站名称\t到站所在线路\t到站名称\t可达性\t类型\t序号\t时间成本\t换乘次数\t综合\t途经线路\t途经换乘站\t换乘车站列表\t途径车站数量\t途经车站");
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
						for(int k=0;k<2*K;k++)
						{
							
						route=ODInfos[indexO][j].AccessRoutesK[type][k];
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
										transferList+=station.stationName+"(换乘："+strTemp+")";
										stationList+="(换乘："+strTemp+")"+station.stationName;
										if(node.station==network.stations[j])
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
										if(node.station==network.stations[j])
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
							
							
							fileWriter.write("\n"+lineO+"\t"+stationO+"\t"+lineD+"\t"+stationD
									+"\t"+access+"\t"+type+"\t"+k+"\t"+costdistance+"\t"+transfertime+"\t"+costcom+"\t"+lineList+"\t"+transferList+"\t"+transferStations+"\t"+stationNum+"\t"+stationList);
						
							
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
    
}
