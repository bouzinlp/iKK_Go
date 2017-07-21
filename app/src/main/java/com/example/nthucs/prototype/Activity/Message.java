package com.example.nthucs.prototype.Activity;

/**
 * Created by menglin on 2017/7/9.
 */
import java.io.Serializable;

public class Message implements Serializable{
    private String query;
    private String action;
    private String answer;
    String id;

    public Message() {
    }

    public Message(String query, String action, String answer, String id) {
        this.query = query;
        this.action = action;
        this.answer = answer;
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getAns() {
        return answer;
    }

    public void setAns(String answer) {
        this.answer = answer;
    }
    public String getAct() {return action;}

    public void setAct(String action){
        this.answer = answer;
    }

    public String getId() {return  id;}

    public void setId(String id) {
        this.id = id;
    }
}
