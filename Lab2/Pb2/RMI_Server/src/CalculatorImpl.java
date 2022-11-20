import java.rmi.RemoteException;

public class CalculatorImpl implements ICalculator{
    public CalculatorImpl() {
        super();
    }

    @Override
    public double add(double v, double v1) throws RemoteException {
        return v+v1;
    }

    @Override
    public double sub(double v, double v1) throws RemoteException {
        return v-v1;
    }

    @Override
    public double mul(double v, double v1) throws RemoteException {
        return v*v1;
    }
}
