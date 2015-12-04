package xyz.johansson.id2212.hw2.marketrmi.server;

import xyz.johansson.id2212.hw2.marketrmi.client.IllegalNameException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Set;

public class TraderImpl extends UnicastRemoteObject implements xyz.johansson.id2212.hw2.marketrmi.client.Trader {

    private Client client;
    private String name;
    private Set<String> items;

    public TraderImpl(Client client, String name) throws RemoteException {
        this.client = client;
        this.name = name;
        items = new HashSet();
    }

    @Override
    public Client getClient() throws RemoteException {
        return client;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public Set<String> getItems() {
        return items;
    }

    @Override
    public boolean addItem(String item) throws IllegalNameException {
        IllegalNameException.check(item);
        return items.add(item);
    }

    @Override
    public boolean removeItem(String item) {
        return items.remove(item);
    }

    @Override
    public boolean containsItem(String item) throws RemoteException {
        for (String s : items) {
            if (s.equals(item)) {
                return true;
            }
        }
        return false;
    }
}
