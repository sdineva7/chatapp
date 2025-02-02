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
            WHERE (f.currentUser.email = :user1 AND f.friend.email = :user2)
            """)
    Optional<Friendship> findExistingFriendship(String user1, String user2);
}
