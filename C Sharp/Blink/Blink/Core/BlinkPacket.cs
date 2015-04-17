
namespace Net.Qiujuer.Blink.Core
{
    /// <summary>
    /// Bink Packet interface
    /// </summary>
    public interface BlinkPacket
    {
        int GetType();

        long GetLength();

        void SetSuccess(bool isSuccess);

        bool IsSucceed();
    }
}
