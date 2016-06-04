package pathFinding;
import java.util.*;

import netWork.*;

public class Route  implements Cloneable{
	

	public List<Node> nodes = new ArrayList<Node>();
//	public List<Field> fields = new ArrayList<Field>();
//	public TransSta transferStations = new TransSta();
	
	public double costInfos[];


	public ODInfo ODObj=null;
	public short staionNum=0;

    public long time=0;
    public long latestTime=0;
    
    public double[] waitTime = new double[24*12];
    
    //行程总时间
    public double travelTime = 0;
    
    //突发事件下的行程总时间
    public double travelTimeBreak=0;
	
    //存放路径满意度指数
//    public double costSatisfaction[][]=new double[3][24*12];
    public double costSatisfaction[][];
//    public double costSatisfaction_in[]=new double[24*12];
//    public double costSatisfaction_tran[]=new double[24*12];
//    public double costSatisfaction_con[]=new double[24*12];
	
	
	public Route()
	{
		costInfos=new double[4];
		costInfos[1]=0;
		costInfos[0]=costInfos[3]=0;
	}
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
	
	
}


