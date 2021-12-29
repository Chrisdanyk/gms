package cd.gms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Operation.
 */
@Entity
@Table(name = "operation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Operation implements Serializable {

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
    @Column(name = "prix", precision = 21, scale = 2, nullable = false)
    private BigDecimal prix;

    @Column(name = "discount", precision = 21, scale = 2)
    private BigDecimal discount;

    @NotNull
    @Column(name = "nuid", nullable = false, unique = true)
    private String nuid;

    @ManyToMany
    @JoinTable(
        name = "rel_operation__mecanicien",
        joinColumns = @JoinColumn(name = "operation_id"),
        inverseJoinColumns = @JoinColumn(name = "mecanicien_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<User> mecaniciens = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_operation__tache",
        joinColumns = @JoinColumn(name = "operation_id"),
        inverseJoinColumns = @JoinColumn(name = "tache_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "garage", "operations" }, allowSetters = true)
    private Set<Tache> taches = new HashSet<>();

    @ManyToMany(mappedBy = "operations")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "notification", "engin", "operations" }, allowSetters = true)
    private Set<Maintenance> maintenances = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Operation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Operation date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getPrix() {
        return this.prix;
    }

    public Operation prix(BigDecimal prix) {
        this.setPrix(prix);
        return this;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public BigDecimal getDiscount() {
        return this.discount;
    }

    public Operation discount(BigDecimal discount) {
        this.setDiscount(discount);
        return this;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getNuid() {
        return this.nuid;
    }

    public Operation nuid(String nuid) {
        this.setNuid(nuid);
        return this;
    }

    public void setNuid(String nuid) {
        this.nuid = nuid;
    }

    public Set<User> getMecaniciens() {
        return this.mecaniciens;
    }

    public void setMecaniciens(Set<User> users) {
        this.mecaniciens = users;
    }

    public Operation mecaniciens(Set<User> users) {
        this.setMecaniciens(users);
        return this;
    }

    public Operation addMecanicien(User user) {
        this.mecaniciens.add(user);
        return this;
    }

    public Operation removeMecanicien(User user) {
        this.mecaniciens.remove(user);
        return this;
    }

    public Set<Tache> getTaches() {
        return this.taches;
    }

    public void setTaches(Set<Tache> taches) {
        this.taches = taches;
    }

    public Operation taches(Set<Tache> taches) {
        this.setTaches(taches);
        return this;
    }

    public Operation addTache(Tache tache) {
        this.taches.add(tache);
        tache.getOperations().add(this);
        return this;
    }

    public Operation removeTache(Tache tache) {
        this.taches.remove(tache);
        tache.getOperations().remove(this);
        return this;
    }

    public Set<Maintenance> getMaintenances() {
        return this.maintenances;
    }

    public void setMaintenances(Set<Maintenance> maintenances) {
        if (this.maintenances != null) {
            this.maintenances.forEach(i -> i.removeOperation(this));
        }
        if (maintenances != null) {
            maintenances.forEach(i -> i.addOperation(this));
        }
        this.maintenances = maintenances;
    }

    public Operation maintenances(Set<Maintenance> maintenances) {
        this.setMaintenances(maintenances);
        return this;
    }

    public Operation addMaintenance(Maintenance maintenance) {
        this.maintenances.add(maintenance);
        maintenance.getOperations().add(this);
        return this;
    }

    public Operation removeMaintenance(Maintenance maintenance) {
        this.maintenances.remove(maintenance);
        maintenance.getOperations().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Operation)) {
            return false;
        }
        return id != null && id.equals(((Operation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Operation{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", prix=" + getPrix() +
            ", discount=" + getDiscount() +
            ", nuid='" + getNuid() + "'" +
            "}";
    }
}
