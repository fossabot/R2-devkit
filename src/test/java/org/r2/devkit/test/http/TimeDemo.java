package org.r2.devkit.test.http;

import org.r2.devkit.core.SystemAPI;
import org.r2.devkit.time.DateTimeAPI;

import java.io.PrintStream;

public class TimeDemo {

    public static void main(String[] args) {
        PrintStream out = System.out;
        out.println(DateTimeAPI.date());
        out.println(DateTimeAPI.time());
        out.println(DateTimeAPI.dateTime());
        out.println(DateTimeAPI.FAIL_ZONE_ID);
        out.println(DateTimeAPI.dateTimeWithMillis(SystemAPI.currentTimestamp(), 0));// UTC +0 标准时区
    }

}
