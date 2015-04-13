using Net.Qiujuer.Blink.Async;
using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Listener;
using Net.Qiujuer.Blink.Listener.Delivery;
using System;
using System.IO;
using System.Net.Sockets;

namespace Net.Qiujuer.Blink
{
    public class Blink
    {
        /**
     * Default on-disk resource directory.
     */
        private static readonly String DEFAULT_RESOURCE_DIR = "Blink";
        /**
         * Default buffer size
         */
        private static readonly int DEFAULT_SOCKET_BUFFER_SIZE = 4 * 1024 * 1024;

        /**
         * Create a Bink connection by socket
         *
         * @param socket           Socket
         * @param socketBufferSize Socket BufferSize
         * @param resourcePath     File Resource Path
         * @param fileMark         File name mark to clear
         * @param listener         ReceiveListener
         * @return BlinkConn
         * @throws Exception
         */
        public static BlinkConn NewConnection(Socket socket, int socketBufferSize, String resourcePath, String fileMark, BlinkListener listener)
        {
            String path = Path.Combine(resourcePath, DEFAULT_RESOURCE_DIR);
            DiskResource resource = new DiskResource(path, fileMark);
            BlinkParser parser = new BlinkParser(resource);
            AsyncSocketAdapter socketAdapter = new AsyncSocketAdapter(socket, socketBufferSize);
            DelegateDelivery delivery = new DelegateDelivery(listener);
            return new BlinkConn(socketAdapter, delivery, socketAdapter, delivery, resource, parser);
        }

        /**
         * Create a Bink connection by socket
         *
         * @param socket       Socket
         * @param resourcePath File Resource Path
         * @param fileMark     File name mark to clear
         * @param listener     ReceiveListener
         * @return BlinkConn
         * @throws Exception
         */
        public static BlinkConn NewConnection(Socket socket, String resourcePath, String fileMark, BlinkListener listener)
        {
            return NewConnection(socket, DEFAULT_SOCKET_BUFFER_SIZE, resourcePath, fileMark, listener);
        }

        /**
         * Create a Bink connection by socket
         *
         * @param socket           Socket
         * @param socketBufferSize Socket BufferSize
         * @param resourcePath     File Resource Path
         * @param listener         ReceiveListener
         * @return BlinkConn
         * @throws Exception
         */
        public static BlinkConn NewConnection(Socket socket, int socketBufferSize, String resourcePath, BlinkListener listener)
        {
            return NewConnection(socket, socketBufferSize, resourcePath, Guid.NewGuid().ToString(), listener);
        }

        /**
         * Create a Bink connection by socket
         *
         * @param socket       Socket
         * @param resourcePath File Resource Path
         * @param listener     ReceiveListener
         * @return BlinkConn
         * @throws Exception
         */
        public static BlinkConn NewConnection(Socket socket, String resourcePath, BlinkListener listener)
        {
            return NewConnection(socket, DEFAULT_SOCKET_BUFFER_SIZE, resourcePath, Guid.NewGuid().ToString(), listener);
        }

        /**
         * Create a Bink connection by socket
         *
         * @param socket   Socket
         * @param listener ReceiveListener
         * @return BlinkConn
         * @throws Exception
         */
        public static BlinkConn NewConnection(Socket socket, BlinkListener listener)
        {
            return NewConnection(socket, DEFAULT_SOCKET_BUFFER_SIZE, getDefaultResourcePath(), Guid.NewGuid().ToString(), listener);
        }


        /**
         * Get Default path with CanonicalPath
         *
         * @return Path
         * @throws IOException
         */
        private static String getDefaultResourcePath()
        {
            return System.IO.Directory.GetCurrentDirectory();
        }
    }
}
