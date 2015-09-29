package com.wwd.mpandroidchallenge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * PeopleAdapter for peopleListView
 */
public class PeopleAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflator;
    private File filesDir;

    public PeopleAdapter(Context c, File _filesDir) {
        mContext = c;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        filesDir = _filesDir;
    }

    public int getCount() {
        PeopleActivity peopleActivity = (PeopleActivity) mContext;
        return peopleActivity.getPeopleList().size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflator.inflate(R.layout.person_item_layout, parent, false);
        }
        // get person
        PeopleActivity peopleActivity = (PeopleActivity) mContext;
        if (peopleActivity.getPeopleList().size() > 0) {
            Person person = peopleActivity.getPeopleList().get(position);
            // update photoImageView
            ImageView photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
            // check if photo has already been downloaded
            Bitmap photoBM = BitmapFactory.decodeFile(filesDir + "/" + person.getPhotoLocalName());
            if (photoBM != null) {// load photo
                photoImageView.setImageBitmap(photoBM);
            } else {// download photo
                // placeholder
                photoImageView.setImageResource(R.drawable.default_person_photo);
                peopleActivity.downloadPhoto(position, person.getPhotoUrlString(), person.getPhotoLocalName());
            }
            // update nameTextView
            TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            nameTextView.setText(person.getName());
            // update locationTextView
            TextView locationTextView = (TextView) convertView.findViewById(R.id.locationTextView);
            locationTextView.setText(person.getLocation());
            // update phoneTextView
            TextView phoneTextView = (TextView) convertView.findViewById(R.id.phoneTextView);
            phoneTextView.setText(person.getPhoneNumber());
            // update emailTextView
            TextView emailTextView = (TextView) convertView.findViewById(R.id.emailTextView);
            emailTextView.setText(person.getEmail());
        }

        return convertView;
    }
}
