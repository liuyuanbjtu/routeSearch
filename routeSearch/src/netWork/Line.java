package netWork;

import java.util.*;

public class Line {
	public int numInnetwork;
	public int lineNo;
	public String lineName;
	public List<Station> stations=new ArrayList<Station>();
	public int lineType=0;
	public int TrainNumlimit;
	public int fee=2;
	public boolean bSameDir=true;
	public double lineTrainInterval[][]=new double[2][24*12];
	public Line()
	{
		for(int j=0;j<2;j++)
		{for(int i=0;i<24*12;i++)
		{
			lineTrainInterval[j][i]=1;
		}
		}
	}
}
