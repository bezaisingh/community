package com.example.community;

public class List_Item {

   private String heading;
   private String contents;

    public List_Item(String heading, String contents) {
        this.heading = heading;
        this.contents = contents;
    }

    public String getHeading() {
        return heading;
    }

    public String getContents() {
        return contents;
    }
}
