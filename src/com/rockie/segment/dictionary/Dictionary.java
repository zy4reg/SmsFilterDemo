package com.rockie.segment;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Dictionary {

    public static boolean isReady = false;

    private static Dictionary mInstance;

    /**
     * store the sub dictionary
     */
    private List<SubDictionary> mSubDictionarys = new ArrayList<>();

    /**
     * store the sub dictionary word length
     */
    private int[] mWordLens = null;

    public static Dictionary getInstance (String url) {
        if (mInstance == null) {
            mInstance = new Dictionary(url);
        }

        return mInstance;
    }

    private Dictionary(String url) {
        long start = System.currentTimeMillis();

        int count = importDictionary(url);
        mWordLens = new int[mSubDictionarys.size()];

        for (int i = 0; i < mWordLens.length; i++) {
            mWordLens[i] = mSubDictionarys.get(i).getWordLen();
        }

        long end = System.currentTimeMillis();

        System.out.println("time for import dictionary: " + (end - start) + ", words count: " + count);
    }

    public List getSubDictionarys() {
        return mSubDictionarys;
    }

    public int[] getWordLens() {
        return mWordLens;
    }

    public DicWord find(String str) {
        DicWord dw = new DicWord(str);

        SubDictionary subDictionary = findSubDictionary(dw);

        if (subDictionary == null) {
            return null;
        }

        return subDictionary.search(dw);
    }

    private int importDictionary(String url) {
        BufferedReader br = null;
        String temp = null;
        DicWord dw = null;
        SubDictionary subDictionary = null;
        String[] array = null;
        int count = 0;

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(url), "UTF-8"));

            while (true) {
                temp = br.readLine();
                if (temp == null) {
                    break;
                }

                dw = new DicWord(temp);
                subDictionary = findSubDictionary(dw);
                if (subDictionary == null) {
                    subDictionary = new SubDictionary();
                    subDictionary.setWordLen(dw.getLength());
                    mSubDictionarys.add(subDictionary);
                }
                if (subDictionary.insert(dw)) {
                    count++;
                }
            }
            isReady = true;

            br.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            br = null;
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return count;
    }

    private SubDictionary findSubDictionary(DicWord dw) {
        SubDictionary subDictionary = null;

        for (int i = 0; i < mSubDictionarys.size(); i++) {
            subDictionary = mSubDictionarys.get(i);
            if (subDictionary.getWordLen() == dw.getLength()) {
                return subDictionary;
            }
        }

        return null;
    }
}
