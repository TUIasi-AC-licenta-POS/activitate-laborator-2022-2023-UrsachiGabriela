import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICalculator extends Remote {

    double add(double a, double b) throws RemoteException;
    double sub(double a, double b) throws RemoteException;
    double mul(double a, double b) throws RemoteException;

}