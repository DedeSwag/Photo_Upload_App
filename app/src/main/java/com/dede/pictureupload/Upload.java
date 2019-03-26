package com.dede.pictureupload;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Upload extends AppCompatActivity {
    private ProgressBar pb_upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        TextView text1 = (TextView) this.findViewById(R.id.tv_title);
        text1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        text1.getPaint().setAntiAlias(true);

        pb_upload = (ProgressBar)this.findViewById(R.id.pb_upload);
        pb_upload.setVisibility(View.VISIBLE);
        handler.post(updateProgress);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            pb_upload.setProgress(msg.arg1);
            handler.postDelayed(updateProgress,100);
        }
    } ;

    Runnable updateProgress = new Runnable() {
        int i =0 ;
        @Override
        public void run() {
            i+=2;
            Message msg = handler.obtainMessage();
            msg.arg1=i;
            handler.sendMessage(msg);
            if(i==100){
                handler.removeCallbacks(updateProgress);
                Intent intent=new Intent(Upload.this,Finish.class);
                startActivity(intent);
            }
        }
    };


}

