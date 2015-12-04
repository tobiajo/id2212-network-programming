package xyz.johansson.id2212.hw3.marketjdbc.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
    void callback(String msg) throws RemoteException;
}
