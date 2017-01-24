package wallet.vayu.com.android_wallet.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import wallet.vayu.com.android_wallet.R;

public class PanCard_Activity extends AppCompatActivity {

    private EditText et_pan_num, et_pan_name;
    ImageView iv_pan_profile;
    private FloatingActionButton fab_pan;
    private SharedPreferences sharedpreferences;
    private Uri CropImageUri;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_card);
        et_pan_num = (EditText) findViewById(R.id.et_pan_number);
        et_pan_name = (EditText) findViewById(R.id.et_pan_name);
        iv_pan_profile = (ImageView) findViewById(R.id.iv_pan_profile);
        iv_pan_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectImage(view);
            }
        });
        fab_pan = (FloatingActionButton) findViewById(R.id.fab_pan_done);
        fab_pan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pan_number = et_pan_num.getText().toString();
                String pan_name = et_pan_name.getText().toString();
                String pan_img = iv_pan_profile.toString();
                sharedpreferences = getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("pan_num", pan_number);
                editor.putString("pan_name", pan_name);
                editor.commit();
                startActivity(new Intent(PanCard_Activity.this, Card_activity.class));
            }
        });
    }

    private void onSelectImage(View view) {
        CropImage.startPickImageActivity(this);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                CropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                startCropImageActivity(imageUri);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri imageUri = result.getUri();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                iv_pan_profile.setImageBitmap(imageBitmap);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                sharedpreferences = getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("pan_img", encoded);
                editor.commit();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (CropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(CropImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAutoZoomEnabled(true)
                .setActivityTitle("Crop Image")
                .setAspectRatio(100, 100)
                .start(this);
    }
}
