package cd.gms.domain;

import cd.gms.domain.enumeration.Type;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Engin.
 */
@Entity
@Table(name = "engin")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Engin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "modele")
    private String modele;

    @NotNull
    @Column(name = "plaque", nullable = false, unique = true)
    private String plaque;

    @Column(name = "date_fabrication")
    private LocalDate dateFabrication;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @ManyToOne
    private User proprietaire;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Engin id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModele() {
        return this.modele;
    }

    public Engin modele(String modele) {
        this.setModele(modele);
        return this;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getPlaque() {
        return this.plaque;
    }

    public Engin plaque(String plaque) {
        this.setPlaque(plaque);
        return this;
    }

    public void setPlaque(String plaque) {
        this.plaque = plaque;
    }

    public LocalDate getDateFabrication() {
        return this.dateFabrication;
    }

    public Engin dateFabrication(LocalDate dateFabrication) {
        this.setDateFabrication(dateFabrication);
        return this;
    }

    public void setDateFabrication(LocalDate dateFabrication) {
        this.dateFabrication = dateFabrication;
    }

    public Type getType() {
        return this.type;
    }

    public Engin type(Type type) {
        this.setType(type);
        return this;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public User getProprietaire() {
        return this.proprietaire;
    }

    public void setProprietaire(User user) {
        this.proprietaire = user;
    }

    public Engin proprietaire(User user) {
        this.setProprietaire(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Engin)) {
            return false;
        }
        return id != null && id.equals(((Engin) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Engin{" +
            "id=" + getId() +
            ", modele='" + getModele() + "'" +
            ", plaque='" + getPlaque() + "'" +
            ", dateFabrication='" + getDateFabrication() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
