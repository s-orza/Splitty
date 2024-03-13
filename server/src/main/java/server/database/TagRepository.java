package server.database;

import commons.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, String> {
    @Query("SELECT name, color FROM Tag")

    List<Tag> getAllTags();
}
