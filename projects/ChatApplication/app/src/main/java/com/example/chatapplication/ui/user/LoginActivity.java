package com.example.chatapplication.ui.user;

import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.chatapplication.R;
import com.example.chatapplication.extra.SslHttpClient;
import com.example.chatapplication.ui.main.MainActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    public static UUID id;
    private static String tag = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText username = findViewById(R.id.editTextUsername);
        EditText password = findViewById(R.id.editTextPassword);

        SslHttpClient.initialize(getApplicationContext());
        AndroidNetworking.initialize(getApplicationContext(),SslHttpClient.getUnsafeOkHttpClient());

        findViewById(R.id.buttonLogin).setOnClickListener(v -> {
            try {
                JSONObject object = new JSONObject();
                object.put("username",username.getText());
                object.put("password",password.getText());

                AndroidNetworking.put(getResources().getString(R.string.url)+"user/login")
                        .addJSONObjectBody (object)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(tag,response);
                                try {
                                    id = UUID.fromString(response.replace("\"", ""));
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    LoginActivity.this.startActivity(intent);
                                }
                                catch (Exception e){
                                    Toast.makeText(getBaseContext(),"Wrong details", Toast.LENGTH_SHORT).show();
                                    Log.d(tag,e.toString());
                                }
                            }
                            @Override
                            public void onError(ANError error) {
                                Log.d(tag,error.toString());
                            }
                        });
            } catch (JSONException e) {
                Log.d(tag,e.toString());
            }
        });

        findViewById(R.id.buttonRegister).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,RegisterUserActivity.class);
            LoginActivity.this.startActivity(intent);
        });
    }
}