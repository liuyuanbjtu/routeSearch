package pathFinding;

import java.util.*;


public class NodeLatestRoute  implements Cloneable {
	public List<NodeT> Nodes=new ArrayList<NodeT>();
	public Boolean bReasonabl=true;
	public int iTransfer=0;
		public Object clone() throws CloneNotSupportedException
		{
			return super.clone();
		}

}
