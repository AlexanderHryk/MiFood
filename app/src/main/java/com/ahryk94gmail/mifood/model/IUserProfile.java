package com.ahryk94gmail.mifood.model;

public interface IUserProfile {

    int getUid();

    byte getGender();

    byte getAge();

    int getHeight();

    int getWeight();

    String getAlias();

    byte getType();

    byte[] getBytes(String address);
}
