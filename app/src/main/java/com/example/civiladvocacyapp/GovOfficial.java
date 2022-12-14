package com.example.civiladvocacyapp;

import java.io.Serializable;

public class GovOfficial implements Serializable {

    private final String name;
    private final String office;
    private final String party;
    private final String address;
    private final String phoneNum;
    private final String email;
    private final String website;
    private final String facebookID;
    private final String twitterID;
    private final String youtubeID;
    private String photoURL;

    // GovOfficial obj holds current info on a particular government official
    public GovOfficial(String name, String office, String party, String address, String phoneNum, String email, String website, String facebookID, String twitterID, String youtubeID, String photoURL) {
        this.name = name;
        this.office = office;
        this.party = party;
        this.address = address;
        this.phoneNum = phoneNum;
        this.email = email;
        this.website = website;
        this.facebookID = facebookID;
        this.twitterID = twitterID;
        this.youtubeID = youtubeID;
        this.photoURL = photoURL;
    }

    String getName() {
        return name;
    }
    String getOffice() {
        return office;
    }
    String getParty() {
        return party;
    }
    String getAddress() {
        return address;
    }
    String getPhoneNum() {
        return phoneNum;
    }
    String getEmail() {
        return email;
    }
    String getWebsite() {
        return website;
    }
    String getFacebookID() {
        return facebookID;
    }
    String getTwitterID() {
        return twitterID;
    }
    String getYoutubeID() {
        return youtubeID;
    }
    String getPhotoURL() {
        if (photoURL.contains("https")) {
            return photoURL;
        }
        photoURL = photoURL.replace("http", "https");
        return photoURL;
    }

}
