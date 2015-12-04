package xyz.johansson.id2212.hw3.marketjdbc.server;

import xyz.johansson.id2212.hw3.marketjdbc.client.Market;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.sql.SQLException;

public class ServerDriver {

    public static final String BANK_NAME = "Nordea";
    public static final String BANK_DB = "Nordea";
    public static final String MARKET_NAME = "Tradera";
    public static final String MARKET_DB = "Tradera";

    public static void main(String[] args) {
        try {
            new ServerDriver().run();
        } catch (RemoteException | MalformedURLException | ClassNotFoundException | SQLException | NotBoundException e) {
            System.out.println("fatal error: " + e.getMessage());
            System.exit(1);
        }
    }

    public void run() throws RemoteException, SQLException, ClassNotFoundException, MalformedURLException, NotBoundException {
        se.kth.id2212.ex3.bankjdbc.Server.main(new String[]{BANK_NAME, BANK_DB, "mysql"});
        Market market = new MarketImpl(MARKET_DB, BANK_NAME);
        try {
            LocateRegistry.getRegistry(1099).list();
        } catch (RemoteException e) {
            LocateRegistry.createRegistry(1099);
        }
        Naming.rebind(MARKET_NAME, market);
        System.out.println(market + " is ready.");
    }
}
