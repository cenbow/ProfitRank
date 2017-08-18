package com.mr.web.controller;

import com.mr.datagather.bean.Page;
import com.mr.util.Base64Util;
import com.mr.util.DateUtil;
import com.mr.util.MD5Util;
import com.mr.web.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * Created by Administrator on 2017/8/9.
 */
//字段名	参数位置	参数类型	描述
//        appId	header	String	由国安社区提供
//        md5	header	String	将请求参数用base64加密后，拼接上秘钥（由国安社区提供），然后求得md5值放入header中
//        请求参数	body	String	在参数中增加请求时间戳参数，并取得base64加密，放入请求body中。
//        时间戳格式:yyyy-MM-dd HH:mm:ss
//

@Controller
@RequestMapping("/wms")
public class WMS extends BaseController {

    @RequestMapping(value = "/test.do", method = RequestMethod.POST)
    public  void test(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        String Str_base64=null;
        request.setCharacterEncoding("utf-8");
        Page ret=new Page();
        String appId= request.getHeader("appId");
        String md5_origen=request.getHeader("md5");
        String receive=request.getParameterNames().nextElement();
        System.out.println("1.解析的base64="+receive);
        String temp=receive+"69bb5313-f3de-4be4-8d8d-0024298a00c7";
        String md5_cal= MD5Util.MD5Encode(temp,"UTF-8",false);
        System.out.println("2.原始的md5_origen="+md5_origen+"<<<--------->>>md5_cal="+md5_cal);
        //经过appId和md5验证后才处理和返回数据。
        if("wms_jst".equals(appId) && md5_origen.equals(md5_cal)){
            //-----------------接收处理------------------------------------
            String json= Base64Util.getDecodedBase64(receive,"UTF-8");
            //-------------------返回处理-----------------------------------
            String jsonobj = "{\"code\":\"1\",\"msg\":\"同步成功\",\"SkuId\":\"P153526394556549971\",\"SkuName\":20,\"Pcs\":8,\"BarCode\":\"6903148026076\",\"Unit\":\"刘\", \"MerchantCode\":\"E162014093443905776\",\"TimeStamp\":"+DateUtil.todayFormate("yyyy-MM-dd HH:mm:ss")+"}";
            Str_base64=Base64Util.getEncodedBase64(jsonobj,"UTF-8");
            String t=Str_base64+"69bb5313-f3de-4be4-8d8d-0024298a00c7";
            String str_md5= MD5Util.MD5Encode(t,"UTF-8",false);
            response.setHeader("appId","wms_jst");
            response.setHeader("md5",str_md5);
            System.out.println("3.返回str_md5==>>"+Str_base64);
            System.out.println("4.md5="+str_md5);

//            try{
//                String jstr= "{\"code\":\"1\",\"msg\":\"同步成功\",\"SkuId\":\"P153526394556549971\",\"SkuName\":20,\"Pcs\":8,\"BarCode\":\"6903148026076\",\"Unit\":\"刘\", \"MerchantCode\":\"E162014093443905776\",\"TimeStamp\":"+DateUtil.todayFormate("yyyy-MM-dd HH:mm:ss")+"}";
//                String success="{\"code\":\"1\",\"msg\":\"同步成功\"}";
//                response.getWriter().write(jstr);
//                response.getWriter().flush();
//                response.getWriter().close();
//            }catch(IOException e){
//                e.printStackTrace();
//            }
            ret.setCode(1);
            ret.setMsg("同步成功");
            ret.setItem(Str_base64);
            try {
                outJson(response, ret);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
              String error="{\"code\":\"0\",\"msg\":\"同步失败\"}";
            try{
                response.getWriter().write(error);
                response.getWriter().flush();
                response.getWriter().close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

    }

}
