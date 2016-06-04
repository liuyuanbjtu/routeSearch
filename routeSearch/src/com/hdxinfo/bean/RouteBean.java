package com.hdxinfo.bean;

import java.util.List;

public class RouteBean {
	private String routeStr;
	private List<String> lineArr;
	private List<String> transferArr;
	private List<String> allStationArr;
	
	private double totalTime;
	private double waitTime;
	//存放路径满意度指数
    private double costSatisfaction;
    //路径满意度指数数组
    private double[]  costSatArray = new double[24*12];
    
	
	public RouteBean() {
		
	}
	
	public String getRouteStr() {
		return routeStr;
	}

	public void setRouteStr(String routeStr) {
		this.routeStr = routeStr;
	}

	public List<String> getLineArr() {
		return lineArr;
	}

	public void setLineArr(List<String> lineArr) {
		this.lineArr = lineArr;
	}

	public List<String> getTransferArr() {
		return transferArr;
	}

	public void setTransferArr(List<String> transferArr) {
		this.transferArr = transferArr;
	}

	public List<String> getAllStationArr() {
		return allStationArr;
	}

	public void setAllStationArr(List<String> allStationArr) {
		this.allStationArr = allStationArr;
	}

	public double getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}

	public double getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(double waitTime) {
		this.waitTime = waitTime;
	}
	
	public double getCostSatisfaction() {
		return costSatisfaction;
	}
	
	public void setCostSatisfaction(double costSatisfaction) {
		this.costSatisfaction = costSatisfaction;
	}
	
	public double[] getCostSatArray() {
		return costSatArray;
	}
	
	public void setCostSatArray(double[][] costSat,int p) {
		this.costSatArray = costSat[p];
	}
}
