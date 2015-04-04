using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Tool
{
    public interface Executor
    {
        void Execute(Runnable command);
    }
}
