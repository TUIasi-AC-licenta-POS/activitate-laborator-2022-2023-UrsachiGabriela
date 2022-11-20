import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class RMIClient {
    public static final String rmiHost = "localhost";
    public static final int rmiPort = 1099;
    public static final String servantObjectRegistryName = "RMIHelloWorld";

    public static void main(String[] args) {
        try{
            Registry remoteRegistry = LocateRegistry.getRegistry(RMIClient.rmiHost,RMIClient.rmiPort);

            ISayHello rmiClientStub = (ISayHello) remoteRegistry.lookup(RMIClient.servantObjectRegistryName);

            System.out.println("Input values,  end with \"quit\" ");

            String consoleInput;
            Scanner consoleScanner = new Scanner(System.in);
            consoleInput = consoleScanner.nextLine();

            while( 0 != consoleInput.compareToIgnoreCase("quit")){
                String result = rmiClientStub.sayHello(consoleInput);
                System.out.println("Received from server: "+ result);
                consoleInput=consoleScanner.nextLine();

            }
        } catch (Exception e) {
            System.err.println("Should not be generic exception unless last resort!");
            e.printStackTrace();
        }
    }
}