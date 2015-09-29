package com.wwd.mpandroidchallenge;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class PeopleActivity extends AppCompatActivity {

    // people
    private static ArrayList<Person> peopleList;

    // listView
    private static ListView peopleListView;

    private static File filesDir;

    public ArrayList<Person> getPeopleList() {
        return peopleList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        // set filesDir
        filesDir = getFilesDir();
        // initialize
        peopleList = new ArrayList<Person>();
        // download people's data
        downloadPeopleData();

        peopleListView = (ListView) findViewById(R.id.people_list);
        peopleListView.setAdapter(new PeopleAdapter(this, filesDir));
        //peopleListView.setAdapter(new PeopleAdapter(this, filesDir));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_people, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // no need for menu
        //if (id == R.id.action_settings) {
        //  return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    /**
     * calls downloads DownloadPhotoService to download person's photo
     */
    public void downloadPhoto(int position, String photoUrlString, String photoLocalName) {
        Intent intent = new Intent(PeopleActivity.this, DownloadPhotoService.class);
        Messenger messenger = new Messenger(downloadPhotoHandler);
        intent.putExtra("messenger", messenger);
        intent.putExtra("position", position);
        intent.putExtra("photoUrlString", photoUrlString);
        intent.putExtra("photoLocalName", photoLocalName);
        startService(intent);
    }

    // handler called from DownloadPhotoService
    Handler downloadPhotoHandler = new Handler() {
        public void handleMessage(Message message) {
            int position = message.getData().getInt("position");
            String photoLocalName = message.getData().getString("photoLocalName");
            if (photoLocalName != null) {// update image in gridView
                if (checkIfViewIsVisibleInListView(position)) {// view at positions is still visible
                    try {
                        // get imageView
                        ImageView imageView = (ImageView) peopleListView.getChildAt(position - peopleListView.getFirstVisiblePosition()).findViewById(R.id.photoImageView);
                        if (imageView != null) {
                            // set bitmap
                            imageView.setImageBitmap(BitmapFactory.decodeFile(filesDir + "/" + photoLocalName));
                        }
                    } catch (Exception e) {
                        // no need to do anything right now
                    }
                }
            }
        }
    };

    /**
     * determine if view at index is visible
     *
     * @param index image's index
     * @return true if index is between firstVisibleRow and lastVisibleRow
     */
    private static Boolean checkIfViewIsVisibleInListView(int index) {
        Boolean viewVisible = false;
        // get firstVisibleRow
        int firstVisibleRow = peopleListView.getFirstVisiblePosition();
        // get lastVisibleRow
        int lastVisibleRow = peopleListView.getLastVisiblePosition();

        // if index is between firstVisibleRow and lastVisibleRow
        // in other words attachment is still visible in gridView
        if (index >= firstVisibleRow && index <= lastVisibleRow) {// view is visible
            viewVisible = true;
        }
        return viewVisible;
    }

    /**
     * call DownloadPeopleDataService to download people data
     */
    private void downloadPeopleData() {
        Intent intent = new Intent(PeopleActivity.this, DownloadPeopleDataService.class);
        Messenger messenger = new Messenger(downloadPeopleDataHandler);
        intent.putExtra("messenger", messenger);
        startService(intent);
    }

    // handler called from DownloadPeopleDataService
    Handler downloadPeopleDataHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.getData().getString("errorMessage") != null) {// error exists while downloading People's Data
                AlertDialog.Builder builder1 = new AlertDialog.Builder(PeopleActivity.this);
                builder1.setMessage(message.getData().getString("errorMessage"));
                builder1.setCancelable(true);
                builder1.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            } else {
                peopleList = message.getData().getParcelableArrayList("peopleList");
                // notify imageAdapter
                PeopleAdapter peopleAdapter = (PeopleAdapter) peopleListView.getAdapter();
                peopleAdapter.notifyDataSetChanged();
            }
        }
    };
}
