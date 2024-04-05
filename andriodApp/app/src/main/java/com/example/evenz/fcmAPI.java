package com.example.evenz;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface fcmAPI {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAb9RcgnE:APA91bEVdYUOmWwKUvPUD79seqqpOpOeoic9zWRhMJdnPaJOlwPD0VNu00a8MuEbzXUVrUuXOCAK1ZAqjfhlFKMkWWzGW-V-OCzwKTwHQl-7P0Vyas9NxvnXwMCa-JbyJ3RzmoNG7BDu"
    })
    @POST("fcm/send")
    Call<JsonObject> sendNotification(@Body JsonObject payload);
}