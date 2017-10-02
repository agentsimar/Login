package com.example.akal.sampleproject.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.akal.sampleproject.R;
import com.example.akal.sampleproject.model.User;
import com.example.akal.sampleproject.model.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegisterActivity extends AppCompatActivity {
    @InjectView(R.id.editTextName)
    EditText eTxtName;

    @InjectView(R.id.editTextEmail)
    EditText eTxtEmail;

    @InjectView(R.id.editTextPassword)
    EditText eTxtPassword;

    @InjectView(R.id.buttonRegister)
    Button btnRegister;

    User user;

    RequestQueue requestQueue;
    StringRequest stringRequest;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);

        requestQueue = Volley.newRequestQueue(this);

        user = new User();

        preferences = getSharedPreferences(Util.PREFS_NAME,MODE_PRIVATE);
        editor = preferences.edit();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setName(eTxtName.getText().toString().trim());
                user.setEmail(eTxtEmail.getText().toString().trim());
                user.setPassword(eTxtPassword.getText().toString().trim());

                registerUserOnServer();
            }
        });
    }
    void registerUserOnServer(){
        stringRequest = new StringRequest(Request.Method.POST, Util.REGISTER_ENDPOINT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");

                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

                    if(success == 1){
                        editor.putBoolean(Util.KEY_LOGREG,true);
                        editor.commit();
                        Intent intent = new Intent(RegisterActivity.this,HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } catch (Exception e) {
                    Toast.makeText(RegisterActivity.this, "Exception:" + e, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this,"Error "+error,Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("name",user.getName());
                map.put("email",user.getEmail());
                map.put("password",user.getPassword());
                return map;
            }
        };

        requestQueue.add(stringRequest);
    }
}
