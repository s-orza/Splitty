package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class PasswordTest {

    private Password password;
    @BeforeEach
    void setUp() {
        password=new Password(3);
    }
    @Test
    void getTest() {
        assertEquals(3,password.getPassword().length());
    }
    @Test
    void testNotNull()
    {
        Password p =new Password();
        assertNotNull(p);
    }
    @Test
    void setTest() {
        password.setPassID(1);
        password.setLength(3);
        password.setPassword("231");
        assertEquals(1,password.getPassID());
        assertEquals(3,password.getLength());
        assertEquals("231",password.getPassword());
    }
}