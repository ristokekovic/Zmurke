package com.example.riki.myplaces;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Riki on 7/28/2017.
 */

public class DownloadManager {
    private static DownloadManager instance = new DownloadManager();
    private OkHttpClient client = new OkHttpClient();
    private IThreadWakeUp wakeUp;

    private DownloadManager()
    {

    }

    public static DownloadManager getInstance()
    {
        return instance;
    }

    public void getUser(final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("https://zmurke.herokuapp.com/api/user")
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void register(final String name, final String email, final String password)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("name", name)
                            .add("email", email)
                            .add("password", password)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://zmurke.herokuapp.com/api/register")
                            .post(formBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void login(final String email, final String password)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("email", email)
                            .add("password", password)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://zmurke.herokuapp.com/api/login")
                            .post(formBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void setThreadWakeUp(IThreadWakeUp i)
    {
        wakeUp = i;
    }
}
