
namespace Net.Qiujuer.Blink.Core
{
    /// <summary>
    /// Blink Data Packet
    /// </summary>
    public abstract class BlinkPacket : EntityNode, HeadNode
    {
        protected int mType;
        protected int mLength;
        private bool mSucceed;

        public BlinkPacket(int type)
        {
            mType = type;
        }

        public new int GetType()
        {
            return mType;
        }

        public int GetLength()
        {
            return mLength;
        }

        public void SetSuccess(bool isSuccess)
        {
            this.mSucceed = isSuccess;
        }

        public bool IsSucceed()
        {
            return mSucceed;
        }


        public abstract object GetEntity();

        public static class Type
        {
            public const int STRING = 0;
            public const int BYTES = 1;
            public const int FILE = 2;
        }
    }
}
