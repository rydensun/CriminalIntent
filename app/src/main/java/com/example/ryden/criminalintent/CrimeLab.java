package com.example.ryden.criminalintent;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by user on 7/01/16.
 */
public class CrimeLab {
    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crime.json";
    public ArrayList<Crime> mCrimes;
    private static CrimeLab sCrimeLab;
    private CriminalIntentJSONSerializer mSerializer;
    private Context mAppContext;

    private CrimeLab(Context appContext) {

        // mCrimes = new ArrayList<Crime>();
        mAppContext = appContext;
        mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);
        try {
            mCrimes = mSerializer.loadCrimes();
        } catch (Exception e) {
            mCrimes = new ArrayList<Crime>();
            Log.e(TAG, "Erro loading crimes", e);
        }

    }

    public static CrimeLab get(Context c) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab((c.getApplicationContext()));
        }
        return sCrimeLab;
    }

    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {

        for (Crime c : mCrimes) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }

    public void addCrime(Crime c) {
        mCrimes.add(c);
    }

    public void deleteCrime(Crime c) {
        mCrimes.remove(c);
    }

    public boolean saveCrimes() {
        try {
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "crimes saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving crimes:", e);
            return false;
        }
    }

}
