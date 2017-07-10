package com.rockie.segment;

import java.util.ArrayList;
import java.util.List;

public class SubDictionary {

    private int mWordLen = 0;
    private List<DicWord> mWordData = new ArrayList<>();

    public boolean insert(DicWord dw) {
        if (dw.getLength() != mWordLen) {
            return false;
        }

        BinarySearch.search(mWordData, dw, true);
        return true;
    }

    public int getWordLen() {
        return mWordLen;
    }

    public void setWordLen(int wordLen) {
        mWordLen = wordLen;
    }

    public List getWordData() {
        return mWordData;
    }

    public DicWord search(DicWord dw) {
        return BinarySearch.search(mWordData, dw, false);
    }
}
