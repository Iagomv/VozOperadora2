package com.example.vozoperadora;

import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Cliente {

    private static Retrofit retrofit;

    // Get Retrofit instance
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://imartinez.colexio-karbo.com/") // Your base URL
                    .addConverterFactory(GsonConverterFactory.create())  // Gson converter
                    .build();
        }
        return retrofit;
    }

    // Send the recorded text to the server
    public static void enviarAlServidor(String recordedText) {
        ApiService apiService = getRetrofitInstance().create(ApiService.class);

        // Prepare the call
        Call<ApiResponse> call = apiService.enviarTexto(recordedText);

        // Execute the call asynchronously
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Retrofit", "Response: " + response.body().getMessage());
                } else {
                    Log.e("Retrofit", "Request failed with status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("Retrofit", "Error: " + t.getMessage());
            }
        });
    }
}
