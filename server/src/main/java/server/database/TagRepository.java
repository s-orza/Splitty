package server.database;

import commons.Tag;
import commons.TagId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, TagId> {
    @Query("SELECT t FROM Tag t WHERE t.id.eventId=:eventId ORDER BY t.id.name")

    List<Tag> getAllTagsFromEvent(long eventId);
    @Query("SELECT t FROM Tag t WHERE t.id.name=:id AND t.id.eventId=:eventId ")
    Tag getTagByIdFromEvent(String id,long eventId);
    @Transactional
    @Modifying
    @Query("UPDATE Tag t SET t.id.name=:newName, t.color=:newColor WHERE t.id.name=:name "+
            "AND t.id.eventId=:eventId")
    Integer updateTag(String name,long eventId,String newName,String newColor);
}
