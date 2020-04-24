import java.rmi.*;

public interface Handler extends Remote
{
    void updateUI(Player player, int stickId, int result) throws RemoteException;
}
