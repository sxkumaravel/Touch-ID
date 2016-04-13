package com.kumars.touchid;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by kumars on 4/6/16.
 */
@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface Preference {

    String userEmail();
}
