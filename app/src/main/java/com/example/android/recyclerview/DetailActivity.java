package com.example.android.recyclerview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailActivity  extends AppCompatActivity{

    @Override
    public void onCreate(@Nullable Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_detail);
        getIncomingIntent();
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("param1")){
            int param1 = getIntent().getIntExtra("param1", -1);
            setValues(param1);
        }
    }

    private void setValues(int value){
        TextView name = (TextView) findViewById(R.id.first_text);
        name.setText(String.valueOf(value));
    }
}
