package com.hdxinfo.bean;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

public class RouteBean {
	private String routeStr;
	private List<String> lineArr;
	private List<String> transferArr;
	private List<String> allStationArr;
	
	private double totalTime;
	private double waitTime;
	//存放路径满意度指数
    private double costSatisfaction;
    private double temp=0;
    //路径满意度指数数组
    private double[][]  costSatArray = new double[3][24*12];
    private double[][]  runSat = new double[3][24*12];
   // private List<Double> costSatArray = new ArrayList<Double>();
  //  private List<List<Double>> costSatArray = new ArrayList<List<Double>>();
    //private List<List<Double>> costSatArray = new ArrayList<List<Double>>();
	
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
	

	/*public double[] getCostSatArray() {
		return costSatArray;
	}


	public void setCostSatArray(double[] costSatArray) {
		
			
			this.costSatArray=costSatArray;
		
	}*/
	public double[][] getCostSatArray() {
		return costSatArray;
	}


	public void setCostSatArray(double[][] costSatArray) {
		
			
			this.costSatArray=costSatArray;
		
	}
	
	public double[][] getRunSat() {
		return runSat;
	}
	
	public void setRunSat(double[][] runSat) {
		this.runSat = runSat;
	}
	
	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}
	
	
}
