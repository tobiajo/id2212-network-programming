package xyz.johansson.id2212.hw4.converterjsf.controller;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import xyz.johansson.id2212.hw4.converterjsf.model.Currency;

@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class ConverterFacade {

    @PersistenceContext(unitName = "converterjsfPU")
    private EntityManager em;

    public boolean persistCurrency(String name, Double rate) {
        if (name == null || name.length() == 0 || rate == null || em.find(Currency.class, name) != null) {
            return false;
        }
        em.persist(new Currency(name, rate));
        return true;
    }

    public List<String> findCurrencyNames() {
        return em.createQuery("SELECT c.id FROM Currency c").getResultList();
    }

    public Double currencyConversion(Double amountIn, String currencyIn, String currencyOut) {
        Double rateIn;
        Double rateOut;
        if (amountIn == null || (rateIn = getRate(currencyIn)) == null || (rateOut = getRate(currencyOut)) == null) {
            return null;
        }
        return amountIn * rateIn / rateOut;
    }

    private Double getRate(String currencyName) {
        Currency currency;
        if (currencyName == null || (currency = em.find(Currency.class, currencyName)) == null) {
            return null;
        }
        return currency.getRate();
    }
}
