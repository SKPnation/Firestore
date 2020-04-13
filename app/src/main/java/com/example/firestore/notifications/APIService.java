package com.example.firestore.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAQSVc1KE:APA91bG7F1mdHFfZpzghKkzDighaY2cizAhZ8CfdtvG-HqkcLRf9K_0DPCzcUtqgMpIPtc7eZZBCd3TH4voxaHk4if32O7p-m8PEawiWhZHWQoLF-0enR1LdVfSMXar9P_95_0qOS0FY"
    })
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
