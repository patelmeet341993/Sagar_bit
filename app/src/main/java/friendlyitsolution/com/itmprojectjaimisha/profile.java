package friendlyitsolution.com.itmprojectjaimisha;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedbottompicker.TedBottomPicker;

public class profile extends AppCompatActivity {

    CircleImageView iv;
    TextView edit;
    Uri imguri = null;
    ProgressDialog pd;
    MaterialEditText et;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView lgout=(ImageView)findViewById(R.id.lgbtn);
        ImageView backbtn=(ImageView)findViewById(R.id.backbtn);
        lgout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile.super.onBackPressed();
            }
        });
        pd=new ProgressDialog(profile.this);
        pd.setCancelable(false);
        pd.setMessage("please wait");
        btn=(Button)findViewById(R.id.btn);
        iv = (CircleImageView) findViewById(R.id.profile_image);
        edit = (TextView) findViewById(R.id.edit);
        edit.setVisibility(View.GONE);
        et=(MaterialEditText)findViewById(R.id.etname);
        setprofile();
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(profile.this)
                        .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                            @Override
                            public void onImageSelected(Uri uri) {

                                // Toast.makeText(getApplicationContext(),"get : "+uri,Toast.LENGTH_LONG).show();
                                if(uri!=null)
                                {
                                    imguri=uri;
                                    iv.setImageURI(uri);
                                    edit.setVisibility(View.VISIBLE);
                                }
                            }
                        })
                        .create();

                tedBottomPicker.show(getSupportFragmentManager());

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(imguri);

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et.getText().toString().equals(""))
                {
                    et.setError("please enter this");
                }
                else
                {
                    App.ref.child("users").child(App.mynumber).child("name").setValue(et.getText().toString());
                }
            }
        });
    }

    private void uploadImage(final Uri uri) {
        //if there is a file to upload

        if (uri != null) {
            //  bnp.setVisibility(View.VISIBLE);
            pd.show();
            final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            StorageReference mStorageRef = mStorageRef = FirebaseStorage.getInstance().getReference();
           final StorageReference riversRef = mStorageRef.child("img/" + App.mynumber + "/"+App.mynumber+".jpg");

            riversRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();




                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUri = uri+"";
                                    edit.setVisibility(View.GONE);
                                    App.ref.child("users").child(App.mynumber).child("imgurl").setValue("" + downloadUri);


                                }});


                        }}).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_LONG).show();

                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            // pd.setProgress(((int) progress));

                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Try again ..", Toast.LENGTH_LONG).show();

        }
    }



    void setprofile()
    {
        try
        {

            if(!App.userdata.get("imgurl").toString().equals(" "))
            {
                Glide.with(iv.getContext()).load(App.userdata.get("imgurl").toString())
                        .override(300, 300)
                        .fitCenter()
                        .into(iv);
            }
            et.setText(App.myname);

        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),"Something went wrong please reopen app", Toast.LENGTH_LONG).show();
        }

    }


}
