package com.example.chatapplication.ui.user;

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
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterUserActivity extends AppCompatActivity {

    private static String tag = "RegisterUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        EditText username = findViewById(R.id.editTextUsername);
        EditText password = findViewById(R.id.editTextPassword);

        findViewById(R.id.buttonRegister).setOnClickListener(v -> {
            try {
                JSONObject object = new JSONObject();
                object.put("username",username.getText());
                object.put("password",password.getText());

                AndroidNetworking.post(getResources().getString(R.string.url)+"user")
                        .addJSONObjectBody (object)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                finish();
                            }
                            @Override
                            public void onError(ANError error) {
                                Log.d(tag,error.toString());
                                Toast.makeText(getBaseContext(),"Problem registering", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (JSONException e) {
                Log.d(tag,e.toString());
            }
        });
    }
}