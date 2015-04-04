using Net.Qiujuer.Blink.Core;
using System;
using System.IO;

namespace Net.Qiujuer.Blink
{
    /**
     * Disk resource implements Resource{@link Resource}
     */
    public class DiskResource : IResource
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

            // Create
            if (Directory.Exists(mRootDirectory) == false)
            {
                Directory.CreateDirectory(mRootDirectory);
            }

            Clear();
        }

        public String Create(long id)
        {
            String path = Path.Combine(mRootDirectory, String.Format("{0}_{1}", mMark, id));
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
            try
            {
                DirectoryInfo theFolder = new DirectoryInfo(mRootDirectory);
                FileInfo[] fileInfo = theFolder.GetFiles();
                //遍历文件夹
                foreach (FileInfo f in fileInfo)
                {
                    if (f.Name.Contains(mMark))
                        f.Delete();
                }
            }
            catch (Exception) { }

            BlinkLog.V("Resource cleared with mark: " + mMark);
        }

        public void ClearAll()
        {
            try
            {
                Directory.Delete(mRootDirectory, true);
            }
            catch (Exception) { }
            BlinkLog.V("Resource cleared path.");
        }

        public String GetMark()
        {
            return mMark;
        }
    }
}
