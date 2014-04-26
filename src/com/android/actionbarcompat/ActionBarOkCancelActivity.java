package com.android.actionbarcompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sabdroidex.R;

/**
 * Created by marc on 22/03/14.
 */
public abstract class ActionBarOkCancelActivity extends ActionBarActivity {

    private Button okButton;
    private Button cancelButton;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (findViewById(R.id.okButton) == null) {
            throw new RuntimeException("Your content must have a Button whose id attribute is 'R.id.okButton'");
        }
        okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOkClick();
            }
        });
        if (findViewById(R.id.cancelButton) == null) {
            throw new RuntimeException("Your content must have a Button whose id attribute is 'R.id.cancelButton'");
        }
        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClick();
            }
        });
    }

    public abstract void onOkClick();
    public abstract void onCancelClick();
}
