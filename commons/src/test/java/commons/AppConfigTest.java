package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    private AppConfig appConfig;
    @BeforeEach
    void setUp() {
        appConfig=new AppConfig("EUR",new ArrayList<>(),"127.0.0.1","8080",new Locale("en", "US"));
    }

    @Test
    void testToString() {
        String x=appConfig.toString();
        assertEquals("AppConfig{currency='EUR', recentEvents=[], ip='127.0.0.1'" +
                ", port='8080', language='en_US}",appConfig.toString());
    }

    @Test
    void getTest() {
        assertEquals("EUR",appConfig.getCurrency());
        assertEquals(new ArrayList<>(),appConfig.getRecentEvents());
        assertEquals("127.0.0.1",appConfig.getIp());
        assertEquals("8080",appConfig.getPort());
        assertNotNull(appConfig.getLang());
        assertNull(appConfig.getEmail());
        assertNull(appConfig.getPassword());
    }

    @Test
    void setTest() {
        ArrayList<Long> list=new ArrayList<>();
        list.add(1L);
        appConfig.setCurrency("RON");
        appConfig.setIp("1");
        appConfig.setRecentEvents(list);
        appConfig.setPort("8");
        appConfig.setEmail("a@a");
        appConfig.setPassword("123");
        appConfig.setLang(null);
        assertEquals("RON",appConfig.getCurrency());
        assertEquals(list,appConfig.getRecentEvents());
        assertEquals("1",appConfig.getIp());
        assertEquals("8",appConfig.getPort());
        assertNull(appConfig.getLang());
        assertEquals("a@a",appConfig.getEmail());
        assertEquals("123",appConfig.getPassword());
    }
    @Test
    void testNotNull()
    {
        AppConfig appConfig =new AppConfig();
        assertNotNull(appConfig);
    }
}