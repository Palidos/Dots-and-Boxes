import java.rmi.*;

public interface PlayerHandler extends Remote
{
    void addNewPlayer(Player player) throws RemoteException;


    void playerTurn(Player player, int stickId, int result)
            throws RemoteException;

    void setPlayerResult(Player player, int result)
            throws RemoteException;

    int getPlayerResult(Player player)
            throws RemoteException;
}