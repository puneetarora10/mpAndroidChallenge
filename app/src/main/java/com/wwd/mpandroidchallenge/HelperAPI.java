package com.wwd.mpandroidchallenge;

import java.net.URL;
import java.util.LinkedHashMap;

/**
 * Helper Methods
 *
 */
public class HelperAPI {
    // global variables
    private final static String GENERIC_ERROR_MESSAGE = "Sorry, Slow Internet Connection on your device!!";

    // returns a default instance of HelperService to implement singleton
    // in other words only one instance of HelperService object exists in the application
    private static HelperAPI helperAPI;

    public static HelperAPI getDefaultInstance() {
        if (helperAPI == null) {
            helperAPI = new HelperAPI();
        }

        return helperAPI;
    }

    // returns GENERIC_ERROR_MESSAGE
    public String returnGenericErrorMessage() {
        return GENERIC_ERROR_MESSAGE;
    }

    /**
     * appends generic errorMessage
     *
     * @param appendGenericErrorMessageToIt hashMap to be which generic errorMessage will be appended
     */
    public void appendGenericErrorMessage(LinkedHashMap<String, String> appendGenericErrorMessageToIt) {
        appendGenericErrorMessageToIt.put("errorMessage", GENERIC_ERROR_MESSAGE);
    }

    /**
     * @param url an absolute url given to this method
     * @return suggested fileName
     */
    public String returnFileName(URL url) {
        // get the file of this url
        String urlString = url.getFile();
        return urlString.substring(urlString.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0];
    }

    /**
     * @param urlString for some file
     * @return suggested fileName
     */
    public String returnFileName(String urlString) {
        return urlString.substring(urlString.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0];
    }
}