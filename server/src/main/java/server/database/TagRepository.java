package server.database;

import commons.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TagRepository extends JpaRepository<Tag, String> {
    @Query("SELECT t FROM Tag t ORDER BY t.name")

    List<Tag> getAllTags();
}
