package com.example.shaishavgandhi.assistant.network;

import com.example.shaishavgandhi.assistant.data.models.AssistantResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by shaishav.gandhi on 1/7/17.
 */

public interface APIService {

    @GET("query")
    Call<AssistantResponse> submitQuery(@Query("q") String query);

}
