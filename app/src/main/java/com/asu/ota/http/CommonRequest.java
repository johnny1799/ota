package com.asu.ota.http;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class CommonRequest {

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
            Log.e(path, e.getMessage());
        }
        return  responseText;
    }

    //delete请求
    public String  sendDelete(String path)throws Exception{
        String responseText = "";
        try{
            URL url = new URL(path);
            //2. HttpURLConnection
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            //3. set(GET)
            conn.setRequestMethod("DELETE");
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
            Log.e("product delete:", e.getMessage());
        }
        return  responseText;
    }
    //put 请求
    public String  sendPut(String path)throws Exception{
        String responseText = "";
        try{
            URL url = new URL(path);
            //2. HttpURLConnection
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            //3. set(GET)
            conn.setRequestMethod("PUT");
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
            Log.e("product delete:", e.getMessage());
        }
        return  responseText;
    }

    public void downloadFile(String path){
        final String url = path;
        final long startTime = System.currentTimeMillis();
        Log.i("DOWNLOAD","startTime="+startTime);
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                e.printStackTrace();
                Log.i("DOWNLOAD","download failed");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath, url.substring(url.lastIndexOf("/") + 1));
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
//                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
//                    listener.onDownloadSuccess();
                    Log.i("DOWNLOAD","download success");
                    Log.i("DOWNLOAD","totalTime="+ (System.currentTimeMillis() - startTime));
                } catch (Exception e) {
                    e.printStackTrace();
//                    listener.onDownloadFailed();
                    Log.i("DOWNLOAD","download failed");
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }


    public void downloadFile3(String path) {
        //下载路径，如果路径无效了，可换成你的下载路径
        final String url = path;
        final long startTime = System.currentTimeMillis();
        Log.i("DOWNLOAD", "startTime=" + startTime);

        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                e.printStackTrace();
                Log.i("DOWNLOAD", "download failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    String mSDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    File dest = new File(mSDCardPath, url.substring(url.lastIndexOf("/") + 1));
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());

                    bufferedSink.close();
                    Log.i("DOWNLOAD", "download success");
                    Log.i("DOWNLOAD", "totalTime=" + (System.currentTimeMillis() - startTime));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("DOWNLOAD", "download failed");
                } finally {
                    if (bufferedSink != null) {
                        bufferedSink.close();
                    }

                }
            }
        });
    }

    public void downloadFile1(String url) {
        try{
            //下载路径，如果路径无效了，可换成你的下载路径
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();

            final long startTime = System.currentTimeMillis();
            Log.i("DOWNLOAD","startTime="+startTime);
            //下载函数
            String filename=url.substring(url.lastIndexOf("/") + 1);
            //获取文件名
            URL myURL = new URL(url);
            URLConnection conn = myURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            int fileSize = conn.getContentLength();//根据响应获取文件大小
            if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
            if (is == null) throw new RuntimeException("stream is null");
            File file1 = new File(path);
            if(!file1.exists()){
                file1.mkdirs();
            }
            //把数据存入路径+文件名
            FileOutputStream fos = new FileOutputStream(path+"/"+filename);
            byte buf[] = new byte[1024];
            int downLoadFileSize = 0;
            do{
                //循环读取
                int numread = is.read(buf);
                if (numread == -1)
                {
                    break;
                }
                fos.write(buf, 0, numread);
                downLoadFileSize += numread;
                //更新进度条
            } while (true);

            Log.i("DOWNLOAD","download success");
            Log.i("DOWNLOAD","totalTime="+ (System.currentTimeMillis() - startTime));

            is.close();
        } catch (Exception ex) {
            Log.e("DOWNLOAD", "error: " + ex.getMessage(), ex);
        }
    }



    /** 获取指定网络文件url，保存至本地文件路径filePath */
    public static void DownloadFile(final String url, final String filePath)
    {
        ConfirmFile(filePath);

        Executors.newCachedThreadPool().execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    URL webUrl = new URL(url);
                    URLConnection con = webUrl.openConnection();	// 打开连接
                    InputStream in = con.getInputStream();			// 获取InputStream

                    File f = new File(filePath);					// 创建文件输出流
                    FileOutputStream fo = new FileOutputStream(f);

                    byte[] buffer = new byte[1024 * 1024];
                    int len = 0;
                    while( (len = in.read(buffer)) > 0)		// 读取文件
                    {
                        fo.write(buffer, 0, len); 			// 写入文件
                    }

                    in.close();

                    fo.flush();
                    fo.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /** 创建目录和文件 */
    public static void ConfirmFile(String filePath)
    {
        try
        {
            File f = new File(filePath);
            File parent = f.getParentFile();

            if (!parent.exists()) parent.mkdirs();
            if (!f.exists()) f.createNewFile();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
