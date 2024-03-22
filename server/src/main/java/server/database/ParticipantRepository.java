/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.database;

import commons.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    // Fetch one participant by ID
    @Query("SELECT p FROM Participant p WHERE p.id = :id")
    Optional<Participant> findById(@Param("id") Long id);

    // Fetch one or more participants by name
    @Query("SELECT p FROM Participant p WHERE p.name = :name")
    List<Participant> findByName(@Param("name") String name);

    // Fetch all participants
    @Query("SELECT p FROM Participant p")
    List<Participant> findAllParticipants();
}