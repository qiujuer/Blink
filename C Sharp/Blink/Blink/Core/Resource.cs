using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink.Core
{
    /// <summary>
    /// Blink Files Resource
    /// </summary>
    public interface Resource
    {
        /// <summary>
        /// Create a file from resource.
        /// </summary>
        /// <param name="id">Packet Id</param>
        /// <returns>File Path</returns>
        string Create(long id);

        /// <summary>
        /// Empties the resource by oneself
        /// </summary>
        void Clear();

        /// <summary>
        /// Empties the resource by the path
        /// </summary>
        void ClearAll();

        /// <summary>
        /// Get The Mark
        /// </summary>
        /// <returns>Receive Mark</returns>
        string GetMark();
    }
}
