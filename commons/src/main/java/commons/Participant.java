package commons;

import jakarta.persistence.*;

import java.util.Objects;
@Entity
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    public long participantID;
    @Column
    public String name;
    @Column
    public String email;

    public long getParticipantID() {
        return participantID;
    }

    @Column
    public String iban;
    @Column
    public String bic;
    public Participant(String name, String email, String iban, String bic) {
        this.name = name;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
    }

    public Participant() {

    }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getIban() {
        return iban;
    }
    public String getBic() {
        return bic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Objects.equals(name, that.name)
                && Objects.equals(email, that.email)
                && Objects.equals(iban, that.iban)
                && Objects.equals(bic, that.bic);
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, email, iban, bic);
    }

    @Override
    public String toString() {
        return  name;
    }

    public void setParticipantID(long participantID) {
        this.participantID = participantID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }
}
