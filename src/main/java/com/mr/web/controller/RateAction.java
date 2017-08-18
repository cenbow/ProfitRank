package com.mr.web.controller;

import com.mr.datagather.bean.CurrentCapitalRecorder;
import com.mr.datagather.bean.Recorder;
import com.mr.util.DateUtil;
import com.mr.web.BaseController;
import org.apache.ibatis.javassist.bytecode.stackmap.TypeData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;


@Controller
@RequestMapping("/rate")
public class RateAction extends BaseController {

    //变量声明
    private static String driver;
    private static String url;
    private static String user;
    private static String password;

	static{
		Properties prop = new Properties();
		try {
			prop.load(TypeData.ClassName.class.getClassLoader().getResourceAsStream("jdbc.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		driver = prop.getProperty("driver");
		url = prop.getProperty("url");
		user = prop.getProperty("username");
		password = prop.getProperty("password");
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	/************ 创建锁对象 ************/
	final Object lock = new Object();
	/**
	 * 设置一个共公的对象来和小小程序交换数据，提高相应速度
	 */
	List<Recorder> valueList = new ArrayList<Recorder>();
	List<Recorder> list5 = new ArrayList<Recorder>();
	List<Recorder> list_my1 = new ArrayList<Recorder>();
	List<CurrentCapitalRecorder> list2 = new ArrayList<CurrentCapitalRecorder>();
	List<CurrentCapitalRecorder> list3 = new ArrayList<CurrentCapitalRecorder>();
	List<CurrentCapitalRecorder> list4 = new ArrayList<CurrentCapitalRecorder>();
	List<CurrentCapitalRecorder> list6 = new ArrayList<CurrentCapitalRecorder>();
	List<CurrentCapitalRecorder> list7 = new ArrayList<CurrentCapitalRecorder>();
	List<CurrentCapitalRecorder> list8 = new ArrayList<CurrentCapitalRecorder>();
	List<CurrentCapitalRecorder> list9 = new ArrayList<CurrentCapitalRecorder>();
	List<CurrentCapitalRecorder> list_my2 = new ArrayList<CurrentCapitalRecorder>();
	List<CurrentCapitalRecorder> list_my3 = new ArrayList<CurrentCapitalRecorder>();
	List<CurrentCapitalRecorder> list_my4 = new ArrayList<CurrentCapitalRecorder>();

	@RequestMapping(value = "/queryratelist", method = RequestMethod.GET)
	public void queryRateList(HttpServletResponse response) {
		// 输出到客户端
		this.queryData();//每次获取就会调用方法，所以速度就满了，解决：定时任务最后加一个公共的排序好的Json，直接的回传就好了。
		try {
			outJson(response, valueList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/getCurrentCapitalData", method = RequestMethod.GET)
	public void getCurrentCapitalData(HttpServletResponse response) {
		this.queryList2();
		try {
			outJson(response, list2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/getRegularFloatData", method = RequestMethod.GET)
	public void getRegularFloatData(HttpServletResponse response) {
		this.queryList3();
		try {
			outJson(response, list3);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/getRegularCapitalData", method = RequestMethod.GET)
	public void getRegularCapitalData(HttpServletResponse response) {
		this.queryList4();
		try {
			outJson(response, list4);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/queryProductions", method = RequestMethod.GET)
	public void queryProductions(HttpServletRequest request ,HttpServletResponse response) throws UnsupportedEncodingException {
		this.queryList5(request);
		try {
			outJson(response, list5);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/queryProductions2", method = RequestMethod.GET)
	public void queryProductions2(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		this.queryList6(request);
		try {
			outJson(response, list6);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/queryProductions3", method = RequestMethod.GET)
	public void queryProductions3(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		this.queryList7(request);
		try {
			outJson(response, list7);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/queryProductions4", method = RequestMethod.GET)
	public void queryProductions4(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		this.queryList8(request);
		try {
			outJson(response, list8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value="/getProfit", method=RequestMethod.GET)//计算收益值

	public void getProfit(HttpServletRequest request, HttpServletResponse response) {
		//得到传过来的数据
		String startDate= request.getParameter("startDate");
		String endDate= request.getParameter("endDate");
		String account= request.getParameter("investAccount");
		String id=request.getParameter("id");
		String interest=request.getParameter("interest");
		String profit=request.getParameter("profit");

		Calendar c = Calendar.getInstance();
		String now =new SimpleDateFormat( "yyyy-MM-dd").format(c.getTime());
		c.add(Calendar.DATE,   -1);
		String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(c.getTime());
		c.setTime(Date.valueOf(yesterday));
		long time1 = c.getTimeInMillis();

		c.setTime(Date.valueOf(startDate));
		long time3 = c.getTimeInMillis();

		c.setTime(Date.valueOf(endDate));
		long time2 = c.getTimeInMillis();



		long between_days=(time2-time1)/(1000*3600*24);//结束日期-昨天
		long days=(time2-time3)/(1000*3600*24);//结束日期-开始日期


		//查询数据库,
		if(Date.valueOf(yesterday).before(Date.valueOf(startDate))){// 今天在开始日期之前，预估值  昨天<开始日期  今天<=开始日

			if("1".equals(interest)) {
				Double profitSum=1d;
				profitSum = Math.pow((1 +Double.parseDouble(profit)/10000) ,days);

				//预期值
				String expect = String.format("%.4f", Double.parseDouble(account) * (profitSum - 1));

				// 输出到客户端
				try {
					outJson(response,  expect);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				Double Sum=0d;
				//预期值
				String expect = String.format("%.4f", Double.parseDouble(account)*(Double.parseDouble(profit)/10000)*days);

				// 输出到客户端
				try {
					outJson(response,  expect);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}else if(Date.valueOf(endDate).after(Date.valueOf(yesterday))){ //今天在开始日后，截至日前  精确值+预期值  开始日<昨天<截至日
			String sql="select * from mr_currentFinancialProducts where date >'"+startDate+"' and date<='"+yesterday+"' and  productId='"+id+"'";
			ArrayList recorders=selectRecorder(sql);
			Double profitSum=1d;
			Double sum=0d;
			if("1".equals(interest)){
				for(int i=0;i<recorders.size();i++){
					profitSum= profitSum*(1+Double.parseDouble((String)recorders.get(i))/10000);
				}
				//精确值
				String accurate=String.format("%.4f",Double.parseDouble(account)*(profitSum-1));
				//预期值
				String expectValue=String.format("%.4f",(Math.pow((1+Double.parseDouble(profit)/10000),between_days)-1)*Double.parseDouble(account));
				//sum
				Double Sum=Double.parseDouble(account)*(profitSum-1)+(Math.pow((1+Double.parseDouble(profit)/10000),between_days)-1)*Double.parseDouble(account);

				// 输出到客户端
				try {
					outJson(response, accurate+","+expectValue+","+ String.format("%.4f",Sum));
					//outJson(response, "12345");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{  //今天在 预期值
				for(int i=0;i<recorders.size();i++){
					sum=sum+Double.parseDouble((String)recorders.get(i));
				}
				//精确值
				String accurate=String.format("%.4f",sum*Double.parseDouble(account)/10000);
				//预期值
				String expectValue=String.format("%.4f",Double.parseDouble(profit)*between_days*Double.parseDouble(account));
				//sum
				String Sum=String.format("%.4f",sum*Double.parseDouble(account)/10000+Double.parseDouble(profit)*between_days*Double.parseDouble(account));

				// 输出到客户端
				try {
					outJson(response, accurate+","+expectValue+","+ String.format("%.4f",Sum));
					//outJson(response, "6789");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}else{
			String sql="select * from mr_currentFinancialProducts where date >'"+startDate+"' and date<='"+endDate+"' and  productId='"+id+"'";
			ArrayList recorders=selectRecorder(sql);
			Double profitSum=1d;
			Double sum=0d;
			if("1".equals(interest)){
				for(int i=0;i<recorders.size();i++){
					profitSum= profitSum*(1+Double.parseDouble((String)recorders.get(i))/10000);
				}
				//精确值
				String accurate=String.format("%.4f",Double.parseDouble(account)*(profitSum-1));
				// 输出到客户端
				try {
					outJson(response, accurate);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				for(int i=0;i<recorders.size();i++){
					sum=sum+Double.parseDouble((String)recorders.get(i));
				}
				String accurate=String.format("%.4f",sum*Double.parseDouble(account)/10000);
				// 输出到客户端
				try {
					outJson(response, accurate);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	@RequestMapping(value = "/updateUserLogin", method = RequestMethod.GET)
	public void updateUserLogin(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		int res=this.loginInfo(request);//每次获取就会调用方法，所以速度就满了，解决：定时任务最后加一个公共的排序好的Json，直接的回传就好了。
		try {
			outJson(response, res);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/queryMyList", method = RequestMethod.GET)
	public void queryMyList(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		this.queryMyFavorite(request);//查询用户收藏信息
		try {
			outJson(response, list_my1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/queryMyList2", method = RequestMethod.GET)
	public void queryMyList2(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		this.queryMyFavorite2(request);//查询用户收藏信息
		try {
			outJson(response, list_my2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/queryMyList3", method = RequestMethod.GET)
	public void queryMyList3(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		this.queryMyFavorite3(request);//查询用户收藏信息
		try {
			outJson(response, list_my3);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/queryMyList4", method = RequestMethod.GET)
	public void queryMyList4(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		this.queryMyFavorite4(request);//查询用户收藏信息
		try {
			outJson(response, list_my4);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/saveChecked", method = RequestMethod.GET)
	public void saveChecked(HttpServletRequest request, HttpServletResponse response) throws SQLException, UnsupportedEncodingException {
		this.favorite(request);//保存用户的选择
		try {
			outJson(response, "1");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/saveChecked2", method = RequestMethod.GET)
	public void saveChecked2(HttpServletRequest request, HttpServletResponse response) throws SQLException, UnsupportedEncodingException {
		this.favorite2(request);//保存用户的选择
		try {
			outJson(response, "1");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/saveChecked3", method = RequestMethod.GET)
	public void saveChecked3(HttpServletRequest request, HttpServletResponse response) throws SQLException, UnsupportedEncodingException {
		this.favorite3(request);//保存用户的选择
		try {
			outJson(response, "1");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/saveChecked4", method = RequestMethod.GET)
	public void saveChecked4(HttpServletRequest request, HttpServletResponse response) throws SQLException, UnsupportedEncodingException {
		this.favorite4(request);//保存用户的选择
		try {
			outJson(response, "1");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/getRegularFloatData30Days", method = RequestMethod.GET)
	public void getRegularFloatData30Days(HttpServletRequest request, HttpServletResponse response) throws SQLException, UnsupportedEncodingException {
		String now=DateUtil.todayFormate("yyyy-MM-dd");
		ArrayList<CurrentCapitalRecorder> list=null;
		String sql30Days="select * from (select * from mr_regular where  profitType like '非保本%' and `limit`<=30 and '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
//		String sql90days="select * from (select * from mr_regular where  profitType like '非保本%' and `limit`>30 and `limit`<=90 and '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
//		String sql180days="select * from (select * from mr_regular where  profitType like '非保本%' and `limit`>90 and `limit`<=180 and '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
//		String sql="select * from (select * from mr_regular where  profitType like '非保本%' and `limit`>180  and '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
		list= queryDBData(sql30Days);
		list3.clear();
		list3=list;
		try {
			outJson(response, list3);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/getRegularFloatData90Days", method = RequestMethod.GET)
	public void getRegularFloatData90Days(HttpServletRequest request, HttpServletResponse response) throws SQLException, UnsupportedEncodingException {
		String now=DateUtil.todayFormate("yyyy-MM-dd");
		ArrayList<CurrentCapitalRecorder> list=null;
		String sql90days="select * from (select * from mr_regular where  profitType like '非保本%' and `limit`>30 and `limit`<=90 and '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
//		String sql180days="select * from (select * from mr_regular where  profitType like '非保本%' and `limit`>90 and `limit`<=180 and '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
//		String sql="select * from (select * from mr_regular where  profitType like '非保本%' and `limit`>180  and '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
		list= queryDBData(sql90days);
		list3.clear();
		list3=list;
		try {
			outJson(response, list3);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/getRegularFloatData180Days", method = RequestMethod.GET)
	public void getRegularFloatData180Days(HttpServletRequest request, HttpServletResponse response) throws SQLException, UnsupportedEncodingException {
		String now=DateUtil.todayFormate("yyyy-MM-dd");
		ArrayList<CurrentCapitalRecorder> list=null;
		String sql180days="select * from (select * from mr_regular where  profitType like '非保本%' and `limit`>90 and `limit`<=180 and '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
//		String sql="select * from (select * from mr_regular where  profitType like '非保本%' and `limit`>180  and '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
		list= queryDBData(sql180days);
		list3.clear();
		list3=list;
		try {
			outJson(response, list3);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/getRegularFloatData180DaysMore", method = RequestMethod.GET)
	public void getRegularFloatData180DaysMore(HttpServletRequest request, HttpServletResponse response) throws SQLException, UnsupportedEncodingException {
		String now=DateUtil.todayFormate("yyyy-MM-dd");
		ArrayList<CurrentCapitalRecorder> list=null;
		String sql="select * from (select * from mr_regular where  profitType like '非保本%' and `limit`>180  and '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
		list= queryDBData(sql);
		list3.clear();
		list3=list;
		try {
			outJson(response, list3);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void queryData(){//'124','127','130','132','133','134','135','136','138','141','146','148','149','151','153','156','157','161','162','164','165','166','169','171','192' 比较这些知名企业，124（余额宝如果没派上，就放到第十的位置）
		Connection conn = null;
		PreparedStatement pst = null;
		ArrayList<Recorder> list=new ArrayList<Recorder> ();
		try {
			conn = DriverManager.getConnection(url, user, password);//获取连接
			Calendar cal   =   Calendar.getInstance();
			cal.add(Calendar.DATE,   -1);
			String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());
//			String fields="productId,productName,type,rate,profit,startAccount,auditFlag,enterpriseName,takeSpeedStr,takeSpeed,takeLimit,weight,date,capitalScale,interest,riskRank,investmentMoney";
//			String filter="(productId ='124'or productId ='127'or productId ='130' or productId ='132'or productId ='133'or productId ='134'or productId ='135' or productId ='136' or productId ='138'or productId ='141' or productId ='146' or productId ='148' or productId ='149' or productId ='151' or productId ='153'or productId ='156' or productId ='157' or productId ='161' or productId ='162' or  productId ='164' or productId ='165' or productId ='166' or productId ='169')";

			String fields="productId,productName,type,rate,profit,startAccount,auditFlag,enterpriseName,takeSpeedStr,takeSpeed,takeLimit,weight,date,capitalScale,interest,riskRank,investmentMoney,enterpriseCode,enterpriceAnomal,legalRepresentative,enterpriseType,registrationCapital,registrationStatus,registrationDate,operateToDate";
			String fields2="a.productId,a.productName,a.type,a.rate,a.profit,a.startAccount,a.auditFlag,a.enterpriseName,a.takeSpeedStr,a.takeSpeed,a.takeLimit,a.weight,a.date,a.capitalScale,a.interest,a.riskRank,a.investmentMoney,c.enterpriseCode,c.enterpriceAnomal,c.legalRepresentative,c.enterpriseType,c.registrationCapital,c.registrationStatus,c.registrationDate,c.operateToDate";
			String filter="a.productId = b.productCode AND b.enterpriseCode = c.enterpriseCode AND a.date = '"+yesterday+"'  AND c.enterpriceAnomal = '0'  and (takeSpeed ='1' or takeSpeed ='2')";

			String sql="select "+fields+"  from (select "+fields2+" from mr_currentFinancialProducts a,mr_enterpriseProduct b,mr_enterprise c where "+filter+" ) temp order by temp.profit desc,temp.productId ";
			pst = conn.prepareStatement(sql);
			ResultSet rs=pst.executeQuery();
			while(rs.next()){
				String productId=rs.getString("productId");
				String name=rs.getString("productName");
				String type=rs.getString("type");
				String rate=rs.getString("rate");
				String profit=rs.getString("profit");
				String startAccount=rs.getString("startAccount");
				String auditFlag=rs.getString("auditFlag");
				String enterpriseName=rs.getString("enterpriseName");
				String takeSpeedStr=rs.getString("takeSpeedStr");
				String takeSpeed=rs.getString("takeSpeed");
				String takeLimit=rs.getString("takeLimit");
				String weight=rs.getString("weight");
				String date=rs.getString("date");
				String capitalScale=rs.getString("capitalScale");
                String interest=rs.getString("interest");
                String riskRank=rs.getString("riskRank");
                String investmentMoney=rs.getString("investmentMoney");

                String enterpriseCode=rs.getString("enterpriseCode");
                String enterpriceAnomal=rs.getString("enterpriceAnomal");
                String legalRepresentative=rs.getString("legalRepresentative");
                String enterpriseType=rs.getString("enterpriseType");
                String registrationCapital=rs.getString("registrationCapital");
                String registrationStatus=rs.getString("registrationStatus");
                String registrationDate=rs.getString("registrationDate");
                String operateToDate=rs.getString("operateToDate");


				Recorder recorder=new Recorder();
				recorder.setProductId(productId);
				recorder.setProductName(name);
				recorder.setType(type);
				recorder.setRate(rate);
				recorder.setProfit(profit);
				recorder.setStartAccount(startAccount);
				recorder.setAuditFlag(auditFlag);
				recorder.setEnterpriseName(enterpriseName);
				recorder.setTakeSpeedStr(takeSpeedStr);
				recorder.setTakeSpeed(takeSpeed);
				recorder.setTakeLimit(takeLimit);
				recorder.setWeight(weight);
				recorder.setDate(date);
				recorder.setCapitalScale(capitalScale);
				recorder.setInterest(interest);
				recorder.setRiskRank(riskRank);
				recorder.setInvestmentMoney(investmentMoney);
				recorder.setEnterpriseCode(enterpriseCode);
				recorder.setEnterpriceAnomal(enterpriceAnomal);
				recorder.setLegalRepresentative(legalRepresentative);
				recorder.setEnterpriseType(enterpriseType);
				recorder.setRegistrationCapital(registrationCapital);
                recorder.setRegistrationStatus(registrationStatus);
				recorder.setRegistrationDate(registrationDate);
				recorder.setOperateToDate(operateToDate);

				list.add(recorder);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			this.close(conn,pst);
		}
		//判断前十是否包含余额宝（124），如果不包含就把124放到第十
		ArrayList<Recorder> subList=new ArrayList<Recorder>();
		for (int i = 0; i <list.size() ; i++) {
			if(list.get(i).getProductId().equals("124")){
				      if(i<10){
						  for (int j = 0; j <10 ; j++) {
							  subList.add(list.get(j));
						  }
					  }else{
						  for (int j = 0; j <9 ; j++) {
							  subList.add(list.get(j));
						  }
						  subList.add(list.get(i));
					  }
			}
		}
		valueList.clear();
		valueList=subList;
	}
     public void queryList2(){
		 String now=DateUtil.todayFormate("yyyy-MM-dd");
		 String sql="select * from (select * from mr_capitalProfit where profitType like '保本%' and '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
		 ArrayList<CurrentCapitalRecorder> list= queryDBData(sql);
		 if(list.size()==0){
			 ArrayList<CurrentCapitalRecorder> list_bank= querList2_bank();
			 list2.clear();
			 list2=list_bank;
		 }else{
			 list2.clear();
			 list2=list;
		 }

	 }

	 public ArrayList<CurrentCapitalRecorder> querList2_bank(){
		 String sql_bank="select name,rateLiquid from mr_bank_rate order by rateLiquid desc limit 10";
		 Connection conn = null;
		 PreparedStatement pst = null;
		 ArrayList<CurrentCapitalRecorder> list=new ArrayList<CurrentCapitalRecorder> ();
		 try {
			 conn = DriverManager.getConnection(url, user, password);//获取连接
			 pst = conn.prepareStatement(sql_bank);
			 ResultSet rs=pst.executeQuery();
			 while(rs.next()){
				 String name=rs.getString("name");
				 String rate=rs.getString("rateLiquid");
				 CurrentCapitalRecorder recorder=new CurrentCapitalRecorder();
				 recorder.setName(name+"活期储蓄");
				 recorder.setBank(name);
				 recorder.setLimit("T+0");
				 recorder.setRate(rate);
				 recorder.setProfitType(String.format("%.4f", 100*Float.parseFloat(rate)/365));
				 recorder.setStartAccount("0");
				 recorder.setCurrency("CNY");
				 list.add(recorder);
			 }
		 } catch (SQLException e) {
			 e.printStackTrace();
		 }finally {
			 this.close(conn,pst);
		 }
		 return list;


	 }
	 public   void queryList3(){
		 String now=DateUtil.todayFormate("yyyy-MM-dd");
		 ArrayList<CurrentCapitalRecorder> list=null;
		 String sql30Days="select * from (select * from mr_regular where  profitType like '非保本%' and `limit`<=30 and '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
		 String sql90days="select * from (select * from mr_regular where  profitType like '非保本%' and `limit`>30 and `limit`<=90 and '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
		 String sql180days="select * from (select * from mr_regular where  profitType like '非保本%' and `limit`>90 and `limit`<=180 and '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
		 String sql="select * from (select * from mr_regular where  profitType like '非保本%' and `limit`>180  and '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
		   list= queryDBData(sql30Days);
		 if(list.isEmpty()){
		   list= queryDBData(sql90days);
		 }
		 list3.clear();
		 list3=list;
	 }

    public void queryList4(){
		String now=DateUtil.todayFormate("yyyy-MM-dd");
		String sql="select * from (select * from mr_regular where profitType like '保本%' and  '"+now+"'>=dateStart and '"+now+"'<=dateEnd) a order by a.rate desc limit 10";
		ArrayList<CurrentCapitalRecorder> list= queryDBData(sql);
		list4.clear();
		list4=list;
	}



	public void queryList5(HttpServletRequest request) throws UnsupportedEncodingException {
		Connection conn = null;
		PreparedStatement pst = null;
		ArrayList<Recorder> list=new ArrayList<Recorder> ();
		this.queryMyFavorite(request);
		try {
			conn = DriverManager.getConnection(url, user, password);
			String yesterday=DateUtil.getNdaysDate(-1,"yyyy_MM-dd");
			String sql="select productId,productName,profit,rate from mr_currentFinancialProducts where date='"+yesterday+"'";
			pst = conn.prepareStatement(sql);
			ResultSet rs=pst.executeQuery();
			while(rs.next()){
				String id=rs.getString("productId");
				String name=rs.getString("productName");
				String rate=rs.getString("rate");
				String profit=rs.getString("profit");
				Recorder recorder=new Recorder();
				recorder.setProductId(id);
				recorder.setProductName(name);
				recorder.setRate(rate);
				recorder.setProfit(profit);

					if(list_my1.size()>0){
						for (int i = 0; i <list_my1.size() ; i++) {
							if(id.equals(list_my1.get(i).getProductId())){
								recorder.setChecked(true);
							}
						}
					}else{
						recorder.setChecked(false);
					}

				list.add(recorder);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		list5.clear();
		list5=list;
	}
	public void queryList6(HttpServletRequest request) throws UnsupportedEncodingException {
		Connection conn = null;
		PreparedStatement pst = null;
		ArrayList<CurrentCapitalRecorder> list=new ArrayList<CurrentCapitalRecorder> ();
		this.queryMyFavorite2(request);
		try {
			conn = DriverManager.getConnection(url, user, password);
			String today=DateUtil.todayFormate("yyyy-MM-dd");
			String sql="select * from mr_bank_rate order by rateLiquid desc";
			pst = conn.prepareStatement(sql);
			ResultSet rs=pst.executeQuery();
			while(rs.next()){
				String name=rs.getString("name");
				String rate=rs.getString("rateLiquid");
				CurrentCapitalRecorder recorder=new CurrentCapitalRecorder();
				recorder.setName(name);
				recorder.setRate(rate);
				if(list_my2.size()>0){
					for (int i = 0; i <list_my2.size() ; i++) {
						if(name.equals(list_my2.get(i).getName())){
							recorder.setChecked(true);
						}
					}
				}else{
					recorder.setChecked(false);
				}

				list.add(recorder);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		list6.clear();
		list6=list;
	}
	public void queryList7(HttpServletRequest request) throws UnsupportedEncodingException {
		Connection conn = null;
		PreparedStatement pst = null;
		ArrayList<CurrentCapitalRecorder> list=new ArrayList<CurrentCapitalRecorder> ();
		this.queryMyFavorite3(request);
		try {
			conn = DriverManager.getConnection(url, user, password);
			String today=DateUtil.todayFormate("yyyy-MM-dd");
			String sql="select * from mr_regular  where profitType like '非保本%'  and '"+today+"'>=dateStart and '"+today+"'<=dateEnd";
			pst = conn.prepareStatement(sql);
			ResultSet rs=pst.executeQuery();
			while(rs.next()){
				String name=rs.getString("name");
				String bank=rs.getString("bank");
				String limit=rs.getString("limit");
				String rate=rs.getString("rate");

				CurrentCapitalRecorder recorder=new CurrentCapitalRecorder();
				recorder.setName(name);
				recorder.setBank(bank);
				recorder.setRate(rate);
				recorder.setLimit(limit);
				if(list_my3.size()>0){
					for (int i = 0; i <list_my3.size() ; i++) {
						if(name.equals(list_my3.get(i).getName())){
							recorder.setChecked(true);
						}
					}
				}else{
					recorder.setChecked(false);
				}

				list.add(recorder);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		list7.clear();
		list7=list;
	}
	public void queryList8(HttpServletRequest request) throws UnsupportedEncodingException {
		Connection conn = null;
		PreparedStatement pst = null;
		ArrayList<CurrentCapitalRecorder> list=new ArrayList<CurrentCapitalRecorder> ();
		this.queryMyFavorite4(request);
		try {
			conn = DriverManager.getConnection(url, user, password);
			String today=DateUtil.todayFormate("yyyy-MM-dd");
			String sql="select * from mr_regular  where profitType like '保本%'  and '"+today+"'>=dateStart and '"+today+"'<=dateEnd";
			pst = conn.prepareStatement(sql);
			ResultSet rs=pst.executeQuery();
			while(rs.next()){
				String name=rs.getString("name");
				String bank=rs.getString("bank");
				String limit=rs.getString("limit");
				String rate=rs.getString("rate");

				CurrentCapitalRecorder recorder=new CurrentCapitalRecorder();
				recorder.setName(name);
				recorder.setBank(bank);
				recorder.setRate(rate);
				recorder.setLimit(limit);
				if(list_my4.size()>0){
					for (int i = 0; i <list_my4.size() ; i++) {
						if(name.equals(list_my4.get(i).getName())){
							recorder.setChecked(true);
						}
					}
				}else{
					recorder.setChecked(false);
				}

				list.add(recorder);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		list8.clear();
		list8=list;
	}
	public ArrayList<CurrentCapitalRecorder> queryDBData(String sql){
		Connection conn = null;
		PreparedStatement pst = null;
		ArrayList<CurrentCapitalRecorder> list=new ArrayList<CurrentCapitalRecorder> ();
		try {
			conn = DriverManager.getConnection(url, user, password);//获取连接
			pst = conn.prepareStatement(sql);
			ResultSet rs=pst.executeQuery();
			while(rs.next()){
				String name=rs.getString("name");
				String bank=rs.getString("bank");
				String type=rs.getString("type");
				String limit=rs.getString("limit");
				String rate=rs.getString("rate");
				String profitType=rs.getString("profitType");
				String startAccount=rs.getString("startAccount");
				String currency=rs.getString("currency");
				String weeklynumber=rs.getString("weeklynumber");
				String dateStart=rs.getString("dateStart");
				String dateEnd=rs.getString("dateEnd");
				CurrentCapitalRecorder recorder=new CurrentCapitalRecorder();
				recorder.setName(name);
				recorder.setBank(bank);
				recorder.setType(type);
				recorder.setLimit(limit);
				recorder.setRate(rate);
				recorder.setProfitType(profitType);
				recorder.setStartAccount(startAccount);
				recorder.setCurrency(currency);
				recorder.setWeeklynumber(weeklynumber);
				recorder.setDateStart(dateStart);
				recorder.setDateEnd(dateEnd);
				list.add(recorder);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			this.close(conn,pst);
		}
		return list;
	}
    public int  loginInfo(HttpServletRequest request) throws UnsupportedEncodingException {
		  int res=0;
		 //数据库插入一条用户登录的语句
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DriverManager.getConnection(url, user, password);//获取连接
			//tomcat get/post处理编码不一致,应该改表，给商品唯一码才好。
			String nickName=new String(request.getParameter("user").getBytes("ISO-8859-1"),"utf-8");
			//System.out.println("----------------->>>>>>>>>>"+name+"=====================>>>>>"+nickName);
			String avatarUrl=request.getParameter("avatarUrl");
			String today=DateUtil.todayFormate("yyyy-MM-dd HH:mm:ss");
			String sql="insert into mr_user(nickName,avatarUrl,loginDate) values(?,?,?)";
			pst = conn.prepareStatement(sql);
			pst.setString(1,nickName);
			pst.setString(2,avatarUrl);
			pst.setString(3,today);
			res=pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(conn,pst);
		}
		return res;
	}
    public void queryMyFavorite(HttpServletRequest request) throws UnsupportedEncodingException {
		Connection conn = null;
		PreparedStatement pst = null;
		ArrayList<Recorder> list=new ArrayList<Recorder> ();
		try {
			conn = DriverManager.getConnection(url, user, password);//获取连接
			String nickName=new String(request.getParameter("user").getBytes("ISO-8859-1"),"utf-8");
			String avatarUrl=request.getParameter("avatarUrl");
			String yersterday=DateUtil.getNdaysDate(-1,"yyyy-MM-dd");
			String today=DateUtil.todayFormate("yyyy-MM-dd HH:mm:ss");
			String sql="select favorite_list1 from mr_favorite where userName='"+nickName+"' or avatarUrl='"+avatarUrl+"'";
			pst=conn.prepareStatement(sql);
			ResultSet rs=pst.executeQuery();
			String favorite_list1="";
			while(rs.next()){
				//喜欢的id,转化recorder 数组返回才好
				favorite_list1=rs.getString("favorite_list1");
			}
			if (!favorite_list1.isEmpty()) {
				String sql_select="select * from mr_currentFinancialProducts where date='"+yersterday+"' and productId in ('"+favorite_list1.replace("\"","\\\"").replace(",","','")+"')";
				//System.out.println(sql_select);
				pst=conn.prepareStatement(sql_select);
				ResultSet rs1=pst.executeQuery();
                while(rs1.next()){
					String productId=rs1.getString("productId");
					String name=rs1.getString("productName");
					String type=rs1.getString("type");
					String rate=rs1.getString("rate");
					String profit=rs1.getString("profit");
					String startAccount=rs1.getString("startAccount");
					String auditFlag=rs1.getString("auditFlag");
					String enterpriseName=rs1.getString("enterpriseName");
					String takeSpeedStr=rs1.getString("takeSpeedStr");
					String takeSpeed=rs1.getString("takeSpeed");
					String takeLimit=rs1.getString("takeLimit");
					String weight=rs1.getString("weight");
					String date=rs1.getString("date");
					String capitalScale=rs1.getString("capitalScale");
					String interest=rs1.getString("interest");
					String riskRank=rs1.getString("riskRank");
					String investmentMoney=rs1.getString("investmentMoney");

					Recorder recorder=new Recorder();
					recorder.setProductId(productId);
					recorder.setProductName(name);
					recorder.setType(type);
					recorder.setRate(rate);
					recorder.setProfit(profit);
					recorder.setStartAccount(startAccount);
					recorder.setAuditFlag(auditFlag);
					recorder.setEnterpriseName(enterpriseName);
					recorder.setTakeSpeedStr(takeSpeedStr);
					recorder.setTakeSpeed(takeSpeed);
					recorder.setTakeLimit(takeLimit);
					recorder.setWeight(weight);
					recorder.setDate(date);
					recorder.setCapitalScale(capitalScale);
					recorder.setInterest(interest);
					recorder.setRiskRank(riskRank);
					recorder.setInvestmentMoney(investmentMoney);
					list.add(recorder);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(conn,pst);
		}
		list_my1.clear();
		list_my1=list;

	}
    public void queryMyFavorite2(HttpServletRequest request) throws UnsupportedEncodingException {
		Connection conn = null;
		PreparedStatement pst = null;
		ArrayList<CurrentCapitalRecorder> list=new ArrayList<CurrentCapitalRecorder> ();
		try {
			conn = DriverManager.getConnection(url, user, password);//获取连接
			String nickName=new String(request.getParameter("user").getBytes("ISO-8859-1"),"utf-8");
			String avatarUrl=request.getParameter("avatarUrl");
			String yersterday=DateUtil.getNdaysDate(-1,"yyyy-MM-dd");
			String today=DateUtil.todayFormate("yyyy-MM-dd");
				String sql="select favorite_list2 from mr_favorite where userName='"+nickName+"' or avatarUrl='"+avatarUrl+"'";
			pst=conn.prepareStatement(sql);
			ResultSet rs=pst.executeQuery();
			String favorite_list2="";
			while(rs.next()){
				//喜欢的id,转化recorder 数组返回才好
				favorite_list2=rs.getString("favorite_list2");
			}
			if (favorite_list2!=null) {
				String sql_select="select * from mr_bank_rate where   name in ('"+favorite_list2.replace("\"","\\\"").replace(",","','")+"')";
				pst=conn.prepareStatement(sql_select);
				ResultSet rs1=pst.executeQuery();
                while(rs1.next()){
					String name=rs1.getString("name");
					String rate=rs1.getString("rateLiquid");
					CurrentCapitalRecorder recorder=new CurrentCapitalRecorder();
					recorder.setName(name);
					recorder.setRate(rate);
					list.add(recorder);
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(conn,pst);
		}
		list_my2.clear();
		list_my2=list;
	}
    public void queryMyFavorite3(HttpServletRequest request) throws UnsupportedEncodingException {
		Connection conn = null;
		PreparedStatement pst = null;
		ArrayList<CurrentCapitalRecorder> list=new ArrayList<CurrentCapitalRecorder> ();
		try {
			conn = DriverManager.getConnection(url, user, password);//获取连接
			String nickName=new String(request.getParameter("user").getBytes("ISO-8859-1"),"utf-8");
			String avatarUrl=request.getParameter("avatarUrl");
			String yersterday=DateUtil.getNdaysDate(-1,"yyyy-MM-dd");
			String today=DateUtil.todayFormate("yyyy-MM-dd");
			String sql="select favorite_list3 from mr_favorite where userName='"+nickName+"' or avatarUrl='"+avatarUrl+"'";
			pst=conn.prepareStatement(sql);
			ResultSet rs=pst.executeQuery();
			String favorite_list3="";
			while(rs.next()){
				//喜欢的id,转化recorder 数组返回才好
				favorite_list3=rs.getString("favorite_list3");
			}
			if (!favorite_list3.isEmpty()) {
				String sql_select="select * from mr_regular where  '"+today+"'>=dateStart and '"+today+"'<=dateEnd and name in ('"+favorite_list3.replace("\"","\\\"").replace(",","','")+"')";
				pst=conn.prepareStatement(sql_select);
				ResultSet rs1=pst.executeQuery();
                while(rs1.next()){
					String name=rs1.getString("name");
					String bank=rs1.getString("bank");
					String limit=rs1.getString("limit");
					String rate=rs1.getString("rate");
					String startAccount=rs1.getString("startAccount");

					CurrentCapitalRecorder recorder=new CurrentCapitalRecorder();
					recorder.setName(name);
					recorder.setLimit(limit);
					recorder.setBank(bank);
					recorder.setRate(rate);
					recorder.setStartAccount(startAccount);
					list.add(recorder);
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(conn,pst);
		}
		list_my3.clear();
		list_my3=list;
	}
    public void queryMyFavorite4(HttpServletRequest request) throws UnsupportedEncodingException {
		Connection conn = null;
		PreparedStatement pst = null;
		ArrayList<CurrentCapitalRecorder> list=new ArrayList<CurrentCapitalRecorder> ();
		try {
			conn = DriverManager.getConnection(url, user, password);//获取连接
			String nickName=new String(request.getParameter("user").getBytes("ISO-8859-1"),"utf-8");
			String avatarUrl=request.getParameter("avatarUrl");
			String yersterday=DateUtil.getNdaysDate(-1,"yyyy-MM-dd");
			String today=DateUtil.todayFormate("yyyy-MM-dd");
			String sql="select favorite_list4 from mr_favorite where userName='"+nickName+"' or avatarUrl='"+avatarUrl+"'";
			pst=conn.prepareStatement(sql);
			ResultSet rs=pst.executeQuery();
			String favorite_list4="";
			while(rs.next()){
				//喜欢的id,转化recorder 数组返回才好
				favorite_list4=rs.getString("favorite_list4");
			}
			if (!favorite_list4.isEmpty()) {
				String sql_select="select * from mr_regular where  '"+today+"'>=dateStart and '"+today+"'<=dateEnd and name in ('"+favorite_list4.replace("\"","\\\"").replace(",","','")+"')";
				pst=conn.prepareStatement(sql_select);
				ResultSet rs1=pst.executeQuery();
                while(rs1.next()){
					String name=rs1.getString("name");
					String bank=rs1.getString("bank");
					String limit=rs1.getString("limit");
					String rate=rs1.getString("rate");
					String startAccount=rs1.getString("startAccount");

					CurrentCapitalRecorder recorder=new CurrentCapitalRecorder();
					recorder.setName(name);
					recorder.setLimit(limit);
					recorder.setBank(bank);
					recorder.setRate(rate);
					recorder.setStartAccount(startAccount);
					list.add(recorder);
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(conn,pst);
		}
		list_my4.clear();
		list_my4=list;
	}
	public void favorite(HttpServletRequest request) throws SQLException, UnsupportedEncodingException {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
				conn = DriverManager.getConnection(url,user,password);//获取连接
				String userName=new String(request.getParameter("user").getBytes("ISO-8859-1"),"utf-8");
				String avatarUrl=request.getParameter("avatarUrl");
				String favorite=new String(request.getParameter("favorite").getBytes("ISO-8859-1"),"utf-8");
                String now=DateUtil.todayFormate("yyyy-MM-dd HH:mm:ss");
				String sql_select="select count(1) from mr_favorite  where userName='"+userName+"' or avatarUrl='"+avatarUrl+"'";
				String sql=" insert into mr_favorite(userName,favorite_list1,avatarUrl,updateDate) values('"+userName+"','"+favorite+"','"+avatarUrl+"','"+now+"')";
				String sql_update="update mr_favorite set favorite_list1='"+favorite+"',updateDate='"+now+"' where userName='"+userName+"' or avatarUrl='"+avatarUrl+"'";
				pst = conn.prepareStatement(sql_select);
				ResultSet rs = pst.executeQuery();
				while(rs.next()){//这个有线程安全问题
					String temp=rs.getString("count(1)");
				if(Integer.parseInt(temp)==0){
					addRecorder(sql);
				}else{
					addRecorder(sql_update);
				}
			}
		} finally {
			close(conn,pst);
		}
	}
	public void favorite2(HttpServletRequest request) throws SQLException, UnsupportedEncodingException {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
				conn = DriverManager.getConnection(url,user,password);//获取连接
				String userName=new String(request.getParameter("user").getBytes("ISO-8859-1"),"utf-8");
				String avatarUrl=request.getParameter("avatarUrl");
				String favorite2=new String(request.getParameter("favorite2").getBytes("ISO-8859-1"),"utf-8");
				String sql_select="select count(1) from mr_favorite  where userName='"+userName+"' or avatarUrl='"+avatarUrl+"'";
				String sql=" insert into mr_favorite(userName,favorite_list2,avatarUrl) values('"+userName+"','"+favorite2+"','"+avatarUrl+"')";
				String sql_update="update mr_favorite set favorite_list2='"+favorite2+"' where userName='"+userName+"' or avatarUrl='"+avatarUrl+"'";
				pst = conn.prepareStatement(sql_select);
				ResultSet rs = pst.executeQuery();
				while(rs.next()){
					String temp=rs.getString("count(1)");
				if(Integer.parseInt(temp)==0){
					addRecorder(sql);
				}else{
					addRecorder(sql_update);
				}
			}
		} finally {
			close(conn,pst);
		}
	}
	public void favorite3(HttpServletRequest request) throws SQLException, UnsupportedEncodingException {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
				conn = DriverManager.getConnection(url,user,password);//获取连接
				String userName=new String(request.getParameter("user").getBytes("ISO-8859-1"),"utf-8");
				String avatarUrl=request.getParameter("avatarUrl");
				String favorite3=new String(request.getParameter("favorite3").getBytes("ISO-8859-1"),"utf-8");
				String sql_select="select count(1) from mr_favorite  where userName='"+userName+"' or avatarUrl='"+avatarUrl+"'";
				String sql=" insert into mr_favorite(userName,favorite_list3,avatarUrl) values('"+userName+"','"+favorite3+"','"+avatarUrl+"')";
				String sql_update="update mr_favorite set favorite_list3='"+favorite3+"' where userName='"+userName+"' or avatarUrl='"+avatarUrl+"'";
				pst = conn.prepareStatement(sql_select);
				ResultSet rs = pst.executeQuery();
				while(rs.next()){
					String temp=rs.getString("count(1)");
				if(Integer.parseInt(temp)==0){
					addRecorder(sql);
				}else{
					addRecorder(sql_update);
				}
			}
		} finally {
			close(conn,pst);
		}
	}
	public void favorite4(HttpServletRequest request) throws SQLException, UnsupportedEncodingException {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
				conn = DriverManager.getConnection(url,user,password);//获取连接
				String userName=new String(request.getParameter("user").getBytes("ISO-8859-1"),"utf-8");
				String avatarUrl=request.getParameter("avatarUrl");
				String favorite4=new String(request.getParameter("favorite4").getBytes("ISO-8859-1"),"utf-8");
				String sql_select="select count(1) from mr_favorite  where userName='"+userName+"' or avatarUrl='"+avatarUrl+"'";
				String sql=" insert into mr_favorite(userName,favorite_list4,avatarUrl) values('"+userName+"','"+favorite4+"','"+avatarUrl+"')";
				String sql_update="update mr_favorite set favorite_list4='"+favorite4+"' where userName='"+userName+"' or avatarUrl='"+avatarUrl+"'";
				pst = conn.prepareStatement(sql_select);
				ResultSet rs = pst.executeQuery();
				while(rs.next()){
					String temp=rs.getString("count(1)");
				if(Integer.parseInt(temp)==0){
					addRecorder(sql);
				}else{
					addRecorder(sql_update);
				}
			}
		} finally {
			close(conn,pst);
		}
	}
	public  void addRecorder(String sql) throws SQLException {
		Connection conn = null;
		PreparedStatement pst = null;
		int row=0;
		try {
			//Class.forName("com.mysql.jdbc.Driver");//指定连接类型
			conn = DriverManager.getConnection(url,user,password);//获取连接
			pst = conn.prepareStatement(sql);
			row=pst.executeUpdate();
			System.out.println("改变数据行数---->>>"+row);
		} finally {
			close(conn,pst);
		}

	}
	public  ArrayList selectRecorder(String sql) {
		Connection conn = null;
		PreparedStatement pst = null;
		ArrayList list=new ArrayList();
		try {
			//Class.forName("com.mysql.jdbc.Driver");//指定连接类型
			try {
                conn = DriverManager.getConnection(url,user,password);//获取连接
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				pst = conn.prepareStatement(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				ResultSet rs = pst.executeQuery();
				while(rs.next()){
				//	String rate=rs.getString("rate");
					String profit=rs.getString("profit");
				//	list.add(rate);
					list.add(profit);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}


		} finally {
			close(conn,pst);
		}
		return list;
	}
    public static void close(Connection conn,PreparedStatement pst){
		try {
			if (conn != null) {
				conn.close();
			}
			if (pst != null) {
				pst.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
