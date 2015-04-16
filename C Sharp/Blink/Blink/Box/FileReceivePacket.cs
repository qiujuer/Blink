using System;
using System.IO;
using System.Security.Cryptography;
using System.Text;

namespace Net.Qiujuer.Blink.Box
{
    public class FileReceivePacket : BaseReceivePacket<FileInfo>
    {
        private string mFileName;
        private HashAlgorithm mHashAlgorithm = null;

        public FileReceivePacket(long id, byte type, long len, String path)
            : base(id, type, len)
        {
            mEntity = new FileInfo(path);
        }

        internal override bool StartPacket()
        {
            try
            {
                mStream = mEntity.OpenWrite();
                mHashAlgorithm = new MD5CryptoServiceProvider();
                return true;
            }
            catch (Exception)
            {
                return false;
            }
        }

        internal override void EndPacket()
        {
            CloseStream();

            // Set Name
            string fileName = mFileName;
            mFileName = null;
            if (fileName != null)
            {
                try
                {
                    mEntity.MoveTo(Path.Combine(mEntity.DirectoryName, fileName));
                }
                catch (Exception) { }
            }

            // Hash      
            HashAlgorithm hash = mHashAlgorithm;
            mHashAlgorithm = null;
            if (hash != null)
            {
                hash.TransformFinalBlock(new byte[0], 0, 0);
                SetHash(BitConverter.ToString(hash.Hash).Replace("-", ""));
                hash.Clear();
                hash.Dispose();
            }
        }

        public override void Write(byte[] buffer, int offset, int count)
        {
            base.Write(buffer, offset, count);

            // Hash
            HashAlgorithm hash = mHashAlgorithm;
            if (hash != null)
                hash.TransformBlock(buffer, offset, count, null, 0);

        }

        public override void WriteInfo(byte[] buffer, int offset, int count)
        {
            try
            {
                mFileName = Encoding.UTF8.GetString(buffer, offset, count);
            }
            catch (Exception) { }
        }
    }
}
