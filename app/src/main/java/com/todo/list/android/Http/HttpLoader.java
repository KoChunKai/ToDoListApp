package com.todo.list.android.Http;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import okhttp3.MultipartBody;
import okhttp3.Response;

/**
 * Created by kevin on 2016/12/23.
 */
public class HttpLoader {

    public final static String EXECUTE_RESULT = "result";
    private HashMap<String, ParamData> paramData = new HashMap<>();
    private String Url = "";
    private Handler handler = null;
    private boolean isBuild = false;
    private byte HttpType = 1;
    /*
         *    1 GET
         *    2 POST
         *    4 PUT
         *    8 DELETE
         *    16 Have File
         *    32 Chat
         * */
    private Bundle extraData = new Bundle();
    private StringBuilder sb = new StringBuilder();
    private HashMap<String, String> moreHeader = new HashMap<>();
    private int SuccessCode = 200, FailureCode = -999;
    private MultipartBody.Builder m_params = new MultipartBody.Builder().setType(MultipartBody.FORM);

    public HttpLoader(){

    }

    public HttpLoader setUrl(String Url){
        this.Url = Url;
        return this;
    }

    public HttpLoader setHandler(Handler handler, int SuccessCode, int FailureCode){
        this.handler = handler;
        this.SuccessCode = SuccessCode;
        this.FailureCode = FailureCode;
        return this;
    }

    public HttpLoader set_paramData(String key, ParamData value){
        paramData.put(key, value);
        m_params.addFormDataPart(key, String.valueOf(value.getData()));
        return this;
    }

    public HttpLoader setmoreHeader(String key, String value){
        moreHeader.put(key, value);
        return this;
    }

    public HttpLoader setExtraData(Serializable object){
        extraData.putSerializable("Data", object);
        return this;
    }

    public HttpLoader setGet(){
        HttpType = 1;
        return this;
    }

    public HttpLoader setPost(){
        HttpType = 2;
        return this;
    }

    public HttpLoader setFilePost(){
        HttpType = 4;
        return this;
    }

    public HttpLoader setPut(){
        HttpType = 8;
        return this;
    }

    public HttpLoader setDelete(){
        HttpType = 16;
        return this;
    }

    public HttpLoader build(){
        if(handler == null){
            handler = new Handler();
        }
        isBuild = true;
        return this;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(isBuild){
                HttpConnection connection = new HttpConnection();
                connection.setMoreHeader(moreHeader);
                Response response = null;
                if(HttpType  == 1){
                    Log.w("Httploader", "HttpType & 1");
                    response = connection.doGet(Url + getPara());
                }else if(HttpType == 2){
                    Log.w("Httploader", "HttpType & 2");
                    response = connection.doPost(Url, m_params);
                }else if(HttpType == 16){
                    Log.w("Httploader", "HttpType & 16");
                    response = connection.doDelete(Url);
                }
                if(response == null){
                    Log.w("Httploader", "response == null");
                    Message message = new Message();
                    message.what = FailureCode;
                    message.setData(extraData);
                    handler.sendMessage(message);
                    return;
                }
                int statusCode = response.code();
                Log.w("Httploader", "statusCode = " + statusCode);
                String result = "";
                try {
                    result = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                extraData.putString(EXECUTE_RESULT, result);
                message.what = SuccessCode;
                message.setData(extraData);
                handler.sendMessage(message);
            }else{
                Log.w("Httploader", "Httploader is not build,");
            }
        }
    };

    private String getPara(){
        String Para;
        if (paramData.size() > 0) {
            for(String key : paramData.keySet()){
                sb.append("&").append(key).append("=").append(String.valueOf(paramData.get(key)));
            }
            Para = sb.toString();
        } else {
            Para = "";
        }
        return Para;
    }

    private String getResult(Response response){
        String result = "{}";
        try {
            if("gzip".equals(response.header("Content-Encoding", "").toLowerCase())){
                InputStream instream = new GZIPInputStream(response.body().byteStream());
                ByteArrayOutputStream o = new ByteArrayOutputStream(instream.available());
                byte[] inn = new byte[instream.available()];
                while ((instream.read(inn)) > -1) {
                    o.write(inn);
                }
                result = new String(o.toByteArray(), "utf-8");
                o.close();
                instream.close();
            }else{
                result = response.body().string();
            }
        } catch (IOException e) {

        }
        return result;
    }

    public void start(){
//        new Thread(runnable).start();
        ThreadCenter.getInstance().start(runnable);
    }
}
