package com.mr.datagather;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.mr.datagather.bean.Rate;
import com.mr.util.HtmlDocument;
import com.mr.util.JacksonWriteUtil;
import org.apache.ibatis.javassist.bytecode.stackmap.TypeData;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static com.mr.util.DateUtil.daysBetween;
import static com.mr.web.controller.RateAction.close;


public class GatherWorker {

	private static String driver;
	private static String url;
	private static String user;
	private static String password;
	//静态代码块创建驱动
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

	/**
	 * slf4j门面+log4j实现
	 * cl-over-slf4j + slf4j-api + slf4j-log4j + log4j
	 */
	private static final Logger logger = LoggerFactory.getLogger(GatherWorker.class);

	/**
	 * 公共的截取所需字符串方式
	 * @param startString
	 * @param endString
	 * @param content
	 * @return
	 */
	public static String  getTargetString(String startString,String endString,String content){
		int index_start=content.indexOf(startString);
		int length=startString.length();
		int index_end=content.indexOf(endString);
		return content.substring(index_start+length,index_end);
	}

	/**
	 * 只用一次
	 */
	public static void getYearRecorders(String url) throws IOException, JSONException {
		//  String url="https://8.baidu.com/product/yieldinfo/current?itemId=91000002&yieldRequestType=4&channel=5";//获取百度钱包全年数据  活期赢
		InputStream is = new URL(url).openStream();
		int row=0;
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);//这个可以得到数据。
			String[] array= json.get("itemYieldInfoList").toString().substring(1,json.get("itemYieldInfoList").toString().length()-1).split("},");
			for(int i=0;i<array.length;i++){
				String[] arr=array[i].split(",");
				String releaseTime=arr[0].split(":")[1].substring(1,arr[0].split(":")[1].length()-1);
				String yieldSevenDays=arr[1].split(":")[1].substring(1,arr[1].split(":")[1].length()-1);
				String yieldTenThousands=arr[2].split(":")[1].substring(1,arr[2].split(":")[1].length()-1);
				//System.out.println(releaseTime+"--"+yieldSevenDays+"--"+yieldTenThousands);
				String sql="insert into mr_recorders(productName,ProductCode,type,rate,profit,lockups,startAccount,manager,trusteeship,riskrank,intestes,date,productStatus) values('活期赢','huoqiying','1',"+yieldSevenDays+","+yieldTenThousands+",'0天','1.00元','百度金融','未知','未知','单利','"+releaseTime+"','零活赎买')";
				try {
					addRecorder(sql);
				} catch (SQLException e) {
					e.printStackTrace();
				}finally {
					is.close();
				}
			}

	}

	/**
	 *
	 * @param url
	 */
   public static void getYearDatatoDB(String url) throws IOException, JSONException {
	   //String url="https://ljbao.lu.com/yeb/service/cash/product/fund-increase/query?productId=18296806&categoryCode=803&version=2.0&code=16012968435&period=360&type=sevenDay&_=1499162596675";//获取陆金所  零活宝-富赢 7日年华利率
	   //String url="https://ljbao.lu.com/yeb/service/cash/product/fund-increase/query?productId=18296806&categoryCode=803&version=2.0&code=16012968435&period=360&type=tenThousand&_=1499163068330";//获取陆金所  零活宝-富赢 万分收益
	   InputStream is = new URL(url).openStream();
	   BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	   String jsonText = readAll(rd);
	   JSONObject json = new JSONObject(jsonText);//这个可以得到数据。
	   String[] dates=json.get("date").toString().substring(1,json.get("date").toString().length()-1).split(",");
	   //String[] rates=json.get("rate").toString().substring(1,json.get("rate").toString().length()-1).split(",");
	   String[] profits=json.get("rate").toString().substring(1,json.get("rate").toString().length()-1).split(",");

	   for(int j=0;j<dates.length;j++){
		 //  String sql="insert into mr_recorders(productName,ProductCode,type,rate,profit,lockups,startAccount,manager,trusteeship,riskrank,intestes,date,productStatus) values('零活宝-富盈','linghuobao_fy','1',"+rates[j]+",'','0天','1.00元','陆金所','未知','5星','日复利',"+dates[j]+",'零活赎买')";
		   //  String sql_update="update mr_recorders set profit="+profits[j] +" where date="+dates[j] +"and  productName='零活宝-富盈'";
		   //String sql="insert into mr_recorders(productName,ProductCode,type,rate,profit,lockups,startAccount,manager,trusteeship,riskrank,intestes,date,productStatus) values('零活宝-金色人生','linghuobao','1',"+rates[j]+",'','0天','1.00元','陆金所','未知','5星','日复利',"+dates[j]+",'零活赎买')";
		     String sql_update="update mr_recorders set profit="+profits[j] +" where date="+dates[j] +"and  productName='零活宝-金色人生'";
		   try {
			   addRecorder(sql_update);
		   } catch (SQLException e) {
			   e.printStackTrace();
		   }finally {
		   	is.close();
		   }

	   }
   }
   public static void getYuebaoYearDatatoDB(String url) throws IOException, JSONException{
	   InputStream is = new URL(url).openStream();
	   BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	   String jsonText = readAll(rd);
	   String[] list=jsonText.toString().split("pop")[1].split("p")[1].split(";");
	   for(int k=0;k<list.length;k++){
	   	   String profit= list[k].split(",")[1];
		   String date=list[k].split(",")[2].replace("\\/","-").split(" ")[0];
		   String sql="insert into mr_recorders(productName,ProductCode,type,rate,profit,lockups,startAccount,manager,trusteeship,riskrank,intestes,date,productStatus) values('余额宝','yuebao','1','',"+profit+",'0天','1.00元','天虹基金','中信银行','R1','日复利','"+date+"','零活赎买')";
		   try {
			   addRecorder(sql);
		   } catch (SQLException e) {
			   e.printStackTrace();
		   }finally {
		   	is.close();
		   }
	   }
   }
   public static void getYoulibaotoDB(String url) throws IOException, JSONException{
	   InputStream is = new URL(url).openStream();
	   BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	   String jsonText = readAll(rd);
	   String[]  list=jsonText.toString().split("data")[1].substring(3,jsonText.toString().split("data")[1].length()-4).split("},");
	   for(int k=0;k<list.length;k++){
	   	   String rate= list[k].split(",")[0].split(":")[1].replace("\"","").replace("\"","");
		   String date=list[k].split(",")[1].split(":")[1].replace("\"","").replace("\"","");
		  // String profit=list[k].split(",")[1].split(":")[1].replace("\"","").replace("\"","");
		  // String date= list[k].split(",")[0].split(":")[1].replace("\"","").replace("\"","");
		  // String sql="insert into mr_recorders(productName,ProductCode,type,rate,profit,lockups,startAccount,manager,trusteeship,riskrank,intestes,date,productStatus) values('无忧宝','wuyoubao','1','',"+rate+",'0天','100.00元','有利网','未知','A','日复利','"+date+"','零活赎买')";
		   String sql_update="update mr_recorders set rate="+rate +" where date='"+date +"' and  productName='无忧宝'";
		   try {
			   addRecorder(sql_update);
		   } catch (SQLException e) {
			   e.printStackTrace();
		   }finally {
		   	is.close();
		   }
	   }
   }
//一次性导入数据
   public static void currentFinancialProducts(String url) throws IOException, JSONException, ClassNotFoundException, SQLException {
	   InputStream is=null;
	   Connection conn = null;
	   PreparedStatement pst = null;
	   try {
		    is = new URL(url).openStream();
		   BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		   String jsonText = readAll(rd);
		   JSONObject json = new JSONObject(jsonText);
		   JSONArray array =(JSONArray) json.get("data");
         //  JSONObject test=array.getJSONObject(0);
		   for (int i = 0; i < array.length(); i++) {
			   String date=(String)array.getJSONObject(i).get("dateStr");//日期
			   String type=array.getJSONObject(i).get("type").toString();
			   JSONObject temp= (JSONObject) array.getJSONObject(i).get("netProduct");
			   String name=temp.get("name").toString();//产品名称
			   String id=temp.get("id").toString();//产品Id
			   String auditFlag=temp.get("auditFlag").toString();//
			   String enterpriseName=temp.get("enterpriseName").toString();//发行机构
			   String profitYear=temp.get("profitYear").toString();//7日年华
			   String profitTenthousand=temp.get("profitTenthousand").toString();//万份收益
			   String takeSpeedStr=temp.get("takeSpeedStr").toString();//提现速度
			   String takeSpeed=temp.get("takeSpeed").toString();
			   String takeLimit=temp.get("takeLimit").toString();//单笔h及单日上限
			   String weight=temp.get("weight").toString();
			   String count=temp.get("count").toString();
			   String capitalScale=temp.get("capitalScale").toString();
			   String investmentMoney=temp.get("investmentMoney").toString();
			   String sql="insert into mr_currentFinancialProducts(productName,ProductId,type,rate,profit,startAccount,date,auditFlag,enterpriseName,takeSpeedStr,takeSpeed,takeLimit,weight,capitalScale,interest,riskRank,investmentMoney) values('"+name+"','"+id+"','"+type+"','"+profitYear+"','"+profitTenthousand+"','"+count+"','"+date+"','"+auditFlag+"','"+enterpriseName+"','"+takeSpeedStr+"','"+takeSpeed+"',ifnull("+takeLimit+",''),'"+weight+"','"+capitalScale+"','复利','低风险','"+investmentMoney+"')";

			 //  Class.forName("com.mysql.jdbc.Driver");//指定连接类型
			   conn = DriverManager.getConnection(url,user,password);//获取连接
			   pst = conn.prepareStatement(sql);
			   pst.executeUpdate();
		   }
	   } catch (JsonIOException e) {
		   e.printStackTrace();
	   } catch (JsonSyntaxException e) {
		   e.printStackTrace();
	   } catch (FileNotFoundException e) {
		   e.printStackTrace();
	   }finally {
		   close(conn,pst);

		   if (is != null) {
			   is.close();
		   }
	   }
	}

	//每日数据更新
	public static void  updateTodayFinancialData(){
		for (int i = 124; i <195 ; i++) {
			String url="http://bank.cngold.org/hlwlicai/getProfitChange.htm?productId="+i+"&type=1";
			Calendar cal   =   Calendar.getInstance();
			cal.add(Calendar.DATE,   -1);
			String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());

			InputStream is = null;
			try {
				is = new URL(url).openStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				String jsonText = readAll(rd);
				JSONObject json = new JSONObject(jsonText);
				JSONArray array =(JSONArray) json.get("data");
				//  JSONObject test=array.getJSONObject(0);
					String date=(String)array.getJSONObject(array.length()-1).get("dateStr");//日期
					String type=array.getJSONObject(array.length()-1).get("type").toString();
					JSONObject temp= (JSONObject) array.getJSONObject(array.length()-1).get("netProduct");
					String name=temp.get("name").toString();//产品名称
					String id=temp.get("id").toString();//产品Id
					String auditFlag=temp.get("auditFlag").toString();//
					String enterpriseName=temp.get("enterpriseName").toString();//发行机构
					String profitYear=temp.get("profitYear").toString();//7日年华
					String profitTenthousand=temp.get("profitTenthousand").toString();//万份收益
					String takeSpeedStr=temp.get("takeSpeedStr").toString();//提现速度
					String takeSpeed=temp.get("takeSpeed").toString();
					String takeLimit=temp.get("takeLimit").toString();//单笔h及单日上限
					String weight=temp.get("weight").toString();
					String count=temp.get("count").toString();
					String capitalScale=temp.get("capitalScale").toString();
					String investmentMoney=temp.get("investmentMoney").toString();
				          String sql_select ="select * from mr_currentFinancialProducts where productId='"+i+"' and date='"+yesterday+"'";
				          String sql_update ="update mr_currentFinancialProducts set rate= '"+profitYear+"' ,profit='"+profitTenthousand+"'  where productId='"+i+"' and date='"+yesterday+"'";
				          String sql_insert ="insert into mr_currentFinancialProducts(productName,ProductId,type,rate,profit,startAccount,date,auditFlag,enterpriseName,takeSpeedStr,takeSpeed,takeLimit,weight,capitalScale,interest,riskRank,investmentMoney) values('"+name+"','"+id+"','"+type+"','"+profitYear+"','"+profitTenthousand+"','"+count+"','"+date+"','"+auditFlag+"','"+enterpriseName+"','"+takeSpeedStr+"','"+takeSpeed+"',ifnull("+takeLimit+",''),'"+weight+"','"+capitalScale+"','复利','低风险','"+investmentMoney+"')";
						ArrayList arr=selectRecorder(sql_select);
				if(yesterday.equals(date)){
					if(arr.size()==0){
						addRecorder(sql_insert);
						System.out.println(i+"---->>>>插入数据");
					}
//					else{
//						addRecorder(sql_update);
//						System.out.println(i+"---->>>>更新数据");
//					}
				}else{
					System.out.println(i+"---->>>>>>> 产品已过期");
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {

				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

//	public static void main(String[] args) throws IOException, JSONException, SQLException, ClassNotFoundException {
//		  String url="https://8.baidu.com/product/yieldinfo/current?itemId=91000002&yieldRequestType=4&channel=5";//获取百度钱包全年数据  活期赢
//		  getYearRecorders(url);

		//String url="https://ljbao.lu.com/yeb/service/cash/product/fund-increase/query?productId=18296806&categoryCode=803&version=2.0&code=16012968435&period=360&type=sevenDay&_=1499162596675";//获取陆金所  零活宝-富赢 7日年华利率
		//String url="https://ljbao.lu.com/yeb/service/cash/product/fund-increase/query?productId=18296806&categoryCode=803&version=2.0&code=16012968435&period=360&type=tenThousand&_=1499163068330";//获取陆金所  零活宝-富赢 万分收益
		//String url="https://ljbao.lu.com/yeb/service/cash/product/fund-increase/query?productId=32534075&categoryCode=803&version=2.0&code=16072712671&period=360&type=sevenDay&_=1499212270894";//获取陆金所  零活宝-金色人生 7日年华利率
	//	String url="https://ljbao.lu.com/yeb/service/cash/product/fund-increase/query?productId=32534075&categoryCode=803&version=2.0&code=16072712671&period=360&type=tenThousand&_=1499212588795";//获取陆金所  零活宝-金色人生 万分收益
	//	getYearDatatoDB(url);

         // String url="";
//		String url="https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?resource_id=16670&query=000198&co=tab&cb=jQuery110202896585636322062";//余额宝 万分收益
//		getYuebaoYearDatatoDB(url);

	//	String url="http://www.yooli.com/latest7dayAnnual.action";//无忧宝 7日年华利率
	//	String url="http://www.yooli.com/nInterestPer10ks.action";//无忧宝 万分收益
	//	getYoulibaotoDB(url);

		//2017-07-07找到的网址 http://bank.cngold.org/hlwlicai/c146.html  好地方呀

//		for(int x=124;x<195;x++){
//			String url="http://bank.cngold.org/hlwlicai/getProfitChange.htm?productId="+x+"&type=1";
//			currentFinancialProducts(url);
//			System.out.println(x+"  --->>>>>完成");
//		}
		//String url="http://bank.cngold.org/hlwlicai/getProfitChange.htm?productId=138&type=1";//微信理财通(华夏财)
		// switch: 189 泰达宏利货币 170 富钱包  171 沃百富  127 掌柜钱包 154 广发证券  156 微信理财通  169 和聚宝  130 零钱宝(汇添富)  131 汇添富现金宝  132 民生如意宝(汇添富) 133 微财富存钱罐  134 网易现金宝
		// 135 电信添益宝 151 百度百赚利滚利 164 京东小金库(嘉实活)  124 余额宝   125 现金快线  126 好买储蓄罐    总共71个产品 124 --194
		//updateTodayFinancialData();
//	}
	public static void addRecorder(String sql) throws SQLException {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = DriverManager.getConnection(url,user,password);//获取连接
			pst = conn.prepareStatement(sql);
			pst.executeUpdate();
			//System.out.println("执行结果---->>>"+row);
		} finally {
			close(conn,pst);
		}

	}

	public static ArrayList selectRecorder(String sql) {
		Connection conn = null;
		PreparedStatement pst = null;
		ArrayList list=new ArrayList();
		try {
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
                     String rate=rs.getString("rate");
                     String profit=rs.getString("profit");
					list.add(rate);
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

	//-------------------------------------------------------------------------------------------------------------------------------------------------


	 //https://www.jisilu.cn/data/repo/


	/**
	 * 银行的理财产品  定期非保本浮利
     * https://bank.cngold.org/yhckll/   银行利率
     * http://finance.sina.com.cn/money/bank/lcweekly236/ 定期理财产品
     *
     * http://www.jjmmw.com/fund/ajax/zoushi/?type=jz&compare1=002446&compare2=szzz&period=all  保本型理财产品 保本期有3-5年
     * 只要银行保本的就好了 '002446','002622','001531','519729','002295','002456','002271','002657','000126','002628','519766','519726','519753','002629','487016','217024','002776','002777'
	 */

	public static void  getBankData( )//可以抓取数据
	{
            final String url_list1="https://bank.cngold.org/yhckll/index.html";
		    final String url_list2="https://bank.cngold.org/yhckll/list_p2.html";
		    final String url_list3="https://bank.cngold.org/yhckll/list_p3.html";
			String[] urls={url_list1,url_list2,url_list3};
            Connection conn = null;
            PreparedStatement pst = null;
            String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			ArrayList arr=new ArrayList();
			Document doc=null;
		try {
                Class.forName("com.mysql.jdbc.Driver");//指定连接类型
				for (int k = 0; k <urls.length ; k++) {
					doc = Jsoup.connect(urls[k]).get();
					Elements trs=doc.select("tr");
					for (int i = 0; i <trs.size() ; i++) {
						Elements tds = trs.get(i).select("td");
						for(int j = 0;j<tds.size();j++){
							String text = tds.get(j).text();
							arr.add(text);
						}
						if(arr.size()>0){
							String TEMP="insert into mr_bank_rate(name,rateLiquid,rateFixed0p3m,rateFixed0p6m,rateFixed0p1y,rateFixed0p2y,rateFixed0p3y,rateFixed0p5y,rateOther1y,rateOther3y,rateOther5y,rateNotice1d,rateNotice7d,updateDate)";
							String sql=TEMP +"  values('"+arr.get(0)+"','"+arr.get(1)+"','"+ arr.get(2) +"','"+ arr.get(3) +"','"+ arr.get(4) +"','"+ arr.get(5) +"','"+ arr.get(6) +"','"+ arr.get(7) +"','"+ arr.get(8) +"','"+ arr.get(9) +"','"+ arr.get(10) +"','"+ arr.get(11) +"','"+ arr.get(12) +"','"+date+"')";
							conn = DriverManager.getConnection(url,user,password);//获取连接
							pst = conn.prepareStatement(sql);
							pst.executeUpdate();
						}
						arr.clear();
						System.out.println(i+"----->>>>"+arr.size());
					}
				}
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
                close(conn,pst);
            }
	}
/**
 * http://finance.sina.com.cn/money/bank/lcweekly247/
 * 247期 产品发售期：2017-07-15 ~2017-07-21
 * 预期收益率
 * 保本理财产品
 */

       public static void getCapitalFund() throws ParseException {
		   SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
		   int temp=daysBetween(sdf.parse("2017-08-18"),new Date());
		   if(temp!=0 && temp%6==1){
			   final  String url_meta="http://finance.sina.com.cn/money/bank/lcweekly"+251+(temp-1)/6;
			   Document doc = null;
			   Connection conn = null;
			   PreparedStatement pst = null;
			   try {
				   doc = Jsoup.connect(url_meta).get();
				   Elements trs=doc.select("tr");
				   for (int i = 0; i <trs.size() ; i++){
					   Elements tds = trs.get(i).select("td");
					   String text = tds.get(4).text();
					   String text2 = tds.get(6).text();
					   //活期
					   if("T+0".equals(text)&& text2.contains("保本")){
						   setData(conn,pst,tds,temp);
					   }else  if( !("T+0".equals(text))&&text2.contains("保本")){
						   setData(conn,pst,tds,temp);
					   }
				   }
			   } catch (IOException e) {
				   e.printStackTrace();
			   }
		   }
	   }
	   public static void setData(Connection conn,PreparedStatement pst,Elements tds,int temp){
		String name=tds.get(1).text();
		String bank=tds.get(2).text();
		String type=tds.get(3).text();
		String Temp=tds.get(4).text();
		   String limit=null;
		   if(Temp.contains("T")){
			    limit=Temp;
		   }else{
			   limit=Temp.substring(0,Temp.length()-1);
		   }
		String rate=tds.get(5).text(). replaceAll("\\s*", "");
		String profitType=tds.get(6).text();
		String startAccount=tds.get(7).text();
		String currency=tds.get(8).text();
		// System.out.println("name="+name+",bank="+bank+",type="+type+",limit="+limit+",rate="+rate+",profitType="+profitType+",startAccount="+startAccount);
		   String sql="";
		   Calendar cal = Calendar.getInstance();
		   cal.set(2017,7,12);
		   cal.add(Calendar.DATE,6*(temp-1)/6);
		   String date1=""+cal.get(Calendar.YEAR)+cal.get(Calendar.MONTH)+cal.get(Calendar.DATE);
		   cal.add(Calendar.DATE,(6*(temp-1)/6+1));
		   String date2=""+cal.get(Calendar.YEAR)+cal.get(Calendar.MONTH)+cal.get(Calendar.DATE);
		if(!name.isEmpty()){
			if(Temp.contains("T")) {
				String TEMP = "insert into mr_capitalProfit(`name`,`bank`,`type`,`limit`,`rate`,`profitType`,`startAccount`,`currency`,`weeklynumber`,`dateStart`,`dateEnd`)";
				sql = TEMP + "  values('" + name + "','" + bank + "','" + type + "','" + limit + "','" + rate + "','" + profitType + "','" + startAccount + "','" + currency + "','"+(251+(temp-1)/6)+"','"+date1+"','"+date2+"')";
			}else{
				String TEMP="insert into mr_regular(`name`,`bank`,`type`,`limit`,`rate`,`profitType`,`startAccount`,`currency`,`weeklynumber`,`dateStart`,`dateEnd`)";
				sql=TEMP +"  values('"+name+"','"+bank+"','"+ type +"','"+ limit +"','"+ rate +"','"+ profitType +"','"+ startAccount+"','"+currency+"','"+(251+(temp-1)/6)+"','"+date1+"','"+date2+"')";
			}

			try {
				conn = DriverManager.getConnection(url,user,password);//获取连接
				pst = conn.prepareStatement(sql);
				pst.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
				close(conn,pst);
			}
		}
	}

	public static void main(String[] args) {
		getContent1();
//		try {
//			getCapitalFund();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 余额宝
	 *
	 */

	public static void  getContent1( )
	{
		String url="http://fund.eastmoney.com/f10/jbgk_000198.html";
		Document doc=null;
		try{
			doc = Jsoup.connect(url).get();
			Elements trs=doc.select("tr");

		}catch(Exception ex) {
			ex.printStackTrace();
			logger.error("余额宝利率获取失败......"+ex.getMessage());
		}
	}


	
	/**
	 * 信融财富  信存宝
	 * @auth leohorry 存在企业行政处罚，所以先不考录它
	 */
	public static void getContent2( ) throws SQLException {
		String url="https://www.xinrong.com/v2/xincunbao/xcb_product_info.jso";
		String rateStr="";
		String profitAccount="";
		Document document = HtmlDocument.getHtmlDocument(url);
		if(document == null || document.text()==null) return ;
		
		StringWriter sw = new StringWriter(); 
		Map map= null;
		Rate rate =null;
		 try {  
			 map=JacksonWriteUtil.getInstance().readValue(document.text(), HashMap.class);
			 rateStr= map.get("rate").toString();
			 profitAccount= map.get("tenThousandIncome").toString();

				if(rateStr!=null)
				{
					rate=new Rate();
					rate.setCode("xincunbao");
					rate.setName("信存宝");
					rate.setOrder(2);
					rate.setRate(rateStr);
					rate.setType(1);
					rate.setLockups("0 天");
					rate.setTime(System.currentTimeMillis()/1000l);
					rate.setProfitAccount(profitAccount);
					rate.setStartAccount("0.01 元");
					rate.setManager("信融财富");
					rate.setTrusteeship("未知");
					rate.setIntestes("日复利");
					rate.setStatus("零活赎买");
					rate.setRiskrank("未知");
					InterestStore.rateMap.put("xincunbao", rate);
					//1分钱起任意金额均可存入,深圳市信融财富投资管理有限公司创办于2012年3月，注册资本6904.55万元人民币，实收资本5704.55万元人民币，总部位于深圳
					System.out.println("信存宝利率="+rateStr+"   万分收益="+String.format("%.5f",(10000*Double.parseDouble(rateStr)/36500)));

					Calendar cal   =   Calendar.getInstance();
					cal.add(Calendar.DATE,   -1);
					String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());

					String sql_select="select * from mr_recorders   where date='"+yesterday+"' and productName='信存宝'";
					String sql_update="update mr_recorders set rate='"+rateStr+"', profit='"+profitAccount+"'  where date='"+yesterday+"' and productName='信存宝'";
					String sql_insert="insert into mr_recorders(productName,ProductCode,type,rate,profit,lockups,startAccount,manager,trusteeship,riskrank,intestes,date,productStatus) values('信存宝','xincunbao','1',"+rateStr+","+profitAccount+",'0天','0.01元','信融财富','未知','未知','日复利','"+yesterday+"','零活赎买')";
					ArrayList arr=selectRecorder(sql_select);
					if(arr.size()==0){
						addRecorder(sql_insert);
					}else{
						addRecorder(sql_update);
					}
				}
				
	        } catch (JsonGenerationException e) {
	            e.printStackTrace();  
	        } catch (JsonMappingException e) {
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }
		
	}

	/**
	 * 无忧宝  有利网
	 */
	public static void getContent5( ) throws IOException {
		Connection conn = null;
		PreparedStatement pst = null;

		String url_rate="http://www.yooli.com/latest7dayAnnual.action";//无忧宝 7日年华利率
		InputStream is_rate = new URL(url_rate).openStream();
		BufferedReader rd_rate = new BufferedReader(new InputStreamReader(is_rate, Charset.forName("UTF-8")));
		String jsonText = readAll(rd_rate);
		String[]  list_rate=jsonText.toString().split("data")[1].substring(3,jsonText.toString().split("data")[1].length()-4).split("},");
		String rate=list_rate[list_rate.length-1].split(",")[0].split(":")[1].replace("\"","");

		String url_profit="http://www.yooli.com/nInterestPer10ks.action";//无忧宝 万分收益
		InputStream is_profit = new URL(url_profit).openStream();
		BufferedReader rd_profit = new BufferedReader(new InputStreamReader(is_profit, Charset.forName("UTF-8")));
		String jsonText2 = readAll(rd_profit);
		String[]  list_profit=jsonText2.toString().split("data")[1].substring(3,jsonText2.toString().split("data")[1].length()-4).split("},");
		String profit=list_profit[list_profit.length-1].split(",")[1].split(":")[1].replace("\"","");

		Rate wuyoubao =null;
		try{

				/**
				 * 不定期，计息中金额可以随时申请转出,1. 转入金额100元起，单笔上限200,000元2. 转入资金不得使投资人在无忧宝计划中的总金额超过200,000元3. 转入资金需要使转入后当期无忧宝计划的剩余金额为0或大于等于100元
				 * 	1. 转出资金100元起2. 可转出额度=投资人在无忧宝计划中的计息金额－转出申请中的金额3. 转出资金需要使转出后投资人在无忧宝计划中的可转出额度为0或大于等于100元
				 * 	转入费率：0.00%   转出费率：每月前3次转出费率 0.00%   超过3次后，转出费率 0.25%（费用不足1元，按1元计），返还转出手续费等额定存宝投资红包（使用比例0.25%）
				 *
				 */
				if(rate!=null)
				{
					wuyoubao=new Rate();
					wuyoubao.setCode("wuyoubao");
					wuyoubao.setName("无忧宝");
					wuyoubao.setLockups("0 天");
					wuyoubao.setOrder(3);
					wuyoubao.setRate(rate);
					wuyoubao.setType(1);
					wuyoubao.setStartAccount("100.00元");
					wuyoubao.setProfitAccount(profit);
					wuyoubao.setManager("有利网");
					wuyoubao.setTrusteeship("未知");
					wuyoubao.setIntestes("日复利");
					wuyoubao.setStatus("零活赎买");
					wuyoubao.setRiskrank("A");
					wuyoubao.setTime(System.currentTimeMillis()/1000l);
					InterestStore.rateMap.put("wuyoubao", wuyoubao);
					System.out.println("无忧宝利率="+rate+"   万分收益="+profit);
					Calendar cal   =   Calendar.getInstance();
					cal.add(Calendar.DATE,   -1);
					String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());

					String sql_select="select * from mr_recorders   where date='"+yesterday+"' and productName='无忧宝'";
					String sql_update="update mr_recorders set rate='"+rate+"', profit='"+profit+"'  where date='"+yesterday+"' and productName='无忧宝'";
					String sql_insert="insert into mr_recorders(productName,ProductCode,type,rate,profit,lockups,startAccount,manager,trusteeship,riskrank,intestes,date,productStatus) values('无忧宝','wuyoubao','1',"+rate+","+profit+",'0天','0.01元','有利网','未知','A','日复利','"+yesterday+"','零活赎买')";
					ArrayList arr=selectRecorder(sql_select);
					if(arr.size()==0){
						addRecorder(sql_insert);
					}else{
						addRecorder(sql_update);
					}
				}

			}catch(Exception ex) {
				ex.printStackTrace();
				logger.error("无忧宝  有利网 获取失败......"+ex.getMessage());
		}
	}

	/**
	 * 陆金所
	 */
	public static void getContent8( )
	{
		try{
		String url="https://list.lu.com/list/huoqi";
		String rateStr="";
		Document document = HtmlDocument.getHtmlDocument(url);
		String content=document.toString();
		if(document == null) return ;
		Rate linghuobao =null;
		Rate linghuobao_fy =null;
		Rate linghuobao_t0 =null;
		Rate linghuobao_28days =null;
//详情是图片
		String[] list=content.split("<div class=\"product \">");
		for(int i=1;i<list.length;i++){
			 if(list[i].contains("<a title=\"零活宝-金色人生\" data-sk=\"huoqi_list_bxxhuoji\" href=\"https://ljbao.lu.com/yeb/huoqi/productDetail/32534075\">零活宝-金色人生</a>")){
				 //零活宝-金色人生
				 int index_list1=list[i].replace("\n","").indexOf("<div class=\"value bold\">");
				 int length_list1="<div class=\"value bold\">".length();
				 String lhb_jsrs=list[i].replace("\n","").substring(index_list1+length_list1,index_list1+length_list1+19).trim().replace("%","");
				 int index2=list[i].replace("\n","").indexOf("<div class=\"value\">");
				 int length2="<div class=\"value\">".length();
				 String profit=list[i].replace("\n","").substring(index2+length2,index2+length2+19).trim().replace("元","");
				 System.out.println("零活宝-金色人生利率="+lhb_jsrs+"  万份收益="+profit);
				 linghuobao=new Rate();
				 linghuobao.setStartAccount("1.00元");
				 linghuobao.setLockups("0");
				 linghuobao.setTime(System.currentTimeMillis()/1000l);
				 linghuobao.setName("零活宝-金色人生");
				 linghuobao.setCode("linghuobao");
				 linghuobao.setRate(lhb_jsrs);
				 linghuobao.setProfitAccount(profit);
				 linghuobao.setType(1);
				 linghuobao.setOrder(4);
				 linghuobao.setManager("陆金所");
				 linghuobao.setTrusteeship("未知");
				 linghuobao.setIntestes("日复利");
				 linghuobao.setStatus("零活赎买");
				 linghuobao.setRiskrank("5星");
				 InterestStore.rateMap.put("linghuobao", linghuobao);

				 Calendar cal   =   Calendar.getInstance();
				 cal.add(Calendar.DATE,   -1);
				 String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());

				 String sql_select="select * from mr_recorders   where date='"+yesterday+"' and productName='零活宝-金色人生'";
				 String sql_update="update mr_recorders set rate='"+lhb_jsrs+"', profit='"+profit+"'  where date='"+yesterday+"' and productName='零活宝-金色人生'";
				 String sql_insert="insert into mr_recorders(productName,ProductCode,type,rate,profit,lockups,startAccount,manager,trusteeship,riskrank,intestes,date,productStatus) values('零活宝-金色人生','linghuobao','1',"+lhb_jsrs+","+profit+",'0天','1.00元','陆金所','未知','5星','日复利','"+yesterday+"','零活赎买')";
				 ArrayList arr=selectRecorder(sql_select);
				 if(arr.size()==0){
					 addRecorder(sql_insert);
				 }else{
					 addRecorder(sql_update);
				 }

			}
			if(list[i].contains("<a title=\"零活宝-富盈\" data-sk=\"huoqi_list_bxxhuoji\" href=\"https://ljbao.lu.com/yeb/huoqi/productDetail/18296806\">零活宝-富盈</a>")){
				//零活宝-富盈
				int index_list2=list[i].replace("\n","").indexOf("<div class=\"value bold\">");
				int length_list2="<div class=\"value bold\">".length();
				String lhb_fy=list[i].replace("\n","").substring(index_list2+length_list2,index_list2+length_list2+19).trim().replace("%","");
				int index2=list[i].replace("\n","").indexOf("<div class=\"value\">");
				int length2="<div class=\"value\">".length();
				String profit=list[i].replace("\n","").substring(index2+length2,index2+length2+19).trim().replace("元","");
				System.out.println("零活宝-富盈="+lhb_fy+"  万份收益="+profit);
				linghuobao_fy=new Rate();
				linghuobao_fy.setStartAccount("1.00元");
				linghuobao_fy.setLockups("0");
				linghuobao_fy.setTime(System.currentTimeMillis()/1000l);
				linghuobao_fy.setName("零活宝-富盈");
				linghuobao_fy.setCode("linghuobao_fy");
				linghuobao_fy.setRate(lhb_fy);
				linghuobao_fy.setProfitAccount(profit);
				linghuobao_fy.setType(1);
				linghuobao_fy.setOrder(5);
				linghuobao_fy.setManager("陆金所");
				linghuobao_fy.setTrusteeship("未知");
				linghuobao_fy.setIntestes("日复利");
				linghuobao_fy.setStatus("零活赎买");
				linghuobao_fy.setRiskrank("5星");
				InterestStore.rateMap.put("linghuobao_fy", linghuobao_fy);

				Calendar cal   =   Calendar.getInstance();
				cal.add(Calendar.DATE,   -1);
				String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());

				String sql_select="select * from mr_recorders   where date='"+yesterday+"' and productName='零活宝-富盈'";
				String sql_update="update mr_recorders set rate='"+lhb_fy+"', profit='"+profit+"'  where date='"+yesterday+"' and productName='零活宝-富盈'";
				String sql_insert="insert into mr_recorders(productName,ProductCode,type,rate,profit,lockups,startAccount,manager,trusteeship,riskrank,intestes,date,productStatus) values('零活宝-富盈','linghuobao_fy','1',"+lhb_fy+","+profit+",'0天','1.00元','陆金所','未知','5星','日复利','"+yesterday+"','零活赎买')";
				ArrayList arr=selectRecorder(sql_select);
				if(arr.size()==0){
					addRecorder(sql_insert);
				}else{
					addRecorder(sql_update);
				}
			}
			if(list[i].contains("<a target=\"_blank\" data-sk=\"huoqi_list_linghuobao\" title=\"零活宝T+0\" href=\"//lbo.lu.com/lbo/detail?productId=30098220\">零活宝T+0</a>")){
				//	零活宝T+0
				int index_list3=list[i].replace("\n","").indexOf("<div class=\"value bold\">");
				int length_list3="<div class=\"value bold\">".length();
				String lhb_t0=list[i].replace("\n","").substring(index_list3+length_list3,index_list3+length_list3+19).trim().replace("%","");
				int index2=list[i].replace("\n","").indexOf("<div class=\"value\">");
				int length2="<div class=\"value\">".length();
				String profit=list[i].replace("\n","").substring(index2+length2,index2+length2+19).trim().replace("元","");
				System.out.println("零活宝T+0="+lhb_t0+"  万份收益="+profit);
				linghuobao_t0=new Rate();
				linghuobao_t0.setStartAccount("1.00元");
				linghuobao_t0.setLockups("0");
				linghuobao_t0.setTime(System.currentTimeMillis()/1000l);
				linghuobao_t0.setName("零活宝T+0");
				linghuobao_t0.setCode("linghuobaoT+0");
				linghuobao_t0.setRate(lhb_t0);
				linghuobao_t0.setProfitAccount(profit);
				linghuobao_t0.setType(1);
				linghuobao_t0.setOrder(6);
				linghuobao_t0.setManager("陆金所");
				linghuobao_t0.setTrusteeship("未知");
				linghuobao_t0.setIntestes("日复利");
				linghuobao_t0.setStatus("零活赎买");
				linghuobao_t0.setRiskrank("未知");
				InterestStore.rateMap.put("linghuobao_t0", linghuobao_t0);

				Calendar cal   =   Calendar.getInstance();
				cal.add(Calendar.DATE,   -1);
				String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());

				String sql_select="select * from mr_recorders   where date='"+yesterday+"' and productName='零活宝T+0'";
				String sql_update="update mr_recorders set rate='"+lhb_t0+"', profit='"+profit+"'  where date='"+yesterday+"' and productName='零活宝T+0'";
				String sql_insert="insert into mr_recorders(productName,ProductCode,type,rate,profit,lockups,startAccount,manager,trusteeship,riskrank,intestes,date,productStatus) values('零活宝T+0','linghuobaoT+0','1',"+lhb_t0+","+profit+",'0天','1.00元','陆金所','未知','5星','日复利','"+yesterday+"','零活赎买')";
				ArrayList arr=selectRecorder(sql_select);
				if(arr.size()==0){
					addRecorder(sql_insert);
				}else{
					addRecorder(sql_update);
				}
			}
			if(list[i].contains("<a target=\"_blank\" data-sk=\"huoqi_list_linghuobao\" title=\"零活宝-28日聚财\" href=\"https://ljbao.lu.com/yeb/product/2070075\">零活宝-28日聚财</a>")){

				//零活宝-28日聚财
				int index_list4=list[i].replace("\n","").indexOf("<div class=\"value bold\">");
				int length_list4="<div class=\"value bold\">".length();
				String lhb_28days=list[i].replace("\n","").substring(index_list4+length_list4,index_list4+length_list4+19).trim().replace("%","");
				int index2=list[i].replace("\n","").indexOf("<div class=\"value\">");
				int length2="<div class=\"value\">".length();
				String profit=list[i].replace("\n","").substring(index2+length2,index2+length2+19).trim().replace("元","");
				System.out.println("零活宝-28日聚财="+lhb_28days+"  万份收益="+profit);
				linghuobao_28days=new Rate();
				linghuobao_28days.setStartAccount("10000.00元");
				linghuobao_28days.setLockups("0");
				linghuobao_28days.setTime(System.currentTimeMillis()/1000l);
				linghuobao_28days.setName("零活宝-28日聚财");
				linghuobao_28days.setCode("linghuobao28Days");
				linghuobao_28days.setRate(lhb_28days);
				linghuobao_28days.setProfitAccount(profit);
				linghuobao_28days.setType(2);
				linghuobao_28days.setOrder(7);
				linghuobao_28days.setManager("陆金所");
				linghuobao_28days.setTrusteeship("未知");
				linghuobao_28days.setIntestes("日复利");
				linghuobao_28days.setStatus("零活赎买");
				linghuobao_28days.setRiskrank("未知");
				InterestStore.rateMap.put("linghuobao_28days", linghuobao_28days);


				Calendar cal   =   Calendar.getInstance();
				cal.add(Calendar.DATE,   -1);
				String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());

				String sql_select="select * from mr_recorders   where date='"+yesterday+"' and productName='零活宝-28日聚财'";
				String sql_update="update mr_recorders set rate='"+lhb_28days+"', profit='"+profit+"'  where date='"+yesterday+"' and productName='零活宝-28日聚财'";
				String sql_insert="insert into mr_recorders(productName,ProductCode,type,rate,profit,lockups,startAccount,manager,trusteeship,riskrank,intestes,date,productStatus) values('零活宝-28日聚财','linghuobao28Days','2',"+lhb_28days+","+profit+",'0天','1.00元','陆金所','未知','5星','日复利','"+yesterday+"','零活赎买')";
				ArrayList arr=selectRecorder(sql_select);
				if(arr.size()==0){
					addRecorder(sql_insert);
				}else{
					addRecorder(sql_update);
				}
			}

		}

		}catch(Exception ex) {
			ex.printStackTrace();
			logger.error("灵活宝利率获取失败......"+ex.getMessage());
		}

	}
	
	/**
	 * 任我赢  e路同心
	 */
	public static void getContent9( )
	{
		String url="https://www.88bank.com/invest/current/6096ea7f52104b4b8315a78bb5ff56ce";
		String rateStr="";
		Document document = HtmlDocument.getHtmlDocument(url);
		if(document == null) return ;
		Rate renwoying =null;
		try{
			Elements rateEles= document.select("h2.c-blue").select("span.f-60");
			if(rateEles!=null && rateEles.size()>=1 )
			{
				rateStr=rateEles.text().trim();
				
				Element parent=rateEles.get(0);
				String rate1=parent.getElementsByTag("em").text().trim();
				String rate2=parent.getElementsByTag("span").text().trim();
				rateStr=rate1+rate2;
				/**
				 * e路同心（www.88bank.com），全称深圳市同心科创金融服务有限公司，由深圳同心基金和广东粤科集团共同发起成立，总部位于深圳的一家互联网金融服务平台，致力于为中小企业和个人投资者提供投融资信息中介服务，注册资金2亿元（查看企业资质），于2015年1月18日上线试运营
				 */

				if(rateStr!=null)
				{
					renwoying=new Rate();
					renwoying.setCode("renwoying");
					renwoying.setName("任我赢");
					renwoying.setOrder(8);
					renwoying.setLockups("0 天");
					renwoying.setRate(rateStr);
					renwoying.setType(2);
					renwoying.setProfitAccount(String.format("%.5f",(10000*Double.parseDouble(rateStr)/36500)));
					renwoying.setTime(System.currentTimeMillis()/1000l);
					renwoying.setStartAccount("100.00元");
					renwoying.setManager("e路同心");
					renwoying.setTrusteeship("未知");
					renwoying.setIntestes("日复利");
					renwoying.setStatus("零活赎买");
					renwoying.setRiskrank("未知");
					InterestStore.rateMap.put("renwoying", renwoying);
					System.out.println("任我赢利率="+rateStr+"   万分收益="+String.format("%.5f",(10000*Double.parseDouble(rateStr)/36500)));
				}
			
			}
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
			logger.error("任我赢  e路同心 获取失败......"+ex.getMessage());
		}
	}

	/**
	 * 活期赢  百度金融
	 */
	public static void getContent10( ) throws IOException, JSONException  {
		String url="https://8.baidu.com/product/yieldinfo/current?itemId=91000002&yieldRequestType=1&channel=5";
		InputStream is = new URL(url).openStream();
		Connection conn = null;
		PreparedStatement pst = null;
		int row=0;
		Rate baiduWallet =null;
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);//这个可以得到数据。
			String[] array= json.get("itemYieldInfoList").toString().substring(1,json.get("itemYieldInfoList").toString().length()-1).split("},");
			String[] arr=array[0].split(",");
			String releaseTime=arr[0].split(":")[1].substring(1,arr[0].split(":")[1].length()-1);
			String yieldSevenDays=arr[1].split(":")[1].substring(1,arr[1].split(":")[1].length()-1);
			String yieldTenThousands=arr[2].split(":")[1].substring(1,arr[2].split(":")[1].length()-1);
		//	System.out.println(baiduWallet);百度金融起步于2013年，正式成立于2015年12月
            if(!yieldSevenDays.isEmpty()){
				baiduWallet=new Rate();
				baiduWallet.setCode("baiduWallet");
				baiduWallet.setName("活期赢");
				baiduWallet.setLockups("0 天");
				baiduWallet.setOrder(9);
				baiduWallet.setRate(yieldSevenDays);
				baiduWallet.setType(1);
				baiduWallet.setProfitAccount(yieldTenThousands);
				baiduWallet.setTime(System.currentTimeMillis()/1000l);
				baiduWallet.setStartAccount("1.00元");
				baiduWallet.setManager("百度金融");
				baiduWallet.setTrusteeship("未知");
				baiduWallet.setIntestes("单利");
				baiduWallet.setStatus("零活赎买");
				baiduWallet.setRiskrank("中低");
				InterestStore.rateMap.put("baiduWallet", baiduWallet);
				System.out.println("活期赢昨日年华收益利率="+yieldSevenDays+"   万分收益="+yieldTenThousands);


				Calendar cal   =   Calendar.getInstance();
				cal.add(Calendar.DATE,   -1);
				String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());

				String sql_select="select * from mr_recorders   where date='"+yesterday+"' and productName='活期赢'";
				String sql_update="update mr_recorders set rate='"+yieldSevenDays+"', profit='"+yieldTenThousands+"'  where date='"+yesterday+"' and productName='活期赢'";
				String sql_insert="insert into mr_recorders(productName,ProductCode,type,rate,profit,lockups,startAccount,manager,trusteeship,riskrank,intestes,date,productStatus) values('活期赢','huoqiying','1',"+yieldSevenDays+","+yieldTenThousands+",'0天','1.00元','百度金融','未知','中低','单利','"+releaseTime+"','零活赎买')";
				ArrayList list=selectRecorder(sql_select);
                if(list.size()==0){
					addRecorder(sql_insert);
				}else{
					addRecorder(sql_update);
				}
			}

		}catch(Exception ex) {
			ex.printStackTrace();
			logger.error(" 百度钱包 获取失败......"+ex.getMessage());
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * 快钱  快利来
	 */
	public static void getContent11( )
	{
		String url="https://jr.99bill.com/atp-platform/common/recome.htm?method=index";
		Document document = HtmlDocument.getHtmlDocument(url);
		if(document == null) return ;
		Rate kuaiqian=null;
		try{
			String content=document.toString().replace("\n","");
			int index=content.indexOf("<span class=\"num\">");
			int length="<span class=\"num\">".length();
			String rate=content.substring(index+length,index+length+6);
			System.out.println("快钱7日年利率="+rate);
			kuaiqian=new Rate();
			kuaiqian.setStartAccount("1.00元");
			kuaiqian.setLockups("0");
			kuaiqian.setTime(System.currentTimeMillis()/1000l);
			kuaiqian.setName("快利来");
			kuaiqian.setCode("kuaiqian");
			kuaiqian.setRate(rate);
			String profit=String.format("%.5f",(10000*Double.parseDouble(rate)/36500));
			kuaiqian.setProfitAccount(profit);
			kuaiqian.setType(1);
			kuaiqian.setOrder(10);
			kuaiqian.setManager("快钱");
			kuaiqian.setTrusteeship("未知");
			kuaiqian.setIntestes("日复利");
			kuaiqian.setStatus("零活赎买");
			kuaiqian.setRiskrank("未知");
			InterestStore.rateMap.put("kuaiqian", kuaiqian);

			Calendar cal   =   Calendar.getInstance();
			cal.add(Calendar.DATE,   -1);
			String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());


			String sql_select="select * from mr_recorders   where date='"+yesterday+"' and productName='快利来'";
			String sql_update="update mr_recorders set rate='"+rate+"', profit='"+profit+"'  where date='"+yesterday+"' and productName='快利来'";
			String sql_insert="insert into mr_recorders(productName,ProductCode,type,rate,profit,lockups,startAccount,manager,trusteeship,riskrank,intestes,date,productStatus) values('快利来','kuaiqian','1',"+rate+","+profit+",'0天','1.00元','快钱','未知','未知','日复利','"+yesterday+"','零活赎买')";
			ArrayList list=selectRecorder(sql_select);
			if(list.size()==0){
				addRecorder(sql_insert);
			}else{
				addRecorder(sql_update);
			}

		}catch(Exception ex) {
			ex.printStackTrace();
			logger.error(" 快钱 获取失败......"+ex.getMessage());
		}
	}
	/**
	 * 增金宝 国寿安保
	 */
	public static void getContent12( )
	{
		String url="https://e.lufunds.com/jijin/detail?productId=31380081";
		Document document = HtmlDocument.getHtmlDocument(url);
		if(document == null) return ;
		Rate zenjinjbao=null;
		try{
			String content=document.toString().replace("\n","");
			int index=content.indexOf("<div class=\"col-2\">          <p>");
            int length="<div class=\"col-2\">          <p>".length();
			String rate=content.substring(index+length,index+length+4);
			int index2=content.indexOf("<div class=\"col-1\">          <p>");
			int length2="<div class=\"col-1\">          <p>".length();
			String profit=content.substring(index2+length2,index2+length2+6);
			System.out.println("增金宝7日年利率="+rate+"  万份收益="+profit);
			zenjinjbao=new Rate();
			zenjinjbao.setStartAccount("1.00元");
			zenjinjbao.setLockups("0");
			zenjinjbao.setTime(System.currentTimeMillis()/1000l);
			zenjinjbao.setName("增金宝");
			zenjinjbao.setCode("zenjinbao");
			zenjinjbao.setRate(rate);
			zenjinjbao.setProfitAccount(profit);
			zenjinjbao.setType(1);
			zenjinjbao.setOrder(11);
			zenjinjbao.setManager("国寿安保");
			zenjinjbao.setTrusteeship("未知");
			zenjinjbao.setIntestes("日复利");
			zenjinjbao.setStatus("零活赎买");
			zenjinjbao.setRiskrank("低风险");
			InterestStore.rateMap.put("zenjinjbao", zenjinjbao);

			Calendar cal   =   Calendar.getInstance();
			cal.add(Calendar.DATE,   -1);
			String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());

			String sql_select="select * from mr_recorders   where date='"+yesterday+"' and productName='增金宝'";
			String sql_update="update mr_recorders set rate='"+rate+"', profit='"+profit+"'  where date='"+yesterday+"' and productName='增金宝'";
			String sql_insert="insert into mr_recorders(productName,ProductCode,type,rate,profit,lockups,startAccount,manager,trusteeship,riskrank,intestes,date,productStatus) values('增金宝','zenjinbao','1',"+rate+","+profit+",'0天','1.00元','国寿安保','未知','低风险','日复利','"+yesterday+"','零活赎买')";
			ArrayList list=selectRecorder(sql_select);
			if(list.size()==0){
				addRecorder(sql_insert);
			}else{
				addRecorder(sql_update);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			logger.error(" 增金宝 获取失败......"+ex.getMessage());
		}
	}



}
