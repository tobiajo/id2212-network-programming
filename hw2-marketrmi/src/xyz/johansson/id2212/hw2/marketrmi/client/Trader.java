package xyz.johansson.id2212.hw2.marketrmi.client;

import xyz.johansson.id2212.hw2.marketrmi.server.Client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface Trader extends Remote {
    Client getClient() throws RemoteException;

    String getName() throws RemoteException;

    Set<String> getItems() throws RemoteException;

    boolean addItem(String item) throws RemoteException, IllegalNameException;

    boolean removeItem(String item) throws RemoteException;

    boolean containsItem(String item) throws RemoteException;
}
