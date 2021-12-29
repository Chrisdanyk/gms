package cd.gms.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Garage.
 */
@Entity
@Table(name = "garage")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Garage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "addresse")
    private String addresse;

    @Column(name = "email")
    private String email;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "rccm")
    private String rccm;

    @Column(name = "url")
    private String url;

    @NotNull
    @Column(name = "nuid", nullable = false, unique = true)
    private String nuid;

    @ManyToMany
    @JoinTable(
        name = "rel_garage__utilisateur",
        joinColumns = @JoinColumn(name = "garage_id"),
        inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<User> utilisateurs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Garage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Garage nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAddresse() {
        return this.addresse;
    }

    public Garage addresse(String addresse) {
        this.setAddresse(addresse);
        return this;
    }

    public void setAddresse(String addresse) {
        this.addresse = addresse;
    }

    public String getEmail() {
        return this.email;
    }

    public Garage email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public Garage telephone(String telephone) {
        this.setTelephone(telephone);
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getRccm() {
        return this.rccm;
    }

    public Garage rccm(String rccm) {
        this.setRccm(rccm);
        return this;
    }

    public void setRccm(String rccm) {
        this.rccm = rccm;
    }

    public String getUrl() {
        return this.url;
    }

    public Garage url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNuid() {
        return this.nuid;
    }

    public Garage nuid(String nuid) {
        this.setNuid(nuid);
        return this;
    }

    public void setNuid(String nuid) {
        this.nuid = nuid;
    }

    public Set<User> getUtilisateurs() {
        return this.utilisateurs;
    }

    public void setUtilisateurs(Set<User> users) {
        this.utilisateurs = users;
    }

    public Garage utilisateurs(Set<User> users) {
        this.setUtilisateurs(users);
        return this;
    }

    public Garage addUtilisateur(User user) {
        this.utilisateurs.add(user);
        return this;
    }

    public Garage removeUtilisateur(User user) {
        this.utilisateurs.remove(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Garage)) {
            return false;
        }
        return id != null && id.equals(((Garage) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Garage{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", addresse='" + getAddresse() + "'" +
            ", email='" + getEmail() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", rccm='" + getRccm() + "'" +
            ", url='" + getUrl() + "'" +
            ", nuid='" + getNuid() + "'" +
            "}";
    }
}
