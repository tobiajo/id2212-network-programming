package xyz.johansson.id2212.hw2.marketrmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
    void callback(String msg) throws RemoteException;
}
