using Net.Qiujuer.Blink.Listener;
using System;

namespace Client
{
    class SendCallBack : SendListener
    {

        public void OnSendProgress(float progress)
        {
            Console.WriteLine("Send->progress: " + progress);
        }
    }
}
