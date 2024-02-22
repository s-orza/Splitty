package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;
@Entity
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    public String name;
    public String email;
    public String iban;
    public String bic;
    public Participant(String name, String email, String iban, String bic) {
        this.name = name;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
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
        return "Participant: \n" +
                "name='" + name +
                "\nemail='" + email +
                "\niban='" + iban +
                "\nbic='" + bic +
                "\n";
    }
}
