package com.mr.datagather.bean;

public class Rate {
	// {"orgname":"余额宝","rate":"4.1060","type":1},
	private long time;   //获取数据时间
	private int order;  //获取数据序号
	private String code;//理财产品代码
	private String name;//理财产品名称
	private String rate;//利率
	private int type;   //1  活期保底   2定期保底 3活期高收益  4定期高收益
	private String profitAccount;//理财收益
	private String lockups;//锁定期
	private String startAccount;//起投金额
	private String manager;//管理人
	private String trusteeship;//托管人
	private String riskrank;//风险等级
	private String intestes;//计利方式
	private String status;//计利方式

	public String getStatus() {return status;}

	public void setStatus(String status) {this.status = status;}

	public String getManager() {return manager;}

	public void setManager(String manager) {this.manager = manager;}

	public String getTrusteeship() {return trusteeship;}

	public void setTrusteeship(String trusteeship) {this.trusteeship = trusteeship;}

	public String getRiskrank() {return riskrank;}

	public void setRiskrank(String riskrank) {this.riskrank = riskrank;}

	public String getIntestes() {return intestes;}

	public void setIntestes(String intestes) {this.intestes = intestes;}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {this.time = time;}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getProfitAccount() {
		return profitAccount;
	}

	public void setProfitAccount(String profitAccount) {
		this.profitAccount = profitAccount;
	}

	public String getLockups() {
		return lockups;
	}

	public void setLockups(String lockups) {
		this.lockups = lockups;
	}

	public String getStartAccount() {
		return startAccount;
	}

	public void setStartAccount(String startAccount) {
		this.startAccount = startAccount;
	}




	
}
