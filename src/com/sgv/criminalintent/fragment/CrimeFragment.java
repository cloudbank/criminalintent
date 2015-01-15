
package com.sgv.criminalintent.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.sgv.criminalintent.PictureUtils;
import com.sgv.criminalintent.R;
import com.sgv.criminalintent.activity.CrimeCameraActivity;
import com.sgv.criminalintent.activity.CrimePagerActivity;
import com.sgv.criminalintent.model.Crime;
import com.sgv.criminalintent.model.CrimeLab;
import com.sgv.criminalintent.model.Photo;

public class CrimeFragment extends Fragment {
    public static final String CRIME_ID = "criminalintent.CRIME_ID";
    public static final String DIALOG_IMAGE = "image";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    protected static final int REQUEST_CONTACT = 2;
    public static final String PORTRAIT = "portrait";
    private boolean portrait = false;

    Crime crime;
    EditText title;
    Button date, suspectBtn, callBtn;
    CheckBox solved;
    ImageView photoView;
    ImageButton mPhotoButton;
    RadioGroup orientation;
    Callbacks callbacks;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID) getArguments().getSerializable(CRIME_ID);
        crime = CrimeLab.get(getActivity()).getCrime(crimeId);

        setHasOptionsMenu(true);
    }

    public void updateDate() {
        date.setText(crime.getDate().toString());
    }

    @Override
    @TargetApi(11)
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        title = (EditText) v.findViewById(R.id.crime_title);
        title.setText(crime.getTitle());
        title.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                crime.setTitle(c.toString());
                callbacks.onCrimeUpdated(crime);
                
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });

        date = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(crime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        solved = (CheckBox) v.findViewById(R.id.crime_solved);
        solved.setChecked(crime.isSolved());
        solved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // set the crime's solved property
                crime.setSolved(isChecked);
                callbacks.onCrimeUpdated(crime);
            }
        });

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // launch the camera activity
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                // get radio button for landscape or portrait and then put extra
                // for orientation
                i.putExtra(PORTRAIT, portrait);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        // if camera is not available, disable camera functionality
        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            mPhotoButton.setEnabled(false);
        }
        photoView = (ImageView) v.findViewById(R.id.crime_imageView);
        photoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Photo p = crime.getPhoto();
                if (p == null) {
                    return;
                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
            }
        });
        photoView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (crime.getPhoto() != null) {
                    // delete the photo on disk
                    // set the
                    ActionMode am = getActivity().startActionMode(new ActionMode.Callback() {

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            return false;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {

                        }

                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            MenuInflater inflater = mode.getMenuInflater();
                            inflater.inflate(R.menu.crime_list_item_context, menu);
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            if (item.getItemId() == R.id.menu_item_delete_crime) {
                                Photo p = crime.getPhoto();
                                photoView.setImageDrawable(null);
                                crime.setPhoto(null);
                                getActivity().getFileStreamPath(p.getFilename()).delete();
                                mode.finish();
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });

                }
                return true;
            }
        });
        orientation = (RadioGroup) v.findViewById(R.id.orientationRg);

        orientation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (R.id.portraitRb == checkedId) {
                    portrait = true;
                } else if (R.id.landscapeRb == checkedId) {
                    portrait = false;
                }
            }
        });
        suspectBtn = (Button) v.findViewById(R.id.crime_suspectButton);
        suspectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });
        if (crime.getSuspect() != null) {
            suspectBtn.setText(crime.getSuspect());
        }

        Button reportButton = (Button) v.findViewById(R.id.crime_reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });
        callBtn = (Button) v.findViewById(R.id.crime_callButton);
        callBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (crime.getSuspect() == null) {
                    return;
                }
                String number = "";
                ContentResolver cr = getActivity().getContentResolver();
                Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                        "DISPLAY_NAME = '" + crime.getSuspect() + "'", null, null);
                if (cursor.moveToFirst()) {
                    String contactId =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    //
                    // Get all phone numbers.
                    //
                    Cursor phones = cr.query(Phone.CONTENT_URI, null,
                            Phone.CONTACT_ID + " = " + contactId, null, null);
                    while (phones.moveToNext()) {
                        number = phones.getString(phones.getColumnIndex(Phone.NUMBER));

                    }
                    phones.close();
                }
                cursor.close();
                if (null != number && number.trim().length() > 0) {
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    i.setData(Uri.parse("tel:" + number));
                    startActivity(i);
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.DATE);
            crime.setDate(date);
            callbacks.onCrimeUpdated(crime);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            // create a new Photo object and attach it to the crime
            String filename = data
                    .getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                if (crime.getPhoto() != null) {
                    // delete the photo on disk
                    Photo p = crime.getPhoto();
                    crime.setPhoto(null);
                    getActivity().getFileStreamPath(p.getFilename()).delete();
                }
                Photo p = new Photo(filename);
                crime.setPhoto(p);
                callbacks.onCrimeUpdated(crime);
                showPhoto();
            }
        } else if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            if (c.getCount() == 0) {
                c.close();
                return;
            }

            c.moveToFirst();
            String suspect = c.getString(0);
            crime.setSuspect(suspect);
            callbacks.onCrimeUpdated(crime);
            suspectBtn.setText(suspect);
            c.close();
        }

    }

    private String getCrimeReport() {
        String solvedString = null;
        if (crime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, crime.getDate()).toString();

        String suspect = crime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, crime.getTitle(), dateString,
                solvedString, suspect);

        return report;
    }

    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                ArrayList<Crime> crimes = (ArrayList<Crime>) CrimeLab.get(getActivity())
                        .getCrimes();
                for (Crime c : crimes) {
                    if (c.getTitle() == null || c.getTitle().trim().length() == 0) {
                        crimes.remove(c);
                    }
                }
                return true;
            case R.id.menu_item_delete_crime_fragment:
                CrimeLab.get(getActivity()).deleteCrime(crime);
                ((CrimePagerActivity) getActivity()).getPager().getAdapter().notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    private void showPhoto() {
        // (re)set the image button's image based on our photo
        Photo p = crime.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            String path = getActivity()
                    .getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
        }
        photoView.setImageDrawable(b);
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(photoView);
    }

}
