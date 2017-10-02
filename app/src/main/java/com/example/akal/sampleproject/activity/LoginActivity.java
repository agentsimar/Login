package com.example.akal.sampleproject.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    @InjectView(R.id.editTextEmail)   //using butterknife and declaring views
    EditText eTxtEmail;

    @InjectView(R.id.editTextPassword)
    EditText eTxtPassword;

    @InjectView(R.id.buttonLogin)
    Button btnLogin;

    @InjectView(R.id.textViewNewUser)
    TextView txtRegister;

    RequestQueue requestQueue;
    StringRequest request;

    User user;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        requestQueue = Volley.newRequestQueue(this);
        user = new User();

        btnLogin.setOnClickListener(this);
        txtRegister.setOnClickListener(this);

        preferences = getSharedPreferences(Util.PREFS_NAME,MODE_PRIVATE);
        editor=preferences.edit();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonLogin:
                user.setEmail(eTxtEmail.getText().toString().trim());
                user.setPassword(eTxtPassword.getText().toString().trim());

                loginUser();
                break;

            case R.id.textViewNewUser:
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    void loginUser() {
        request = new StringRequest(Request.Method.POST, Util.LOGIN_ENDPOINT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();

                    if (success == 1) {
                        editor.putBoolean(Util.KEY_LOGREG,true);
                                editor.commit();

                        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Some Exception", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(LoginActivity.this,"Some Volley Error ",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("email",user.getEmail());
                map.put("password",user.getPassword());

                return map;
            }
        };

        requestQueue.add(request);
    }
}
