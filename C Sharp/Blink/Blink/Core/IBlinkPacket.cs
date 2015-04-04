
namespace Net.Qiujuer.Blink.Core
{
    /// <summary>
    /// Bink Packet interface
    /// </summary>
    public interface IBlinkPacket
    {
        int GetType();

        int GetLength();

        void SetSuccess(bool isSuccess);

        bool IsSucceed();
    }
}
