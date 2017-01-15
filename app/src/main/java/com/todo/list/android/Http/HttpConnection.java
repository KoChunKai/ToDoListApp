package com.todo.list.android.Http;

import com.todo.list.android.MyApp;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kevin on 2016/12/23.
 */
public class HttpConnection {

    public static OkHttpClient client = null;
    public static String USER_AGENT = "";
    private HashMap<String, String> moreHeader = null;

    public HttpConnection() {
        if(client == null){
            OkHttpClient.Builder config = new OkHttpClient.Builder();
            config.connectTimeout(120, TimeUnit.SECONDS);
            config.readTimeout(120, TimeUnit.SECONDS);
            config.writeTimeout(120, TimeUnit.SECONDS);
            config.cookieJar(new CookieJar() {
                private final PersistentCookieStore cookieStore = new PersistentCookieStore(MyApp.getContext());

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    if (cookies != null && cookies.size() > 0) {
                        for (Cookie item : cookies) {
                            cookieStore.add(url, item);
                        }
                    }
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url);
                    return cookies;
                }
            });
            client = config.build();
        }
    }

    public Response doPost(String RequestURL, MultipartBody.Builder m_entity) {
        RequestBody requestBody;
        try{
            requestBody = m_entity.build();
        }catch (Exception e){
            requestBody = new FormBody.Builder().build();
        }
        Request.Builder request = new Request.Builder()
                .url(RequestURL)
                .post(requestBody);
        if (moreHeader != null && moreHeader.size() > 0) {
            for(String key : moreHeader.keySet()){
                request.addHeader(key, moreHeader.get(key));
            }
        }
        return getResponse(request);
    }

    public Response doGet(String RequestURL) {
        Request.Builder request = new Request.Builder()
                .url(RequestURL)
                .get();
        return getResponse(request);
    }

    public Response doDelete(String RequestURL) {
        Request.Builder request = new Request.Builder()
                .url(RequestURL)
                .delete();
        if (moreHeader != null && moreHeader.size() > 0) {
            for(String key : moreHeader.keySet()){
                request.addHeader(key, moreHeader.get(key));
            }
        }
        return getResponse(request);
    }

    public Response doPut(String RequestURL, File file) {
        Request.Builder request = new Request.Builder()
                .url(RequestURL)
                .put(RequestBody.create(MediaType.parse("binary/octet-stream"), file));
        if (moreHeader != null && moreHeader.size() > 0) {
            for(String key : moreHeader.keySet()){
                request.addHeader(key, moreHeader.get(key));
            }
        }
        return getResponse(request);
    }

    public void setMoreHeader(HashMap<String, String> moreHeader) {
        this.moreHeader = moreHeader;
    }

    public Response getResponse(Request.Builder request) {
        Response response = null;
        try {
            request.addHeader("User-Agent", USER_AGENT);
            if(moreHeader != null){
                Set<String> set = moreHeader.keySet();
                for(String key : set){
                    request.addHeader(key.toString(), moreHeader.get(key));
                }
            }
            response = client.newCall(request.build()).execute();
        } catch (SocketTimeoutException e){
            e.printStackTrace();
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

}
