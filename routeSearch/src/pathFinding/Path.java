package pathFinding;

public class Path {
	public Route route=null;
	public NodePath nodepaths[]=null;
	public double costInfos[];
	public Path() {
		
		costInfos=new double[3];
		costInfos[1]=0;
		costInfos[0]=costInfos[2]=Double.MAX_EXPONENT;
	}

}
