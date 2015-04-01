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
