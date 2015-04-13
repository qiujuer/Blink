using Net.Qiujuer.Blink.Core;
using System;
using System.Net.Sockets;

namespace Net.Qiujuer.Blink.Async
{
    public class AsyncSocketAdapter : ISender, IReceiver
    {
        private int mBufferSize;
        private Socket mSocket;

        public AsyncSocketAdapter(Socket socket, int bufferSize)
        {
            mSocket = socket;
            mSocket.ReceiveBufferSize = bufferSize;
            mSocket.SendBufferSize = bufferSize;
            mBufferSize = bufferSize;
        }

        public bool ReceiveAsync(SocketAsyncEventArgs e)
        {
            Socket socket = mSocket;
            if (socket != null)
                return mSocket.ReceiveAsync(e);
            else
                return false;
        }

        public bool SendAsync(SocketAsyncEventArgs e)
        {
            Socket socket = mSocket;
            if (socket != null)
                return mSocket.SendAsync(e);
            else
                return false;
        }

        public int GetBufferSize()
        {
            return mBufferSize;
        }

        public void Destroy()
        {
            Socket socket = mSocket;
            mSocket = null;

            if (socket != null)
            {
                try
                {
                    socket.Shutdown(SocketShutdown.Both);
                    socket.Dispose();
                    socket.Close();
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.ToString());
                }
            }
        }
    }
}
