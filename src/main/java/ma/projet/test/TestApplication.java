package ma.projet.test;

import ma.projet.classes.*;
import ma.projet.service.*;
import ma.projet.util.HibernateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Programme principal de test — Exercice 1 : Gestion de Stock
 *
 * Tests couverts :
 *   1. Insertion des données (catégories, produits, commandes, lignes)
 *   2. Produits par catégorie
 *   3. Produits commandés entre deux dates
 *   4. Détail d'une commande (format tableau)
 *   5. Produits avec prix > 100 DH  (@NamedQuery)
 */
public class TestApplication {

    static final CategorieService    categorieService = new CategorieService();
    static final ProduitService      produitService   = new ProduitService();
    static final CommandeService     commandeService  = new CommandeService();
    static final LigneCommandeService ligneService    = new LigneCommandeService();

    public static void main(String[] args) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // ── Test 1 : Insertion ───────────────────────────────────────────────
        sep("TEST 1 — Insertion des données");

        Categorie catInfo = new Categorie("PROC", "Processeurs");
        Categorie catRam  = new Categorie("RAM",  "Mémoires RAM");
        categorieService.create(catInfo);
        categorieService.create(catRam);
        System.out.println("Catégories : " + catInfo + " | " + catRam);

        Produit p1 = new Produit("ES12", 120f, catInfo);
        Produit p2 = new Produit("ZR85", 100f, catInfo);
        Produit p3 = new Produit("EE85", 200f, catRam);
        Produit p4 = new Produit("RM16",  80f, catRam);
        produitService.create(p1);
        produitService.create(p2);
        produitService.create(p3);
        produitService.create(p4);
        System.out.println("Produits insérés : " + p1 + " | " + p2 + " | " + p3 + " | " + p4);

        Commande cmd1 = new Commande(sdf.parse("14/03/2013"));
        Commande cmd2 = new Commande(sdf.parse("20/06/2024"));
        commandeService.create(cmd1);
        commandeService.create(cmd2);
        System.out.println("Commandes insérées : " + cmd1 + " | " + cmd2);

        ligneService.create(new LigneCommandeProduit(7,  p1, cmd1));
        ligneService.create(new LigneCommandeProduit(14, p2, cmd1));
        ligneService.create(new LigneCommandeProduit(5,  p3, cmd1));
        ligneService.create(new LigneCommandeProduit(3,  p3, cmd2));
        ligneService.create(new LigneCommandeProduit(10, p4, cmd2));
        System.out.println("Lignes de commande insérées.");

        // ── Test 2 : Produits par catégorie ─────────────────────────────────
        sep("TEST 2 — Produits de la catégorie : " + catInfo.getLibelle());
        List<Produit> parCat = produitService.getProduitsByCategorie(catInfo);
        parCat.forEach(p -> System.out.println("  " + p.getReference() + "  →  " + (int) p.getPrix() + " DH"));

        // ── Test 3 : Produits commandés entre deux dates ─────────────────────
        sep("TEST 3 — Produits commandés entre 01/01/2013 et 31/12/2013");
        Date debut = sdf.parse("01/01/2013");
        Date fin   = sdf.parse("31/12/2013");
        List<Produit> parDate = produitService.getProduitsCommandesEntreDates(debut, fin);
        parDate.forEach(p -> System.out.println("  " + p.getReference() + "  →  " + (int) p.getPrix() + " DH"));

        // ── Test 4 : Détail commande (format tableau) ────────────────────────
        sep("TEST 4 — Détail de la commande n°" + cmd1.getId());
        produitService.afficherProduitsParCommande(cmd1.getId());

        // ── Test 5 : Prix > 100 DH (@NamedQuery) ────────────────────────────
        sep("TEST 5 — Produits avec prix > 100 DH  (@NamedQuery)");
        List<Produit> chers = produitService.getProduitsAvecPrixSuperieurA100();
        chers.forEach(p -> System.out.println("  " + p.getReference() + "  →  " + (int) p.getPrix() + " DH"));

        // ── Fin ──────────────────────────────────────────────────────────────
        HibernateUtil.shutdown();
        System.out.println("\nTous les tests ont été exécutés avec succès ✔");
    }

    private static void sep(String titre) {
        String line = "=======================================================";
        System.out.println("\n" + line);
        System.out.println("  " + titre);
        System.out.println(line);
    }
}
