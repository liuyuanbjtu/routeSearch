package netWork;
import java.util.*;

public class StopInfo 

{
   public TrainInfo trainInfo;
   public long arrivalTime=0;
   public long departureTime=0;
   public Station currentStation;
   public StopInfo previousStopInfo;
   public StopInfo nextStopInfo;
   public int positionInStation;
   public StopInfo()
   {
	   previousStopInfo=null;
	   nextStopInfo=null;
	   trainInfo=null;

   }
  
   public StopInfo[] getValidPreviousStopInfo()
   {
	   StopInfo stopinfoReturns[]=new StopInfo[2];
	   stopinfoReturns[1]=stopinfoReturns[0]=null;
	   StopInfo stopinfo=null;
	   int refDir=0;
	  
	   if(this.currentStation.bEnd==2&&this.currentStation.line.lineType==2) 
	   {
		   refDir=2;
	   }

	   if((refDir==0&&this.currentStation.fields[3]==null&&trainInfo.direction==1)
			   ||(refDir==0&&this.currentStation.fields[2]==null&&trainInfo.direction==2))
		   return stopinfoReturns;
	
		
	   
	   int index=0;
	   int b=0;
	   if(refDir==0)
		   stopinfoReturns[0]=previousStopInfo;
	   List<StopInfo> stopInfos=null;
	   StopInfo stopinfopre=null;
	   if(refDir==0&&previousStopInfo==null)
	   {   stopInfos=this.currentStation.stopInfoss[2-this.trainInfo.direction].stopInfos;
		   index=this.positionInStation;
		   
		   
		   while(index>0)
		   {
			   index--;
			   stopinfopre=stopInfos.get(index).previousStopInfo;
				   if(stopinfopre!=null)
					{
						   stopinfoReturns[0]=stopinfopre;   
						   
						   break;
					}
			   }
		   if(index==0)
			{
				System.out.println("没有找到衔接列车"+this.currentStation.stationName+":"+this.currentStation.line.lineName+":"+this.trainInfo.direction+":"+arrivalTime);
				
			}
	   }
	   if(refDir!=0)
	   {
		   for(int i=0;i<2;i++)
		   {
			   stopinfo=this.currentStation.getStopinfoBeforeTime(2-i, this.departureTime);
			   
			   if(stopinfo==null) 
				   continue;
			   if(stopinfo.previousStopInfo!=null)
			   {
				   stopinfoReturns[i]=stopinfo.previousStopInfo;
				  continue;
			   }
			   stopInfos=this.currentStation.stopInfoss[i].stopInfos;
			   index=stopinfo.positionInStation;
			   while(index>=0)
			   {
				   
				   if(stopInfos.get(index).previousStopInfo!=null)
					{
						   stopinfoReturns[i]=stopInfos.get(index).previousStopInfo;   
						   break;
					}
				   index--;
			   }
			   
		   }
	   }
	

	   return stopinfoReturns;
   }

public StopInfo getValidPreviousStopInfo(Station s)
{
	   StopInfo stopinfo=null;
	   int refDir=0;
	   if(this.currentStation.bEnd==3&&this.currentStation.line.lineType==2) 
	   {
		   refDir=1;
	   }
	   if(this.currentStation.bEnd==2&&this.currentStation.line.lineType==2) 
	   {
		   refDir=2;
	   }

	   if((refDir==0&&this.currentStation.fields[3]==null&&trainInfo.direction==1)
			   ||(refDir==0&&this.currentStation.fields[2]==null&&trainInfo.direction==2))
		   return null;
	
		
	   
	   int index=0;
	   int b=0;
	   if(refDir==0)
		   {
		   if(previousStopInfo!=null&&previousStopInfo.currentStation==s)
			   return previousStopInfo;
		   }
	   
	   List<StopInfo> stopInfos=null;
	   StopInfo stopinfopre=null;
	   if(refDir==0&&previousStopInfo==null)
	   {   stopInfos=this.currentStation.stopInfoss[2-this.trainInfo.direction].stopInfos;
		   index=this.positionInStation;
		   
		   
		   while(index>0)
		   {
			   index--;
			   stopinfopre=stopInfos.get(index).previousStopInfo;
				   if(stopinfopre!=null)
					{
						   if(stopinfopre.currentStation==s)
							   return stopinfopre;
						   break;
					}
			   }
		   if(index==0)
			{
				System.out.println("没有找到衔接列车"+this.currentStation.stationName+":"+this.currentStation.line.lineName+":"+this.trainInfo.direction+":"+arrivalTime);
				
			}
	   }
	   if(refDir!=0)
	   {
		   for(int i=0;i<2;i++)
		   {
			   stopinfo=this.currentStation.getStopinfoBeforeTime(2-i, this.departureTime);
			   
			   if(stopinfo==null) 
				   continue;
			    if(stopinfo.previousStopInfo!=null)
			   {
			    	if(stopinfo.previousStopInfo.currentStation==s)
				      return stopinfo.previousStopInfo;
				  continue;
			   }
			   stopInfos=this.currentStation.stopInfoss[i].stopInfos;
			   index=stopinfo.positionInStation;
			   while(index>=0)
			   {
				   stopinfo=stopInfos.get(index).previousStopInfo;
				   if(stopinfo!=null)
					{
						   if(stopinfo.currentStation==s)
						      return stopinfo;
						   continue;
					}
				   index--;
			   }
			   
		   }
	   }
	   return null;
}
public StopInfo[] getValidNextStopInfo()
{
	StopInfo stopinfoReturns[]=new StopInfo[2];
	   stopinfoReturns[1]=stopinfoReturns[0]=null;
	   StopInfo stopinfo=null;
	   int refDir=0;
	 
	   if(this.currentStation.bEnd==2&&this.currentStation.line.lineType==2) 
	   {
		   refDir=2;
	   }
	  
		
	   if((refDir==0&&this.currentStation.fields[1]==null&&trainInfo.direction==1)
			   ||(refDir==0&&this.currentStation.fields[0]==null&&trainInfo.direction==2))
		   return stopinfoReturns;
	
		
	   
	   int index=0;
	   int b=0;
	   if(refDir==0)
	   {
		   if(this.nextStopInfo!=null)
		   {
			   stopinfoReturns[0]=nextStopInfo;
			   return stopinfoReturns;
		   }
		}
	   List<StopInfo> stopInfos=null;
	   StopInfo stopinfonext=null;
	   if(refDir==0&&nextStopInfo==null)
	   {   stopInfos=this.currentStation.stopInfoss[2-this.trainInfo.direction].stopInfos;
		   index=this.positionInStation;
		   
		   
		   while(index<stopInfos.size()-1)
		   {
			   index++;
			   stopinfonext=stopInfos.get(index).nextStopInfo;
				   if(stopinfonext!=null)
					{
					   stopinfoReturns[0]=stopinfonext;
					   return stopinfoReturns;
						 
					}
			   }
		   if(index==0)
			{
				System.out.println("没有找到衔接列车"+this.currentStation.stationName+":"+this.currentStation.line.lineName+":"+this.trainInfo.direction+":"+arrivalTime);
				
			}
	   }
	   if(refDir==2)
	   {
		   for(int i=0;i<2;i++)
		   {
			   stopinfo=this.currentStation.getStopinfoAfterTime(2-i, this.arrivalTime);
			   
			   if(stopinfo==null) 
				   continue;
			  
			    if(stopinfo.nextStopInfo!=null)
			   {
			    	stopinfoReturns[i]=stopinfo.nextStopInfo;
				  continue;
			   }
			   stopInfos=this.currentStation.stopInfoss[i].stopInfos;
			   index=stopinfo.positionInStation;
			   while(index<stopInfos.size())
			   {
				   stopinfo=stopInfos.get(index).nextStopInfo;
				   if(stopinfo!=null)
					{
					   stopinfoReturns[i]=stopinfo;
					   break;
						  
					}
				   index++;
			   }
			   
		   }
		   return stopinfoReturns;
	   }
	   
	   return stopinfoReturns;
	  
}
public StopInfo  getNextStopInfo()
{
	StopInfo stopinfoReturn=null ;
	   StopInfo stopinfo=null;
	   int refDir=0;
	   if((refDir==0&&this.currentStation.fields[1]==null&&trainInfo.direction==1)
			   ||(refDir==0&&this.currentStation.fields[0]==null&&trainInfo.direction==2))
		   return stopinfoReturn;
	   int index=0;
	   int b=0;
	   if(refDir==0)
	   {
		   if(this.nextStopInfo!=null)
		   {
			   stopinfoReturn=nextStopInfo;
			   return stopinfoReturn;
		   }
		}
	   List<StopInfo> stopInfos=null;
	   StopInfo stopinfonext=null;
	   if(refDir==0&&nextStopInfo==null)
	   {   stopInfos=this.currentStation.stopInfoss[2-this.trainInfo.direction].stopInfos;
		   index=this.positionInStation;
		   while(index<stopInfos.size()-1)
		   {
			   index++;
			   stopinfonext=stopInfos.get(index).nextStopInfo;
				   if(stopinfonext!=null)
					{
					   stopinfoReturn=stopinfonext;
					   return stopinfoReturn;
						 
					}
			   }
		   if(index==0)
			{
				System.out.println("没有找到衔接列车"+this.currentStation.stationName+":"+this.currentStation.line.lineName+":"+this.trainInfo.direction+":"+arrivalTime);
				
			}
	   }
	
		   return stopinfoReturn;
	   }
	   

public StopInfo getValidNextStopInfo(Station s)
{
	StopInfo stopinfoReturn=null;
	 
	   StopInfo stopinfo=null;
	   int refDir=0;
	  
	   if(this.currentStation.line.lineType==2&&currentStation.bEnd!=0)
		   refDir=1;
	   if((refDir==0&&this.currentStation.fields[1]==null&&trainInfo.direction==1)
			   ||(refDir==0&&this.currentStation.fields[0]==null&&trainInfo.direction==2))
		   return stopinfoReturn;
	
		
	   
	   int index=0;
	   int b=0;
	   if(refDir==0)
	   {
		   if(this.nextStopInfo!=null)
		   {
			   stopinfoReturn=nextStopInfo;
			   return stopinfoReturn;
		   }
		}
	   List<StopInfo> stopInfos=null;
	   StopInfo stopinfonext=null;
	   if(refDir==0&&nextStopInfo==null)
	   {   stopInfos=this.currentStation.stopInfoss[2-this.trainInfo.direction].stopInfos;
		   index=this.positionInStation;
		   
		   
		   while(index<stopInfos.size()-1)
		   {
			   index++;
			   stopinfonext=stopInfos.get(index).nextStopInfo;
				   if(stopinfonext!=null)
					{
					   stopinfoReturn=stopinfonext;
					   return stopinfoReturn;
						 
					}
			   }
		   if(index==0)
			{
				System.out.println("没有找到衔接列车"+this.currentStation.stationName+":"+this.currentStation.line.lineName+":"+this.trainInfo.direction+":"+arrivalTime);
				
			}
	   }
	   if(refDir!=0)
	   {
		   for(int i=0;i<2;i++)
		   {
			   stopinfo=this.currentStation.getStopinfoAfterTime(2-i, this.arrivalTime);
			   
			   if(stopinfo==null) 
				   continue;
			  
			    if(stopinfo.nextStopInfo!=null&&stopinfo.nextStopInfo.currentStation==s)
			   {
			    	stopinfoReturn=stopinfo.nextStopInfo;
				  return stopinfoReturn;
			   }
			   stopInfos=this.currentStation.stopInfoss[i].stopInfos;
			   index=stopinfo.positionInStation;
			   while(index<stopInfos.size())
			   {
				   stopinfo=stopInfos.get(index).nextStopInfo;
				   if(stopinfo!=null&&stopinfo.currentStation==s)
					{
					   stopinfoReturn=stopinfo;
					   return stopinfoReturn;
						  
					}
				   index++;
			   }
			   
		   }
		   return stopinfoReturn;
	   }
	

	   return stopinfoReturn;
}
}
