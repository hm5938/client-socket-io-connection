package com.example.client_socket_io_connection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

public class MainActivity extends AppCompatActivity {

TextView textView;
EditText editText;
Button button;
String TAG ="MainActivity";
    private Socket socket;{
        try {
            socket = IO.socket("http://192.168.0.187:4002");
        }catch (URISyntaxException e) {}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText =  findViewById(R.id.edit_text);
        textView = findViewById(R.id.text_view);
        button = findViewById(R.id.button);

//        IO.Options opts = new IO.Options();
//        opts.transports = new String[] { WebSocket.NAME };

        socket.on("test", onNewMessage);
        socket.connect();

        Log.d(TAG, socket.connected()+"" );

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               attemptSend();
            }
        });
    }
    private void attemptSend() {
        String message = editText.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        editText.setText("");
        socket.emit("test", message);
    }

    public void addMessage(String username, String message){
        textView.setText(username +" : "+message);
    }

    // socket events listeners
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
           runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    JSONObject data = (JSONObject) args[0];
                    String text = (String) args[0];
                    String username;
                    String message;
//                    try {
//                        username = data.getString("username");
//                        message = data.getString("message");
//
//                    } catch (JSONException e) {
//                        return;
//                    }

                    // add the message to view
                    addMessage("me", text);
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        socket.disconnect();
        socket.off("new message", onNewMessage);
    }
    }
// Adding authentication headers when encountering EVENT_TRANSPORT
//        socket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                Transport transport = (Transport) args[0];
//                // Adding headers when EVENT_REQUEST_HEADERS is called
//                transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
//                    @Override
//                    public void call(Object... args) {
//                        Log.v(TAG, "Caught EVENT_REQUEST_HEADERS after EVENT_TRANSPORT, adding headers");
//                        Map<String, List<String>> mHeaders = (Map<String, List<String>>)args[0];
//                        mHeaders.put("Authorization", Arrays.asList("Basic bXl1c2VyOm15cGFzczEyMw=="));
//                    }
//                });
//            }
//        });