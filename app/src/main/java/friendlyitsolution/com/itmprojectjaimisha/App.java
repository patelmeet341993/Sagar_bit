package friendlyitsolution.com.itmprojectjaimisha;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class App extends Application {

    public static SharedPreferences pref;
    public static String mynumber,myname;
    public static DatabaseReference myref;
    public static Map<String, Object> userdata;
  public static FirebaseDatabase db;
  public static DatabaseReference dataref,canref,ref;

  public static List<String> can;
  public static List<String> data;
  public static Context con;
 public static Map<String, Object> dataobj;
  // In a real app, rather than attaching singletons (such as the API client instance) to your Application instance,
  // it's recommended that you use something like Dagger 2, and inject your client instance.
  // Since that would be a distraction here, we will just use a regular singleton.
  private static App INSTANCE;

  @NonNull
  public static App get() {
    final App instance = INSTANCE;
    if (instance == null) {
      throw new IllegalStateException("App has not been created yet!");
    }
    return instance;
  }


  @Override
  public void onCreate() {

      super.onCreate();
    db= FirebaseDatabase.getInstance();

    db.setPersistenceEnabled(true);
    ref=db.getReference();
    dataref=db.getReference().child("data");
    canref=db.getReference().child("can");
    can=new ArrayList<>();
    data=new ArrayList<>();
    dataobj=new HashMap<>();
    con=getApplicationContext();

      pref=getSharedPreferences("myinfo12",MODE_PRIVATE);
      mynumber=pref.getString("mynumber","");
      if(!mynumber.equals(""))
      {
          getUserdata();

      }
    INSTANCE = this;

  }


    void getUserdata()
    {
        myref=db.getReference("users").child(mynumber);
        myref.keepSynced(true);
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userdata=(Map<String, Object>)dataSnapshot.getValue();
                myname=userdata.get("name")+"";


                //   Toast.makeText(getApplicationContext(),"Data : "+userdata,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



  public static void showMsg(String msg)
  {
    Toast.makeText(con,msg, Toast.LENGTH_LONG).show();
  }


}
