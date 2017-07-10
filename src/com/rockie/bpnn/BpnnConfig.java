package com.rockie.bpnn;

import java.io.*;
import java.util.Properties;

public class BpnnConfig {

    private static BpnnConfig mInstance;

    private Properties mProperties;

    public static BpnnConfig getInstance(String url) {
        if (mInstance == null) {
            synchronized (BpnnConfig.class) {
                if (mInstance == null) {
                    mInstance = new BpnnConfig(url);
                }
            }
        }
        return mInstance;
    }

    private BpnnConfig(String url) {
        mProperties = new Properties();
        readConfigFile(url);
    }

    private void readConfigFile(String url) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(url));
            mProperties.load(reader);
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getIntValue(BpnnConfigKeys key) {
        String ret = mProperties.getProperty(key.getKey());
        int retInt = key.getDefaultInt();
        if (ret != null) {
            retInt = Integer.parseInt(ret);
        }

        return retInt;
    }

    public double getDoubleValue(BpnnConfigKeys key) {
        String ret = mProperties.getProperty(key.getKey());
        Double retDouble = key.getDefaultDouble();
        if (ret != null) {
            retDouble = Double.parseDouble(ret);
        }

        return retDouble;
    }
}
