import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServantRunner {
    public static final String rmiServantName = "RMIHelloWorld";

    public static void main(String[] args) {
        if(null == System.getSecurityManager()){
            System.setSecurityManager(new SecurityManager());
        }

        try{
            ISayHello remoteWorker = new SayHelloImpl();

            ISayHello servantStub = (ISayHello) UnicastRemoteObject.exportObject(remoteWorker,0);

            Registry targetRegistry = LocateRegistry.getRegistry();
            targetRegistry.rebind(ServantRunner.rmiServantName,servantStub);

            System.out.println("Greeter awaiting messages... ");

        } catch (Exception e) {
            System.err.println("Should not be generic exception unless last resort!");
            e.printStackTrace();
        }
    }
}