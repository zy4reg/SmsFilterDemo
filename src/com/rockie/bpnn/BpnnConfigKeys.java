package com.rockie.bpnn;

public enum BpnnConfigKeys {

    INPUT_SIZE("input_size", 700),
    HIDDEN_SIZE("hidden_size", 525),
    OUTPUT_SIZE("output_size", 2),
    LEARN_RATE("learn_rate", 0.25),
    MOMENTUM_RATE("momentum_rate", 0.2),

    /**
     * specify the last member, useless
     */
    CONFIG_KEYS_END("config_keys_end");

    private String mKey;
    private int mDefaultInt;
    private double mDefaultDouble;

    private BpnnConfigKeys(String key) {
        mKey = key;
    }

    private BpnnConfigKeys(String key, int defaultInt) {
        mKey = key;
        mDefaultInt = defaultInt;
    }

    private BpnnConfigKeys(String key, double defaultDouble) {
        mKey = key;
        mDefaultDouble = defaultDouble;
    }

    public String getKey() {
        return mKey;
    }

    public int getDefaultInt() {
        return mDefaultInt;
    }

    public double getDefaultDouble() {
        return mDefaultDouble;
    }
}
