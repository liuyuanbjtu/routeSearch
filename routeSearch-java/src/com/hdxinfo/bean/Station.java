package com.hdxinfo.bean;

public class Station {
	private int odNo = 0;// 序号，唯一
	private int stationNo = 0;// 车站编号
	private Line line = null;// 线路对象
	private short type = 0;// 0非换乘站1换乘站
	private short bEnd = 9;// 车站在线路的位置
	private short bCount = 0;//
	private String stationName = "";// 车站名称
	private String lineName = "";//线路名称

	public int getOdNo() {
		return odNo;
	}

	public void setOdNo(int odNo) {
		this.odNo = odNo;
	}

	public int getStationNo() {
		return stationNo;
	}

	public void setStationNo(int stationNo) {
		this.stationNo = stationNo;
	}

	public Line getLine() {
		return line;
	}

	public void setLine(Line line) {
		this.line = line;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public short getBEnd() {
		return bEnd;
	}

	public void setBEnd(short end) {
		bEnd = end;
	}

	public short getBCount() {
		return bCount;
	}

	public void setBCount(short count) {
		bCount = count;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	@Override
	public boolean equals(Object obj) {
		final Station s = (Station) obj;
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof Station))
			return false;
		if(!(getStationName().equals(s.getStationName())))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return getStationName().hashCode();
	}
}
