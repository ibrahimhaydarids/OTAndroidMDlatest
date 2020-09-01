package com.ids.fixot.model;



import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dev on 10/4/2016.
 */

public class NotificationItem implements Parcelable {

    public static final Creator CREATOR
            = new Creator() {
        public NotificationItem createFromParcel(Parcel in) {
            return new NotificationItem(in);
        }

        public NotificationItem[] newArray(int size) {
            return new NotificationItem[size];
        }
    };
    private int DeviceId, NotificationId;
    private String DateSent, Message, Notes;
    private Boolean IsSent;

    public NotificationItem() {

    }

    // "De-parcel object
    public NotificationItem(Parcel in) {

        DateSent = in.readString();
        DeviceId = in.readInt();
        IsSent = in.readByte() != 0;
        Message = in.readString();
        Notes = in.readString();
        NotificationId = in.readInt();



    }

    public static Creator getCREATOR() {
        return CREATOR;
    }

    public int getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(int deviceId) {
        DeviceId = deviceId;
    }

    public int getNotificationId() {
        return NotificationId;
    }

    public void setNotificationId(int notificationId) {
        NotificationId = notificationId;
    }

    public String getDateSent() {
        return DateSent;
    }

    public void setDateSent(String dateSent) {
        DateSent = dateSent;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public Boolean getSent() {
        return IsSent;
    }

    public void setSent(Boolean sent) {
        IsSent = sent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(DateSent);
        dest.writeInt(DeviceId);
        dest.writeByte((byte) (IsSent ? 1 : 0));
        dest.writeString(Message);
        dest.writeString(Notes);
        dest.writeInt(NotificationId);


    }
}
