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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;

public class home extends AppCompatActivity {
    ImageView iv;
    TextView tv, editpro;
    FloatingActionButton fab;
    ImageView iv1;
    TextView tv1;

    Uri imguri=null;
    ProgressDialog pd;
    static String txtdata="";

    DatabaseReference ref;

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
        pd.setTitle("Converting");
        pd.setCancelable(false);
        iv1=(ImageView)findViewById(R.id.img);
        tv1=(TextView)findViewById(R.id.txt);

        tv1.setMovementMethod(new ScrollingMovementMethod());
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkWriteExternalPermissionandOpen();
            }
        });

        ref=App.ref;
        ref.child("enable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot!=null)
                {
                    if(dataSnapshot.getValue().toString().equals("yes"))
                    {

                        fab.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        fab.setVisibility(View.GONE);
                    }
                }
                else
                {

                    fab.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    void checkWriteExternalPermissionandOpen()
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
        else
        {
            TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(home.this)
                    .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                        @Override
                        public void onImageSelected(Uri uri) {

                            // Toast.makeText(getApplicationContext(),"get : "+uri,Toast.LENGTH_LONG).show();
                            if(uri!=null)
                            {

                                Crop(uri);

                            }

                        }
                    })
                    .create();

            tedBottomPicker.show(getSupportFragmentManager());

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imguri = result.getUri();
                iv1.setImageURI(imguri);
                Bitmap bits = null;
                try {
                    bits = MediaStore.Images.Media.getBitmap(getContentResolver(), imguri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //   iv.setImageBitmap(bits);
                pd.show();
                processDocumentImage();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private FirebaseVisionDocumentTextRecognizer startTextRecoLocal() {
        // [START mlkit_local_doc_recognizer]
        FirebaseVisionDocumentTextRecognizer detector = FirebaseVision.getInstance()
                .getCloudDocumentTextRecognizer();
        // [END mlkit_local_doc_recognizer]

        return detector;
    }

    private void processDocumentImage() {

        try {
            FirebaseVisionDocumentTextRecognizer detector = startTextRecoLocal();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imguri);

            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
            // [START mlkit_process_doc_image]
            detector.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionDocumentText>() {
                        @Override
                        public void onSuccess(FirebaseVisionDocumentText result) {

                            processDocumentTextBlock(result);

                            pd.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            // ...
                            tv1.setText("Error :  fail "+e.getMessage());
                            pd.dismiss();
                        }
                    });

        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),"Error "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        // [END mlkit_process_doc_image]
    }
    private void processDocumentTextBlock(FirebaseVisionDocumentText result) {
        // [START mlkit_process_document_text_block]
        String resultText = result.getText();
        tv1.setText("");

        for (FirebaseVisionDocumentText.Block block: result.getBlocks()) {
            String blockText = block.getText();

            tv1.setText(tv1.getText().toString()+"\n"+blockText);



            Float blockConfidence = block.getConfidence();
            List<RecognizedLanguage> blockRecognizedLanguages = block.getRecognizedLanguages();
            Rect blockFrame = block.getBoundingBox();
            for (FirebaseVisionDocumentText.Paragraph paragraph: block.getParagraphs()) {
                String paragraphText = paragraph.getText();
                Float paragraphConfidence = paragraph.getConfidence();
                List<RecognizedLanguage> paragraphRecognizedLanguages = paragraph.getRecognizedLanguages();
                Rect paragraphFrame = paragraph.getBoundingBox();
                for (FirebaseVisionDocumentText.Word word: paragraph.getWords()) {
                    String wordText = word.getText();
                    Float wordConfidence = word.getConfidence();
                    List<RecognizedLanguage> wordRecognizedLanguages = word.getRecognizedLanguages();
                    Rect wordFrame = word.getBoundingBox();
                    for (FirebaseVisionDocumentText.Symbol symbol: word.getSymbols()) {
                        String symbolText = symbol.getText();
                        Float symbolConfidence = symbol.getConfidence();
                        List<RecognizedLanguage> symbolRecognizedLanguages = symbol.getRecognizedLanguages();
                        Rect symbolFrame = symbol.getBoundingBox();
                    }
                }
            }
        }

        App.ref.child("data1").setValue(tv1.getText().toString());
        // [END mlkit_process_document_text_block]
    }

    void Crop(Uri uri)
    {


// start cropping activity for pre-acquired image saved on the device
        CropImage.activity(uri).setCropMenuCropButtonTitle("Crop").setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(this);

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
