package com.example.ryden.criminalintent;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;
import android.support.v4.app.Fragment;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by ryden on 12/23/2015.
 */
public class CrimeFragment extends Fragment {
    public static final String EXTRA_CRIME_ID = "com.example.ryden.criminalintent.crime.id";
    public static final String DIALOG_DATE = "date";
    public static final String DIALOG_IMAGE = "image";
    public static final String TAG = "CrimeFragment";
    public static final int REQUEST_DATE = 0;
    public static final int REQUEST_PHOTO=1;
    public static final int REQUEST_CONTACT = 2;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mreportButton;
    private Button mSuspectButton;
    private CheckBox mSolvedCheckBox;
    private TextView mTitleLable;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCrime = new Crime();
        Log.v("1", "create");
        //UUID crimeId = (UUID)getActivity().getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    public void updateDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy/mm/dd hh:mm:ss");
        mDateButton.setText(dateFormat.format(mCrime.getmDate()));
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setmDate(date);
            updateDate();
        }else if (requestCode == REQUEST_PHOTO){
            String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if(filename!=null){
                Photo p  =new Photo(filename);
                mCrime.setPhoto(p);
                showPhoto();

                Log.i(TAG, "filename"+filename);

            }
        }else if (requestCode==REQUEST_CONTACT){
            Uri contactUri = data.getData();
            //fields that query retrun values for
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            //PERFORM QUERY
            Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            if (cursor.getCount()==0){
                cursor.close();
                return;
            }
            cursor.moveToFirst();
            String suspect = cursor.getString(0);
            mCrime.setmSuspect(suspect);
            mSuspectButton.setText(suspect);
            cursor.close();

        }
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.v("222", "oncreateview");
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);
        Log.v("inflater?", "inflater?");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }

        }


        mTitleLable = (TextView) v.findViewById(R.id.crime_title_label);
        mTitleLable.setText(mCrime.getmTitle());
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getmTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //mTitleField.removeTextChangedListener(this);
                mCrime.setmTitle(charSequence.toString());
                // mTitleField.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //mCrime.setmTitle(editable.toString());
                mTitleLable.setText(editable.toString());

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        //mDateButton.setText(mCrime.getmDate().toString());
        updateDate();

        //mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                // DatePickerFragment dialog  = new DatePickerFragment();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getmDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.ismSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setmSolved(isChecked);
            }
        });
        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(getActivity(),CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });
        //if device has no camera
        PackageManager packageManager = getActivity().getPackageManager();
        if(!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)&&
                !packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
            mPhotoButton.setEnabled(false);
        }
        mPhotoView = (ImageView)v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo p = mCrime.getmPhoto();
                if(p==null){
                    return;
                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
            }
        });
        mreportButton = (Button)v.findViewById(R.id.crime_reportButton);
        mreportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent. EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                i = Intent.createChooser(i,getString(R.string.send_report));
                /*
                //pass intent to the packagemanager to check if the implicit intent is safe
                PackageManager pm = getPackageManager();
                List<ResolveInfo> activities = pm.queryIntentActivities(yourIntent, 0);
                boolean isIntentSafe = activities.size() > 0;
                */
                startActivity(i);
            }
        });

        mSuspectButton = (Button)v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);

            }
        });

        return v;
    }
    private void showPhoto(){
        //(Re)set the image button's image based on our photo
        Photo p = mCrime.getmPhoto();
        BitmapDrawable b = null;
        if(p!=null){
            String path = getActivity()
                    .getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(),path);
        }
        mPhotoView.setImageDrawable(b);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private String getCrimeReport(){
        String solvedString = null;
        if(mCrime.ismSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM, dd";
        String dateString = android.text.format.DateFormat.format(dateFormat, mCrime.getmDate()).toString();
        String suspect = mCrime.getmSuspect();
        if(suspect == null){
            suspect =  getString(R.string.crime_report_suspect);

        }
        if(suspect != null){
            suspect = getString(R.string.crime_report_no_suspect);
        }
        String report = getString(R.string.crime_report, mCrime.getmTitle(), dateString, solvedString, suspect);

        return report;
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }
    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }
    @Override
    public void onStop(){
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

}
