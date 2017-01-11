package wallet.vayu.com.android_wallet.activity;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import wallet.vayu.com.android_wallet.Constants.PrefConstants;
import wallet.vayu.com.android_wallet.Constants.SAppUtil;
import wallet.vayu.com.android_wallet.R;

public class Splash_Activity extends AppCompatActivity {

    // Set Duration of the Splash Screen
    long Delay = 2000;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Get the view from splash_screen.xml
        setContentView(R.layout.content_splash_);
        SharedPreferences shortcut = getSharedPreferences("icon", 0);
        SharedPreferences.Editor editor = shortcut.edit();

        if (shortcut.getString("exists", "").equalsIgnoreCase("true")) {


        } else {
            Intent shortcutIntent = new Intent(this, Splash_Activity.class);

            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Mapprr Merchant");
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_launcher));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            this.sendBroadcast(addIntent);
            editor.putString("exists", "true");
            editor.commit();
        }
        if (
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)

                ) {


            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.GET_ACCOUNTS,
                            Manifest.permission.CAMERA,

                    },
                    1000);


        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    sharedPreferences = getSharedPreferences("MyPref", 0);
                    String name = sharedPreferences.getString("name", "");
                    String mail = sharedPreferences.getString("mail", "");
                    String mpin = sharedPreferences.getString("mpin", "");
                    try {
                        //String regToken = FirebaseInstanceId.getInstance().getToken();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    int oldVersionCode = PrefConstants.getAppPrefInt(Splash_Activity.this, "version_code");
                    int currentVersionCode = SAppUtil.getAppVersionCode(Splash_Activity.this);
                    if(currentVersionCode>oldVersionCode){
                        startActivity(new Intent(Splash_Activity.this,TourActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else if (mpin.equalsIgnoreCase("")) {
                        startActivity(new Intent(Splash_Activity.this, Login_Activity.class));
                    } else {
                        startActivity(new Intent(Splash_Activity.this, Card_activity.class));
                    }
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            }, 300);
        }
    }
}
