package co.dewald.guardian.realm;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * @author Dewald Pretorius
 */
@Embeddable
public class State implements Serializable {

    static final long serialVersionUID = -1175497139999098709L;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private Boolean bypass;

    /**
     * 
     */
    public State() {
        this(Boolean.FALSE, Boolean.FALSE);
    }

    public State(Boolean active, Boolean bypass) {
        setActive(active);
        setBypass(bypass);
    }

    //@formatter:off
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Boolean getBypass() { return bypass; }
    public void setBypass(Boolean bypass) { this.bypass = bypass; }
    //@formatter:on
}
