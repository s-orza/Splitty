package commons;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Tag   {
    @EmbeddedId
    @Column
    private TagId id;
    @Column
    private String color;
    public Tag(TagId id, String color) {
        this.id = id;
        this.color = color;
    }

    public Tag() {
    }

    public TagId getId(){
        return id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setId(TagId id) {
        this.id = id;
    }

    @Override
    public String toString() {
        if(id==null)
            return "Tag->null+"+color;
        return "Tag{"+id.getName()+", "+id.getEventId()+", "+color+"}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id) && Objects.equals(getColor(), tag.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getColor());
    }
}
