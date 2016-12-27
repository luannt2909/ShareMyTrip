package com.example.nguyentanluan.sharemytrip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private int GALLERY = 100;
    private EditText edtEmail, edtPassword;
    private Button btnSignin;
    private ImageView imgavatar;
    private ProgressBar pbLogin;
    private FirebaseAuth firebaseAuth;
    private EditText edtusername;
    private Button btnOk;
    private Bitmap bitmap;
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
        imgavatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndFinish();

            }
        });
    }

    private void saveAndFinish() {
        String username = edtusername.getText().toString();
        String avatar = "";
        if (bitmap != null) {
            avatar = BitMapToString(bitmap);
        } else {
            Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.ic_profile);
            avatar = BitMapToString(icon);
        }
        if (TextUtils.isEmpty(username)) {
            edtusername.setError("Enter username address");
            return;
        }
        editor = pref.edit();
        editor.putString("username", username);
        editor.putString("avatar", avatar);
        editor.putBoolean("isfirst", true);
        editor.commit();
        Log.e("usernameshare", username + " - " + avatar);
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.putExtra("username", username);
        i.putExtra("avatar", avatar);
        startActivity(i);


    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        startActivityForResult(gallery, GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            Uri IMAGE_URI = data.getData();
            try {
                InputStream image_stream = getContentResolver().openInputStream(IMAGE_URI);
                bitmap = BitmapFactory.decodeStream(image_stream);
                imgavatar.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        edtEmail = (EditText) findViewById(R.id.email);
        edtPassword = (EditText) findViewById(R.id.password);
        btnSignin = (Button) findViewById(R.id.email_sign_in_button);
        pbLogin = (ProgressBar) findViewById(R.id.login_progress);
        edtusername = (EditText) findViewById(R.id.edtUsername);
        imgavatar = (ImageView) findViewById(R.id.imgavatar);
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

