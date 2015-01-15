
package com.sgv.criminalintent.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.sgv.criminalintent.CriminalIntentJSONSerializer;

public class CrimeLab {

    private static final String FILENAME = "crimes.json";
    private static CrimeLab instance;
    private Context ctx;
    private CriminalIntentJSONSerializer serializer;
    ArrayList<Crime> crimes;

    public void addCrime(Crime c) {
        crimes.add(c);
    }

    public void deleteCrime(Crime c) {
        crimes.remove(c);
    }
    
    public List<Crime> getCrimes() {
        return crimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime c : crimes) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    private CrimeLab(Context ctx) {
        this.ctx = ctx;
        serializer = new CriminalIntentJSONSerializer(ctx, FILENAME);
        try {
            crimes = serializer.loadCrimes();
        } catch (Exception e) {
            Log.d("DEBUG", "error loading crimes" + e) ;
            crimes = new ArrayList<Crime>();

        }

    }

    public boolean saveCrimes() {
        try {
            serializer.saveCrimes(crimes);
            Log.d("DEBUG", "Success saving crimes file");
            return true;
        } catch (Exception e) {
            // toast to user
            Log.d("DEBUG", "Error saving crimes file");
            return false;
        }
    }

    public static CrimeLab get(Context c) {
        if (instance == null) {
            instance = new CrimeLab(c.getApplicationContext());

        }
        return instance;
    }
}
