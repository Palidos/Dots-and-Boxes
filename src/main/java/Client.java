import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


public class Client extends UnicastRemoteObject
        implements Handler {
    private static Player playerRED, playerBLUE;
    private static UIGame uiRED, uiBLUE;
    private List<Player> players;

    @Override
    public void updateUI(Player player, int stickId, int result) throws RemoteException {
            if(player.getName().equals("RED")){
                uiBLUE.update(stickId, result);
            } else if(player.getName().equals("BLUE")){
                uiRED.update(stickId, result);
            }
    }

    public Client() throws RemoteException {
        super();
        players = new ArrayList<>();
    }


    public static void main(String[] args) throws Exception {
        String localhost = "127.0.0.1";
        String RMI_HOSTNAME = "java.rmi.server.hostname";
        try {
            playerRED = new Player("RED");
            playerBLUE = new Player("BLUE");
            System.setProperty(RMI_HOSTNAME, localhost);
            Client service = new Client();

            String serviceName = "Client";

            System.out.println("Initializing " + serviceName);

            Registry registry = LocateRegistry.getRegistry(null, 1099);
            registry.rebind(serviceName, service);

            PlayerHandler ph;
            ph = (PlayerHandler) registry.lookup("Server");
            ph.addNewPlayer(playerRED);
            ph.addNewPlayer(playerBLUE);

            uiRED = new UIGame("RED", 1 ,ph, playerRED);
            uiBLUE = new UIGame("BLUE", 0, ph, playerBLUE);
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