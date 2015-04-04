using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Tool
{
    /// <summary>
    /// Auto Lock Queue By Thread
    /// </summary>
    /// <typeparam name="T">Any</typeparam>
    public class AutoQueue<T> : Queue<T>
    {
        public new void Enqueue(T item)
        {
            lock (this)
            {
                base.Enqueue(item);
                if (Count == 1)
                {
                    // Wake up any blocked dequeue
                    Monitor.PulseAll(this);
                }
            }

        }

        public new T Dequeue()
        {
            lock (this)
            {
                while (Count == 0)
                {
                    Monitor.Wait(this);
                }
                T item = base.Dequeue();
                return item;
            }
        }

        public new void Clear()
        {
            lock (this)
            {
                base.Clear();
            }
        }
    }
}
