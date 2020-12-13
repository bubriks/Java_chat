package com.example.chatapplication.ui.user;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.chatapplication.R;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {

    private static String tag = "Settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText username = findViewById(R.id.editTextUsername);
        EditText password = findViewById(R.id.editTextPassword);

        AndroidNetworking.get(getResources().getString(R.string.url)+"user/{userId}")
                .addPathParameter("userId", LoginActivity.id.toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(tag,response.toString());
                        try {
                            username.setText(response.getString("username"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.d(tag,error.toString());
                        Toast.makeText(getApplicationContext(),"Bad information", Toast.LENGTH_SHORT).show();
                    }
                });

        findViewById(R.id.buttonSave).setOnClickListener(v -> {
            try {
                JSONObject object = new JSONObject();
                object.put("username",username.getText());
                object.put("password",password.getText());

                AndroidNetworking.put(getResources().getString(R.string.url)+"user/{userId}")
                        .addPathParameter("userId", LoginActivity.id.toString())
                        .addJSONObjectBody(object)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(tag,response);
                                finish();
                            }
                            @Override
                            public void onError(ANError error) {
                                Log.d(tag,error.toString());
                                Toast.makeText(getApplicationContext(),"Bad information", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (JSONException e) {
                Log.d(tag,e.toString());
            }
        });
    }
}