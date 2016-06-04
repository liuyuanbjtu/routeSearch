package pathFinding;

public class Compare {

	public Compare() {
		
	}
	public int routeEqual(Route route1, Route route2)
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
    public int compareCost(int type,Node nodeOld, Node nodeNew)
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
    public int compareCostInfo(int type,double costInfo1[], double costInfo2[])
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
}
