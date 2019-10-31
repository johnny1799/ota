package com.asu.ota.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Request {

    //post请求
    public String sendPost(String url, String param) {
        //网络请求对应的输出流，就是客户端把参数给服务器  叫输出，
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {

            return "send_fail";
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public String  sendGet(String path)throws Exception{
        String responseText = "";
        try{
            URL url = new URL(path);
            //2. HttpURLConnection
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            //3. set(GET)
            conn.setRequestMethod("GET");
            //4. getInputStream
            InputStream is = conn.getInputStream();
            //5. 解析is，获取responseText，这里用缓冲字符流
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line=reader.readLine()) != null){
                sb.append(line);
            }
            //获取响应文本
            responseText = sb.toString();
        }catch (Exception e){
            Log.e("product list:", e.getMessage());
        }
        return  responseText;
    }

}
