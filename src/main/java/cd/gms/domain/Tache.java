package cd.gms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Tache.
 */
@Entity
@Table(name = "tache")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Tache implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotNull
    @Column(name = "prix_unitaire", precision = 21, scale = 2, nullable = false)
    private BigDecimal prixUnitaire;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "disponible", nullable = false)
    private Boolean disponible;

    @NotNull
    @Column(name = "nuid", nullable = false, unique = true)
    private String nuid;

    @ManyToOne
    @JsonIgnoreProperties(value = { "utilisateurs" }, allowSetters = true)
    private Garage garage;

    @ManyToMany(mappedBy = "taches")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "mecaniciens", "taches", "maintenances" }, allowSetters = true)
    private Set<Operation> operations = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tache id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Tache nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public BigDecimal getPrixUnitaire() {
        return this.prixUnitaire;
    }

    public Tache prixUnitaire(BigDecimal prixUnitaire) {
        this.setPrixUnitaire(prixUnitaire);
        return this;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public String getDescription() {
        return this.description;
    }

    public Tache description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDisponible() {
        return this.disponible;
    }

    public Tache disponible(Boolean disponible) {
        this.setDisponible(disponible);
        return this;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public String getNuid() {
        return this.nuid;
    }

    public Tache nuid(String nuid) {
        this.setNuid(nuid);
        return this;
    }

    public void setNuid(String nuid) {
        this.nuid = nuid;
    }

    public Garage getGarage() {
        return this.garage;
    }

    public void setGarage(Garage garage) {
        this.garage = garage;
    }

    public Tache garage(Garage garage) {
        this.setGarage(garage);
        return this;
    }

    public Set<Operation> getOperations() {
        return this.operations;
    }

    public void setOperations(Set<Operation> operations) {
        if (this.operations != null) {
            this.operations.forEach(i -> i.removeTache(this));
        }
        if (operations != null) {
            operations.forEach(i -> i.addTache(this));
        }
        this.operations = operations;
    }

    public Tache operations(Set<Operation> operations) {
        this.setOperations(operations);
        return this;
    }

    public Tache addOperation(Operation operation) {
        this.operations.add(operation);
        operation.getTaches().add(this);
        return this;
    }

    public Tache removeOperation(Operation operation) {
        this.operations.remove(operation);
        operation.getTaches().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tache)) {
            return false;
        }
        return id != null && id.equals(((Tache) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tache{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prixUnitaire=" + getPrixUnitaire() +
            ", description='" + getDescription() + "'" +
            ", disponible='" + getDisponible() + "'" +
            ", nuid='" + getNuid() + "'" +
            "}";
    }
}
