package xyz.johansson.id2212.hw3.marketjdbc.client;

import xyz.johansson.id2212.hw3.marketjdbc.server.Client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface Market extends Remote {

    boolean accountExist(String userName) throws RemoteException, SQLException;
    boolean newAccount(Client client, String userName, String password) throws RemoteException, SQLException;
    boolean login(Client client, String userName, String password) throws RemoteException, SQLException;
    void logout(Client client) throws RemoteException;

    String listItems(Client client) throws RemoteException, SQLException;

    void addItem(Client client, String item) throws RemoteException, SQLException;

    int removeItem(Client client, int id) throws RemoteException, SQLException;

    boolean sell(Client client, int id, float price) throws RemoteException, SQLException;

    int buy(Client client, int id) throws RemoteException, SQLException;

    String inspect() throws RemoteException, SQLException;

    String activity(Client client) throws RemoteException, SQLException;
}