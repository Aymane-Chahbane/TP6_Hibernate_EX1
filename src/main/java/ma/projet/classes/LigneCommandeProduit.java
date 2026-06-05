package ma.projet.classes;

import javax.persistence.*;

@Entity
@Table(name = "ligne_commande_produit")
public class LigneCommandeProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int quantite;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commande commande;

    public LigneCommandeProduit() {}

    public LigneCommandeProduit(int quantite, Produit produit, Commande commande) {
        this.quantite = quantite;
        this.produit  = produit;
        this.commande = commande;
    }

    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public int getQuantite()                { return quantite; }
    public void setQuantite(int quantite)   { this.quantite = quantite; }

    public Produit getProduit()             { return produit; }
    public void setProduit(Produit p)       { this.produit = p; }

    public Commande getCommande()           { return commande; }
    public void setCommande(Commande c)     { this.commande = c; }

    @Override
    public String toString() {
        return "LigneCommandeProduit{id=" + id + ", quantite=" + quantite + "}";
    }
}
