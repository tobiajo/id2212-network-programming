package xyz.johansson.id2212.hw4.converterjsf.view;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import xyz.johansson.id2212.hw4.converterjsf.controller.ConverterFacade;

@Named("currencyManager")
@RequestScoped
public class CurrencyManager implements Serializable {

    @EJB
    private ConverterFacade converterFacade;
    private String newCurrencyName;
    private Double newCurrencyRate;
    private String currencyIn;
    private String currencyOut;
    private Double amountIn;
    private Double amountOut;

    public void add() {
        if (getNewCurrencyName().length() > 0 && getNewCurrencyRate() != null && converterFacade.findCurrency(getNewCurrencyName()) == null) {
            converterFacade.persistCurrency(getNewCurrencyName(), getNewCurrencyRate());
        }
    }

    public List<String> list() {
        return converterFacade.findAllCurrencies();
    }

    public void convert() {
        Double rateIn = converterFacade.findCurrency(getCurrencyIn()).getRate();
        Double rateOut = converterFacade.findCurrency(getCurrencyOut()).getRate();
        if (getAmountIn() != null && rateIn != null && rateOut != null) {
            setAmountOut(getAmountIn() * rateIn / rateOut);
        }
    }

    /**
     * @return the newCurrencyName
     */
    public String getNewCurrencyName() {
        return newCurrencyName;
    }

    /**
     * @param newCurrencyName the newCurrencyName to set
     */
    public void setNewCurrencyName(String newCurrencyName) {
        this.newCurrencyName = newCurrencyName;
    }

    /**
     * @return the newCurrencyRate
     */
    public Double getNewCurrencyRate() {
        return newCurrencyRate;
    }

    /**
     * @param newCurrencyRate the newCurrencyRate to set
     */
    public void setNewCurrencyRate(Double newCurrencyRate) {
        this.newCurrencyRate = newCurrencyRate;
    }

    /**
     * @return the currencyIn
     */
    public String getCurrencyIn() {
        return currencyIn;
    }

    /**
     * @param currencyIn the currencyIn to set
     */
    public void setCurrencyIn(String currencyIn) {
        this.currencyIn = currencyIn;
    }

    /**
     * @return the currencyOut
     */
    public String getCurrencyOut() {
        return currencyOut;
    }

    /**
     * @param currencyOut the currencyOut to set
     */
    public void setCurrencyOut(String currencyOut) {
        this.currencyOut = currencyOut;
    }

    /**
     * @return the amountIn
     */
    public Double getAmountIn() {
        return amountIn;
    }

    /**
     * @param amountIn the amountIn to set
     */
    public void setAmountIn(Double amountIn) {
        this.amountIn = amountIn;
    }

    /**
     * @return the amountOut
     */
    public Double getAmountOut() {
        return amountOut;
    }

    /**
     * @param amountOut the amountOut to set
     */
    public void setAmountOut(Double amountOut) {
        this.amountOut = amountOut;
    }
}
