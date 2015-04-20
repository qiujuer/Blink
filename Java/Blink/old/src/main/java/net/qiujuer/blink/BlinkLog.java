/*
 * Copyright (C) 2014 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 03/31/2015
 * Changed 04/02/2015
 * Version 1.0.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.qiujuer.blink;

import java.util.Locale;

/**
 * Logging helper class.
 */
public class BlinkLog {
    public static String TAG = "Blink";
    public static boolean DEBUG = true;

    public static void setTag(String tag) {
        TAG = tag;
    }

    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }

    public static void v(String format, Object... args) {
        if (DEBUG) {
            System.out.println(TAG + buildMessage(format, args));
        }
    }

    public static void d(String format, Object... args) {
        if (DEBUG) {
            System.out.println(TAG + buildMessage(format, args));
        }
    }

    public static void e(String format, Object... args) {
        if (DEBUG) {
            System.out.println(TAG + buildMessage(format, args));
        }
    }

    public static void e(Throwable tr, String format, Object... args) {
        if (DEBUG) {
            System.out.println(TAG + tr.getMessage()
                    + buildMessage(format, args));
        }
    }

    public static void wtf(String format, Object... args) {
        if (DEBUG) {
            System.out.println(TAG + buildMessage(format, args));
        }
    }

    public static void wtf(Throwable tr, String format, Object... args) {
        if (DEBUG) {
            System.out.println(TAG + tr.getMessage()
                    + buildMessage(format, args));
        }
    }

    /**
     * Formats the caller's provided message and prepends useful info like
     * calling thread ID and method name.
     */
    private static String buildMessage(String format, Object... args) {
        String msg = (args == null) ? format : String.format(Locale.US, format,
                args);
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();

        String caller = "<unknown>";
        // Walk up the stack looking for the first caller outside of VolleyLog.
        // It will be at least two frames up, so start there.
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(BlinkLog.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass
                        .lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass
                        .lastIndexOf('$') + 1);

                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread()
                .getId(), caller, msg);
    }
}
