using Net.Qiujuer.Blink.Core;
using Net.Qiujuer.Blink.Listener;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Box
{
    public class StringSendPacket : ByteSendPacket
    {
        public StringSendPacket(String entity)
            : this(entity, null)
        {

        }

        public StringSendPacket(String entity, SendListener listener)
            : base(System.Text.Encoding.UTF8.GetBytes(entity), listener)
        {
            mType = Type.STRING;
        }
    }
}
