package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Random;

@Entity
public class Password {
    @Id
    @GeneratedValue
    private long passID;

    private int length = 7;
    private String password;

    /**
     * Constructor for a password
     */
    public Password() {
        password = genPass();

    }

    /**
     * Constructor for a password of specified length
     * @param length length of password
     */
    public Password(int length) {
        this.length = length;
        password = genPass();

    }

    /**
     * generates a random String password
     * @return a random String
     */
    private String genPass(){
        String chars =  "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789";
        StringBuilder pass = new StringBuilder();
        Random r = new Random();

        for(int i=0;i<length;i++){
            pass.append(chars.charAt(r.nextInt(chars.length())));
        }

        return pass.toString();
    }

    /**
     * The getter method for the debtID attribute
     *
     * @return debtID of this object
     **/
    public long getPassID() {
        return passID;
    }

    /**
     * The setter method for the debtID attribute
     *
     * @param passID The value to set debtID to
     **/
    public void setPassID(long passID) {
        this.passID = passID;
    }

    /**
     * The getter method for the length attribute
     *
     * @return length of this object
     **/
    public int getLength() {
        return length;
    }

    /**
     * The setter method for the length attribute
     *
     * @param length The value to set length to
     **/
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * The getter method for the password attribute
     *
     * @return password of this object
     **/
    public String getPassword() {
        return password;
    }

    /**
     * The setter method for the password attribute
     *
     * @param password The value to set password to
     **/
    public void setPassword(String password) {
        this.password = password;
    }
}
