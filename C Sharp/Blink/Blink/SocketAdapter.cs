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
    class SocketAdapter : ISender, IReceiver
    {
        private readonly int mBufferSize;
        private byte[] mInBuffer;
        private byte[] mOutBuffer;
        /**
         * Receive Parser to create entity
         */
        private readonly IBlinkParser mParser;
        private Socket mSocket;


        public SocketAdapter(Socket socket, int bufferSize, IBlinkParser parser)
        {
            mSocket = socket;

            mBufferSize = bufferSize;

            mInBuffer = new byte[bufferSize];
            mOutBuffer = new byte[bufferSize];

            mParser = parser;
        }

        public bool SendHead(SendPacket entity)
        {
            IList<ArraySegment<byte>> head = entity.GetHeadInfo();
            if (head == null)
                return false;
            else
            {
                try
                {
                    // Send
                    mSocket.Send(head, SocketFlags.None);
                    return true;
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.Message);
                    return false;
                }
            }
        }

        public bool SendEntity(SendPacket entity, ISendDelivery delivery)
        {
            long cursor = 0;
            long total = entity.GetLength();
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
                    delivery.PostSendProgress(entity, (float)cursor / total);
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

        public ReceivePacket ReceiveHead()
        {
            byte[] bytes = new byte[8];
            if (mSocket.Receive(bytes, 1, SocketFlags.None) == 1)
            {
                int type = bytes[0];
                if (type != -1)
                {
                    mSocket.Receive(bytes);
                    long len = BitConverter.ToInt64(bytes, 0);
                    ReceivePacket entity = mParser.ParseReceive(type, len);
                    if (entity == null)
                        receiveRedundancy();
                    return entity;
                }
            }
            return null;
        }

        public bool ReceiveEntity(ReceivePacket entity, IReceiveDelivery delivery)
        {
            Stream stream = entity.GetOutputStream();
            HashAlgorithm hashAlgorithm = null;
            long surplusLen = entity.GetLength();
            long cursor = 0;
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
                        readLen = mSocket.Receive(mInBuffer, 0, (int)surplusLen, SocketFlags.None);


                    // Write
                    stream.Write(mInBuffer, 0, readLen);
                    stream.Flush();

                    // Hash
                    hashAlgorithm.TransformBlock(mInBuffer, 0, readLen, null, 0);

                    // Compute surplusLen
                    surplusLen -= readLen;
                    cursor += readLen;

                    // Post progress
                    delivery.PostReceiveProgress(entity, (float)cursor / entity.GetLength());
                }
                return true;
            }
            catch (Exception)
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
                    entity.SetHash(md5String);
                }
            }
            return false;
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

        public void Dispose()
        {
            if (mSocket != null)
            {
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
            }
            mOutBuffer = null;
        }
    }
}
