package wallet.vayu.com.android_wallet.activity;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;


import wallet.vayu.com.android_wallet.R;

public class Login_Activity extends AppCompatActivity {

    private EditText et_name, et_email, et_password;
    private AppCompatButton btn_go;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.email);
        et_password = (EditText) findViewById(R.id.password);
        btn_go = (AppCompatButton) findViewById(R.id.btn_go);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et_name.getText().toString();
                String mail = et_email.getText().toString();
                String mpin = et_password.getText().toString();
                sharedpreferences = getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("name", name);
                editor.putString("mail", mail);
                editor.putString("mpin", mpin);
                editor.commit();
                startActivity(new Intent(Login_Activity.this, AadharCard_Activity.class));
            }
        });
    }
}
