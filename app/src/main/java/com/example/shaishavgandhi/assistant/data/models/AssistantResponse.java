package com.example.shaishavgandhi.assistant.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by shaishav.gandhi on 1/7/17.
 */

public class AssistantResponse implements Parcelable, Serializable {

    private Response response;

    public static class Response implements Serializable, Parcelable {

        private boolean success;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(this.success ? (byte) 1 : (byte) 0);
        }

        public Response() {
        }

        protected Response(Parcel in) {
            this.success = in.readByte() != 0;
        }

        public static final Creator<Response> CREATOR = new Creator<Response>() {
            @Override
            public Response createFromParcel(Parcel source) {
                return new Response(source);
            }

            @Override
            public Response[] newArray(int size) {
                return new Response[size];
            }
        };
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.response, flags);
    }

    public AssistantResponse() {
    }

    protected AssistantResponse(Parcel in) {
        this.response = in.readParcelable(Response.class.getClassLoader());
    }

    public static final Creator<AssistantResponse> CREATOR = new Creator<AssistantResponse>() {
        @Override
        public AssistantResponse createFromParcel(Parcel source) {
            return new AssistantResponse(source);
        }

        @Override
        public AssistantResponse[] newArray(int size) {
            return new AssistantResponse[size];
        }
    };
}
