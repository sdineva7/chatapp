package com.fmi.chatappservice.services;


import com.fmi.chatappservice.dto.ChannelDTO;
import com.fmi.chatappservice.data.*;
import com.fmi.chatappservice.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository, ChannelRepository channelRepository, ChannelMemberRepository channelMemberRepository, RoleRepository roleRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.channelMemberRepository = channelMemberRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public ChannelDTO addFriend(Long currentUserId, Long friendId) {
        var existingRelation = friendshipRepository.findExistingFriendship(currentUserId, friendId);

        if (existingRelation.isPresent()) {
            Friendship existingFriendship = existingRelation.get();
            var sharedChannel = existingFriendship.getChannel();
            var hasFriendInitiatedFriendship = existingFriendship.getCurrentUser().getId().equals(friendId);
            var hasCurrentUserInitiatedFriendShip = friendshipRepository.findExistingFriendship(friendId, currentUserId).isPresent();

            if (hasFriendInitiatedFriendship && !hasCurrentUserInitiatedFriendShip) {
                var currentUser = userRepository.findById(currentUserId).orElseThrow();
                var friend = userRepository.findById(friendId).orElseThrow();

                ensureMembership(sharedChannel, currentUser);
                ensureMembership(sharedChannel, friend);

                // To ensure availability in user.friendshipsInitiated
                var newFriendship = new Friendship();
                newFriendship.setCurrentUser(currentUser);
                newFriendship.setFriend(friend);
                newFriendship.setChannel(sharedChannel);
                friendshipRepository.save(newFriendship);
            }

            return mapToChannelDTO(existingFriendship.getChannel());

        } else {
            // No friendship at all
            User currentUser = userRepository.findById(currentUserId).orElseThrow();
            User friend = userRepository.findById(friendId).orElseThrow();

            Channel channel = new Channel();
            channel.setIsDeleted(false);
            String channelName = currentUserId < friendId
                    ? (currentUserId + "-" + friendId)
                    : (friendId + "-" + currentUserId);
            channel.setName(channelName);
            channelRepository.save(channel);

            Friendship friendship = new Friendship();
            friendship.setCurrentUser(currentUser);
            friendship.setFriend(friend);
            friendship.setChannel(channel);
            friendshipRepository.save(friendship);

            Role guestRole = roleRepository.findByRoleName("GUEST").orElseThrow();
            addMembership(channel, currentUser, guestRole);
            addMembership(channel, friend, guestRole);

            return mapToChannelDTO(channel);
        }
    }

    private void ensureMembership(Channel channel, User user) {
        boolean alreadyMember = channelMemberRepository
                .findAll()
                .stream()
                .anyMatch(m -> m.getChannel().getId().equals(channel.getId())
                        && m.getUser().getId().equals(user.getId()));
        if (!alreadyMember) {
            Role guestRole = roleRepository.findByRoleName("GUEST").orElseThrow();
            addMembership(channel, user, guestRole);
        }
    }

    private void addMembership(Channel channel, User user, Role role) {
        ChannelMember channelMember = new ChannelMember();
        channelMember.setChannel(channel);
        channelMember.setUser(user);
        channelMember.setRole(role);
        channelMemberRepository.save(channelMember);
    }

    public ChannelDTO mapToChannelDTO(Channel channel) {
        ChannelDTO dto = new ChannelDTO();
        dto.setId(channel.getId());
        dto.setName(channel.getName());
        dto.setIsDeleted(channel.getIsDeleted());
        return dto;
    }
}
