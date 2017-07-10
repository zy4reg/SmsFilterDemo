package com.rockie;

public class Utils {

    public static int getCharType(char c) {
        int i = (int)c;
        if (i >= 19968 && i <= 40869)
            return Constant.TEXT_TYPE_CHINESE;
        if ((i >= 48 && i <= 57) || (i >= 65296 && i <= 65305 ))
            return Constant.TEXT_TYPE_NUMBER;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122) ||
                (i >= 65313 && i <= 65338) || (i >= 65345 && i <= 65370))
            return Constant.TEXT_TYPE_CHAR;
        return Constant.TEXT_TYPE_OTHER;
    }
}
