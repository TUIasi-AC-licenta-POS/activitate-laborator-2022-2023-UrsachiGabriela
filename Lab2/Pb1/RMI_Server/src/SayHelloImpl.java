import java.rmi.RemoteException;

public class SayHelloImpl implements  ISayHello{

    public SayHelloImpl(){
        super();
    }

    @Override
    public String sayHello(String s) throws RemoteException {
        StringBuilder resultString = new StringBuilder();
        resultString.append("Hello "+s);
        return resultString.toString();
    }
}
