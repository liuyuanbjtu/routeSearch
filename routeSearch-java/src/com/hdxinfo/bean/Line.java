package com.hdxinfo.bean;

public class Line {
	private int lineNo;// ��·���
	private String lineName;// ����
	private int fee = 2;// ���ѵ�Ǯֵ

	public int getLineNo() {
		return lineNo;
	}

	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public int getFee() {
		return fee;
	}

	public void setFee(int fee) {
		this.fee = fee;
	}

}
