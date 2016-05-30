package cphbusiness.dk.androidproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by kalkun on 30-05-2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendRequest {
    private String accepted;
    private String friendSeeker;
    private String friendAccepter;

    public FriendRequest() {
    }

    public FriendRequest(String accepted, String friendSeeker, String friendAccepter) {
        this.accepted = accepted;
        this.friendSeeker = friendSeeker;
        this.friendAccepter = friendAccepter;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public String getFriendSeeker() {
        return friendSeeker;
    }

    public void setFriendSeeker(String friendSeeker) {
        this.friendSeeker = friendSeeker;
    }

    public String getFriendAccepter() {
        return friendAccepter;
    }

    public void setFriendAccepter(String friendAccepter) {
        this.friendAccepter = friendAccepter;
    }
}
