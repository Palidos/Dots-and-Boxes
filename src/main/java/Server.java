import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


public class Server extends UnicastRemoteObject
        implements PlayerHandler {
    private static List<Handler> clients;

    private List<Player> players;

    public Server() throws RemoteException {
        super();
        players = new ArrayList<>();
        clients = new ArrayList<>();
    }

    public void addNewPlayer(Player player) throws RemoteException {
        players.add(player);
            try {
                Registry registry = LocateRegistry.getRegistry(null, 1099);
                Handler h;
                h = (Handler) registry.lookup("Client");
               clients.add(h);
               System.out.println("New player joined!");
            } catch (Exception e) {
            }

    }

    public void playerTurn(Player player, int stickId, int result)
            throws RemoteException {
        for (Player ply : players) {
            if (ply.equals(player)) {
                clients.get(0).updateUI(player, stickId, result);
                break;
            }
        }
    }

    public void setPlayerResult(Player player, int result)
            throws RemoteException {
        for (Player ply : players) {
            if (ply.equals(player)) {
                break;
            }
        }
    }

    @Override
    public int getPlayerResult(Player player)
            throws RemoteException {
        for (Player ply : players) {
            if (ply.equals(player)) {
                break;
            }
        }
        return 0;
    }

    public static void main(String[] args) throws Exception {
        String localhost = "127.0.0.1";
        String RMI_HOSTNAME = "java.rmi.server.hostname";
        try {
            System.setProperty(RMI_HOSTNAME, localhost);
            PlayerHandler service = new Server();

            String serviceName = "Server";

            System.out.println("Initializing " + serviceName);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(serviceName, service);

            System.out.println("Start " + serviceName);
        } catch (RemoteException e) {
            System.err.println("RemoteException : " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Exception : " + e.getMessage());
            System.exit(2);
        }
    }
}