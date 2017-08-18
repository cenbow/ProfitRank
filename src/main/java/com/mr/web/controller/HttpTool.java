package com.mr.web.controller;

import com.mr.util.Base64Util;
import com.mr.util.MD5Util;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class HttpTool {

    /**
     * 发送post请求
     *
     * @author leohorry
     * @param params
     *            参数
     * @param requestUrl
     *            请求地址
     * @param authorization
     *            授权书
     * @return 返回结果
     * @throws IOException
     */
    public static String sendPost(String params, String requestUrl) throws IOException {

        byte[] requestBytes = params.getBytes("utf-8"); // 将参数转为二进制流
        HttpClient httpClient = new HttpClient();// 客户端实例化
        PostMethod postMethod = new PostMethod(requestUrl);
        //设置请求头
        postMethod.setRequestHeader("appId", "wms_jst");
        String str = Base64Util.getEncodedBase64(params,"UTF-8")+"69bb5313-f3de-4be4-8d8d-0024298a00c7";
        String str_md5 = MD5Util.MD5Encode(str,"UTF-8",false);
        postMethod.setRequestHeader("md5", str_md5);
        // 设置请求头  Content-Type
        postMethod.setRequestHeader("Content-Type", "application/json");
        InputStream inputStream = new ByteArrayInputStream(requestBytes, 0,
                requestBytes.length);
        RequestEntity requestEntity = new InputStreamRequestEntity(inputStream,
                requestBytes.length, "application/json; charset=utf-8"); // 请求体
        postMethod.setRequestEntity(requestEntity);
        httpClient.executeMethod(postMethod);// 执行请求
        InputStream soapResponseStream = postMethod.getResponseBodyAsStream();// 获取返回的流
        byte[] datas = null;
        try {
            datas = readInputStream(soapResponseStream);// 从输入流中读取数据
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = new String(datas, "UTF-8");// 将二进制流转为String
        // 打印返回结果
        // System.out.println(result);

        return result;

    }

    /**
     * 从输入流中读取数据
     *
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }

    public static void main(String[] args) throws IOException {
       String data = sendPost("{\t\"proId\":\"123213\",\"requestTime\":\"2017-08-09\"}","https://gasq-web-thirdparty.guoanshequ.wang/gasq-web-thirdparty/protest/inserttest");
        System.out.println(data);
    }
}
