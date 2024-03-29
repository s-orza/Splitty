package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.nio.charset.Charset;
import java.util.Random;

@Entity
public class Password {
    @Id
    private long debtID;

    private String password;

    public Password() {

        password = genPass();

    }

    private String genPass(){
        return "";
    }
}
