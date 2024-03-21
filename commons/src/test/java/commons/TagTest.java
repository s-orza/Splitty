package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {
    Tag a1;
    @BeforeEach
    void setUp() {
        a1=new Tag(new TagId("food",1),"#001234");
    }
    @Test
    void testC(){
        Tag tag=new Tag();
        assertEquals(tag.getId(),null);
    }

    @Test
    void getName() {
        assertEquals(a1.getId().getName(),"food");
    }

    @Test
    void getColor() {
        assertEquals(a1.getColor(),"#001234");
    }
    @Test
    void getEventId(){
        assertEquals(1,a1.getId().getEventId());
    }

    @Test
    void setColor() {
        a1.setColor("#000000");
        assertEquals("#000000",a1.getColor());
    }
    @Test
    void setEventId(){
        a1.getId().setEventId(2);
        assertEquals(2,a1.getId().getEventId());
    }
    @Test
    void testToString() {
        String s=a1.toString();
        assertEquals(s,"Tag{food, 1, #001234}");
    }
    @Test
    void testEquals() {
        Tag a2=new Tag(new TagId("food",1),"#001234");
        assertEquals(a1,a2);
    }

    @Test
    void testHashCode() {
        Tag a2=new Tag(new TagId("food",1),"#001234");
        assertEquals(a1.hashCode(),a2.hashCode());
    }
}