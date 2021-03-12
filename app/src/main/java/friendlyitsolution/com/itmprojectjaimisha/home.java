package friendlyitsolution.com.itmprojectjaimisha;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gun0912.tedbottompicker.TedBottomPicker;

public class home extends AppCompatActivity {
    ImageView iv;
    TextView tv, editpro;
    FloatingActionButton fab;

    TextView tv1;
    ProgressDialog pd;
    DatabaseReference ref;
    Button btn;

    boolean isOn=false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView lgout = (ImageView) findViewById(R.id.lgbtn);
        ImageView backbtn = (ImageView) findViewById(R.id.backbtn);
        lgout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.super.onBackPressed();
            }
        });


        btn=findViewById(R.id.btn);
        iv = (ImageView) findViewById(R.id.profile_image);
        tv = (TextView) findViewById(R.id.name);
        editpro = (TextView) findViewById(R.id.editpro);
        setprofile();

        editpro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Intent i=new Intent(home.this,profile.class);
                startActivity(i);
            }
        });

        checkWriteExternalPermission();
        pd=new ProgressDialog(home.this);
        pd.setMessage("please wait");

        pd.setCancelable(false);
        tv1=(TextView)findViewById(R.id.txt);

        tv1.setMovementMethod(new ScrollingMovementMethod());

        ref=App.ref;
        ref.child("device").child("1111").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot!=null)
                {

                    Map<String,String> data=(Map<String, String>)dataSnapshot.getValue();
                    if(data.get("flag").equals("on"))
                    {
                        btn.setText("Stop");
                        tv1.setText(data.get("status")+"\nBubble Count : "+data.get("count"));
                        isOn=true;

                    }
                    else
                    {
                        tv1.setText("Stopped\nStart new test");
                        btn.setText("Start");
                        isOn=false;
                    }


                }
                else
                {

                    tv.setText("Something error");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pd.show();
                Map<String,Object> data=new HashMap<>();
                if(isOn)
                {
                    data.put("flag","off");
                    data.put("count","0");
                    data.put("status","stop");



                }
                else
                {
                    data.put("flag","on");
                    data.put("count","0");
                    data.put("status","starting");

                }

                ref.child("device").child("1111").updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        pd.dismiss();


                    }
                });

            }
        } );
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void checkWriteExternalPermission()
    {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);

        String permission1 = Manifest.permission.CAMERA;
        int res1 = getApplicationContext().checkCallingOrSelfPermission(permission1);


        if(res != PackageManager.PERMISSION_GRANTED || res1 != PackageManager.PERMISSION_GRANTED)
        {

            String[] permissions=new String[]{permission,permission1};
            requestPermissions(permissions,101);


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if(requestCode==101)
        {
            String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            int res = getApplicationContext().checkCallingOrSelfPermission(permission);

            if(res== PackageManager.PERMISSION_GRANTED)
            {
                iv.setEnabled(true);

            }
            else
            {

                iv.setEnabled(false);
            }

        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        setprofile();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setprofile();
    }

    void setprofile()
    {
        try
        {

            if(!App.userdata.get("imgurl").toString().equals(" "))
            {
                Glide.with(iv.getContext()).load(App.userdata.get("imgurl").toString())
                        .override(200, 200)
                        .fitCenter()
                        .into(iv);
            }
            tv.setText(App.myname);

        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),"Something went wrong please reopen app", Toast.LENGTH_LONG).show();
        }

    }
    void logOut()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialoug_logout);
        dialog.show();

        final Button btnInDialog = (Button) dialog.findViewById(R.id.btn);
        btnInDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = App.pref.edit();
                editor.clear();
                editor.commit();
                finish();
                App.mynumber = "";
                App.myname = "";
                App.userdata = null;

                Intent i = new Intent(home.this, Main2Activity.class);
                startActivity(i);

                finish();
            }
        });
        final ImageView btnClose = (ImageView) dialog.findViewById(R.id.canclebtn);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }
}
