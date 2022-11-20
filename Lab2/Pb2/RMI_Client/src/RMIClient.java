import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class RMIClient {

    public static final String rmiHost = "localhost";
    public static final int rmiPort = 1099;
    public static final String servantObjectRegistryName = "RMICalculator";

    public static void main(String[] args) {
        try{
            Registry remoteRegistry = LocateRegistry.getRegistry(RMIClient.rmiHost,RMIClient.rmiPort);

            ICalculator rmiClientStub = (ICalculator) remoteRegistry.lookup(RMIClient.servantObjectRegistryName);

            System.out.println("Choose operation (add,sub),  end with \"quit\" ");

            String consoleInput;
            Scanner consoleScanner = new Scanner(System.in);
            consoleInput = consoleScanner.nextLine();

            while( 0 != consoleInput.compareToIgnoreCase("quit")){
                double x1 = Double.parseDouble(consoleScanner.nextLine());
                double x2 = Double.parseDouble(consoleScanner.nextLine());
                Double result;

                switch(consoleInput){
                    case "add":
                        result = rmiClientStub.add(x1,x2);
                        break;
                    case "sub":
                        result=rmiClientStub.sub(x1,x2);
                        break;
                    case "mul":
                        result = rmiClientStub.mul(x1,x2);
                        break;
                    default:
                        result = null;
                }

                System.out.println("Received from server: "+ result);
                consoleInput=consoleScanner.nextLine();

            }
        } catch (Exception e) {
            System.err.println("Should not be generic exception unless last resort!");
            e.printStackTrace();
        }
    }
  }