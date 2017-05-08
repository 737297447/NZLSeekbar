package com.nzl.seekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NZLSeekbar.OnSeekChangeListener{


    private NZLSeekbar seekbar;
    private TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.text);

        seekbar = (NZLSeekbar) findViewById(R.id.seekbar);
        seekbar.setPosition(5);
        seekbar.setOnSeekChangeListener(this);
    }

    @Override
    public void onSeekChange(String changeText) {
        text.setText(changeText);
    }
}
