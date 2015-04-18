using Net.Qiujuer.Blink.Async;
using Net.Qiujuer.Blink.Box;
using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Kit;
using Net.Qiujuer.Blink.Listener;
using System;
using System.IO;
using System.Net.Sockets;

namespace Net.Qiujuer.Blink
{
    public class Blink
    {

        // Default on-disk resource directory.
        private static readonly String DEFAULT_RESOURCE_DIR = "Blink";
        // Default buffer size
        private static readonly int DEFAULT_SOCKET_BUFFER_SIZE = 4 * 1024 * 1024;
        // Default progress precision
        private static readonly float DEFAULT_PROGRESS_PRECISION = 0.001f;


        /// <summary>
        /// Create a Bink connection by socket
        /// </summary>
        /// <param name="socket">Socket</param>
        /// <param name="socketBufferSize">Socket BufferSize</param>
        /// <param name="resourcePath">File Resource Path</param>
        /// <param name="fileMark">File name mark to clear and create</param>
        /// <param name="progressPrecision">Send and Receive notify progress precision</param>
        /// <param name="receiveListener">ReceiveListener</param>
        /// <param name="blinkListener">BlinkListener</param>
        /// <returns>BlinkConn</returns>
        public static BlinkConn NewConnection(
            Socket socket,
            int socketBufferSize,
            String resourcePath,
            String fileMark,
            float progressPrecision,
            ReceiveListener receiveListener,
            BlinkListener blinkListener)
        {
            String path = Path.Combine(resourcePath, DEFAULT_RESOURCE_DIR);
            DiskResource resource = new DiskResource(path, fileMark);
            BlinkParserImpl parser = new BlinkParserImpl(resource);
            AsyncSocketAdapter socketAdapter = new AsyncSocketAdapter(socket, socketBufferSize);
            DelegateDelivery delivery = new DelegateDelivery(blinkListener, receiveListener);
            return new BlinkConn(socketAdapter, socketAdapter, delivery, delivery, delivery, resource, parser, progressPrecision);
        }

        /// <summary>
        /// Create a Bink connection by socket
        /// </summary>
        /// <param name="socket">Socket</param>
        /// <param name="resourcePath">File Resource Path</param>
        /// <param name="fileMark">File name mark to clear and create</param>
        /// <param name="progressPrecision">Send and Receive notify progress precision</param>
        /// <param name="receiveListener">ReceiveListener</param>
        /// <param name="blinkListener">BlinkListener</param>
        /// <returns>BlinkConn</returns>
        public static BlinkConn NewConnection(
            Socket socket,
            String resourcePath,
            String fileMark,
            float progressPrecision,
            ReceiveListener receiveListener,
            BlinkListener blinkListener)
        {
            return NewConnection(socket, DEFAULT_SOCKET_BUFFER_SIZE, resourcePath, fileMark, progressPrecision, receiveListener, blinkListener);
        }

        /// <summary>
        /// Create a Bink connection by socket
        /// </summary>
        /// <param name="socket">Socket</param>
        /// <param name="resourcePath">File Resource Path</param>
        /// <param name="progressPrecision">Send and Receive notify progress precision</param>
        /// <param name="receiveListener">ReceiveListener</param>
        /// <param name="blinkListener">BlinkListener</param>
        /// <returns>BlinkConn</returns>
        public static BlinkConn NewConnection(
            Socket socket,
            int socketBufferSize,
            String resourcePath,
            float progressPrecision,
            ReceiveListener receiveListener,
            BlinkListener blinkListener)
        {
            return NewConnection(socket, socketBufferSize, resourcePath, Guid.NewGuid().ToString(), progressPrecision, receiveListener, blinkListener);
        }

        /// <summary>
        /// Create a Bink connection by socket
        /// </summary>
        /// <param name="socket">Socket</param>
        /// <param name="resourcePath">File Resource Path</param>
        /// <param name="receiveListener">ReceiveListener</param>
        /// <param name="blinkListener">BlinkListener</param>
        /// <returns>BlinkConn</returns>
        public static BlinkConn NewConnection(Socket socket, String resourcePath, ReceiveListener receiveListener, BlinkListener blinkListener)
        {
            return NewConnection(socket, DEFAULT_SOCKET_BUFFER_SIZE, resourcePath, Guid.NewGuid().ToString(), DEFAULT_PROGRESS_PRECISION, receiveListener, blinkListener);
        }


        /// <summary>
        /// Create a Bink connection by socket
        /// </summary>
        /// <param name="socket">Socket</param>
        /// <param name="receiveListener">ReceiveListener</param>
        /// <param name="blinkListener">BlinkListener</param>
        /// <returns>BlinkConn</returns>
        public static BlinkConn NewConnection(Socket socket, ReceiveListener receiveListener, BlinkListener blinkListener)
        {
            return NewConnection(socket, DEFAULT_SOCKET_BUFFER_SIZE, GetDefaultResourcePath(), Guid.NewGuid().ToString(), DEFAULT_PROGRESS_PRECISION, receiveListener, blinkListener);
        }


        /// <summary>
        /// Get Default path with CanonicalPath
        /// </summary>
        /// <returns>Path</returns>
        private static String GetDefaultResourcePath()
        {
            return System.IO.Directory.GetCurrentDirectory();
        }
    }
}
