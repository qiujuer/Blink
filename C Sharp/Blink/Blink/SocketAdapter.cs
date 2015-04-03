using Net.Qiujuer.Blink.Core;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink
{
    class SocketAdapter : Sender, Receiver
    {
        private readonly int mBufferSize;
        private byte[] mInBuffer;
        private byte[] mOutBuffer;
        /**
         * Receive Parser to create entity
         */
        private readonly ReceiveParser mParser;
        private Socket mSocket;


        public SocketAdapter(Socket socket, int bufferSize, ReceiveParser parser)
        {
            mSocket = socket;

            mBufferSize = bufferSize;

            mInBuffer = new byte[bufferSize];
            mOutBuffer = new byte[bufferSize];

            mParser = parser;
        }

        public bool SendHead(SendPacket entity)
        {
            int length = entity.GetLength();
            if (length <= 0)
                return false;
            try
            {
                byte[] lenBytes = BitConverter.GetBytes((length));
                // Send Type
                mSocket.Send(new byte[] { (byte)entity.GetType() }, SocketFlags.None);
                // Send Length
                mSocket.Send(lenBytes, 0, 4, SocketFlags.None);
                return true;
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                return false;
            }
        }

        public bool SendEntity(SendPacket entity, SendDelivery delivery)
        {
            int cursor = 0;
            int total = entity.GetLength();
            Stream stream = entity.GetInputStream();
            int count;
            try
            {
                while ((count = stream.Read(mOutBuffer, 0, mBufferSize)) != -1)
                {
                    // Write
                    mSocket.Send(mOutBuffer, 0, count, SocketFlags.None);
                    cursor += count;

                    // Post progress
                    delivery.PostSendProgress(entity, total, cursor);
                }
                return true;
            }
            catch (IOException e)
            {
                Console.WriteLine(e.Message);
            }
            finally
            {
                CloseStream(stream);
            }
            return false;
        }

        public void DestroySendIO()
        {
            if (mSocket != null)
                try
                {
                    mSocket.Dispose();
                    mSocket.Close();
                }
                catch (IOException e)
                {

                    Console.WriteLine(e.Message);
                }
                finally
                {
                    mSocket = null;
                }
            mOutBuffer = null;
        }

        public ReceivePacket ReceiveHead()
        {
            try
            {
                byte[] bytes = new byte[4];
                if (mSocket.Receive(bytes, 1, SocketFlags.None) == 1)
                {
                    int type = bytes[0];
                    if (type != -1)
                    {
                        byte[] lenByte = new byte[4];
                        mSocket.Receive(lenByte);
                        int len = BitConverter.ToInt32(lenByte, 0);
                        ReceivePacket entity = mParser.ParseReceive(type, len);
                        if (entity == null)
                            receiveRedundancy();
                        return entity;
                    }
                }
            }
            catch (IOException)
            {
                return null;
            }
            return null;
        }

        public bool ReceiveEntity(ReceivePacket entity, ReceiveDelivery delivery)
        {
            Stream stream = entity.GetOutputStream();
            HashAlgorithm hashAlgorithm = null;
            int surplusLen = entity.GetLength();
            int cursor = 0;
            try
            {
                hashAlgorithm = new MD5CryptoServiceProvider();
                int readLen;
                while (surplusLen > 0)
                {
                    // Read
                    if (surplusLen > mBufferSize)

                        readLen = mSocket.Receive(mInBuffer);
                    else
                        readLen = mSocket.Receive(mInBuffer, 0, surplusLen, SocketFlags.None);


                    // Write
                    stream.Write(mInBuffer, 0, readLen);
                    stream.Flush();

                    // Hash
                    hashAlgorithm.TransformBlock(mInBuffer, 0, readLen, null, 0);

                    // Compute surplusLen
                    surplusLen -= readLen;
                    cursor += readLen;

                    // Post progress
                    delivery.PostReceiveProgress(entity, entity.GetLength(), cursor);
                }
                return true;
            }
            catch (Exception e)
            {
                receiveRedundancy();
            }
            finally
            {
                // Hash
                hashAlgorithm.TransformFinalBlock(mInBuffer, 0, 0);
                if (hashAlgorithm != null)
                {
                    string md5String = BitConverter.ToString(hashAlgorithm.Hash).Replace("-", "");
                    entity.SetHashCode(md5String);
                }
            }
            return false;
        }

        public void DestroyReceiveIO()
        {
            // do...
        }


        static void CloseStream(Stream stream)
        {
            try
            {
                stream.Close();
            }
            catch (IOException e)
            {
                Console.WriteLine(e.Message);
            }
        }

        private void receiveRedundancy()
        {
            try
            {
                while (true)
                {
                    if (mSocket.Receive(mInBuffer) <= 0)
                        return;
                }
            }
            catch (IOException e)
            {
                Console.WriteLine(e.Message);
            }
        }
    }
}
