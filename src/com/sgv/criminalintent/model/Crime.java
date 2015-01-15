
package com.sgv.criminalintent.model;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Crime {
    private static final String JSON_DATE = "date";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_TITLE = "title";
    private static final String JSON_ID = "id";
    private static final String JSON_PHOTO = "photo";
    private static final String JSON_SUSPECT = "suspect";
    

    public String getSuspect() {
        return suspect;
    }



    public void setSuspect(String suspect) {
        this.suspect = suspect;
    }

    private UUID id;
    private Photo photo;
    private String title, suspect;
    private Date date;
    private boolean solved;

    public Photo getPhoto() {
        return photo;
    }



    public void setPhoto(Photo photo) {
        this.photo = photo;
    }



    public Crime() {
        id = UUID.randomUUID();
        date = new Date();
    }
    
    

    public Crime(JSONObject json) throws JSONException {
        id = UUID.fromString(json.getString(JSON_ID));
        title = json.getString(JSON_TITLE);
        solved = json.getBoolean(JSON_SOLVED);
        date = new Date(json.getLong(JSON_DATE));
        if (json.has(JSON_PHOTO)) {
            photo = new Photo(json.getJSONObject(JSON_PHOTO));
        }
        suspect = json.getString(JSON_SUSPECT);
    }



    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getId() {
        return id;
    }

    public String toString() {
        return title;
    }

    public CharSequence dateString() {
       return android.text.format.DateFormat.format("EEEE, MMM dd, yyyy", this.getDate());
    }

    public Object toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, id.toString());
        json.put(JSON_TITLE, title.toString());
        json.put(JSON_SOLVED, solved);
        json.put(JSON_DATE, date.getTime());
        if ( photo != null)  {
            json.put(JSON_PHOTO, photo.toJSON());
        }
        json.put(JSON_SUSPECT, suspect.toString());
        return json;
    }        
        
}
