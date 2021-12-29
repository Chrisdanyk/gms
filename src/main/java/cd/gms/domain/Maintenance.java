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
import org.hibernate.annotations.Type;

/**
 * A Maintenance.
 */
@Entity
@Table(name = "maintenance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Maintenance implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @NotNull
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "rapport_global")
    private String rapportGlobal;

    @NotNull
    @Column(name = "prix_total", precision = 21, scale = 2, nullable = false)
    private BigDecimal prixTotal;

    @Column(name = "discount_total", precision = 21, scale = 2)
    private BigDecimal discountTotal;

    @NotNull
    @Column(name = "nuid", nullable = false, unique = true)
    private String nuid;

    @JsonIgnoreProperties(value = { "maintenance" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Notification notification;

    @ManyToOne
    @JsonIgnoreProperties(value = { "proprietaire" }, allowSetters = true)
    private Engin engin;

    @ManyToMany
    @JoinTable(
        name = "rel_maintenance__operation",
        joinColumns = @JoinColumn(name = "maintenance_id"),
        inverseJoinColumns = @JoinColumn(name = "operation_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "mecaniciens", "taches", "maintenances" }, allowSetters = true)
    private Set<Operation> operations = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Maintenance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateDebut() {
        return this.dateDebut;
    }

    public Maintenance dateDebut(LocalDate dateDebut) {
        this.setDateDebut(dateDebut);
        return this;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return this.dateFin;
    }

    public Maintenance dateFin(LocalDate dateFin) {
        this.setDateFin(dateFin);
        return this;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getRapportGlobal() {
        return this.rapportGlobal;
    }

    public Maintenance rapportGlobal(String rapportGlobal) {
        this.setRapportGlobal(rapportGlobal);
        return this;
    }

    public void setRapportGlobal(String rapportGlobal) {
        this.rapportGlobal = rapportGlobal;
    }

    public BigDecimal getPrixTotal() {
        return this.prixTotal;
    }

    public Maintenance prixTotal(BigDecimal prixTotal) {
        this.setPrixTotal(prixTotal);
        return this;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }

    public BigDecimal getDiscountTotal() {
        return this.discountTotal;
    }

    public Maintenance discountTotal(BigDecimal discountTotal) {
        this.setDiscountTotal(discountTotal);
        return this;
    }

    public void setDiscountTotal(BigDecimal discountTotal) {
        this.discountTotal = discountTotal;
    }

    public String getNuid() {
        return this.nuid;
    }

    public Maintenance nuid(String nuid) {
        this.setNuid(nuid);
        return this;
    }

    public void setNuid(String nuid) {
        this.nuid = nuid;
    }

    public Notification getNotification() {
        return this.notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Maintenance notification(Notification notification) {
        this.setNotification(notification);
        return this;
    }

    public Engin getEngin() {
        return this.engin;
    }

    public void setEngin(Engin engin) {
        this.engin = engin;
    }

    public Maintenance engin(Engin engin) {
        this.setEngin(engin);
        return this;
    }

    public Set<Operation> getOperations() {
        return this.operations;
    }

    public void setOperations(Set<Operation> operations) {
        this.operations = operations;
    }

    public Maintenance operations(Set<Operation> operations) {
        this.setOperations(operations);
        return this;
    }

    public Maintenance addOperation(Operation operation) {
        this.operations.add(operation);
        operation.getMaintenances().add(this);
        return this;
    }

    public Maintenance removeOperation(Operation operation) {
        this.operations.remove(operation);
        operation.getMaintenances().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Maintenance)) {
            return false;
        }
        return id != null && id.equals(((Maintenance) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Maintenance{" +
            "id=" + getId() +
            ", dateDebut='" + getDateDebut() + "'" +
            ", dateFin='" + getDateFin() + "'" +
            ", rapportGlobal='" + getRapportGlobal() + "'" +
            ", prixTotal=" + getPrixTotal() +
            ", discountTotal=" + getDiscountTotal() +
            ", nuid='" + getNuid() + "'" +
            "}";
    }
}
