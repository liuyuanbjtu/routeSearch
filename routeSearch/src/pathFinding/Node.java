package pathFinding;
import netWork.*;
public class Node implements Cloneable {
	
	public Station station=null;
	public Node nodeFather=null;
	public long costToFather=0;
	public short relationFather=0;
	public double costInfos[];
	public int direction=0;
	public StopInfo stopInfo=null;
	public int routeIndex=0;
	
	public long arriveTime=0;
	public long departureTime=0;
	public Node()
	{
		costInfos=new double[4];
		costInfos[1]=0;
		costInfos[0]=costInfos[2]=0;
		costInfos[3]=0;
	}
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

}
