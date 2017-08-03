package com.cascade.listenerscrollview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final Random random = new Random();

    private LinearLayout linearLayoutScrollContent;
    private TextView textViewScrollLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeActivity();
    }

    private void initializeActivity() {
        textViewScrollLabel = (TextView) findViewById(R.id.tv_scroll_label);
        linearLayoutScrollContent = (LinearLayout) findViewById(R.id.ll_scroll_content);

        Button buttonAddContent = (Button) findViewById(R.id.b_add_content);
        buttonAddContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContent();
            }
        });

        ListenerScrollView listenerScrollViewShowcase = (ListenerScrollView) findViewById(R.id.lsw_showcase);
        listenerScrollViewShowcase.setScrollViewListener(new ListenerScrollView.ScrollViewListener() {
            @Override
            public void onScrollStateChanged(ListenerScrollView.ScrollState scrollState, float positionProportion) {
                String scrollAmount = "%" + String.valueOf((int) (positionProportion * 100));

                if (getSupportActionBar() != null) {
                    String scrollLabel = scrollAmount + System.getProperty("line.separator") + scrollState.name();
                    textViewScrollLabel.setText(scrollLabel);
                }

                Log.i(this.getClass().getSimpleName(), String.valueOf(positionProportion));
            }

            @Override
            public void onSwipedDown() {
                Toast.makeText(MainActivity.this, "onSwipedDown", Toast.LENGTH_SHORT).show();
            }
        });

        addContent();
    }

    private void addContent() {
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);

        View dummyView = new View(MainActivity.this);
        dummyView.setMinimumHeight(getResources().getDimensionPixelSize(android.R.dimen.thumbnail_height));
        dummyView.setBackgroundColor(Color.rgb(r, g, b));
        linearLayoutScrollContent.addView(dummyView);
    }
}