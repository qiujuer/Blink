using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink
{
    /**
 * Logging helper class.
 */
    public class BlinkLog
    {
        public static String TAG = "Blink";
        public static bool DEBUG = true;

        public static void setTag(String tag)
        {
            TAG = tag;
        }

        public static void setDebug(bool debug)
        {
            DEBUG = debug;
        }

        public static void v(String format, Object[] args)
        {
            if (DEBUG)
            {
                Console.WriteLine(TAG + buildMessage(format, args));
            }
        }

        public static void d(String format, Object[] args)
        {
            if (DEBUG)
            {
                Console.WriteLine(TAG + buildMessage(format, args));
            }
        }

        public static void e(String format, Object[] args)
        {
            if (DEBUG)
            {
                Console.WriteLine(TAG + buildMessage(format, args));
            }
        }

        public static void e(Thread tr, String format, Object[] args)
        {
            if (DEBUG)
            {
                Console.WriteLine(TAG + tr.ToString()
                        + buildMessage(format, args));
            }
        }

        public static void wtf(String format, Object[] args)
        {
            if (DEBUG)
            {
                Console.WriteLine(TAG + buildMessage(format, args));
            }
        }

        public static void wtf(Thread tr, String format, Object[] args)
        {
            if (DEBUG)
            {
                Console.WriteLine(TAG + tr.ToString()
                        + buildMessage(format, args));
            }
        }

        /**
         * Formats the caller's provided message and prepends useful info like
         * calling thread ID and method name.
         */
        private static String buildMessage(String format, Object[] args)
        {
            String msg = (args == null) ? format : String.Format(format,
                    args);
            return msg;
        }
    }
}
