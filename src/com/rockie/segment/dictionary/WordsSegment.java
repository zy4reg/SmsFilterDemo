package com.rockie.segment;

import com.rockie.Constant;
import com.rockie.Utils;

import java.util.ArrayList;
import java.util.List;

public class WordsSegment {

    private Dictionary mDictionary;
    private List<TextPiece> mListTextPiece = new ArrayList();
    private List<DicWord> mListResultL2R = new ArrayList();
    private List<DicWord> mListResultR2L = new ArrayList();
    private List<DicWord> mListResultFinal = new ArrayList();

    public WordsSegment(Dictionary dictionary) {
        mDictionary = dictionary;
    }

    public List getResult() {
        return mListResultFinal;
    }

    public String segment(String str) {
        String result = "";
        filter(str);
        result = build();
        return result;
    }

    private void filter(String str) {
        TextPiece textPiece = null;
        String temp = "";
        char c = str.charAt(0);
        int type = Utils.getCharType(c);
        temp += c;

        mListTextPiece.clear();

        for (int i = 1; i < str.length(); i++) {
            c = str.charAt(i);
            int tempType = Utils.getCharType(c);

            if (tempType == type) {
                temp += c;
            } else {
                textPiece = new TextPiece(temp, type);
                mListTextPiece.add(textPiece);
                type = tempType;
                temp = "" + c;
            }
        }

        textPiece = new TextPiece(temp, type);
        mListTextPiece.add(textPiece);
    }

    private String build() {
        TextPiece textPiece = null;
        String result = "";
        mListResultFinal.clear();

        for (int i = 0; i < mListTextPiece.size(); i++) {
            textPiece = mListTextPiece.get(i);
            result += operate(textPiece);
        }
        return result;
    }

    private String operate(TextPiece textPiece) {
        String result = "";

        if (textPiece.getType() != Constant.TEXT_TYPE_CHINESE) {
            result = textPiece.getText() + " ";
            DicWord dw = new DicWord(result);

            int type = Utils.getCharType(result.charAt(0));
            if (type == Constant.TEXT_TYPE_NUMBER) {
                if (result.length()==15 || result.length()==18) {
                    dw.setType(Constant.DICWORD_TYPE_ID_NUMBER);
                } else if (result.charAt(0) == '1' && result.length() == 11) {
                    dw.setType(Constant.DICWORD_TYPE_PHONE_NUMBER);
                } else if (result.length() >= 19 && result.length() <= 26) {
                    dw.setType(Constant.DICWORD_TYPE_BANK_NUMBER);
                } else {
                    dw.setType(Constant.DICWORD_TYPE_GENERAL_NUMBER);
                }
            } else if (type == Constant.TEXT_TYPE_CHAR) {
                dw.setType(Constant.DICWORD_TYPE_ENGLISH);
            } else {
                dw.setType(Constant.DICWORD_TYPE_OTHER);
            }

            mListResultFinal.add(dw);
        } else {
            List listL2R = segmentCHL2R(textPiece.getText());
            List listR2L = segmentCHR2L(textPiece.getText());

            result = compare(listL2R, listR2L);
        }

        return result;
    }

    private List segmentCHL2R(String str) {
        String temp = "";
        List<String> list = new ArrayList<>();
        int[] wordLen = mDictionary.getWordLens();
        DicWord result = null;
        int start = 0;
        int length = (str == null ? 0 : str.length());

        if (wordLen.length == 0) {
            return null;
        }
        mListResultL2R.clear();

        int minReadLen = wordLen[0];

        while (start < length) {
            minReadLen = wordLen[wordLen.length - 1];
            if (minReadLen > (length - start)) {
                minReadLen = length - start;
            }

            while (minReadLen > 1) {
                if (!BinarySearch.search(wordLen, minReadLen)) {
                    minReadLen--;
                    continue;
                }
                temp = str.substring(start, start + minReadLen);

                if ((result = mDictionary.find(temp)) != null) {
                    break;
                } else {
                    minReadLen--;
                }
            }

            temp = str.substring(start, start + minReadLen);
            list.add(temp);
            start += minReadLen;

            if (result == null) {
                result = mDictionary.find(temp);
            }
            if (minReadLen > 0 && result != null) {
                mListResultL2R.add(result);
                result = null;
            }
        }

        return list;
    }

    private List segmentCHR2L(String str) {
        String temp = "";
        List<String> list = new ArrayList<>();
        List<String> list1 = new ArrayList<>();
        int[] wordLen = mDictionary.getWordLens();
        DicWord result = null;
        int start = 0;
        int length = (str == null ? 0 : str.length());

        if (wordLen.length == 0) {
            return null;
        }
        mListResultR2L.clear();

        int minReadLen = wordLen[0];

        while (start < length) {
            minReadLen = wordLen[wordLen.length - 1];
            if (minReadLen > (length - start)) {
                minReadLen = length - start;
            }

            while (minReadLen > 1) {
                if (!BinarySearch.search(wordLen, minReadLen)) {
                    minReadLen--;
                    continue;
                }

                temp = str.substring(length - start - minReadLen, length - start);
                if ((result = mDictionary.find(temp)) != null) {
                    break;
                } else {
                    minReadLen--;
                }
            }

            temp = str.substring(length - start - minReadLen, length - start);
            list.add(temp);
            start += minReadLen;

            if (result == null) {
                result = mDictionary.find(temp);
            }
            if (minReadLen > 0 && result != null) {
                mListResultR2L.add(result);
                result = null;
            }
        }

        for (int i = list.size() - 1; i >= 0; i--) {
            temp = list.get(i);
            list1.add(temp);
        }

        return list1;
    }

    private String compare(List<String> list1, List<String> list2) {
        if (list1 == null || list2 == null) {
            return "";
        }

        List<String> list = null;
        String result = "";
        String temp = "";
        int len = 0, len1 = 0, len2 = 0;
        boolean copyFromL2R = true;

        if (list1.size() < list2.size()) {
            list = list1;
        } else if (list1.size() > list2.size()) {
            list = list2;
            copyFromL2R = false;
        } else {
            for (int i = 0; i < list1.size(); i++) {
                len = list1.get(i).length();
                if (len1 < len) {
                    len1 = len;
                }
                len = list2.get(i).length();
                if (len2 < len) {
                    len2 = len;
                }
            }
            if (len1 > len2) {
                list = list1;
            } else {
                list = list2;
                copyFromL2R = false;
            }
        }

        if (copyFromL2R) {
            for (int i = 0; i < mListResultL2R.size(); i++) {
                mListResultFinal.add(mListResultL2R.get(i));
            }
        } else {
            for (int i = 0; i < mListResultR2L.size(); i++) {
                mListResultFinal.add(mListResultR2L.get(i));
            }
        }

        for (int i = 0; i < list.size(); i++) {
            result += list.get(i) + " ";
        }

        return result;
    }
}
