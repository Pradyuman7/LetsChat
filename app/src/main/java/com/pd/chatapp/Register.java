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
import com.firebase.client.Firebase;
import com.pd.chatapp.databinding.ActivityRegisterBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private String user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Firebase.setAndroidContext(this);

        binding.login.setOnClickListener(v ->
                startActivity(new Intent(Register.this, Login.class)));

        binding.registerButton.setOnClickListener(v -> {
            user = binding.username.getText().toString();
            pass = binding.password.getText().toString();

            if (user.equals("")) {
                binding.username.setError("can't be blank");
            } else if (pass.equals("")) {
                binding.password.setError("can't be blank");
            } else if (!user.matches("[A-Za-z0-9]+")) {
                binding.username.setError("only alphabet or number allowed");
            } else if (user.length() < 5) {
                binding.username.setError("at least 5 characters long");
            } else if (pass.length() < 5) {
                binding.password.setError("at least 5 characters long");
            } else {
                String url = "https://chatapp-60323.firebaseio.com/users.json";
                final ProgressDialog pd = new ProgressDialog(Register.this);
                pd.setMessage("Loading...");
                pd.show();

                StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
                    Firebase reference = new Firebase("https://chatapp-60323.firebaseio.com/users");

                    if (response.equals("null")) {
                        reference.child(user).child("password").setValue(pass);
                        Toast.makeText(Register.this, "registration successful", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            JSONObject obj = new JSONObject(response);

                            if (!obj.has(user)) {
                                reference.child(user).child("password").setValue(pass);
                                Toast.makeText(Register.this, "registration successful", Toast.LENGTH_LONG).show();

                                startActivity(new Intent(getApplicationContext(), Login.class));
                                finish();
                            } else {
                                Toast.makeText(Register.this, "username already exists", Toast.LENGTH_LONG).show();

                                // reset focus
                                resetDataAndFocus();
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

                RequestQueue rQueue = Volley.newRequestQueue(Register.this);
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