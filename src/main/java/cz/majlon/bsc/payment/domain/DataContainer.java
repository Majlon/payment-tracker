package cz.majlon.bsc.payment.domain;

import java.util.*;

public class DataContainer {

    final private Collection<Payment> data;
    final private Map<String, Double> exchangeRate;

    public DataContainer() {
        this.data = Collections.synchronizedList(new ArrayList<>());
        this.exchangeRate = Collections.synchronizedMap(new HashMap<>());
    }

    public Collection<Payment> getData() {
        return data;
    }

    public Map<String, Double> getExchangeRate() {
        return exchangeRate;
    }
}
