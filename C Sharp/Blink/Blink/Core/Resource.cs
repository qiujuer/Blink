using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Core
{
    /**
     * Blink files Resource
     */
    public interface Resource
    {
        /**
         * Create a file from resource.
         *
         * @return New file
         */
        String Create(long id);

        /**
         * Empties the resource by oneself
         */
        void Clear();

        /**
         * Empties the resource by the path
         */
        void ClearAll();

        /**
         * Get the Mark
         *
         * @return T
         */
        string GetMark();
    }
}
