using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Core
{
    public abstract class BaseReceivePacket<T> : ReceivePacket where T : class
    {
        protected T mEntity;

        public BaseReceivePacket(long id, int type, int len)
            : base(id, type, len)
        {
        }

        public override object GetEntity()
        {
            return mEntity;
        }
    }
}
