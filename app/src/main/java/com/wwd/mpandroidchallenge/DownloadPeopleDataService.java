package com.wwd.mpandroidchallenge;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadPeopleDataService extends Service {
    private Messenger peopleActivityMessenger;
    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    /*
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    // A queue of Runnables
    private final BlockingQueue<Runnable> mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();

    // list of people
    ArrayList<Person> peopleList = new ArrayList<>();

    public DownloadPeopleDataService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // get extras using intent
        peopleActivityMessenger = intent.getParcelableExtra("messenger");
        // threadPoolExecutor
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDecodeWorkQueue);
        Thread downloadPeopleDataThread = new Thread(new DownloadPeopleDataThread());
        threadPoolExecutor.execute(downloadPeopleDataThread);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Downloads images' metaData
     */
    private class DownloadPeopleDataThread implements Runnable {
        @Override
        public void run() {
            // send peopleList to PeopleActivity
            Bundle dataToSendToHandler = new Bundle();

            try {
                // for formatting
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd'T'hh:mm:ss");
                JSONArray allPeopleData = MixpanelAPI.getDefaultInstance().fetchEngageResults();
                // loop through allPeopleData and create Person Objects
                for (int i = 0; i < allPeopleData.length(); i++) {
                    JSONObject personsData = allPeopleData.getJSONObject(i);
                    JSONObject personsProperties = personsData.getJSONObject("$properties");
                    String name = "N/A", email = "N/A", city = "N/A", state = "N/A", location = "", countryCode = "N/A", phoneNumber = "N/A", photoUrlString = "N/A", photoLocalName = "N/A";
                    Date createdAt = null;
                    // update properties
                    if (!personsProperties.optString("$name").isEmpty()) {// update name if exists
                        name = personsProperties.getString("$name");
                    }
                    if (!personsProperties.optString("$email").isEmpty()) {// update email if exists
                        email = personsProperties.getString("$email");
                    }
                    if (!personsProperties.optString("$city").isEmpty()) {// update city if exists
                        city = personsProperties.getString("$city");
                        location = city;
                    }
                    if (!personsProperties.optString("$region").isEmpty()) {// update state if exists
                        state = personsProperties.getString("$region");
                        location += ", " + state;
                    }
                    if (!personsProperties.optString("$country_code").isEmpty()) {// update countryCode if exists
                        countryCode = personsProperties.getString("$country_code");
                    }
                    if (!personsProperties.optString("phone").isEmpty()) {// update phoneNumber if exists
                        phoneNumber = personsProperties.getString("phone");
                    }
                    if (!personsProperties.optString("photo_url").isEmpty()) {// update photoUrlString if exists
                        photoUrlString = personsProperties.getString("photo_url");
                        photoLocalName = HelperAPI.getDefaultInstance().returnFileName(photoUrlString);
                    }
                    if (!personsProperties.optString("$created").isEmpty()) {// update photoUrlString if exists
                        createdAt = formatter.parse(personsProperties.getString("$created"));
                    }
                    // build person object
                    Person person = new Person.Builder(name)
                            .email(email)
                            .city(city)
                            .state(state)
                            .location(location)
                            .countryCode(countryCode)
                            .phoneNumber(phoneNumber)
                            .photoUrlString(photoUrlString)
                            .photoLocalName(photoLocalName)
                            .createdAt(createdAt)
                            .build();
                    // add it to peopleList
                    peopleList.add(person);
                }
            } catch (Exception e) {// return errorMessage
                dataToSendToHandler.putString("errorMessage", HelperAPI.getDefaultInstance().returnGenericErrorMessage());
            }

            try {
                dataToSendToHandler.putParcelableArrayList("peopleList", peopleList);
                Message message = Message.obtain();
                message.setData(dataToSendToHandler);
                peopleActivityMessenger.send(message);
            } catch (Exception e) {
                // no need to do anything right now
            }
        }
    }
}
