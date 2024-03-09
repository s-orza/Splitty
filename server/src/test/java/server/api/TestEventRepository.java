package server.api;

import commons.Event;
import commons.Quote;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestEventRepository implements EventRepository {
    public final List<Event> events = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<Event> findAll() {
        calledMethods.add("findAll");
        return events;
    }


    @Override
    public List<Event> findAll(Sort sort) {
        calledMethods.add("findAll(Sort)");
        return events;
    }

    @Override
    public List<Event> findAllById(Iterable<Long> ids) {
        calledMethods.add("findAllById");
        List<Event> foundEvents = new ArrayList<>();
        for (Long id : ids) {
            for (Event event : events) {
                if (event.getEventId() == id) {
                    foundEvents.add(event);
                    break;
                }
            }
        }
        return foundEvents;
    }

    @Override
    public <S extends Event> List<S> saveAll(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub
    }

    @Override
    public <S extends Event> S saveAndFlush(S entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Event> List<S> saveAllAndFlush(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Event> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllInBatch() {
        // TODO Auto-generated method stub

    }

    @Override
    public Event getOne(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Event getById(Long id) {
        call("getById");
        return find(id).get();
    }

    @Override
    public Event getReferenceById(Long id) {
        call("getReferenceById");
        return find(id).get();
    }

    private Optional<Event> find(Long id) {
        for (Event event : events) {
            if (event.getEventId()==id) {
                return Optional.of(event);
            }
        }
        return Optional.empty();
    }

    @Override
    public <S extends Event> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Event> List<S> findAll(Example<S> example, Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<Event> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

        @Override
        public <S extends Event> S save(S entity) {
            call("save");
            entity.setEventId((long) events.size());
            events.add(entity);
            return entity;
        }

    @Override
    public Optional<Event> findById(Long id) {
        for (Event event : events) {
            if (event.getEventId()==id) {
                return Optional.of(event);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        call("existsById");
        return find(id).isPresent();
    }

    @Override
    public long count() {
        return events.size();
    }

    @Override
    public void deleteById(Long id) {
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (event.getEventId() == id) {
                events.remove(i);
                return;
            }
        }
    }

    @Override
    public void delete(Event entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll(Iterable<? extends Event> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub

    }

    @Override
    public <S extends Event> Optional<S> findOne(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Event> Page<S> findAll(Example<S> example, Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends Event> long count(Example<S> example) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <S extends Event> boolean exists(Example<S> example) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <S extends Event, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        // TODO Auto-generated method stub
        return null;
    }
}