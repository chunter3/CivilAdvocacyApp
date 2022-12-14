package com.example.civiladvocacyapp;


import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class GovOfficialVolley {

    private static RequestQueue queue;
    private static ArrayList<GovOfficial> govOfficialList = new ArrayList<>();
    private static final String civicInfoURL = "https://www.googleapis.com/civicinfo/v2/representatives";
    private static final String myAPIKey = "AIzaSyBXyX_kmPL2BWVJAeUTxkgMqrYaDvGTbhg"; // API key from Google Civic Information API
    private static final String TAG = "GovOfficialVolley";

    public static void downloadCivicInfo(MainActivity mainActivityIn, String location) {

        queue = Volley.newRequestQueue(mainActivityIn);

        Uri.Builder buildURL = Uri.parse(civicInfoURL).buildUpon();
        buildURL.appendQueryParameter("key", myAPIKey);
        buildURL.appendQueryParameter("address", location);
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener = response -> handleResult(mainActivityIn, response.toString());

        Response.ErrorListener error = error1 -> handleResult(mainActivityIn, null);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToUse, null, listener, error);
        queue.add(jsonObjectRequest);
    }

    private static void handleResult(MainActivity mainActivityIn, String response) {
        if (response == null) {
            mainActivityIn.downloadFailed();
            return;
        }
        final ArrayList<GovOfficial> govOfficialList = parseJSON(response);
        mainActivityIn.updateHomeScreen(govOfficialList);
    }

    private static ArrayList<GovOfficial> parseJSON(String response) {
        try {
            govOfficialList.clear();
            JSONObject jObjMain = new JSONObject(response);

            JSONArray officesArray = jObjMain.getJSONArray("offices");
            JSONArray officialsArray = jObjMain.getJSONArray("officials");

            for (int i = 0; i < officesArray.length(); i++) {
                JSONObject officeObj = officesArray.getJSONObject(i);
                String officeOfOfficial = officeObj.getString("name");
                JSONArray officeIndices = officeObj.getJSONArray("officialIndices");
                for (int j = 0; j < officeIndices.length(); j++) {
                    JSONObject officialObj = officialsArray.getJSONObject((Integer) officeIndices.get(j));
                    String nameOfOfficial = officialObj.getString("name");
                    String addressOfOfficial = concatAddress(officialObj.getJSONArray("address"));
                    String partyOfOfficial;
                    if (officialObj.has("party")) {
                        partyOfOfficial = officialObj.getString("party");
                    } else {
                        partyOfOfficial = "Unknown";
                    }
                    String phoneNumOfOfficial;
                    if (officialObj.has("phones")) {
                        JSONArray phoneNums = officialObj.getJSONArray("phones");
                        phoneNumOfOfficial = (String) phoneNums.get(0);
                    } else {
                        phoneNumOfOfficial = "N/A";
                    }
                    String websiteOfOfficial;
                    if (officialObj.has("urls")) {
                        JSONArray websites = officialObj.getJSONArray("urls");
                        websiteOfOfficial = (String) websites.get(0);
                    } else {
                        websiteOfOfficial = "N/A";
                    }
                    String emailOfOfficial;
                    if (officialObj.has("emails")) {
                        JSONArray emails = officialObj.getJSONArray("emails");
                        emailOfOfficial = (String) emails.get(0);
                    } else {
                        emailOfOfficial = "N/A";
                    }
                    String photoURLOfOfficial;
                    if (officialObj.has("photoUrl")) {
                        photoURLOfOfficial = officialObj.getString("photoUrl");
                    } else {
                        photoURLOfOfficial = "";
                    }
                    String facebookID = getChannelIDs(officialObj.getJSONArray("channels"), true, false);
                    String twitterID = getChannelIDs(officialObj.getJSONArray("channels"), false, true);
                    String youtubeID = getChannelIDs(officialObj.getJSONArray("channels"), false, false);

                    govOfficialList.add(new GovOfficial(nameOfOfficial, officeOfOfficial, partyOfOfficial, addressOfOfficial, phoneNumOfOfficial, emailOfOfficial, websiteOfOfficial, facebookID, twitterID, youtubeID, photoURLOfOfficial));
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return govOfficialList;
    }

    public static String concatAddress(JSONArray addressArray) { // constructs the address of a particular official
        try {
            if (addressArray == null || addressArray.length() == 0) {
                return "N/A"; // N/A means the address is not available
            }
            JSONObject addressObj = (JSONObject) addressArray.get(0);
            String line1 = "";
            String line2 = "";
            String line3 = "";
            if (addressObj.has("line1")) {
                line1 = addressObj.getString("line1") + ", ";
            }
            if (addressObj.has("line2")) {
                line2 = addressObj.getString("line2") + ", ";
            }
            if (addressObj.has("line3")) {
                line3 = addressObj.getString("line3") + ", ";
            }
            String city = addressObj.getString("city") + ", ";
            String state = addressObj.getString("state") + " ";
            String zip = addressObj.getString("zip");

            return line1 + line2 + line3 + city + state + zip;

        } catch (Exception e) {
            Log.d(TAG, "concatAddress: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    public static String getChannelIDs(JSONArray channelArray, boolean facebook, boolean twitter) { // retrieves the channel IDs of the related social media of a particular official
        try {
            if (facebook) {
                for (int i = 0; i < channelArray.length(); i++) {
                    JSONObject channelObj = channelArray.getJSONObject(i);
                    if (channelObj.has("type")) {
                        String channelType = channelObj.getString("type");
                        if (channelType.equals("Facebook")) {
                            return channelObj.getString("id");
                        }
                    }
                }
            }
            else if (twitter) {
                for (int i = 0; i < channelArray.length(); i++) {
                    JSONObject channelObj = channelArray.getJSONObject(i);
                    if (channelObj.has("type")) {
                        String channelType = channelObj.getString("type");
                        if (channelType.equals("Twitter")) {
                            return channelObj.getString("id");
                        }
                    }
                }
            }
            else {
                for (int i = 0; i < channelArray.length(); i++) {
                    JSONObject channelObj = channelArray.getJSONObject(i);
                    if (channelObj.has("type")) {
                        String channelType = channelObj.getString("type");
                        if (channelType.equals("YouTube")) {
                            return channelObj.getString("id");
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.d(TAG, "getChannelIDs: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
}
