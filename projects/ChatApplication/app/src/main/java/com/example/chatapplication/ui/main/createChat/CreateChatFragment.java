package com.example.chatapplication.ui.main.createChat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.chatapplication.ui.user.LoginActivity;
import com.example.chatapplication.R;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateChatFragment extends Fragment {

    private static String tag = "CreateChat";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_chat, container, false);

        EditText chatName = root.findViewById(R.id.editTextTextChatName);

        root.findViewById(R.id.buttonSave).setOnClickListener(v -> {
            try {
                JSONObject object = new JSONObject();
                object.put("name",chatName.getText());

                AndroidNetworking.post(getResources().getString(R.string.url)+"chat?userId={userId}")
                        .addPathParameter("userId", LoginActivity.id.toString())
                        .addJSONObjectBody (object)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(tag,response);
                                chatName.setText("");
                                Toast.makeText(root.getContext(),"Created", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onError(ANError error) {
                                Log.d(tag,error.toString());
                                Toast.makeText(root.getContext(),"Problem occurred", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (JSONException e) {
                Log.d(tag,e.toString());
            }
        });

        return root;
    }
}