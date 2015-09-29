package com.wwd.mpandroidchallenge;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Person's details
 *
 */
public class Person implements Parcelable {
    /**
     * name
     */
    private String name;

    /**
     * email
     */
    private String email;

    /**
     * city
     */
    private String city;

    /**
     * state / region
     */
    private String state;

    /**
     * location (city, state) -> for faster access
     */
    private String location;

    /**
     * country code
     */
    private String countryCode;

    /**
     * phone number
     */
    private String phoneNumber;

    /**
     * photo url
     */
    private String photoUrlString;

    /**
     * photo localName
     * saving to disk
     */
    private String photoLocalName;

    /**
     * created at
     */
    private Date createdAt;

    // Person's Builder
    public static class Builder {
        // required
        private final String name;
        // optional
        private String email;
        private String city;
        private String state;
        private String location;
        private String countryCode;
        private String phoneNumber;
        private String photoUrlString;
        private String photoLocalName;
        private Date createdAt;

        public Builder(String name) {
            this.name = name;
        }

        public Builder email(String email1) {
            email = email1;
            return this;
        }

        public Builder city(String city1) {
            city = city1;
            return this;
        }

        public Builder state(String state1) {
            state = state1;
            return this;
        }

        public Builder location(String location1) {
            location = location1;
            return this;
        }

        public Builder countryCode(String countryCode1) {
            countryCode = countryCode1;
            return this;
        }

        public Builder phoneNumber(String phoneNumber1) {
            phoneNumber = phoneNumber1;
            return this;
        }

        public Builder photoUrlString(String photoUrlString1) {
            photoUrlString = photoUrlString1;
            return this;
        }

        public Builder photoLocalName(String photoLocalName1) {
            photoLocalName = photoLocalName1;
            return this;
        }

        public Builder createdAt(Date createdAt1) {
            createdAt = createdAt1;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }

    private Person(Builder builder) {
        name = builder.name;
        email = builder.email;
        city = builder.city;
        state = builder.state;
        location = builder.location;
        countryCode = builder.countryCode;
        phoneNumber = builder.phoneNumber;
        photoUrlString = builder.photoUrlString;
        photoLocalName = builder.photoLocalName;
        createdAt = builder.createdAt;
    }

    /**
     * getters
     */
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getLocation() {
        return location;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhotoUrlString() {
        return photoUrlString;
    }

    public String getPhotoLocalName() {
        return photoLocalName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    // parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    // only writing necessary properties to the parcel
    @Override
    public void writeToParcel(Parcel pc, int flags) {
        pc.writeString(name);
        pc.writeString(email);
        pc.writeString(city);
        pc.writeString(state);
        pc.writeString(location);
        pc.writeString(countryCode);
        pc.writeString(phoneNumber);
        pc.writeString(photoUrlString);
        pc.writeString(photoLocalName);
        pc.writeValue(createdAt);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    // only writing necessary properties from the parcel
    private Person(Parcel in) {
        name = in.readString();
        email = in.readString();
        city = in.readString();
        state = in.readString();
        location = in.readString();
        countryCode = in.readString();
        phoneNumber = in.readString();
        photoUrlString = in.readString();
        photoLocalName = in.readString();
        createdAt = (Date) in.readValue(Boolean.class.getClassLoader());
    }
}