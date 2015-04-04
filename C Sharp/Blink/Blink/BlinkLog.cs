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
    public static class BlinkLog
    {
        public static bool DEBUG = true;

        public static void setDebug(bool debug)
        {
            DEBUG = debug;
        }

        public static void I(String str)
        {
            if (DEBUG)
            {
                Console.ResetColor();
                Console.WriteLine("INFO:" + str);
            }
        }

        public static void V(String str)
        {
            if (DEBUG)
            {
                Console.ForegroundColor = ConsoleColor.Blue;
                Console.WriteLine("VERBOSE:" + str);
            }
        }

        public static void W(String str)
        {
            if (DEBUG)
            {
                Console.ForegroundColor = ConsoleColor.Yellow;
                Console.WriteLine("WARN:" + str);
            }
        }

        public static void E(String str)
        {
            if (DEBUG)
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine("ERROR:" + str);
            }
        }
    }
}
