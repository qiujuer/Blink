using Net.Qiujuer.Blink.Core;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Box
{
    public abstract class BaseReceivePacket<T> : ReceivePacket
    {
        protected T mEntity;

        public BaseReceivePacket(long id, int type, long len)
            : base(id, type, len)
        {
        }

        public T GetEntity()
        {
            return mEntity;
        }
    }
}
