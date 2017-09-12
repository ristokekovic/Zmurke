package com.example.riki.myplaces;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    public void getAnyUser(final String apiToken, final String id)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("https://zmurke.herokuapp.com/api/user/" + id)
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


    public void update(final String firstname, final String lastname,final String phone, final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("first_name", firstname)
                            .add("last_name", lastname)
                            .add("phone_number",phone)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://zmurke.herokuapp.com/api/update")
                            .post(formBody)
                            .addHeader("api", apiToken)
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


   /* public void upload1(String url, File file) throws IOException {
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("text/plain"), file))
                .addFormDataPart("other_field", "other_field_value")
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        Response response = this.client.newCall(request).execute();
    }*/


    public void newUpdate(final String firstname, final String lastname, final String phone, final File photo, final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("first_name", firstname)
                            .addFormDataPart("last_name", lastname)
                            .addFormDataPart("phone_number",phone)
                            .addFormDataPart("avatar", photo.getName(),
                                    RequestBody.create(MediaType.parse("image/png"), photo))
                            //.addFormDataPart("avatar", photo.getName(),
                           // RequestBody.create(MediaType.parse("text/plain"), photo))
                            .build();
                    Request request = new Request.Builder()
                            .url("https://zmurke.herokuapp.com/api/update")
                            .post(formBody)
                            .addHeader("api", apiToken)
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


    public void updatePass(final String opass, final String npass, final String email, final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("old_password", opass)
                            .add("new_password",npass)
                            .add("email",email )
                            .build();
                    Request request = new Request.Builder()
                            .url("https://zmurke.herokuapp.com/api/update")
                            .post(formBody)
                            .addHeader("api", apiToken)
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

    public void getFriends(final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("https://zmurke.herokuapp.com/api/user/friends")
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void addFriend(final int friendId, final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("friend_id", String.valueOf(friendId))
                            .build();
                    Request request = new Request.Builder()
                            .url("https://zmurke.herokuapp.com/api/user/friends")
                            .post(formBody)
                            .addHeader("api", apiToken)
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

    public void getLocation(int id, final String apiToken)
    {
        final String url = "https://zmurke.herokuapp.com/api/user/" + id + "/location";
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void addLocation(final float latitude, final float longitude, final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("latitude", String.valueOf(latitude))
                            .add("longitude", String.valueOf(longitude))
                            .build();
                    Request request = new Request.Builder()
                            .url("https://zmurke.herokuapp.com/api/location")
                            .post(formBody)
                            .addHeader("api", apiToken)
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

    public void createGame(final int numberOfPlayers, final int timeLimit, final int[] players, final String apiToken)
    {
        String playersString = "";
        for(int i = 0; i < players.length; i++)
            playersString += String.valueOf(players[i]) + ",";
        final String playrs = playersString;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("number_of_players", String.valueOf(numberOfPlayers))
                            .add("time_limit", String.valueOf(timeLimit))
                            .add("players", playrs)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://zmurke.herokuapp.com/api/game")
                            .post(formBody)
                            .addHeader("api", apiToken)
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

    public void getGame(final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("https://zmurke.herokuapp.com/api/game")
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void getFriendsLocation(final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("https://zmurke.herokuapp.com/api/user/friends_location")
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void createSafeZone(final String apiToken, final double latitude, final double longitude)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("latitude", String.valueOf(latitude))
                            .add("longitude", String.valueOf(longitude))
                            .build();
                    Request request = new Request.Builder()
                            .url("https://zmurke.herokuapp.com/api/safe_zone")
                            .post(formBody)
                            .addHeader("api", apiToken)
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

    public void getSafeZone(final String apiToken)
    {
        final String url = "https://zmurke.herokuapp.com/api/safe_zone";
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }


    public void getFriendsSafeZones(final String apiToken)
    {
        final String url = "https://zmurke.herokuapp.com/api/user/friends_safe_zones";
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                } catch (IOException e) {
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
