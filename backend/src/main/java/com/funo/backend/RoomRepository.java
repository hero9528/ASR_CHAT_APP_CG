package com.funo.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query(value = "SELECT * FROM rooms WHERE is_public = true ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Room findRandomPublicRoom();

    List<Room> findByIsPublic(boolean isPublic);
}