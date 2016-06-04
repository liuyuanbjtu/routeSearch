package pathFinding;
import netWork.*;
public class NodeT implements Cloneable {
	public Station station=null;
	public NodeT nodeFather=null;
	public int relationFather=0;
	
	
	public int direction=0;
	public StopInfo stopInfo=null;
	public long time=0;
	public double costInfos[];
	
  public NodeT()
  {
	  costInfos=new double[4];
		costInfos[1]=0;
		costInfos[0]=costInfos[2]=0;
		costInfos[3]=2;
  }
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

}
