package com.dede.pictureupload;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Finish extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        Button btn1 = (Button) this.findViewById(R.id.btn_again);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Finish.this,MainActivity.class);
                startActivity(intent);
            }
        });

        TextView text1 = (TextView) this.findViewById(R.id.tv_title);
        text1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        text1.getPaint().setAntiAlias(true);
    }
}
