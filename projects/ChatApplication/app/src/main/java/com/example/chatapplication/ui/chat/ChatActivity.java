package com.example.chatapplication.ui.chat;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.chatapplication.R;
import com.example.chatapplication.extra.SslHttpClient;
import com.example.chatapplication.ui.user.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import okhttp3.OkHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.util.*;

public class ChatActivity extends AppCompatActivity {

    private SSLSocket socket;
    private ArrayAdapter<String> arrayAdapter;
    private static String tag = "Chat";
    private static int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        UUID chatId = UUID.fromString(getIntent().getStringExtra("CHATID"));

        ListView simpleList = findViewById(R.id.simpleListView);
        EditText messageText = findViewById(R.id.editTextMessage);
        Switch switchInvite = findViewById(R.id.switchInvite);

        establishConnection(chatId);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });

        messageText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                String text = messageText.getText().toString();
                Log.d(tag,text);

                if(switchInvite.isChecked()){
                    inviteUser(chatId,text);
                }
                else {
                    sendMessage(text);
                }
                messageText.setText("");
            }
            return true;
        });

        AndroidNetworking.get(getResources().getString(R.string.url)+"chat?chatId={chatId}")
                .addPathParameter("chatId", chatId.toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(tag, response.toString());

                            arrayAdapter = new ArrayAdapter(ChatActivity.this, R.layout.activity_listview, R.id.textView, new ArrayList());
                            JSONArray messages = sortMessages(response.getJSONArray("messages"));

                            for (int i = 0; i < messages.length(); i++) {
                                JSONObject object = (JSONObject) messages.get(i);
                                String sender = object.getJSONObject("sender").getString("username");
                                String text  = object.getString("message");
                                arrayAdapter.insert(sender + ": " + text, 0);
                            }

                            simpleList.setAdapter(arrayAdapter);
                            receiveMessages();
                        }
                        catch (Exception e){
                            runOnUiThread(() -> finish());//leave activity
                            Log.d(tag,e.toString());
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.d(tag,error.toString());
                        runOnUiThread(() -> finish());//leave activity
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            Uri uri = data.getData();
            new Thread(() -> {
                try {
                    PrintWriter outputStream = new PrintWriter(socket.getOutputStream(), true);
                    outputStream.println("file_message::"+getFileName(uri));

                    InputStream in = getContentResolver().openInputStream(uri);
                    OutputStream out = socket.getOutputStream();

                    byte[] bytes = new byte[16 * 1024];
                    int count;
                    while ((count = in.read(bytes)) > 0) {
                        out.write(bytes, 0, count);
                    }

                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private String getFileName(Uri uri){
        String displayName = "";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
            try {
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }finally {
                cursor.close();
            }
        }
        return displayName;
    }

    private void establishConnection(UUID chatId){
        new Thread(() -> {
            try {
                OkHttpClient okHttpClient = SslHttpClient.getUnsafeOkHttpClient();

                socket = (SSLSocket)okHttpClient.sslSocketFactory().createSocket("10.0.2.2", 6483);
                socket.startHandshake();

                PrintWriter outputStream = new PrintWriter(socket.getOutputStream(), true);
                outputStream.println(chatId + "::" + LoginActivity.id);
            }
            catch(Exception e){
                Log.d(tag,e.toString());
                runOnUiThread(() -> finish());//leave activity
            }
        }).start();
    }

    private void sendMessage(String text) {
        new Thread(() -> {
            try {
                JSONObject object = new JSONObject();
                object.put("text",text);
                JSONObject objectSender = new JSONObject();
                objectSender.put("id",LoginActivity.id);
                object.put("sender",objectSender);

                PrintWriter outputStream = new PrintWriter(socket.getOutputStream(), true);
                outputStream.println(object);
            }
            catch(Exception e){
                Log.d(tag,e.toString());
            }
        }).start();
    }

    private void receiveMessages(){
        new Thread(() -> {
            try {
                while(true){
                    BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String response = inputStream.readLine();

                    Log.d(tag,response);
                    JSONObject object = new JSONObject(response);
                    String sender = object.getJSONObject("sender").getString("username");
                    String text  = object.getString("text");

                    runOnUiThread(() -> arrayAdapter.insert(sender + ": " + text, 0));
                }
            }
            catch (Exception e){
                Log.d(tag,e.toString());
            }
        }).start();
    }

    private void inviteUser(UUID chatId, String text){
        AndroidNetworking.put(getResources().getString(R.string.url)+"chat/user/add?username={username}&chatId={chatId}")
                .addPathParameter("username", text)
                .addPathParameter("chatId", chatId.toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getBaseContext(),"User added", Toast.LENGTH_SHORT).show();
                        Log.d(tag,response);
                    }
                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(getBaseContext(),"No user found", Toast.LENGTH_SHORT).show();
                        Log.d(tag,error.toString());
                    }
                });
    }

    private JSONArray sortMessages(JSONArray messages) throws JSONException {
        JSONArray sortedJsonArray = new JSONArray();
        List list = new ArrayList();
        for(int i = 0; i < messages.length(); i++) {
            list.add(messages.getJSONObject(i));
        }
        Collections.sort(list, new Comparator() {
            private static final String KEY_NAME = "id";
            @Override
            public int compare(Object a, Object b) {
                int str1=0, str2=0;
                try {
                    str1 = ((JSONObject)a).getInt(KEY_NAME);
                    str2 = ((JSONObject)b).getInt(KEY_NAME);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                return str1 - str2;
            }
        });
        for(int i = 0; i < messages.length(); i++) {
            sortedJsonArray.put(list.get(i));
        }
        return sortedJsonArray;
    }
}