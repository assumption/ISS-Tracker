package edu.calpoly.isstracker.IssData;

public class ListItem {
    public String left_text = "";
    public String right_text = "";
    public boolean isHeadline = false;
    public boolean isAstronaut = false;

    ListItem(String left_text, String right_text,
             boolean isHeadline, boolean isAstronaut){
        this.left_text = left_text;
        this.right_text = right_text;
        this.isHeadline = isHeadline;
        this.isAstronaut = isAstronaut;
    }
}
