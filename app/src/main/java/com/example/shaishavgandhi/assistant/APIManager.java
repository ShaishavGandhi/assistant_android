package com.example.shaishavgandhi.assistant;

import android.content.Context;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by shaishav.gandhi on 1/7/17.
 */

public class APIManager {

    private static APIManager mAPIManager;
    private Context mContext;
    public static final String BASE_URL = "http://10.0.1.51:5000/";
    Retrofit retrofit;

    private APIManager(Context context) {
        mContext = context;
        init();
    }

    public static APIManager getInstance(Context context) {
        if (mAPIManager == null) {
            mAPIManager = new APIManager(context);
        }

        return mAPIManager;
    }

    private void init() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void submitQuery(String query) {
        APIService apiService =
                retrofit.create(APIService.class);
        Call<AssistantResponse> call = apiService.submitQuery(query);
        call.enqueue(new Callback<AssistantResponse>() {
            @Override
            public void onResponse(Call<AssistantResponse> call, Response<AssistantResponse> response) {
                Toast.makeText(mContext, "Command Successfully Sent!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<AssistantResponse> call, Throwable t) {
                Toast.makeText(mContext, "Command Failed. Sorry about that!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
