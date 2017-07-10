package com.rockie.bpnn;

import com.rockie.segment.DicWord;
import com.rockie.segment.Dictionary;
import com.rockie.segment.Word;
import com.rockie.segment.WordsSegment;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BpnnJudge {

    private static final int TRAIN_TIMES = 200;

    private static final String FILE_DIR = "files/";
    private static final String DICTION_FILE_NAME = "dicdata";
    private static final String SAMPLE_SMS_FILE_NAME = "sample_sms.txt";
    private static final String PARAMS_CONF = "params_conf.txt";

    private static BpnnJudge mInstance = new BpnnJudge();

    private Bpnn mBp;
    private int mNodes;

    private BpnnJudge() {
        BpnnConfig config = BpnnConfig.getInstance(FILE_DIR + PARAMS_CONF);

        mNodes = config.getIntValue(BpnnConfigKeys.INPUT_SIZE);
        int hiddenSize = config.getIntValue(BpnnConfigKeys.HIDDEN_SIZE);
        int outputSize = config.getIntValue(BpnnConfigKeys.OUTPUT_SIZE);
        double learnRate = config.getDoubleValue(BpnnConfigKeys.LEARN_RATE);
        double momentumRate = config.getDoubleValue(BpnnConfigKeys.MOMENTUM_RATE);

        System.out.println("train parameters: inputSize = " + mNodes + ", hiddenSize = " + hiddenSize
                + ", outputSize = " + outputSize + ", learnRate = " + learnRate
                + ", momentumRate = " + momentumRate);

        if (mBp == null) {
            mBp = new Bpnn(mNodes, hiddenSize, outputSize, learnRate, momentumRate);
        }
    }

    public static BpnnJudge getInstance() {
        return mInstance;
    }

    public void loadData(String url) {
        File file = new File(url);
        try {
            mBp.loadFromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void trainBp() {
        for (int i = 0; i < TRAIN_TIMES; i++) {
            trainBySampleFile(FILE_DIR + SAMPLE_SMS_FILE_NAME);
        }

        Date d = new Date();
        String fileName = "" + (1900 + d.getYear()) + "-" + (1 + d.getMonth()) + "-"
                + d.getDate() + "-" + d.getHours() + d.getMinutes()
                + d.getSeconds() + ".dat";
        try {
            mBp.saveToFile(FILE_DIR + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isGarbage(String sms) {
        if (sms.length() == 0) {
            return false;
        }
        List<Word> words = segment(sms);

        if (words.size() == 0) {
            return false;
        }
        double[] in = new double[mNodes];

        calculateInput(words, in);
        double[] target = mBp.test(in);
        System.out.println(String.format("target[0] is %f, target[1] is %f, %s, ##### %s #####",
                target[0], target[1], sms, target[0] > target[1] ? "GARBAGE" : "NORMAL"));
        return target[0] > target[1];

    }

    private void trainBySampleFile(String url) {
        BufferedReader br = null;
        String temp = null;
        try {
            br = new BufferedReader(new FileReader(url));

            while (true) {
                temp = br.readLine();
                if (temp == null)
                    break;
                if (temp.length() < 2)
                    continue;

                boolean isGarbage = temp.startsWith("1");
                temp = temp.substring(1).trim();
                trainByOneSms(temp, isGarbage);
            }

            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            br = null;
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void trainByOneSms(String sms, boolean isGarbage) {
        System.out.println("train by one sms: " + sms + ", is garbage? " + isGarbage);
        if (sms.length() == 0) {
            return;
        }

        List<Word> words = segment(sms);
        if (words.size() == 0) {
            return;
        }
        double[] in = new double[mNodes];
        calculateInput(words, in);

        double[] target = new double[2];
        if (isGarbage) {
            target[0] = 1.0; // garbage
            target[1] = 0;
        } else {
            target[0] = 0; // not garbage
            target[1] = 1.0;
        }

        mBp.train(in, target);
    }

    private void calculateInput(List<Word> words, double[] in) {
        for (Word w : words) {
            int c = (int) (w.getCode() % mNodes);
            in[c] = 1d;
        }
    }

    public List<Word> segment(String line) {
        Dictionary d = Dictionary.getInstance(FILE_DIR + DICTION_FILE_NAME);
        WordsSegment ws = new WordsSegment(d);
        ws.segment(line);
        List<Word> list = new ArrayList();
        List<DicWord> wl = ws.getResult();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i = 0; i < wl.size(); i++) {
            DicWord dw = (DicWord) wl.get(i);
            Word w = null;
            // if (dw.getType() == TYPE.CHINESE)
            {
                String word = dw.getContent();
                md.update(word.getBytes());
                byte[] mdbytes = md.digest();
                // System.out.println(mdbytes);
                BigInteger bigInt = new BigInteger(1, mdbytes);
                Long v = Math.abs(bigInt.longValue());
                w = new Word(v, dw.getType());
            }

            list.add(w);
        }
        return list;
    }
}
