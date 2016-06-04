package netWork;
import java.util.*;

import pathFinding.*;


public class Station implements Cloneable{
	  
    public int odNo=0;
    public int stationNo=0;
    
    public int numInline=0;
    public Line line=null;
    
    
    public short type=0;
    public int transferType=0;
    public short bEnd=9;
    public short bCount=0;
    public String stationName="";
    
    public StationDir staiondir[]=new StationDir[3];
    public TransSta transferStation=null;
    public StopInfos stopInfoss[]=new StopInfos[2];
    public int enterTime=0;
    
    public Field fields[]=new Field[4];
    public boolean inSetA=false;
    public boolean inSetB=false;
    public double cost[]={Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE};
   
 
    
    
    public Station()
    {
    	
    	
    	for(int i=0;i<2;i++)
    	{
    		
    		stopInfoss[i]=new StopInfos();
    	staiondir[i]=new StationDir();
     	fields[i]=null;
    	}
    	staiondir[2]=new StationDir();
    	staiondir[2].station=this;
    	staiondir[2].direction=0;
 	   fields[2]=null;
 	   fields[3]=null;
    }
    public StationDir getNextStationDir(int direction)
    {
    	StationDir sDReturn=null;
    	if(direction!=0)
    	{
    	Station s=this.fields[2-direction].stationNext;
    	if(s!=null)
    		return s.staiondir[2-direction];}
    	if(direction==0)
    	{
    		sDReturn=this.staiondir[2];
    	}
    	return sDReturn;
    }
    public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
    public StationDir getStaionDir(int direction)
    {
    	
    	for(int i=0;i<3;i++)
    	{
    		if(this.staiondir[i].direction==direction)
    			return staiondir[i]; 
    	}
    	return null;
    }
    public void addStopInfo(StopInfo stopInfo)
    {
    	List<StopInfo> stopInfos=this.stopInfoss[2-stopInfo.trainInfo.direction].stopInfos;
    	
    	int index1=stopInfos.size()-1;
    	if(stopInfos.isEmpty())
    	{
    		stopInfos.add(stopInfo);
    		return;
    	}
    	if(stopInfo.arrivalTime<stopInfos.get(0).arrivalTime)
    		{
    		stopInfos.add(0,stopInfo);
    		return;
    		}
    	if(stopInfo.arrivalTime>=stopInfos.get(index1).arrivalTime&&(stopInfo.departureTime>=stopInfos.get(index1).departureTime))
    		{stopInfos.add(stopInfo);
    		return;
    		}
    	ListIterator<StopInfo> iter=null;
		iter=stopInfos.listIterator();
		StopInfo stopinfoOld=null;
    	while(iter.hasNext())
    	{
    		stopinfoOld=iter.next();
    		if((stopInfo.arrivalTime<=stopinfoOld.arrivalTime)&&(stopInfo.departureTime<=stopinfoOld.departureTime))
    		{
    			iter.previous();
    			iter.add(stopInfo);
        		return;
    		}
    	}
    	
    	
    }
    
    
    public StopInfo[] getlaststopInfos()
    {
    	StopInfo stopinfoReturn[]=new StopInfo[2];
    	stopinfoReturn[0]=stopinfoReturn[1]=null;
    	StopInfo stopinfo=null;

    	int index=0;
    	for(int i=0;i<2;i++)
    	{
    		List<StopInfo> stopInfos=this.stopInfoss[i].stopInfos;
    		index=stopInfos.size()-1;
    		if(index<0)
    			continue;
    		stopinfo=stopInfos.get(index);
    		stopinfoReturn[i]=stopinfo;
    	}
    	return stopinfoReturn;
    }
    public StopInfo getlaststopInfo(int direction)
    {
    	StopInfo stopinfoReturn=null;
    	StopInfo stopinfo=null;

    	int index=0;
    	
    	{
    		List<StopInfo> stopInfos=this.stopInfoss[2-direction].stopInfos;
    		index=stopInfos.size()-1;
    		if(index<0)
    			return null;
    		stopinfoReturn=stopInfos.get(index);
    	}
    	return stopinfoReturn;
    }
    public StopInfo getStopinfoBeforeTime(int direction, long time)
	
	{
		
		StopInfo stopinfo=null;
		
     	List<StopInfo> stopInfos=this.stopInfoss[2-direction].stopInfos;
     	int index1=stopInfos.size()-1;
    	int index=0;
    	int i=0;
    	if(stopInfos.isEmpty())
    	{
    		
    		return null;
    	}
    	if(time<=stopInfos.get(0).arrivalTime)
    	{
    		
    		return null;
    	}
    	if(time>stopInfos.get(index1).arrivalTime)
    	{
    		
    		return stopInfos.get(index1);
    	}
    	if(time==stopInfos.get(index1).arrivalTime&&stopInfos.get(index1-1).arrivalTime<time)
    	{
    		
    		return stopInfos.get(index1-1);
    	}
    	while(true)
    	{
    		i=(index1-index)/2+index;
    		if(stopInfos.get(i).arrivalTime==time&&stopInfos.get(i-1).arrivalTime<time)
    			return stopInfos.get(i-1);
    		if(stopInfos.get(i).arrivalTime==time&&stopInfos.get(i-1).arrivalTime==time)
    			{i=i-1;
    			if(i<0)
    			{
    				System.out.println("没有找到衔接列车"+this.stationName+":"+time);
    				return null;
    			}
    			}
    		if(stopInfos.get(i).arrivalTime<time&&stopInfos.get(i+1).arrivalTime>=time)
    			return stopInfos.get(i);
    		if(time<=stopInfos.get(i).arrivalTime)
    		{
    			index1=i;
    		}
    		if(time>stopInfos.get(i).arrivalTime)
    		{
    			index=i;
    		}
    		
    	}
    	
    	

   }
    public StopInfo getStopinfoAfterTime(int direction, long time)
	
	{
		
		StopInfo stopinfo=null;
		
     	List<StopInfo> stopInfos=this.stopInfoss[2-direction].stopInfos;
     	int index1=stopInfos.size()-1;
    	int index=0;
    	int i=0;
    	if(stopInfos.isEmpty())
    	{
    		
    		return null;
    	}
    	if(time>=stopInfos.get(index1).departureTime)
    	{
    		
    		return null;
    	}
    	if(time<stopInfos.get(0).departureTime)
    	{
    		
    		return stopInfos.get(0);
    	}
    	if(time==stopInfos.get(0).departureTime&&stopInfos.get(1).departureTime>time)
    	{
    		
    		return stopInfos.get(1);
    	}
    	while(true)
    	{
    		i=(index1-index)/2+index;
    		if(stopInfos.get(i).departureTime==time&&stopInfos.get(i+1).departureTime>time)
    			return stopInfos.get(i+1);
    		if(stopInfos.get(i).departureTime==time&&stopInfos.get(i+1).departureTime==time)
    			{i=i+1;
    			if(i>stopInfos.size()-1)
    			{
    				System.out.println("没有找到衔接列车"+this.stationName+":"+time);
    				return null;
    			}
    			}
    		if(stopInfos.get(i).departureTime>time&&stopInfos.get(i-1).departureTime<=time)
    			return stopInfos.get(i);
    		if(stopInfos.get(i).departureTime<=time&&stopInfos.get(i+1).departureTime>time)
    			return stopInfos.get(i+1);
    		if(time<stopInfos.get(i).departureTime)
    		{
    			index1=i-1;
    		}
    		if(time>stopInfos.get(i).departureTime)
    		{
    			index=i+1;
    		}
    		
    	}
    	

   }

}
