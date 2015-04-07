using Net.Qiujuer.Blink.Tool;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

/**
 * Provides a thread for performing receive dispatch from a socket of BinkConn
 * {@link BlinkConn}.
 */
namespace Net.Qiujuer.Blink.Core
{
    public class ReceiveDispatcher : Runnable
    {
        /**
         * The sender interface for processing sender requests.
         */
        private readonly IReceiver mReceiver;
        /**
         * For posting receive responses.
         */
        private readonly IReceiveDelivery mDelivery;
        /**
         * Used for telling us to die.
         */
        private volatile bool mQuit = false;
        private Thread mWork;
        private BlinkConn mBlinkConn;

        public ReceiveDispatcher(IReceiver receiver, IReceiveDelivery delivery, BlinkConn blinkConn)
        {
            mReceiver = receiver;
            mDelivery = delivery;
            mBlinkConn = blinkConn;
        }

        public void Start()
        {
            mWork = new Thread(Run);
            mWork.Start();
        }


        /**
         * Forces this dispatcher to quit immediately. If any requests are still in
         * the queue, they are not guaranteed to be processed.
         */
        public void Quit()
        {
            mQuit = true;
            mWork.Interrupt();
        }


        public void Run()
        {
            int err = 0;
            while (!mQuit)
            {
                ReceivePacket packet;
                try
                {
                    // Receive head
                    packet = mReceiver.ReceiveHead();

                    // Adjust Stream
                    packet.AdjustStream();

                    // Post Start
                    mDelivery.PostReceiveStart(packet);

                    // Receive entity
                    bool status = mReceiver.ReceiveEntity(packet, mDelivery);

                    // Adjust Result value form stream
                    packet.AdjustPacket();

                    // Post End
                    mDelivery.PostReceiveEnd(packet, status);

                }
                catch (Exception e)
                {
                    if (err > 3)
                    {
                        mBlinkConn.Send("dasds");
                    }
                    err++;
                    BlinkLog.E(e.ToString());
                }
                finally
                {
                    sleepSomeTime();
                }
            }
        }

        private void sleepSomeTime()
        {
            try
            {
                Thread.Sleep(1000);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
    }
}
