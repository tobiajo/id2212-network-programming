package xyz.johansson.id2212.hw4.converterjsf.controller;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import xyz.johansson.id2212.hw4.converterjsf.model.Currency;
import xyz.johansson.id2212.hw4.converterjsf.model.CurrencyDTO;

@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class ConverterFacade {

    @PersistenceContext(unitName = "converterjsfPU")
    private EntityManager em;

    public void persistCurrency(String name, Double rate) {
        em.persist(new Currency(name, rate));
    }

    public List<String> findAllCurrencies() {
        List<Currency> currencies = em.createQuery("SELECT c FROM Currency c").getResultList();
        List<String> currencyNames = new ArrayList();
        currencies.stream().forEach((c) -> {
            currencyNames.add(c.getId());
        });
        return currencyNames;
    }

    public CurrencyDTO findCurrency(String name) {
        return em.find(Currency.class, name);
    }
}
