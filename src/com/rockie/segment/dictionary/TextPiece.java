package com.rockie.segment;

public class TextPiece {

    private int mType = -1;
    private String mText = "";

    public TextPiece(String text, int type) {
        mText = text;
        mType = type;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
