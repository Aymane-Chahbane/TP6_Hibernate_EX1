package ma.projet.service;

import ma.projet.classes.Categorie;
import ma.projet.classes.LigneCommandeProduit;
import ma.projet.classes.Produit;
import ma.projet.dao.IDao;
import ma.projet.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProduitService implements IDao<Produit, Integer> {

    // ── CRUD ──────────────────────────────────────────────────────────────────

    @Override
    public boolean create(Produit p) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(p);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Produit p) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(p);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Produit p) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(p);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Produit findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Produit.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Produit> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Produit", Produit.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ── Méthodes métier ───────────────────────────────────────────────────────

    /** Liste des produits d'une catégorie donnée. */
    public List<Produit> getProduitsByCategorie(Categorie categorie) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                .createQuery("FROM Produit p WHERE p.categorie = :cat", Produit.class)
                .setParameter("cat", categorie)
                .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /** Produits commandés entre deux dates (inclusif). */
    public List<Produit> getProduitsCommandesEntreDates(Date dateDebut, Date dateFin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT lcp.produit FROM LigneCommandeProduit lcp "
                       + "WHERE lcp.commande.date BETWEEN :debut AND :fin";
            return session.createQuery(hql, Produit.class)
                .setParameter("debut", dateDebut)
                .setParameter("fin",   dateFin)
                .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Affiche les produits d'une commande au format :
     * <pre>
     * Commande : 4     Date : 14 Mars 2013
     * Liste des produits :
     * Référence   Prix    Quantité
     * ES12        120 DH  7
     * </pre>
     */
    public void afficherProduitsParCommande(int commandeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            ma.projet.classes.Commande commande = session.get(
                ma.projet.classes.Commande.class, commandeId);

            if (commande == null) {
                System.out.println("Commande introuvable : " + commandeId);
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("fr", "FR"));
            System.out.println("Commande : " + commande.getId()
                + "     Date : " + sdf.format(commande.getDate()));
            System.out.println("Liste des produits :");
            System.out.printf("%-12s %-10s %s%n", "Référence", "Prix", "Quantité");
            System.out.println("------------------------------------");

            List<LigneCommandeProduit> lignes = session
                .createQuery("FROM LigneCommandeProduit lcp WHERE lcp.commande.id = :id",
                             LigneCommandeProduit.class)
                .setParameter("id", commandeId)
                .list();

            for (LigneCommandeProduit lcp : lignes) {
                System.out.printf("%-12s %-10s %d%n",
                    lcp.getProduit().getReference(),
                    (int) lcp.getProduit().getPrix() + " DH",
                    lcp.getQuantite());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Produits avec prix > 100 DH via @NamedQuery. */
    public List<Produit> getProduitsAvecPrixSuperieurA100() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                .createNamedQuery("Produit.prixSuperieurA100", Produit.class)
                .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
