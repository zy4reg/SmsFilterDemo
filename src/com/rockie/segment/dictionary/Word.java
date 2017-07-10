package com.rockie.segment;

public class Word {

    private DicWord mDicWord;
    private int mType;

    /**
     * MD5 value
     */
    private long mCode;

    public Word(long code, int type) {
        mType = type;
        mCode = code;
    }

    public DicWord getDicWord() {
        return mDicWord;
    }

    public void setDicWord(DicWord dw) {
        mDicWord = dw;
    }

    public long getCode() {
        return mCode;
    }

    public void setCode(long code) {
        mCode = code;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
