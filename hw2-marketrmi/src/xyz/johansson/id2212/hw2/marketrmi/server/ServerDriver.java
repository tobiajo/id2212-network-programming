package xyz.johansson.id2212.hw2.marketrmi.server;

import xyz.johansson.id2212.hw2.marketrmi.client.Market;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ServerDriver {

    public static final String BANK_NAME = "Nordea";
    public static final String MARKET_NAME = "Tradera";

    public static void main(String[] args) {
        try {
            new ServerDriver().run();
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            System.out.println("fatal error: " + e.getMessage());
            System.exit(1);
        }
    }

    public void run() throws RemoteException, NotBoundException, MalformedURLException {
        se.kth.id2212.ex2.bankrmi.Server.main(new String[]{BANK_NAME});
        Market market = new MarketImpl(BANK_NAME);
        try {
            LocateRegistry.getRegistry(1099).list();
        } catch (RemoteException e) {
            LocateRegistry.createRegistry(1099);
        }
        Naming.rebind(MARKET_NAME, market);
        System.out.println(market + " is ready.");
    }
}
