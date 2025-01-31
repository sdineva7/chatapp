package com.fmi.chatappservice.repo;


import com.fmi.chatappservice.data.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("""
            SELECT f
            FROM Friendship f
            WHERE (f.currentUser.id = :userId2 AND f.friend.id = :userId1)
            """)
    Optional<Friendship> findExistingFriendship(Long userId1, Long userId2);
}
