using Net.Qiujuer.Blink.Listener;
using System;
using System.Text;

namespace Net.Qiujuer.Blink.Box
{
    public class StringSendPacket : ByteSendPacket
    {
        public StringSendPacket(String entity)
            : this(entity, null)
        {

        }

        public StringSendPacket(String entity, SendListener listener)
            : base(Encoding.UTF8.GetBytes(entity), listener)
        {
            mType = PacketType.STRING;
        }
    }
}
