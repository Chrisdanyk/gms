package cd.gms.domain;

import cd.gms.domain.enumeration.Statut;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Notification.
 */
@Entity
@Table(name = "notification")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private Statut statut;

    @Column(name = "date_prochaine_maintenance")
    private LocalDate dateProchaineMaintenance;

    @NotNull
    @Column(name = "nuid", nullable = false, unique = true)
    private String nuid;

    @JsonIgnoreProperties(value = { "notification", "engin", "operations" }, allowSetters = true)
    @OneToOne(mappedBy = "notification")
    private Maintenance maintenance;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Notification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Notification date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Statut getStatut() {
        return this.statut;
    }

    public Notification statut(Statut statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public LocalDate getDateProchaineMaintenance() {
        return this.dateProchaineMaintenance;
    }

    public Notification dateProchaineMaintenance(LocalDate dateProchaineMaintenance) {
        this.setDateProchaineMaintenance(dateProchaineMaintenance);
        return this;
    }

    public void setDateProchaineMaintenance(LocalDate dateProchaineMaintenance) {
        this.dateProchaineMaintenance = dateProchaineMaintenance;
    }

    public String getNuid() {
        return this.nuid;
    }

    public Notification nuid(String nuid) {
        this.setNuid(nuid);
        return this;
    }

    public void setNuid(String nuid) {
        this.nuid = nuid;
    }

    public Maintenance getMaintenance() {
        return this.maintenance;
    }

    public void setMaintenance(Maintenance maintenance) {
        if (this.maintenance != null) {
            this.maintenance.setNotification(null);
        }
        if (maintenance != null) {
            maintenance.setNotification(this);
        }
        this.maintenance = maintenance;
    }

    public Notification maintenance(Maintenance maintenance) {
        this.setMaintenance(maintenance);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        return id != null && id.equals(((Notification) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notification{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", statut='" + getStatut() + "'" +
            ", dateProchaineMaintenance='" + getDateProchaineMaintenance() + "'" +
            ", nuid='" + getNuid() + "'" +
            "}";
    }
}
