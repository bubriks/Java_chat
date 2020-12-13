package com.example.chatapplication.ui.main.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.chatapplication.ui.chat.ChatActivity;
import com.example.chatapplication.ui.user.LoginActivity;
import com.example.chatapplication.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ListView simpleList;
    private JSONArray jsonArray;
    private static String tag = "ChatRooms";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        refreshList();

        root.findViewById(R.id.buttonRefresh).setOnClickListener(v -> refreshList());

        simpleList = root.findViewById(R.id.simpleListView);

        simpleList.setOnItemClickListener((parent, view, position, id) -> {
            //String name = (String) parent.getItemAtPosition(position);
            try {
                JSONObject object = (JSONObject) jsonArray.get(position);
                String chatId = object.getString("id");
                Log.d("Home", chatId);

                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("CHATID", chatId);
                getContext().startActivity(intent);
            } catch (JSONException e) {
                Log.d(tag,e.toString());
            }
        });

        return root;
    }

    private void refreshList(){
        AndroidNetworking.get(getResources().getString(R.string.url)+"chat?userId={userId}")
                .addPathParameter("userId", LoginActivity.id.toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            jsonArray = response;
                            Log.d(tag, response.toString());

                            List<String> list = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object = (JSONObject) response.get(i);
                                String value = object.getString("name");
                                int count = object.getJSONArray("users").length();
                                list.add(value + ": " + count);
                            }

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter(HomeFragment.this.getContext(), R.layout.activity_listview, R.id.textView, list);
                            simpleList.setAdapter(arrayAdapter);
                        }
                        catch (JSONException e){
                            Log.d(tag,e.toString());
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.d(tag,error.toString());
                    }
                });
    }
}