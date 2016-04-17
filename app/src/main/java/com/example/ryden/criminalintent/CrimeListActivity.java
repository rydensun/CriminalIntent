package com.example.ryden.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by user on 11/01/16.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

}
