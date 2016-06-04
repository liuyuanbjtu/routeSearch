package netWork;


public interface StopInfoIF {
	
	   public abstract void setnextStopInfo(StopInfo nextStopInfos);
	   public abstract StopInfo getnextStopInfo();
	   public abstract void setpreviousStopInfo(StopInfo previousStopInfos);
	   public abstract StopInfo getpreviousStopInfo();
	   public abstract void setcurrentStation(Station currentStations);
	   public abstract Station getcurrentStation();
	   public abstract void setdepartureTime(long departureTimes);
	   public abstract long getdepartureTime();
	   public abstract void setarrivalTime(long arrivalTimes);
	   public abstract long getarrivalTime();
	   public abstract void setTrainInfo(TrainInfo trainInfos);
	   public abstract TrainInfo getTrainInfo();
	   public abstract StopInfo[] getValidPreviousStopInfo();
	   public abstract StopInfo getValidPreviousStopInfo(Station s);
	   public abstract StopInfo getValidNextStopInfo(Station s);
}
