
package com.sgv.criminalintent.activity;

import java.util.ArrayList;


import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.sgv.criminalintent.R;
import com.sgv.criminalintent.fragment.CrimeFragment;
import com.sgv.criminalintent.model.Crime;
import com.sgv.criminalintent.model.CrimeLab;

public class CrimePagerActivity extends FragmentActivity implements CrimeFragment.Callbacks {

    private ViewPager pager;
    private ArrayList<Crime> crimes;
    
    public ViewPager getPager() {
        return pager;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        pager = new ViewPager(this);
        //the fragmentManager requires that any view used as a fragment must have an id
        //we create this programmatically as it is a simple view
        pager.setId(R.id.viewPager);
        //sets this VP as a view for the content for the activity
        setContentView(pager);
        crimes = (ArrayList<Crime>) CrimeLab.get(this).getCrimes();
        FragmentManager fm = getSupportFragmentManager();

        pager.setAdapter(new FragmentStatePagerAdapter(fm) {

            @Override
            public Fragment getItem(int index) {
                Crime c = crimes.get(index);
                return CrimeFragment.newInstance(c.getId());
            }

            @Override
            public int getCount() {
                return crimes.size();
            }

        });

        UUID id = (UUID)getIntent().getSerializableExtra(CrimeFragment.CRIME_ID);
        
        for (int i = 0; i < crimes.size(); i++) {
            if (crimes.get(i).getId().equals(id)) {
                pager.setCurrentItem(i);
                setTitle(crimes.get(i).getTitle());
                break;
            }
        }

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int pos) {
                Crime c = crimes.get(pos);
                if (c.getTitle() != null) {
                    setTitle(c.getTitle());
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        
    }
}
