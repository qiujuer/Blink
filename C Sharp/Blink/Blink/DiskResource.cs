using Net.Qiujuer.Blink.Core;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Net.Qiujuer.Blink
{
    /**
     * Disk resource implements Resource{@link Resource}
     */
    public class DiskResource : Resource
    {
        /**
         * A unique identifier on the Resource clear
         */
        private readonly String mMark;
        /**
         * The root directory to use for the resource.
         */
        private readonly String mRootDirectory;

        public DiskResource(String rootDirectory, String mark)
        {
            if (mark == null || mark.Trim().Length == 0)
                throw new EntryPointNotFoundException("Mark is not allow null.");

            mRootDirectory = rootDirectory;
            mMark = mark;

            if (Directory.Exists(mRootDirectory) == false)//如果不存在就创建file文件夹
            {
                Directory.CreateDirectory(mRootDirectory);
            }

            Clear();
        }

        public String Create(long id)
        {
            //UUID.randomUUID();
            String path = String.Format("%1$s_%2$d", mRootDirectory, mMark, id);
            if (!File.Exists(path))
                try
                {
                    File.Create(path).Close();

                }
                catch (Exception e)
                {
                    Console.WriteLine(e.Message);
                }
            if (!File.Exists(path))
                return null;
            else
                return path;
        }

        public void Clear()
        {
            DirectoryInfo theFolder = new DirectoryInfo(mRootDirectory);
            FileInfo[] fileInfo = theFolder.GetFiles();
            //遍历文件夹
            foreach (FileInfo f in fileInfo)
            {
                if (f.Name.Contains(mMark))
                    f.Delete();
            }

            //BlinkLog.d("Resource cleared with mark: " + mMark);
        }

        public void ClearAll()
        {
            Directory.Delete(mRootDirectory, true);
            //BlinkLog.d("Resource cleared path.",null);
        }

        public String GetMark()
        {
            return mMark;
        }
    }
}
