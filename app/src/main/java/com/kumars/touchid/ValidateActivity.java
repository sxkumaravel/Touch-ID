package com.kumars.touchid;

import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;


/**
 * @author kumars on 4/6/16.
 */
@EActivity(R.layout.activity_validate)
public class ValidateActivity extends AppCompatActivity {

    @Pref
    Preference_ mPreference;

    @ViewById(R.id.saved_user_id)
    EditText mEmailET;

    @ViewById(R.id.button_validate)
    Button mValidate;

    @AfterViews
    protected void afterViews() {
        mEmailET.setText(mPreference.userEmail().getOr(""));
        if (mPreference.userEmail().getOr("").length() < 5) {
            mValidate.setEnabled(false);
        }
    }

    @Click(R.id.button_validate)
    protected void onValidateClicked() {
        FingerPrintAuthDialog_
                .builder()
                .build()
                .show(getFragmentManager(), "");
    }
}
