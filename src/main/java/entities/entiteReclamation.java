package entities;

import java.util.Date;

public class entiteReclamation {
    private int id;
    private String type;
    private int iduser;
    private Date datedepublication;
    private String contenu;
    private String statut;
    private String reponse;
    private Date dateReponse;
    private String adresseEmail;

    public entiteReclamation() {
        // Default constructor
    }

    public entiteReclamation(int id, String type, int iduser, Date datedepublication, String contenu, String statut, String adresseEmail) {
        this.id = id;
        this.type = type;
        this.iduser = iduser;
        this.datedepublication = datedepublication;
        this.contenu = contenu;
        this.statut = statut;
        this.adresseEmail = adresseEmail;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getIduser() { return iduser; }
    public void setIduser(int iduser) { this.iduser = iduser; }
    public Date getDatedepublication() { return datedepublication; }
    public void setDatedepublication(Date datedepublication) { this.datedepublication = datedepublication; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getReponse() { return reponse; }
    public void setReponse(String reponse) { this.reponse = reponse; }
    public Date getDateReponse() { return dateReponse; }
    public void setDateReponse(Date dateReponse) { this.dateReponse = dateReponse; }
    public String getAdresseEmail() {
        return adresseEmail;
    }
    public void setAdresseEmail(String adresseEmail) {
        this.adresseEmail = adresseEmail;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", type=" + type +
                ", iduser=" + iduser +
                ", date=" + datedepublication +
                ", contenu='" + contenu + '\'' +
                ", statut='" + statut + '\'' +
                ", reponse='" + reponse + '\'' +
                ", dateReponse=" + dateReponse +
                '}';
    }
}