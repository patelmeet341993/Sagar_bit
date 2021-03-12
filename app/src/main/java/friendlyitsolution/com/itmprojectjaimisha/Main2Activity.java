package friendlyitsolution.com.itmprojectjaimisha;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Thread th=new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2500);
                    Intent i=new Intent(getApplicationContext(),login.class);
                    startActivity(i);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        th.start();
    }

}
