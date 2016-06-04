package pathFinding;
import java.util.*;

import netWork.*;

public class ODInfo {
	public Station stationOrigin=null;
	public Station stationDestination=null;
	public long latestTime=0;
	public long latestTimeODS=0;
	public int fastTimeCost=0;
	public double LatestRef[]=new double[3];
	public double costInfos[];
	public double costRef[];
	public NodeT nodeTRef[]=new NodeT[2];
	public List<NodeLatestRoute> latestRoutes=null;
	public Route latestPaths []=null;
	public List<NodeLatestRoute> TempNodeList=new LinkedList<NodeLatestRoute>();
	public  Route shortestRoutesTemp[];

	
	
	
	public Routes ReasonableRoutes[]=new Routes[3];
	public Routes ReasonableRoutesRef[]=new Routes[3];
	public Routes AccessRoutes=new Routes();
	public Route AccessRoutesK[][];
	public Route KRoutes[][];
	
	public Boolean bwrite=false;
	public ODInfo()
	{
		costInfos=new double[3];
		costInfos[1]=0;
		costInfos[0]=costInfos[2]=Double.MAX_EXPONENT;
		costRef=new double[3];
		costRef[1]=0;
		costRef[0]=costRef[2]=Double.MAX_EXPONENT;
		shortestRoutesTemp=new Route[3];
		for(int i=0;i<3;i++)
		{shortestRoutesTemp[i]=new Route();
		LatestRef[i]=0;
		}
		for(int i=0;i<3;i++)
		{
			ReasonableRoutes[i]=new Routes();
		ReasonableRoutesRef[i]=new Routes();
		
		}


		
	}
	public void addReasonableRoute(int type, Route route, int bChanged)
	{
		ListIterator<Route> iter1=null;
		ListIterator<Node> iter2=null;
		ListIterator<Node> iter3=route.nodes.listIterator();
		Route routeRef=null;
		Node node=null;
		Node nodeRef=null;
		int b=1;

			
		if(bChanged==0)
			iter1=ReasonableRoutesRef[type].routes.listIterator();
		if(bChanged==1)
			iter1=ReasonableRoutes[type].routes.listIterator();
		
		while(iter1.hasNext())
		{
			
			routeRef=iter1.next();
			if(type!=1)
			{
			if(routeRef.costInfos[type]>route.costInfos[type])
				{
				iter1.previous();
				iter1.add(route);
				return;
				}
			}
			if(type==1)
			{
			if(routeRef.costInfos[0]>route.costInfos[0])
				{
				iter1.previous();
				iter1.add(route);
				return;
				}
			}


		}
		
		iter1.add(route);
	
	}
	public void addAccessRoute( Route route)
	{
		ListIterator<Route> iter1=null;
		ListIterator<Node> iter2=null;
		ListIterator<Node> iter3=route.nodes.listIterator();
		Route routeRef=null;
		Node node=null;
		Node nodeRef=null;
		int b=1;

			
			iter1=this.AccessRoutes.routes.listIterator();

		iter1.add(route);
		return;
	
	}

}
