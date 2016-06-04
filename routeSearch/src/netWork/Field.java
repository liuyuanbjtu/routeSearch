package netWork;

public class Field {
	public Station staionPre=null;
	public Station stationNext=null;
	public int fieldNo=0;
	public String fieldNoStr="";
	public int lineNo=0;
	public short fieldDirection=0;
	public float length=0;
	public int runTime=0;
	public int fee=2;
	public double obsrate=1;
	public short direction=0;
	public boolean bValid=true;
	public double congestions[]=new double[24*12];//分时拥挤度 三个连续的平均值【暂时不做修改】
	
//	public double trainsIntervals[][]=new double[3][24*12];//分时发车频率 （对数）
//	public int flowsRef[][]=new int[3][24*12];//分时断面客流量
	public double congestionRefs[][]=new double[3][24*12];//分时最大满载率
	public int tranInWaittime[][]=new int[3][24*12];//分时换乘候车时间
	public int getOnWaittime[][]=new int[3][24*12];//分时进站候车时间
	
	public int getOnTime[][]=new int[3][24*12];//分时进站上车集散时间
	public int tranInTime[][]=new int[3][24*12];//分时换乘上车集散时间
	
	public Field()
	{
		for(int i=0;i<3;i++)
			for(int j=0;j<24*12;j++)
		{
			congestionRefs[i][j]=0;
//			congestions[i]=0;
//			trainsIntervals[i]=0;
//			flowsRef[i]=0;
			tranInWaittime[i][j]=0;
			getOnWaittime[i][j]=0;
			getOnTime[i][j]=0;
			tranInTime[i][j]=0;
		}
	}
}
