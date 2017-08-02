package com.nzl.seekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NZLSeekbar.OnSeekChangeListener{


    private NZLSeekbar seekbar;
    private TextView text;
    private Button bt_te;

    private int maxMoney = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.text);
        bt_te = (Button) findViewById(R.id.bt_te);

        seekbar = (NZLSeekbar) findViewById(R.id.seekbar);
        seekbar.setMaxMoney(maxMoney);
        seekbar.setMaxNoMoney(6000);
        seekbar.setPosition(5);
        seekbar.setOnSeekChangeListener(this);


        bt_te.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxMoney += 100;
                System.out.println("***********maxMoney***********"+maxMoney);
                seekbar.setChangeMaxMoney(maxMoney);
            }
        });
    }

    @Override
    public void onSeekChange(String changeText) {
        text.setText(changeText);
    }

    @Override
    public void onRangeChange(int type) {

    }
}
