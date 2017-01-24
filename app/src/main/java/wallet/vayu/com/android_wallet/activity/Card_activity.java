package wallet.vayu.com.android_wallet.activity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopeer.cardstack.CardStackView;

import java.util.Arrays;

import wallet.vayu.com.android_wallet.R;
import wallet.vayu.com.android_wallet.adapter.RecyclerAdapter;

public class Card_activity extends AppCompatActivity implements CardStackView.ItemExpendListener {

    public static Integer[] TEST_DATAS = new Integer[]{
            R.color.color_1,
            R.color.color_2,
            R.color.color_3,
            R.color.color_4,
            R.color.color_5,
            R.color.color_6,
            R.color.color_7,
            R.color.color_8,
            R.color.color_9,
            R.color.color_10,
            R.color.color_11,
            R.color.color_12,
            R.color.color_13,
            R.color.color_14,
            R.color.color_15
    };

    private CardStackView mStackView;
    private RecyclerAdapter mTestStackAdapter;
    private ImageView iv_logout, iv_plus;
    private SharedPreferences sp;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable drawable = wallpaperManager.getDrawable();
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.activity_main);
        drawable.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.color_15), PorterDuff.Mode.LIGHTEN);
        ll.setBackground(drawable);
        mStackView = (CardStackView) findViewById(R.id.stackview);
        iv_logout = (ImageView) findViewById(R.id.logout);
        iv_plus = (ImageView) findViewById(R.id.plus_btn);

        iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertlayout = new AlertDialog.Builder(Card_activity.this);
                WindowManager manager = (WindowManager) getSystemService(Activity.WINDOW_SERVICE);
                View view1 = getLayoutInflater().inflate(R.layout.add_cards_activity, null);
                alertlayout.setView(view1);
                final AlertDialog dialog = alertlayout.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = manager.getDefaultDisplay().getWidth();
                lp.height = manager.getDefaultDisplay().getHeight();
                lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                dialog.getWindow().setAttributes(lp);
                dialog.show();
            }
        });

        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(Card_activity.this);
                View view = layoutInflater.inflate(R.layout.shoppopup, null);
                final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setContentView(view);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.setElevation(25);
                }
                popupWindow.showAsDropDown(iv_logout);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                TextView tv_edit_aadhar = (TextView) view.findViewById(R.id.tv_edit_aadhar_card);
                TextView tv_edit_pan = (TextView) view.findViewById(R.id.tv_Pancard_edit);
                TextView tv_logout = (TextView) view.findViewById(R.id.tv_logout);
                tv_edit_aadhar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Card_activity.this, AadharCard_Activity.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.slide_up);
                    }
                });
                tv_edit_pan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Card_activity.this, PanCard_Activity.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.slide_up);
                    }
                });

                tv_logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sp = getSharedPreferences("MyPref", 0);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("name", "");
                        editor.putString("mail", "");
                        editor.putString("mpin", "");
                        editor.putString("pan_num", "");
                        editor.putString("pan_name", "");
                        editor.putString("pan_img", "");
                        editor.putString("aad_num", "");
                        editor.putString("aad_name", "");
                        editor.putString("aad_address", "");
                        editor.putString("aadhar_img", "");
                        editor.commit();
                        finish();
                        startActivity(new Intent(Card_activity.this, Login_Activity.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.slide_up);
                    }
                });
            }
        });
        mStackView.setItemExpendListener(this);
        mTestStackAdapter = new RecyclerAdapter(this);
        mStackView.setAdapter(mTestStackAdapter);

        StringBuilder smsBuilder = new StringBuilder();
        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_ALL = "content://sms/";
        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            Cursor cur = getContentResolver().query(uri, projection, "address=VK-BMSHOW", null, "date desc");
            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Type = cur.getColumnIndex("type");
                do {
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    int int_Type = cur.getInt(index_Type);
                    smsBuilder.append("[ ");
                    smsBuilder.append(strAddress + ", ");
                    smsBuilder.append(intPerson + ", ");
                    smsBuilder.append(int_Type);
                    smsBuilder.append(" ]\n\n");
                } while (cur.moveToNext());
                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                smsBuilder.append("no result!");
            } // end if
        } catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        mTestStackAdapter.updateData(Arrays.asList(TEST_DATAS));
                    }
                }
                , 200
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shopsactmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void onPreClick(View view) {
        mStackView.pre();
    }

    public void onNextClick(View view) {
        mStackView.next();
    }

    @Override
    public void onItemExpend(boolean expend) {
//        mActionButtonContainer.setVisibility(expend ? View.VISIBLE : View.GONE);
    }
}
