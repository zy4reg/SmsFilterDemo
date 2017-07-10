package com.rockie.segment;

import com.rockie.Constant;

public class DicWord implements Comparable {

    private String mContent = "";
    private int mValue = 0;
    private int mLength = 0;
    private int mType = Constant.DICWORD_TYPE_CHINESE;

    public DicWord() {

    }

    public DicWord(String content) {
        setContent(content);
    }

    public boolean equals(DicWord dw) {
        return mContent.equals(dw.getContent());
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
        mLength = (content == null? 0: content.length());

        mValue = 0;
        for (int i = 0; i < mContent.length(); i++) {
            mValue += mContent.charAt(i);
        }
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public int getValue() {
        return mValue;
    }

    public int getLength() {
        return mLength;
    }

    public int compareTo(Object obj) {
        DicWord dw = (DicWord) obj;

        if (equals(dw)) {
            return 0;
        }

        if (mValue > dw.getValue()) {
            return 1;
        }

        return -1;
    }
}
