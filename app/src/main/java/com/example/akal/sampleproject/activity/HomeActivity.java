package com.example.akal.sampleproject.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.akal.sampleproject.R;
import com.example.akal.sampleproject.adapter.UserAdapter;
import com.example.akal.sampleproject.model.User;
import com.example.akal.sampleproject.model.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

//retrieve using volley
public class HomeActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    StringRequest stringRequest;
    ArrayList<User> userList;

    @InjectView(R.id.listView)
    ListView listView;

    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.inject(this);

        requestQueue = Volley.newRequestQueue(this);
        retrieveFromServer();
    }
    void retrieveFromServer(){

        stringRequest = new StringRequest(Request.Method.GET, Util.RETRIEVE_ENDPOINT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");
                    if(success == 1){
                        Toast.makeText(HomeActivity.this,message,Toast.LENGTH_LONG).show();

                        JSONArray jsonArray = jsonObject.getJSONArray("users");

                        int id =0;
                        String n="",e="",p="";

                        userList = new ArrayList<>();

                        for(int i=0; i<jsonArray.length() ;i++){
                            JSONObject jObj = jsonArray.getJSONObject(i);
                            id = jObj.getInt("uid");
                            n = jObj.getString("name");
                            e = jObj.getString("email");
                            p = jObj.getString("password");

                            User user = new User(id,n,e,p);
                            userList.add(user);
                        }
                        userAdapter = new UserAdapter(HomeActivity.this,R.layout.listitem,userList);
                        listView.setAdapter(userAdapter);

                    }else{
                        Toast.makeText(HomeActivity.this,message,Toast.LENGTH_LONG).show();

                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(HomeActivity.this,"Some Exception "+e,Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(HomeActivity.this,"Some Volleyerror"+error,Toast.LENGTH_LONG).show();

            }
        });

        requestQueue.add(stringRequest);
    }
}
