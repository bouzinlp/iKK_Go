package com.example.nthucs.prototype.Activity;

/**
 * Created by menglin on 2017/7/9.
 */
import java.io.Serializable;

public class Message implements Serializable{
    private String message;
    private String action;
    String id;

    public Message() {
    }

    public Message(String message, String action,String id) {
        this.message = message;
        this.action = action;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getAct() {return action;}

    public void setAct(String action){
        this.action = action;
    }

    public String getId() {return  id;}

    public void setId(String id) {
        this.id = id;
    }
}
