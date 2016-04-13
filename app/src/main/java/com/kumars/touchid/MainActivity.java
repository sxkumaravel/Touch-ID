package com.kumars.touchid;

import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @Click(R.id.button_register)
    protected void onRegisterClicked() {
        RegisterActivity_.intent(this).start();
    }

    @Click(R.id.button_validate)
    protected void onValidateClicked() {
        ValidateActivity_.intent(this).start();
    }
}
