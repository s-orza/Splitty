package commons;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Tag {
    @Id
    @Column
    private String name;
    @Column
    private String color;

    public Tag(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Tag() {

    }


    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Tag{"+name+", "+color+"}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(getName(), tag.getName()) && Objects.equals(getColor(), tag.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getColor());
    }
}
