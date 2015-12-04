package xyz.johansson.id2212.hw2.marketrmi.client;

import xyz.johansson.id2212.hw2.marketrmi.server.Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Market extends Remote {
    Trader newTrader(Client client, String userName) throws RemoteException, IllegalNameException;

    Trader getTrader(String userName) throws RemoteException;

    int sell(String item, float price, Trader trader) throws RemoteException;

    int buy(String item, Trader trader) throws RemoteException;

    String inspect() throws RemoteException;

    void wish(Client client, String item, float price) throws RemoteException;
}
