package xyz.johansson.id2212.hw4.converterjsf.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Currency implements Serializable {

    @Id
    private String id;
    private Double rate;

    public Currency() {
    }

    public Currency(String id, Double rate) {
        this.id = id;
        this.rate = rate;
    }

    public String getId() {
        return id;
    }

    public Double getRate() {
        return rate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Currency)) {
            return false;
        }
        Currency other = (Currency) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "xyz.johansson.id2212.hw4.converterjsf.model.Currency[ id=" + id + " ]";
    }
}
