package com.example.nguyentanluan.sharemytrip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword;
    private Button btnSignin;
    private ProgressBar pbLogin;
    private FirebaseAuth firebaseAuth;
    private EditText edtusername;
    private Button btnOk;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Enter password address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                pbLogin.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pbLogin.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            if (email.length() < 6) {
                                edtPassword.setError("Password too short, enter minimum 6 characters!");

                            } else
                                Toast.makeText(LoginActivity.this, "Login fail!", Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            }
        });
        if (checkisFirsttime()) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
        }
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtusername.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    edtusername.setError("Enter username address");
                    return;
                } else {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.putExtra("username", username);
                    startActivity(i);
                    editor = pref.edit();
                    editor.putString("username", username);
                    editor.putBoolean("isfirst", true);
                    editor.commit();
                }
            }
        });
    }

    private void initView() {
        edtEmail = (EditText) findViewById(R.id.email);
        edtPassword = (EditText) findViewById(R.id.password);
        btnSignin = (Button) findViewById(R.id.email_sign_in_button);
        pbLogin = (ProgressBar) findViewById(R.id.login_progress);
        edtusername = (EditText) findViewById(R.id.edtUsername);
        btnOk = (Button) findViewById(R.id.btnOK);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private boolean checkisFirsttime() {
        pref = getSharedPreferences(MainActivity.MYKEY, MODE_PRIVATE);
        boolean isfirst = pref.getBoolean("isfirst", false);
        return isfirst;
    }

    /**
     * Id to identity READ_CONTACTS permission request.
     */

}

