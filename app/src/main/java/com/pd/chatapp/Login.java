package com.pd.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pd.chatapp.databinding.ActivityLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private String user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.register.setOnClickListener(v ->
                startActivity(new Intent(Login.this, Register.class)));

        binding.loginButton.setOnClickListener(v -> {
            user = binding.username.getText().toString();
            pass = binding.password.getText().toString();

            if (user.equals("")) {
                binding.username.setError("can't be blank");
            } else if (pass.equals("")) {
                binding.password.setError("can't be blank");
            } else {
                String url = "https://chatapp-60323.firebaseio.com/users.json";
                final ProgressDialog pd = new ProgressDialog(Login.this);
                pd.setMessage("Loading...");
                pd.show();

                StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
                    if (response.equals("null")) {
                        Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            JSONObject obj = new JSONObject(response);

                            if (!obj.has(user)) {
                                Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                                resetDataAndFocus();
                            } else if (obj.getJSONObject(user).getString("password").equals(pass)) {
                                UserDetails.username = user;
                                UserDetails.password = pass;
                                startActivity(new Intent(Login.this, Users.class));
                            } else {
                                Toast.makeText(Login.this, "incorrect password", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    pd.dismiss();
                }, volleyError -> {
                    System.out.println("" + volleyError);
                    pd.dismiss();
                });

                RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                rQueue.add(request);
            }

        });
    }

    /*** Reset Data and Focus **/
    private void resetDataAndFocus() {
        binding.username.setText("");
        binding.password.setText("");
        binding.password.clearFocus();
        binding.password.clearFocus();
    }
}