package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MailStructureTest {

    private MailStructure mail;
    @BeforeEach
    void setUp() {
        mail=new MailStructure("Ana are","mere");
    }

    @Test
    void getSubject() {
        assertEquals("Ana are",mail.getSubject());
    }
    @Test
    void getMessage() {
        assertEquals("mere",mail.getMessage());
    }
    @Test
    void setTest() {
        mail.setSubject("1");
        mail.setMessage("2");
        assertEquals("1",mail.getSubject());
        assertEquals("2",mail.getMessage());
    }
    @Test
    void testNotNull()
    {
        MailStructure m =new MailStructure();
        assertNotNull(m);
    }
}