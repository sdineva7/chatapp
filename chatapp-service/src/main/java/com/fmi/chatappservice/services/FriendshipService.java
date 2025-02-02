package com.fmi.chatappservice.services;

import com.fmi.chatappservice.dto.ChannelDTO;
import com.fmi.chatappservice.data.*;
import com.fmi.chatappservice.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository,
                             ChannelRepository channelRepository, ChannelMemberRepository channelMemberRepository,
                             RoleRepository roleRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.channelMemberRepository = channelMemberRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public ChannelDTO addFriend(Long currentUserId, Long friendId) {
        User currentUser = userRepository.getUserById(currentUserId);
        User friend = userRepository.getUserById(friendId);
        Optional<Friendship> existingFriendshipOpt = friendshipRepository.findExistingFriendship(currentUser.getEmail(), friend.getEmail());

        return existingFriendshipOpt.map(friendship -> handleExistingFriendship(friendship, currentUser.getEmail(), friend.getEmail())).orElseGet(() -> createNewFriendship(currentUser.getEmail(), friend.getEmail(    )));

    }

    private ChannelDTO handleExistingFriendship(Friendship existingFriendship, String currentUserEmail, String friendEmail) {
        Channel sharedChannel = existingFriendship.getChannel();
        boolean isFriendInitiator = existingFriendship.getCurrentUser().getEmail().equals(friendEmail);
        boolean isCurrentUserAlreadyFriend = friendshipRepository.findExistingFriendship(currentUserEmail, friendEmail).isPresent();

        if (isFriendInitiator && !isCurrentUserAlreadyFriend) {
            User currentUser = getUserById(existingFriendship.getCurrentUser().getId());
            User friend = getUserById(existingFriendship.getFriend().getId());

            ensureMembership(sharedChannel, currentUser);
            ensureMembership(sharedChannel, friend);

            saveFriendship(currentUser, friend, sharedChannel);
        }

        return mapToChannelDTO(sharedChannel, null);
    }

    private ChannelDTO createNewFriendship(String currentUserEmail, String friendEmail) {
        User currentUser = userRepository.getUserByEmail((currentUserEmail));
        User friend = userRepository.getUserByEmail(friendEmail);

        Channel channel = createChannel(currentUser.getEmail(), friend.getEmail());
        saveFriendship(currentUser, friend, channel);

        Role guestRole = getGuestRole();
        addMembership(channel, currentUser, guestRole);
        addMembership(channel, friend, guestRole);

        return mapToChannelDTO(channel, guestRole.getId());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    private Channel createChannel(String user1, String user2) {
        Channel channel = new Channel();
        channel.setIsDeleted(false);
        channel.setName(generateChannelName(user1, user2));
        return channelRepository.save(channel);
    }

    private String generateChannelName(String  user1, String user2) {
        return user1 + "-" + user2;
    }

    private void saveFriendship(User user, User friend, Channel channel) {
        Friendship friendship = new Friendship();
        friendship.setCurrentUser(user);
        friendship.setFriend(friend);
        friendship.setChannel(channel);
        friendshipRepository.save(friendship);
    }

    private void ensureMembership(Channel channel, User user) {
        boolean isAlreadyMember = channelMemberRepository.findAll()
                .stream()
                .anyMatch(m -> m.getChannel().getId().equals(channel.getId()) &&
                        m.getUser().getId().equals(user.getId()));

        if (!isAlreadyMember) {
            addMembership(channel, user, getGuestRole());
        }
    }

    private void addMembership(Channel channel, User user, Role role) {
        ChannelMember channelMember = new ChannelMember();
        channelMember.setChannel(channel);
        channelMember.setUser(user);
        channelMember.setRole(role);
        channelMemberRepository.save(channelMember);
    }

    private Role getGuestRole() {
        return roleRepository.findByName("GUEST")
                .orElseThrow(() -> new IllegalArgumentException("Guest role not found"));
    }

    private ChannelDTO mapToChannelDTO(Channel channel, Long roleId) {
        return new ChannelDTO(channel.getId(), channel.getName(), channel.getIsDeleted(), roleId);
    }
}
