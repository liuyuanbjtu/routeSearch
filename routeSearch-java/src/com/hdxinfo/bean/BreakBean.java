package com.hdxinfo.bean;

import com.hdxinfo.bean.Station;
/**
 *���ܣ��ж����Bean
 *�����ˣ�ţ�޶�
 *�������ڣ�2014-02-14
 */
public class BreakBean {
	private Station sStation;//中断区间的起点站
	private Station eStation;//中断区间的终点站
	private String eTime;//中断结束时间
	private String lineName;//线路名称
	private String breakNo;//编号
	private String breakStaStr;//中断车站名称
	private String lastTime;//持续时间
	private String sTime;//中断起始时间
	
	public BreakBean(){}

	public Station getSStation() {
		return sStation;
	}

	public void setSStation(Station station) {
		sStation = station;
	}

	public Station getEStation() {
		return eStation;
	}

	public void setEStation(Station station) {
		eStation = station;
	}

	public String getETime() {
		return eTime;
	}

	public void setETime(String time) {
		eTime = time;
	}

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public String getBreakNo() {
		return breakNo;
	}

	public void setBreakNo(String breakNo) {
		this.breakNo = breakNo;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getBreakStaStr() {
		return breakStaStr;
	}

	public void setBreakStaStr(String breakStaStr) {
		this.breakStaStr = breakStaStr;
	}

	public String getsTime() {
		return sTime;
	}

	public void setsTime(String sTime) {
		this.sTime = sTime;
	}
	
	
}
