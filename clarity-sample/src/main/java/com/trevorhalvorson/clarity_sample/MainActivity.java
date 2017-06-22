package com.trevorhalvorson.clarity_sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.trevorhalvorson.clarity.ClarityView;

import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ClarityView clarityView = (ClarityView) findViewById(R.id.clarity_view);
        clarityView.setText(getString(R.string.test_text));

        Button generateButton = (Button) findViewById(R.id.generate_button);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clarityView.setText(getRandomString());
            }
        });
    }

    private String getRandomString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            char c = (char) (ThreadLocalRandom.current().nextInt(33, 127));
            builder.append(c);
        }
        return builder.toString();
    }
}
