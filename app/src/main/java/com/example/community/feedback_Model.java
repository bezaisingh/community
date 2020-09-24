package com.example.community;

public class feedback_Model {

    private String Feedback;

    public feedback_Model()
    {

    }

    public feedback_Model(String feedback) {
        Feedback = feedback;
    }

    public String getFeedback() {
        return Feedback;
    }

    public void setFeedback(String feedback) {
        Feedback = feedback;
    }
}
