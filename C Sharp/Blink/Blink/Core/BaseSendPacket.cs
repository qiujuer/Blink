using Net.Qiujuer.Blink.Listener;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Core
{
    public abstract class BaseSendPacket<T> : SendPacket
    {
        protected T mEntity;

        public BaseSendPacket(T entity, int type, SendListener listener)
            : base(type, listener)
        {
            mEntity = entity;
        }

        public override object GetEntity()
        {
            return mEntity;
        }
    }
}
