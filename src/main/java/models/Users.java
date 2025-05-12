package models;

public class Users {
    private int userId;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String password; // Ajout du champ mot de passe
    private String status; // "bloqué" ou "actif"

    public Users(int userId, String nom, String prenom, String email, String telephone, String password, String status) {
        this.userId = userId;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.password = password;
        this.status = status;
    }

    public Users(String nom, String prenom, String email, String telephone, String password, String status) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.password = password;
        this.status = status;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getPassword() {
        return password;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Users{" +
                "userId=" + userId +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

