package com.fmi.chatappservice.repo;

import com.fmi.chatappservice.data.ChannelMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long> {
    List<ChannelMember> findByUserId(Long userId);
    Optional<ChannelMember> findByChannelIdAndUserIdAndRoleId(Long channelId, Long userId, Long roleId);
    List<ChannelMember> findByChannelId(Long channelId);
    boolean existsByChannelIdAndUserId(Long channelId, Long userId);
    boolean existsByChannelIdAndUserIdAndRoleId(Long channelId, Long userId, Long roleId);
}
