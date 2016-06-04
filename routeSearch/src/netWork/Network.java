package netWork;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;




import java.io.*;
public class Network {

	public Station stations[];
	public List<Line> lines=new ArrayList<Line>();
	 HashMap<String, Station[]> stationNameToStation = new HashMap<String, Station[]>();
	 HashMap<Integer, Station> stationIdToStation = new HashMap<Integer, Station>();
	 HashMap<Integer, Field> FieldIdToField = new HashMap<Integer, Field>();
	 public int dayRef=15;
	 public double highcongetRef=1.3;
	 public double obsrate=10;
	 public double refCongest[][]=new double[3][2];
	 public Network()
	 {
		 
		 refCongest[0][0]=1.5;
		 refCongest[0][1]=4;

		 refCongest[1][0]=1.2;
		 refCongest[1][1]=2;

		 refCongest[2][0]=1.2;
		 refCongest[2][1]=5;
	 }
 
	
    public Station[] GetStationsByStationName(String stationName)
    {
        Station[] stations = null;
        stations=stationNameToStation.get(stationName);
        return stations;
       
    }

    public Station GetStationByStationNameAndLine(String stationName, String line)
    {
        Station[] stations = GetStationsByStationName(stationName);
        if (stations == null)
            return null;
     
        for (Station station : stations)
        {
            if (station.line.lineName.equals(line))
            {
                return station;
            }
        }

        return null;
    }

    public Station GetStationByStationId(int stationId)
    {
        Station station = null;
        station=stationIdToStation.get(stationId);
            return station;
   
        
    }
    public Field GetFieldByFieldId(int fieldId)
    {
        Field field = null;
        field=this.FieldIdToField.get(fieldId);
            return field;
   
        
    }
    

    
    public void LoadStationInfo(String file)
    {
    	try{
    	FileInputStream fis = new FileInputStream(file); 
    	InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); 
    	BufferedReader br = new BufferedReader(isr); 
    	
//    	Scanner sr = new Scanner(new FileReader(file));
        String line;
        String[] fieldData;
        
//        line=sr.nextLine();
        line=br.readLine();
        fieldData=line.split("\t");
        int n=Integer.parseInt(fieldData[0].trim());
        
        stations=new Station[n];
        Station previousStation = null;
        Station station1=null;
        Station station2=null;
        Line lineObj=null;
//        sr.nextLine();
        br.readLine();
        int ODNo=0;
        int lineNum=0;
        int j=0;
      
        for(int i=0;i<n;i++)
        {
        	Station s = new Station();
//         	line=sr.nextLine();
        	line=br.readLine();
        	fieldData=line.split("\t");
        	if(fieldData.length<5)
        		continue;
            s.odNo=ODNo;
          
            int lineNo=Integer.parseInt(fieldData[0]);
            
           String lineName=fieldData[1];
            s.stationNo = Integer.parseInt(fieldData[2]);
            
            s.stationName=fieldData[3];
            s.type=Short.parseShort(fieldData[4]);
            s.bEnd=Short.parseShort(fieldData[5]);
            
            for(j=0;j<2;j++)
            {
            	s.staiondir[j].station=s;
            	s.staiondir[j].direction=(short)(2-j);
            }

           if(previousStation==null
        		   ||(previousStation != null &&!previousStation.line.lineName.equals(lineName)))
           {
        	   lineObj=new Line();
        	   lineObj.lineName=lineName;
        	   lineObj.lineNo=lineNo;
        	   lineObj.numInnetwork=lineNum;
        	   lineObj.stations.add(s);
        	   s.line=lineObj;
               s.line.lineType=Integer.parseInt(fieldData[6]);
        	   s.numInline=0;
        	   lineNum++;
        	   this.lines.add(lineObj);
        	   if(previousStation != null)
        		   previousStation.line.stations.toArray();
        	  
        	   station1=null;
        	   station2=null;
           }
            
           if(previousStation != null && previousStation.line.lineName.equals(lineName))
            {
            	
 
            	
            	s.line=previousStation.line;
            	 s.numInline+=previousStation.numInline;
            	s.line.stations.add(s);
            }
               stationIdToStation.put(s.stationNo, s) ;

            if (stationNameToStation.containsKey(s.stationName))
            {
                Station[] oldStations = stationNameToStation.get(s.stationName);
                Station[] stations1 = Arrays.copyOf(oldStations,oldStations.length + 1);
                 stations1[stations1.length - 1] = s;
                stationNameToStation.remove(s.stationName);
                stationNameToStation.put(s.stationName,stations1);
            }
            else
            {
            	 Station[] stations2= new Station[1];
            	 stations2[0]=s;
            	 stationNameToStation.put(s.stationName,stations2);
            }
            stations[i]=s;
            ODNo++;
          
           if(s.line.lineType==1&&s.bEnd==0)
            {
            	station1=s;
            	if(station1!=null&&station2!=null)
        		{
        		
        		
        		}
            }
            if(s.line.lineType==1&&s.bEnd==1)
            {
            	station2=s;
            	if(station1!=null&&station2!=null)
            		{
            		
            		
            		}
            }
           
         
            previousStation = s;
        }
        this.lines.toArray();
//        sr.close();
        fis.close();
        isr.close();
        br.close();
    	}
    	catch(IOException exception)
    	{
    		exception.printStackTrace();
    	}
    }
    public void LoadTarget_lineInfo(String file,String fileName)
    {
    	try{
    		FileInputStream fis = new FileInputStream(file+"\\"+fileName); 
    	    InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); 
    	    BufferedReader br = new BufferedReader(isr); 
    		
//    		Scanner sr = new Scanner(new FileReader(file+"\\"+fileName));
            String line;
            String[] fieldData;
           
            br.readLine();
//            sr.nextLine();
             while((line=br.readLine())!=null)
            {
//            	line=sr.nextLine();
            	fieldData=line.split("\t");
            	int time=0;
            	//CODE	TIME	MLCODE	NUM	TRAINNUM	PEDNUM	MAXLOAD
            	//20150326	7:45:00	金台路-呼家楼	061036:2279	1	2279	116.28
            	 try
                 {
            		 SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm:ss");
                     Date date=dateFormat.parse(fieldData[1]);
                 	 time=date.getHours()*3600+date.getMinutes()*60+date.getSeconds();
                 	
                   }
                 catch (ParseException e)
                 {
                 	e.printStackTrace();
                 }
            	 int index_t=time/300;
            	 String s[]=fieldData[2].split("-");
            	 Station stations1[]=this.GetStationsByStationName(s[0]);
            	 Station stations2[]=this.GetStationsByStationName(s[1]);
            	 Station station1=null;
            	 Station station2=null;
            	 ///两个station处于同一线路则break
            	 int b=0;
            	 for(int i=0;i<stations1.length;i++)
            	 {
            		 station1=stations1[i];
            		 for(int j=0;j<stations2.length;j++)
            		 {
            			 station2=stations2[j];
            			 if(station1.line==station2.line)
            			 {
            				 b=1;
            				 break;
            			 }
            		 }
            		 if(b==1)
            			 break;
            	 }
            	 int direction=this.setDirection(station1, station2);
            	 Field field=station1.fields[2-direction];
            	 
            	 if(fileName.equals("target_mainline_0325.txt")){
//            		 field.flowsRef[0][index_t]=Integer.parseInt(fieldData[5]);
                	 field.congestionRefs[0][index_t]=Double.parseDouble(fieldData[6]);;
//                	 field.trainsIntervals[0][index_t]=Integer.parseInt(fieldData[4]);
            	 }
            	 else if(fileName.equals("target_mainline_0326.txt")){
//            		 field.flowsRef[1][index_t]=Integer.parseInt(fieldData[5]);
                	 field.congestionRefs[1][index_t]=Double.parseDouble(fieldData[6]);;
//                	 field.trainsIntervals[1][index_t]=Integer.parseInt(fieldData[4]);
            	 }
            	 else{
//            		 field.flowsRef[2][index_t]=Integer.parseInt(fieldData[5]);
                	 field.congestionRefs[2][index_t]=Double.parseDouble(fieldData[6]);;
//                	 field.trainsIntervals[2][index_t]=Integer.parseInt(fieldData[4]);
            	 }
            	
            	 
             }     
             fis.close();
             isr.close();
             br.close();
//             sr.close();
    	}
    	catch(IOException exception)
    	{
    		exception.printStackTrace();
    	}
       
   }
    public void LoadTarget_stationInfo(String file,String fileName)
    {
    	try{
    		FileInputStream fis = new FileInputStream(file+"\\"+fileName); 
    	    InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); 
    	    BufferedReader br = new BufferedReader(isr); 
    	    
//    		Scanner sr = new Scanner(new FileReader(file+"\\"+fileName));
            String line;
            String[] fieldData;
           
            br.readLine();
//            sr.nextLine();
             while((line=br.readLine())!=null)
            {
//             	line=sr.nextLine();
             	fieldData=line.split("\t");
             	int time=0;
             	//（1）TIME	N	VARCHAR2(10)	Y			时间 7:00:00 5分钟间隔
             	//（2）STATIONCODE	N	VARCHAR2(20)	Y			车站编码慈寿寺
             	//(10)GETONWAITTIME	N	VARCHAR2(1000)	Y			进站上车候车时间
             	 //10号线上:100;10号线下:189;6号线上:151
             	//(13)TRANINWAITTIME	N	VARCHAR2(1000)	Y			换乘上车候车时间
             	//10号线上:356;10号线下:365;6号线下:357;6号线上:314

             	 try
                  {
             		 SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm:ss");
                      Date date=dateFormat.parse(fieldData[1]);
                  	 time=date.getHours()*3600+date.getMinutes()*60+date.getSeconds();
                  	
                    }
                  catch (ParseException e)
                  {
                  	e.printStackTrace();
                  }
             	 //把date(7：30：15)转换成一个整数index_t;
             	 int index_t=time/300;
             	 //String s[]=fieldData[2].split("-");
             	 String stationname=fieldData[2];
             	// Station stations[]=this.GetStationsByStationName(stationname);
             	// Station stations2[]=this.GetStationsByStationName(s[1]);
             	String wait_ins[]=fieldData[10].split(";");//分时进站候车时间GETONWAITINGTIME
             	String wait_trs[]=fieldData[13].split(";");//分时换乘候车时间TRANINWAITINGTIME
             	String walk_ins[]=fieldData[9].split(";");//分时进站集散时间GETONTIME
             	String walk_trs[]=fieldData[12].split(";");//分时换乘集散时间TRANINTIME
             	//两个station处于同一线路则break
             	
             	//index=0，表示field.getOnWaittime，index=1，表示field.tranInWaittime
             	//index=2,表示field.getOnTime,index=3，表示field.tranInTime
             	for(int index=0;index<4;index++)
             	{
             		String str[]=null;
             		if(index==0)
             			str=wait_ins;
             		else if(index==1)
             			str=wait_trs;
             		else if(index==2)
             			str=walk_ins;
             		else
             			str=walk_trs;
             	 for(int i=0;i<str.length;i++)
             	 {
             		String wait_str[]=str[i].split(":");
             		int len=wait_str[0].length()-1;
             		//判断该车站换乘到哪个方向（wait_str）,用dir表示字符串的第四个字。
             		String dir="";
             		//判断len的长度很关键，可以排除单元格为空的情况
             		if(len!=-1)
             		{
             		dir+=wait_str[0].charAt(len);
             		int directionref=0;
             		if(dir.equals("上"))
             			directionref=2;
             		if(dir.equals("下"))
             			directionref=1;
             		//找到对应的区间
             		String linename=wait_str[0].substring(0, len);
             		//Line lineobj=this.getLine(linename);
             		Station station=this.GetStationByStationNameAndLine(stationname,linename);
             		//以车站起始的区间中fielddirection相同的区间
             		/*
             		 * Field field=null;
             		//j=0，1表示从这个车站出去的
             		for(int j=0;j<2;j++)
             		{
             			 field=station.fields[j];
             			if(field.fieldDirection==directionref)
             				break;
             		}
             		*/
             		Field field=null;
             		for(int j=0;j<4;j++)
             		{
             		    field=station.fields[j];
             		    if(field!=null&&field.fieldDirection==directionref)
             		    break;
             		}
             		if(fileName.equals("target_station_0325.txt")){
             			if(index==0)
                 			field.getOnWaittime[0][index_t]=Integer.parseInt(wait_str[1]);
                 		else if(index==1)
                 			field.tranInWaittime[0][index_t]=Integer.parseInt(wait_str[1]);
                 		else if(index==2)
                 			field.getOnTime[0][index_t]=Integer.parseInt(wait_str[1]);
                 		else
                 			field.tranInTime[0][index_t]=Integer.parseInt(wait_str[1]);
             		}
             		else if(fileName.equals("target_station_0326.txt")){
             			if(index==0)
                 			field.getOnWaittime[1][index_t]=Integer.parseInt(wait_str[1]);
                 		else if(index==1)
                 			field.tranInWaittime[1][index_t]=Integer.parseInt(wait_str[1]);
             			else if(index==2)
                 			field.getOnTime[1][index_t]=Integer.parseInt(wait_str[1]);
                 		else
                 			field.tranInTime[1][index_t]=Integer.parseInt(wait_str[1]);
             		}
             		else{
             			if(index==0)
                 			field.getOnWaittime[2][index_t]=Integer.parseInt(wait_str[1]);
                 		else if(index==1)
                 			field.tranInWaittime[2][index_t]=Integer.parseInt(wait_str[1]);
             			else if(index==2)
                 			field.getOnTime[2][index_t]=Integer.parseInt(wait_str[1]);
                 		else
                 			field.tranInTime[2][index_t]=Integer.parseInt(wait_str[1]);
             		}
             		
             		}
             	 }
             	 
             	}
              }     
             fis.close();
             isr.close();
             br.close();
//             sr.close();
    	}
    	catch(IOException exception)
    	{
    		exception.printStackTrace();
    	}
       
   }
    public void LoadTranferInfo(String file)
    {
    	try{
    		FileInputStream fis = new FileInputStream(file); 
    	    InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); 
    	    BufferedReader br = new BufferedReader(isr); 
    		
//    		Scanner sr = new Scanner(new FileReader(file));
            String line;
            String[] fieldData;
           
            TransSta previousTransferStation = null;
            TransSta transferStation=null;
  
            br.readLine();
//            sr.nextLine();
            StaTransCon sTransCon=null;
            while((line=br.readLine())!=null)
            {
            	 
            	
//            	line=sr.nextLine();
            	fieldData=line.split("\t");
            	if(fieldData.length<7)
            		continue;
                sTransCon = new StaTransCon();
                if(previousTransferStation==null||!(previousTransferStation.name.equals(fieldData[0])))
                {	if(previousTransferStation!=null)
                {
                	previousTransferStation.stationTransferCons.toArray();
                }
                	transferStation =new TransSta();
                    transferStation.name=fieldData[0];
                    transferStation.stations=stationNameToStation.get(transferStation.name); 
                    for(Station station:transferStation.stations)
                    {
                    	station.transferStation=transferStation;
                    }
                }
                
                String preLineName= fieldData[1];
                String nextLineName= fieldData[3];
                sTransCon.Stationpre=GetStationByStationNameAndLine(transferStation.name,preLineName);
                sTransCon.StationNext=GetStationByStationNameAndLine(transferStation.name,nextLineName);
                short direction= Short.parseShort(fieldData[2]);
                if(direction!=0&&sTransCon.Stationpre.line.bSameDir==false)
                {
                	direction=(short)(3-direction);
                }
                sTransCon.direction1=direction;
                direction=Short.parseShort(fieldData[4]);
                if(direction!=0&&sTransCon.StationNext.line.bSameDir==false)
                {
                	direction=(short)(3-direction);
                }
                sTransCon.direction2= direction;
                if(sTransCon.direction1!=0)
                {
                	sTransCon.Stationpre.transferType=1;
                }
                if(sTransCon.direction2!=0)
                {
                	 sTransCon.StationNext.transferType=1;
                }
                sTransCon.distance=Integer.parseInt(fieldData[5]);
                sTransCon.walkTime=Integer.parseInt(fieldData[6]);
                if(sTransCon.StationNext.line.lineNo==98||sTransCon.Stationpre.line.lineNo==98)
                {
                	sTransCon.fee=20;
                }
                transferStation.stationTransferCons.add(sTransCon);
                previousTransferStation=transferStation;
             }     
            fis.close();
            isr.close();
            br.close();
//            sr.close();
    	}
    	catch(IOException exception)
    	{
    		exception.printStackTrace();
    	}
       
   }
    public void LoadTimeTableall(String file)
    {	try{
    	FileInputStream fis = new FileInputStream(file); 
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); 
        BufferedReader br = new BufferedReader(isr); 
    	
//    	Scanner sr = new Scanner(new FileReader(file));
        String line;
        String[] fieldData1;
        
        StopInfo previousStopInfo = null;
        TrainInfo trainInfo=null;
        br.readLine();
//        sr.nextLine();
        int b=0;int c=0;
        long time1=0;
        long time2=0;
        Station s1=null;
        //Station s2=null;
        int lineId,stationId;
        StopInfo stopInfo=null;
       while((line = br.readLine()) != null)
        {   
//    	   line=sr.nextLine();
        	fieldData1=line.split("\t");
        	lineId =Integer.parseInt(fieldData1[2]);
            stationId= Integer.parseInt(fieldData1[3]);
            s1 = this.GetStationByStationId(stationId);
            if (s1 == null)
            {
               
                continue;
            }
        	
        	 try
             {
             	
        		 SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm:ss");
                 Date date=dateFormat.parse(fieldData1[6]);
             	time1=date.getHours()*3600+date.getMinutes()*60+date.getSeconds();
             	if(date.getHours()<3)
             	{
             			time1+=24*3600;
             	}
             		
           date=dateFormat.parse(fieldData1[7]);
         	time2=date.getHours()*3600+date.getMinutes()*60+date.getSeconds();
         	if(date.getHours()<3)
     		{
     		
     			time2+=24*3600;
     		}
              }
             catch (ParseException e)
             {
             	e.printStackTrace();
             }
        	if(previousStopInfo!=null&&previousStopInfo.currentStation==s1
        			&&previousStopInfo.trainInfo.trainNoS.equals(fieldData1[1]))
        	{
        		previousStopInfo.departureTime=time2;
        		continue;
        	}

        	 stopInfo = new StopInfo();          
             stopInfo.currentStation = s1;
             stopInfo.arrivalTime=time1;
             stopInfo.departureTime=time2;
            if(previousStopInfo!=null&&previousStopInfo.currentStation!=s1
        			&&previousStopInfo.trainInfo.trainNoS.equals(fieldData1[1]))
            	c=1;//ͬ车次不变
            if(previousStopInfo==null||!previousStopInfo.trainInfo.trainNoS.equals(fieldData1[1]))
            {//车次变
            	if(previousStopInfo!=null)//���α仯���ϸ��г����յ�վ��ֵ
            		previousStopInfo.trainInfo.destinationS=previousStopInfo.currentStation;
            	b=1;c=0;
            	
            	trainInfo=new TrainInfo();
            	//trainInfo.destinationS=s2;
            	trainInfo.originStop=stopInfo;
            	trainInfo.trainNoS=fieldData1[1];
            	if(previousStopInfo!=null)
            		previousStopInfo.trainInfo.destinationStop=previousStopInfo;
             }
            stopInfo.trainInfo = trainInfo;
            if (previousStopInfo != null && previousStopInfo.trainInfo.trainNoS.equals(stopInfo.trainInfo.trainNoS))
            {
                previousStopInfo.nextStopInfo = stopInfo;
                stopInfo.previousStopInfo = previousStopInfo;
            }
             
            if(c==1&&b==1)//新车次停靠两次
            {
            	trainInfo.direction=setTrainDirection(previousStopInfo,stopInfo);
            	b=0;
            	previousStopInfo.currentStation.addStopInfo(previousStopInfo);
            			
            }
            if(trainInfo.direction!=0)//��ʼ��վ��ͣ��
            s1.addStopInfo(stopInfo);
           

            previousStopInfo = stopInfo;
        }
       if(previousStopInfo!=null)//列车终到
       {
   		previousStopInfo.trainInfo.destinationStop=previousStopInfo;
   		previousStopInfo.trainInfo.destinationS=previousStopInfo.currentStation;
       }
       fis.close();
       isr.close();
       br.close(); 	
//       sr.close();
       
   
    	}
    	catch(IOException exception)
    	{
    		exception.printStackTrace();
    	}
		List<StopInfo> stopInfos=null;
       for(Station station: stations)
       {
    	   for(int i=0;i<2;i++)
    	   {
   		   stopInfos=station.stopInfoss[i].stopInfos;
   		   stopInfos.toArray();
    		   for(int j=0;j<stopInfos.size();j++)
    		   {
    			   stopInfos.get(j).positionInStation=j;
    		   }
    	   }
       }
       computeTimeofField();}

   
    
   
    public int setDirection(Station s1,Station s2)
    {
    	int direction=0;
    	if(s1.stationName.equals(s2.stationName))
    		return 0;
    	
    		if(s1.stationNo<s2.stationNo)
        	{
        		direction=1;
        	}
        	if(s1.stationNo>s2.stationNo)
    		{
        		direction=2;
    		}		
    	
    	if(s1.line.lineType==1)
    	{
    		if((s1.bEnd==0)&&(s2.bEnd!=1))
    		{
    			direction=1;
    		}
    		if((s1.bEnd==0)&&(s2.bEnd==1))
    		{
    			direction=2;
    		}
    		if((s1.bEnd==1)&&(s2.bEnd!=0))
    		{
    			direction=2;
    		}
    		if((s1.bEnd==1)&&(s2.bEnd==0))
    		{
    			direction=1;
    		}
    	}
    	if(s1.line.lineType==2)
    	{
    		if(s2.bEnd==2&&s1.bEnd==1)
    		{
    			direction=1;
    		}
    		if(s1.bEnd==2&&s2.bEnd==1)
    		{
    			direction=2;
    		}
     	}
    	return direction;
    }
    public int setTrainDirection(StopInfo previousStopInfo,StopInfo stopInfo)
    {
    	int direction=	this.setDirection(previousStopInfo.currentStation, stopInfo.currentStation);
    	
    		
    	return direction;
    }
     public void LoadFiledTable(String file)
    {try{
    	FileInputStream fis = new FileInputStream(file); 
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); 
        BufferedReader br = new BufferedReader(isr); 
    	
//    	Scanner sr = new Scanner(new FileReader(file));
        String line;
        String[] fieldData1;       
       
//       sr = new Scanner(new FileReader(file));
       br.readLine();
//        sr.nextLine();
//       while(sr.hasNext())
       while((line=br.readLine())!=null)
       {
//    	   line=sr.nextLine();
    	   fieldData1=line.split("\t");
    	   Field field=new Field();
    	   field.lineNo=Integer.parseInt(fieldData1[1]);
    	   if( field.lineNo==493)
    		   field.lineNo=4;
    	   field.fieldNo=Integer.parseInt(fieldData1[2]);
    	   String str="";
    	   if(fieldData1[2].length()==7)
    	   {
    		   str+="0";
    	   }
    	   str+=fieldData1[2];
    	   field.fieldNoStr+=str;
    	   if(field.fieldNo==9321467)
    		   field.fieldNo=93219319;
    	   field.fieldDirection=Short.parseShort(fieldData1[8]);
    	   field.length=Float.parseFloat(fieldData1[9]);
    	   int is1=Integer.parseInt(fieldData1[4]);
    	   int is2=Integer.parseInt(fieldData1[6]);
    	   if(field.lineNo==93)
    	   {
    		   field.lineNo=4;
    		   is1=is1-8852;
    		   is2=is2-8852;
    	   }
    	   if(field.lineNo==98)
    		   field.fee=20;
    	   field.staionPre=this.GetStationByStationId(is1);
    	   field.stationNext=this.GetStationByStationId(is2);
    	   if(field.staionPre==null)
    		   System.out.println(is1);
    	   if(field.stationNext==null)
    		   System.out.println(is2);
    	   field.direction=(short)this.setDirection(field.staionPre, field.stationNext);
    	   field.staionPre.fields[2-field.direction]=field;
    	   field.stationNext.fields[4-field.direction]=field;
    	   this.FieldIdToField.put(field.fieldNo, field) ;
    	   if(field.direction!=field.fieldDirection)
    		   field.staionPre.line.bSameDir=false;
    	   

		   
       }
       fis.close();
       isr.close();
       br.close();
//       sr.close();
    	
		}
		catch(IOException exception)
		{
			exception.printStackTrace();
		}

}
    public int getDirectionbyStations(Station s1, Station s2)
    {
    	 if(s1.fields[0]!=null&&s2==s1.fields[0].stationNext)
		   {
 		   return 2;
		   }
  	   if(s1.fields[1]!=null&&s2==s1.fields[1].stationNext)
		   {
  		 return 1;
 		   }
    	return 0;
    }
    public int getDirectionbyStations(String line, String str1, String str2)
    {
    	Station s1=this.GetStationByStationNameAndLine(str1, line);
    	Station s2=this.GetStationByStationNameAndLine(str2, line);
    	 if(s1.fields[0]!=null&&s2==s1.fields[0].stationNext)
		   {
		   return 2;
		   }
	   if(s1.fields[1]!=null&&s2==s1.fields[1].stationNext)
		   {
		 return 1;
		   }
  	return 0;
    }
    public void computeTimeofField()
    {
    	
    	
    	for(int i=0;i<this.lines.size();i++)
    	{
    		Line line=lines.get(i);
    		Station s1,s2;
    		s1=line.stations.get(0);
    		s2=line.stations.get(1);
    		int direction=this.getDirectionbyStations(s1, s2);
    		StopInfo stopinfo1,stopinfo2;
    		stopinfo1=s1.stopInfoss[2-direction].stopInfos.get(2);
    		int j;
    		do
    		{
    			stopinfo2=stopinfo1.getNextStopInfo();
    			Field field=s1.fields[2-direction];
    			field.runTime=(int)(stopinfo2.arrivalTime-stopinfo1.departureTime)+30;
    			stopinfo1=stopinfo2;
    			s1=stopinfo2.currentStation;
    			if(s1.line.lineType==0&&s1.bEnd==1)
    				break;
    			if(s1.line.lineType==1&&s1.bEnd==0)
    				break;
    			if(s1.line.lineType==2&&s1.bEnd==2)
    				break;
    		}while(true);
    		direction=3-direction;
    		if(direction==0)
    			System.out.println("direction=0");
    		if(s1.stopInfoss[2-direction].stopInfos.size()==0)
    			System.out.println("无列车"+s1.stationName+"  "+direction);
    		stopinfo1=s1.stopInfoss[2-direction].stopInfos.get(2);
       		do
    		{
    			stopinfo2=stopinfo1.getNextStopInfo();
    			Field field=s1.fields[2-direction];
    			field.runTime=(int)(stopinfo2.arrivalTime-stopinfo1.departureTime)+30;
    			stopinfo1=stopinfo2;
    			s1=stopinfo2.currentStation;
    			if(s1.line.lineType==0&&s1.bEnd==0)
    				break;
    			if(s1.line.lineType==1&&s1.bEnd==0)
    				break;
    			if(s1.line.lineType==2&&s1.bEnd==0)
    				break;
    		}while(true);
    	}
    }
    public void BreakRelease(String linename, String sStart, String sEnd)
	{
		Station s1=GetStationByStationNameAndLine(sStart, linename);
		Station s2=GetStationByStationNameAndLine(sEnd, linename);
		if(s1==null||s2==null)
			return;
		int direction=getDirectionbyStations(s1, s2);

				for( int i=0;i<s1.fields.length;i++)
		
				{
					if(s1.fields[i].direction==direction)
						{
						s1.fields[i].bValid=true;
						break;
						}
				}
	}
	public void SetBreak(String linename, String sStart, String sEnd)
	{
		Station s1=GetStationByStationNameAndLine(sStart, linename);
		Station s2=GetStationByStationNameAndLine(sEnd, linename);
		if(s1==null||s2==null)
			return;
		int direction=getDirectionbyStations(s1, s2);

				for( int i=0;i<s1.fields.length;i++)
		
				{
					if(s1.fields[i].direction==direction)
						{
						s1.fields[i].bValid=false;
						break;
						}
				}
	}
	 public void SetFieldObsRate(Station station1, Station station2,double obsrate)
	  {
		  if(station1.stationName.equals(station2.stationName))
		  {
			  TransSta transferStation=station1.transferStation;
			  if(transferStation==null)
				  return;
			  for(StaTransCon transferConnection:transferStation.stationTransferCons)
				{
				  if(transferConnection.Stationpre==station1&&transferConnection.StationNext==station2)
				  {
					  transferConnection.obsrate=obsrate;
					  return;
				  }
			  }
			  return;
		  }
		  int direction = this.getDirectionbyStations(station1, station2);
		  Field field=station1.fields[2-direction];
		  field.obsrate=obsrate;
	  }
	public void SetBreak(String linename1,String linename2,String sStart, String sEnd,int direction1,int direction2)
	{
		if(!sStart.equals(sEnd))
			return;
		Station s1=GetStationByStationNameAndLine(sStart, linename1);
		Station s2=GetStationByStationNameAndLine(sEnd, linename2);
		if(s1==null||s2==null)
			return;
		TransSta transferStation=s1.transferStation;
		  if(transferStation==null)
			  return;
		  for(StaTransCon transferConnection:transferStation.stationTransferCons)
			{
			  if(transferConnection.Stationpre==s1&&transferConnection.StationNext==s2
					  &&transferConnection.direction1==direction1
					  &&transferConnection.direction2==direction2)
			  {
				  transferConnection.bValid=false;
				  return;
			  }
		  }
	}
	  public void BreakRelease(String linename1,String linename2,String sStart, String sEnd,int direction1,int direction2)
		{
		  if(!sStart.equals(sEnd))
				return;
			Station s1=GetStationByStationNameAndLine(sStart, linename1);
			Station s2=GetStationByStationNameAndLine(sEnd, linename2);
			if(s1==null||s2==null)
				return;
			TransSta transferStation=s1.transferStation;
			  if(transferStation==null)
				  return;
			  for(StaTransCon transferConnection:transferStation.stationTransferCons)
				{
				  if(transferConnection.Stationpre==s1&&transferConnection.StationNext==s2
						  &&transferConnection.direction1==direction1
						  &&transferConnection.direction2==direction2)
				  {
					  transferConnection.bValid=true;
					  return;
				  }
			  }
		}
	  
	  public void loadLineTrainLimit(String file)
	  {
		  try{
			  FileInputStream fis = new FileInputStream(file); 
			  InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); 
			  BufferedReader br = new BufferedReader(isr); 
//			    Scanner sr = new Scanner(new FileReader(file));
	            String lineData;
	            String[] fieldData;
//	           sr = new Scanner(new FileReader(file));
	           br.readLine();
//	            sr.nextLine();
	           while((lineData = br.readLine()) != null)
	           {
//	        	   lineData=sr.nextLine();
	        	   fieldData=lineData.split("\t");
	        	   Line line=this.getLine(Integer.parseInt(fieldData[0]));
	        	   line.TrainNumlimit=Integer.parseInt(fieldData[1]);
	           }
	           fis.close();
	           isr.close();
	           br.close();
//		    	sr.close();
				}
				catch(IOException exception)
				{
					exception.printStackTrace();
				}
	  }
	  
	  /*public void loadFieldFlow(String file)
	  {
		  try{
	        	Scanner sr = new Scanner(new FileReader(file));
	            String lineData;
	            String[] fieldData;
           
	           sr = new Scanner(new FileReader(file));
	           sr.nextLine();
	           while(sr.hasNext())
	           {
	        	   lineData=sr.nextLine();
	        	   fieldData=lineData.split("\t");
	        	   int index=(Integer.parseInt(fieldData[1])-50000+36)%288;
	        	   int FieldNo=Integer.parseInt(fieldData[3]);
	        	   int flowNum=Integer.parseInt(fieldData[4]);
	        	   Field field=this.GetFieldByFieldId(FieldNo);
	        	   if(field==null)
	        		   System.out.println("field null:"+FieldNo);
	        	   field.flowsRef[index]=flowNum;
	           }
		    	sr.close();
		    	
				}
				catch(IOException exception)
				{
					exception.printStackTrace();
				}

	  }*/
	  
	  public void loadLineinterval(String file)
	  {
		  try{
	        	Scanner sr = new Scanner(new FileReader(file));
	            String lineData;
	            String[] fieldData;
           
	           sr = new Scanner(new FileReader(file));
	           sr.nextLine();
	           int ref=6*12+6;
	           while(sr.hasNext())
	           {
	        	   lineData=sr.nextLine();
	        	   fieldData=lineData.split("\t");
	        	   Line line=this.getLine(Integer.parseInt(fieldData[0]));
	        	   int direction=Integer.parseInt(fieldData[1]);
	        	   for(int i=2;i<8;i++)
	        	   {
	        		   double frequency=Double.parseDouble(fieldData[i]);
	        		   for(int j=0;j<6;j++)
	        		   {
	        			   int timeindex=ref+j*(i-2);
	        			   line.lineTrainInterval[2-direction][timeindex]=frequency;
	        		   }
	        	   }
	           }
		    	sr.close();
		    	
				}
				catch(IOException exception)
				{
					exception.printStackTrace();
				}

	  }
	  
	 /* public void computertrainsIntervals()
	  {
		  for(int i=0;i<this.stations.length;i++)
		  {
			  Station s=stations[i];
			  for(int j=0;j<2;j++)
			  {
				  for(int k=0;k<s.stopInfoss[j].stopInfos.size();k++)
				  {
					  StopInfo stopinfo=s.stopInfoss[j].stopInfos.get(k);
					  int index=(int)(stopinfo.departureTime/300)%288;
					  int direction=stopinfo.trainInfo.direction;
					  Field field=stopinfo.currentStation.fields[2-direction]; 
					  if(field==null)
						  continue;
					  field.trainsIntervals[index]++;
					  
				  }
			  }
		  }
	  }*/
	  
	 /* public void computerFieldCongestRef()
	  {
		  for(int i=0;i<this.stations.length;i++)
		  {
			  Station station = this.stations[i];
			  Line line=station.line;
				for(int j = 0 ; j < 2 ; j++)
				{
					Field field= station.fields[j];
					if(field == null)
						continue;
					for(int k = 0 ; k < 24 * 12 ; k++)
					{
						
						field.congestionRefs[k]=(double)field.flowsRef[k]/(line.lineTrainInterval[j][k]*line.TrainNumlimit);

					}
					for(int k = 0 ; k < 24 * 12 ; k++)
					{
						int refk=k-1;
						if(refk<0)
							refk=24 * 12-1;
						field.congestions[k]=(field.congestionRefs[refk]+field.congestionRefs[k]+field.congestionRefs[(k+1)%(24 * 12)])/3;
					}
				}
		  }
	  }*/
	  
	  /*public void computerFieldCongest()
	  {
		  for(int i=0;i<this.stations.length;i++)
		  {
			  Station station = this.stations[i];
			  Line line=station.line;
				for(int j = 0 ; j < 2 ; j++)
				{
					Field field= station.fields[j];
					if(field == null)
						continue;
					for(int k = 0 ; k < 24 * 12 ; k++)
					{
						if(field.flowsRef[k]!=0&&field.trainsIntervals[k]==0)
						{
							System.out.println(k+"无列车");
							continue;
						}
						field.congestionRefs[k]=(double)field.flowsRef[k]/(field.trainsIntervals[k]*line.TrainNumlimit);

					}
					for(int k = 0 ; k < 24 * 12 ; k++)
					{
						int refk=k-1;
						if(refk<0)
							refk=24 * 12-1;
						field.congestions[k]=(field.congestionRefs[refk]+field.congestionRefs[k]+field.congestionRefs[(k+1)%(24 * 12)])/3;
					}
				}
		  }
	  }
	  */
	
	  public Line getLine(String lineName)
	  {
		  Line line=null;
		  for(int i=0;i<this.lines.size();i++)
		  {
			  line=this.lines.get(i);
			  if(line.lineName.equals(lineName))
				  break;
		  }
		  return line;
	  }
	  public Line getLine(int lineID)
	  {
		  Line line=null;
		  for(int i=0;i<this.lines.size();i++)
		  {
			  line=this.lines.get(i);
			  if(line.lineNo==lineID)
				  break;
		  }
		  return line;
	  }
	  public double computeCongestRef(Field field, long time, int type)
	  {
		  double congestRef=1;
		  int index=(int)(time/300);
		 double cRef=field.congestions[index];
		 if(type==3)
		{
			 if (cRef<1.2)
				 cRef=0;
			 else
				 cRef=1.2;
		}
		 congestRef=1+refCongest[type-1][0]*Math.pow(cRef,refCongest[type-1][1]);
		  return congestRef;
	  }
	  
	  public double computeHighCongest(Field field, long time, int type)
	  {
		  double congestRef=1;
		  int index=(int)(time/300);
		  if(field.congestions[index]>highcongetRef)
			  congestRef=obsrate;

		  return congestRef;
	  }
	  
	
	
	
	
	  /*public void writeFieldCongest( )
	  {
			try{
				String filePath="Data\\FieldCongest.txt";
				FileWriter fileWriter=new FileWriter(filePath);
				fileWriter.write("时间索引\t区间编号\t平均拥挤度\t拥挤度\t断面客流量\t列车定员\n");
				String FieldID = "";
				String stationname ="";
				int timeindex=0;
				int passengerNum=0;
				for(int k = 0 ; k < 24 * 12 ; k++){
				for(int i = 0 ; i < stations.length ; i++)
				{
					Station station = stations[i];
					timeindex=k;
					int Trainlimit=station.line.TrainNumlimit;
					double congest[]={0,0};
					for(int j = 0 ; j < 2 ; j++){
						Field field= station.fields[j];
						
						
						if(field == null){
							
							continue;
						}
						congest[0] = field.congestions[k];
						congest[1] = field.congestionRefs[k];
						passengerNum=field.flowsRef[k];
						
							
						fileWriter.write(timeindex+"\t"+field.fieldNo+"\t"+congest[0]+"\t"+congest[1]+"\t"+passengerNum+"\t"+Trainlimit+"\n");
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
	  }*/
}






