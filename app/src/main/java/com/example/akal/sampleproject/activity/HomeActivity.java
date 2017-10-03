package com.example.akal.sampleproject.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

//retrieve using volley
public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    RequestQueue requestQueue;
    StringRequest stringRequest;
    ArrayList<User> userList;

    @InjectView(R.id.listView)
    ListView listView;

    UserAdapter userAdapter;
    User user;
    int pos;

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
                        listView.setOnItemClickListener(HomeActivity.this);

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

    void showUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(user.getName()+"Details");
        builder.setMessage(user.toString());
        builder.setPositiveButton("Done",null);
        builder.create().show();
    }
    void deleteUser(){

        stringRequest = new StringRequest(Request.Method.POST, Util.DELETE_ENDPOINT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");
                    if(success == 1){
                        Toast.makeText(HomeActivity.this,message,Toast.LENGTH_LONG).show();
                        userList.remove(pos);
                        userAdapter.notifyDataSetChanged();
                    }
                }catch (Exception e){
                    Toast.makeText(HomeActivity.this,"Some Exception: "+e,Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(HomeActivity.this,"Some Error: "+error,Toast.LENGTH_LONG).show();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("id",String.valueOf(user.getUid()));
                return map;
            }
        };
        requestQueue.add(stringRequest);

    }

    void askForDeletion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete "+user.getName());
        builder.setMessage("Are you sure about this?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteUser();
            }
        });
        builder.setNegativeButton("cancel",null);
        builder.create().show();

    }

    void showOptions(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = {"View","Delete","Update"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i){
                    case 0:
                        showUser();
                        break;
                    case 1:
                        askForDeletion();
                        break;
                    case 2:
                }
            }
        });
        builder.create().show();

    }




    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        user = userList.get(i);
        pos = i;
        showOptions();

    }
}
