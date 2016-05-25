package com.ahryk94gmail.mifood.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.ahryk94gmail.mifood.miband.Constants;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class UserProfile implements IUserProfile, Parcelable {

    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 0;

    private int mUid;
    private byte mGender;
    private byte mAge;
    private byte mHeight;
    private byte mWeight;
    private String mAlias = "";
    private byte mType;

    private UserProfile() {

    }

    public UserProfile(int uid, int gender, int age, int height, int weight, String alias, int type) {
        this.mUid = uid;
        this.mGender = (byte) gender;
        this.mAge = (byte) age;
        this.mHeight = (byte) height;
        this.mWeight = (byte) weight;
        this.mAlias = alias;
        this.mType = (byte) type;
    }

    public UserProfile(byte[] data) {
        this();

        if (data.length >= 20) {
            this.mUid = data[3] << 24 | (data[2] & 0xFF) << 16 | (data[1] & 0xFF) << 8 | (data[0] & 0xFF);
            this.mGender = data[4];
            this.mAge = data[5];
            this.mHeight = data[6];
            this.mWeight = data[7];
            this.mType = data[8];
            try {
                this.mAlias = new String(data, 9, 8, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                this.mAlias = "";
            }
        }
    }

    private UserProfile(Parcel in) {
        this.mUid = in.readInt();
        this.mGender = in.readByte();
        this.mAge = in.readByte();
        this.mHeight = in.readByte();
        this.mWeight = in.readByte();
        this.mAlias = in.readString();
        this.mType = in.readByte();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mUid);
        dest.writeByte(mGender);
        dest.writeByte(mAge);
        dest.writeByte(mHeight);
        dest.writeByte(mWeight);
        dest.writeString(mAlias);
        dest.writeByte(mType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

    @Override
    public int getUid() {
        return this.mUid;
    }

    @Override
    public byte getGender() {
        return this.mGender;
    }

    @Override
    public byte getAge() {
        return this.mAge;
    }

    @Override
    public int getHeight() {
        return this.mHeight & 0xFF;
    }

    @Override
    public int getWeight() {
        return this.mWeight & 0xFF;
    }

    @Override
    public String getAlias() {
        return this.mAlias;
    }

    @Override
    public byte getType() {
        return this.mType;
    }

    @Override
    public byte[] getBytes(String address) {
        byte[] aliasBytes;

        try {
            aliasBytes = this.mAlias.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            aliasBytes = new byte[0];
        }

        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.put((byte) this.mUid);
        buffer.put((byte) (this.mUid >> 8));
        buffer.put((byte) (this.mUid >> 16));
        buffer.put((byte) (this.mUid >> 24));
        buffer.put(this.mGender);
        buffer.put(this.mAge);
        buffer.put(this.mHeight);
        buffer.put(this.mWeight);
        buffer.put(this.mType);
        if (address.startsWith(Constants.type.MI1A)) {
            buffer.put((byte) 5);
        } else if (address.startsWith(Constants.type.MI1S)) {
            buffer.put((byte) 4);
        } else {
            buffer.put((byte) 0);
            //TODO
        }
        buffer.put((byte) 0);

        if (aliasBytes.length <= 8) {
            buffer.put(aliasBytes);
            buffer.put(new byte[8 - aliasBytes.length]);
        } else {
            buffer.put(aliasBytes, 0, 8);
        }

        byte[] crcSequence = new byte[19];
        for (int i = 0; i < crcSequence.length; i++)
            crcSequence[i] = buffer.array()[i];

        byte crcb = (byte) ((getCRC8(crcSequence) ^ Integer.parseInt(address.substring(address.length() - 2), 16)) & 0xFF);
        buffer.put(crcb);

        return buffer.array();
    }

    private int getCRC8(byte[] seq) {
        int len = seq.length;
        int i = 0;
        byte crc = 0x00;

        while (len-- > 0) {
            byte extract = seq[i++];
            for (byte tempI = 8; tempI != 0; tempI--) {
                byte sum = (byte) ((crc & 0xFF) ^ (extract & 0xFF));
                sum = (byte) ((sum & 0xFF) & 0x01);
                crc = (byte) ((crc & 0xFF) >>> 1);
                if (sum != 0) {
                    crc = (byte) ((crc & 0xFF) ^ 0x8C);
                }
                extract = (byte) ((extract & 0xFF) >>> 1);
            }
        }
        return (crc & 0xFF);
    }
}
