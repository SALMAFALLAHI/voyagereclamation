package tests;

import entities.entiteReclamation;

import entities.entiteReponseAdmin;
import services.Reclamation;

import services.AdminService;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Reclamation service = new Reclamation();
       
        AdminService adminService = new AdminService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Mode Utilisateur");
            System.out.println("2. Mode Administrateur");
            System.out.println("0. Quitter");
            System.out.print("Choix: ");
            int mode = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (mode == 1) {
                // Mode Utilisateur
                while (true) {
                    System.out.println("\n1. Ajouter Reclamation");
                    System.out.println("2. Afficher mes Reclamations");
                    System.out.println("3. Modifier une Reclamation");
                    System.out.println("4. Supprimer une Reclamation");
                    System.out.println("0. Retour");
                    System.out.print("Choix: ");
                    int choix = scanner.nextInt();
                    scanner.nextLine(); // consume newline

                    if (choix == 0) break;

                    try {
                        if (choix == 1) {
                            // Afficher les types de réclamation
                           
                            System.out.println("Types de réclamation disponibles :");
                            System.out.println("1 - Problème technique");
                            System.out.println("2 - Service client");
                            System.out.println("3 - Facturation");
                            System.out.println("4 - Autre");

                            System.out.print("Choisissez le type de réclamation (1-4) : ");
                            String type = scanner.nextLine();

                            System.out.print("Votre id utilisateur : ");
                            int iduser = scanner.nextInt();
                            scanner.nextLine();

                            System.out.print("Contenu : ");
                            String contenu = scanner.nextLine();



// Création de la réclamation avec les champs nécessaires
// Comme les champs date et statut sont gérés par la base de données,
// nous les incluons dans l'objet mais ils ne seront pas utilisés dans la méthode ajouter()
                            Date date = new Date(); // Conservé pour la structure de l'objet
                            String statut = "non traite"; // Conservé pour la structure de l'objet

                            entiteReclamation rec = new entiteReclamation(0, type, iduser, date, contenu, statut);
                            service.ajouter(rec);
                        } else if (choix == 2) {
                            // Afficher
                            List<entiteReclamation> list = service.afficher();
                            for (entiteReclamation r : list) {
                                System.out.println(r);
                            }
                        } else if (choix == 3) {
                            // Modifier
                            System.out.print("ID de la reclamation à modifier: ");
                            int id = scanner.nextInt();
                            System.out.println("Types de réclamation disponibles :");
                            System.out.println("1 - Problème technique");
                            System.out.println("2 - Service client");
                            System.out.println("3 - Facturation");
                            System.out.println("4 - Autre");
                            System.out.print("Nouveau type: ");
                            String type = scanner.nextLine();
                            System.out.print("Nouveau iduser: ");
                            int iduser = scanner.nextInt();
                            scanner.nextLine();
                            System.out.print("Nouvelle date de publication (yyyy-MM-dd): ");
                            String dateStr = scanner.nextLine();
                            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                            System.out.print("Nouveau contenu: ");
                            String contenu = scanner.nextLine();


                            entiteReclamation rec = new entiteReclamation(id, type, iduser, date, contenu, "non traite");
                            service.modifier(rec);
                        } else if (choix == 4) {
                            // Supprimer
                            System.out.print("ID de la reclamation à supprimer: ");
                            int id = scanner.nextInt();
                            service.supprimer(id);
                        }
                    } catch (SQLException e) {
                        System.out.println("Erreur SQL: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Erreur: " + e.getMessage());
                    }
                }
            } else if (mode == 2) {
                // Mode Administrateur
                while (true) {
                    System.out.println("\n1. Afficher les réclamations non traitées");
                    System.out.println("2. Répondre à une réclamation");
                    System.out.println("0. Retour");
                    System.out.print("Choix: ");
                    int choix = scanner.nextInt();
                    scanner.nextLine(); // consume newline

                    if (choix == 0) break;

                    try {
                        if (choix == 1) {
                            List<entiteReclamation> reclamations = adminService.getReclamationsNonTraitees();
                            if (reclamations.isEmpty()) {
                                System.out.println("Aucune réclamation non traitée trouvée.");
                            } else {
                                System.out.println("Réclamations non traitées :");
                                for (entiteReclamation r : reclamations) {
                                    System.out.println(r);
                                }
                            }
                        } else if (choix == 2) {
                            System.out.print("ID de la réclamation à traiter : ");
                            int idReclamation = scanner.nextInt();
                            scanner.nextLine(); // consume newline
                            System.out.print("Votre réponse : ");
                            String reponse = scanner.nextLine();
                            adminService.repondreReclamation(idReclamation, reponse);

                            // Afficher les réponses pour cette réclamation
                            List<entiteReponseAdmin> reponses = adminService.getReponsesPourReclamation(idReclamation);
                            if (!reponses.isEmpty()) {
                                System.out.println("\nRéponses pour cette réclamation :");
                                for (entiteReponseAdmin r : reponses) {
                                    System.out.println("Date: " + r.getDateReponse());
                                    System.out.println("Réponse: " + r.getReponse());
                                    System.out.println("-------------------");
                                }
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("Erreur SQL: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Erreur: " + e.getMessage());
                    }
                }
            } else if (mode == 0) {
                System.out.println("Au revoir!");
                break;
            } else {
                System.out.println("Choix invalide.");
            }
        }
        scanner.close();
    }
}
