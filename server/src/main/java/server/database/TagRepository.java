package server.database;

import commons.Tag;
import commons.TagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, TagId> {
    @Query("SELECT t FROM Tag t WHERE t.id.eventId=:eventId ORDER BY t.id.name")

    List<Tag> getAllTagsFromEvent(long eventId);
    @Query("SELECT t FROM Tag t WHERE t.id.name=:id AND t.id.eventId=:eventId ")
    Tag getTagByIdFromEvent(String id,long eventId);

}
