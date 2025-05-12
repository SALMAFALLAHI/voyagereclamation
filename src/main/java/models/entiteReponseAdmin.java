package models;

import java.util.Date;

public class entiteReponseAdmin {
    private int id;
    private int reclamationId;
    private String reponse;
    private Date dateReponse;

    public entiteReponseAdmin(int id, int reclamationId, String reponse, Date dateReponse) {
        this.id = id;
        this.reclamationId = reclamationId;
        this.reponse = reponse;
        this.dateReponse = dateReponse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReclamationId() {
        return reclamationId;
    }

    public void setReclamationId(int reclamationId) {
        this.reclamationId = reclamationId;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public Date getDateReponse() {
        return dateReponse;
    }

    public void setDateReponse(Date dateReponse) {
        this.dateReponse = dateReponse;
    }

    @Override
    public String toString() {
        return "ReponseAdmin{" +
                "id=" + id +
                ", reclamationId=" + reclamationId +
                ", reponse='" + reponse + '\'' +
                ", dateReponse=" + dateReponse +
                '}';
    }
}
