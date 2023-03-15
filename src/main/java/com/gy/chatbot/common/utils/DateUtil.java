package com.gy.chatbot.common.utils;

import java.util.Date;

public class DateUtil {

    public static long currentTimeMillis() {
        return new Date().getTime();
    }

    public static long currentTimeSeconds() {
        return new Date().getTime()/1000;
    }

}
