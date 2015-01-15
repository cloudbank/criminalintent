
package com.sgv.criminalintent.fragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import com.sgv.criminalintent.R;

public class DatePickerFragment extends DialogFragment {

    static final String DATE = "date";
    Date date;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        date = (Date) getArguments().getSerializable(DATE);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);
        DatePicker dp = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        dp.init(year, month, day, new OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker arg0, int year, int month, int day) {
                date = new GregorianCalendar(year, month, day).getTime();
                getArguments().putSerializable(DATE, date);
            }

        });

        return new AlertDialog.Builder(getActivity()).setView(v)
                .setTitle(R.string.date_picker_title).
                setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                        
                    }
                }).create();
    }

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(DATE, date);
        DatePickerFragment dialog = new DatePickerFragment();
        dialog.setArguments(args);
        return dialog;

    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent i = new Intent();
        i.putExtra(DATE, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
        
    }
}
