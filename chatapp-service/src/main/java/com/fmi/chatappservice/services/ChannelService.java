package com.fmi.chatappservice.services;

import com.fmi.chatappservice.data.ChannelMember;
import com.fmi.chatappservice.dto.ChannelDTO;
import com.fmi.chatappservice.dto.ChannelMemberDTO;
import com.fmi.chatappservice.dto.RoleDTO;
import com.fmi.chatappservice.dto.UserDTO;
import com.fmi.chatappservice.data.Channel;
import com.fmi.chatappservice.data.Role;
import com.fmi.chatappservice.data.User;
import com.fmi.chatappservice.repo.ChannelMemberRepository;
import com.fmi.chatappservice.repo.ChannelRepository;
import com.fmi.chatappservice.repo.RoleRepository;
import com.fmi.chatappservice.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public ChannelService(ChannelRepository channelRepository, UserRepository userRepository,
                          ChannelMemberRepository channelMemberRepository, RoleRepository roleRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.channelMemberRepository = channelMemberRepository;
        this.roleRepository = roleRepository;
    }

    public ChannelDTO createChannel(Long userId, ChannelDTO channelRequest) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        Channel channel = new Channel();
        channel.setName(channelRequest.getName());
        channel.setIsDeleted(false);
        channel = channelRepository.save(channel);

        Role ownerRole = roleRepository.findByName("OWNER").orElse(null);
        if (ownerRole == null) {
            return null;
        }

        ChannelMember membership = new ChannelMember();
        membership.setChannel(channel);
        membership.setUser(user);
        membership.setRole(ownerRole);
        channelMemberRepository.save(membership);

        return mapToChannelDTO(channel);
    }

    public void editChannelName(Long ownerId, Long channelId, String newName) {
        Channel channel = channelRepository.findById(channelId).orElse(null);
        if (channel == null) {
            return;
        }

        Role ownerRole = roleRepository.findByName("OWNER").orElse(null);
        if (ownerRole == null) {
            return;
        }

        boolean isOwner = channelMemberRepository.existsByChannelIdAndUserIdAndRoleId(
                channelId, ownerId, ownerRole.getId());
        if (!isOwner) {
            return;
        }

        if (newName == null || newName.trim().isEmpty()) {
            return;
        }

        channel.setName(newName.trim());
        channelRepository.save(channel);
    }

    public List<ChannelDTO> getUserChannels(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Collections.emptyList();
        }

        return channelMemberRepository.findByUserId(userId)
                .stream()
                .filter(membership -> !membership.getChannel().getIsDeleted())
                .map(membership -> mapToChannelDTO(membership.getChannel(), membership.getRole().getId()))
                .collect(Collectors.toList());
    }

    public void deleteChannel(Long userId, Long channelId) {
        Channel channel = channelRepository.findById(channelId).orElse(null);
        if (channel == null) {
            return;
        }

        Role ownerRole = roleRepository.findByName("OWNER").orElse(null);
        if (ownerRole == null) {
            return;
        }

        boolean isOwner = channelMemberRepository.existsByChannelIdAndUserIdAndRoleId(
                channelId, userId, ownerRole.getId());
        if (!isOwner) {
            return;
        }

        channel.setIsDeleted(true);
        channelRepository.save(channel);
    }

    public void addGuestToChannel(Long userId, Long channelId, Long guestId) {
        Channel channel = channelRepository.findById(channelId).orElse(null);
        if (channel == null) {
            return;
        }

        Role ownerRole = roleRepository.findByName("OWNER").orElse(null);
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        if (ownerRole == null || adminRole == null) {
            return;
        }

        boolean hasPermission = channelMemberRepository.existsByChannelIdAndUserIdAndRoleId(
                channelId, userId, ownerRole.getId())
                || channelMemberRepository.existsByChannelIdAndUserIdAndRoleId(channelId, userId, adminRole.getId());

        if (!hasPermission) {
            return;
        }

        User guest = userRepository.findById(guestId).orElse(null);
        if (guest == null) {
            return;
        }

        boolean isAlreadyMember = channelMemberRepository.existsByChannelIdAndUserId(channelId, guestId);
        if (isAlreadyMember) {
            return;
        }

        Role guestRole = roleRepository.findByName("GUEST").orElse(null);
        if (guestRole == null) {
            return;
        }

        ChannelMember newMembership = new ChannelMember();
        newMembership.setChannel(channel);
        newMembership.setUser(guest);
        newMembership.setRole(guestRole);
        channelMemberRepository.save(newMembership);
    }

    public void removeGuestFromChannel(Long ownerId, Long channelId, Long guestId) {
        Channel channel = channelRepository.findById(channelId).orElse(null);
        if (channel == null) {
            return;
        }

        Role ownerRole = roleRepository.findByName("OWNER").orElse(null);
        if (ownerRole == null) {
            return;
        }

        boolean isOwner = channelMemberRepository.existsByChannelIdAndUserIdAndRoleId(
                channelId, ownerId, ownerRole.getId());
        if (!isOwner) {
            return;
        }

        Role guestRole = roleRepository.findByName("GUEST").orElse(null);
        if (guestRole == null) {
            return;
        }

        ChannelMember guestMembership = channelMemberRepository
                .findByChannelIdAndUserIdAndRoleId(channelId, guestId, guestRole.getId())
                .orElse(null);
        if (guestMembership == null) {
            return;
        }

        channelMemberRepository.delete(guestMembership);
    }

    public void promoteToAdmin(Long ownerId, Long channelId, Long guestId) {
        Channel channel = channelRepository.findById(channelId).orElse(null);
        if (channel == null) {
            return;
        }

        Role ownerRole = roleRepository.findByName("OWNER").orElse(null);
        if (ownerRole == null) {
            return;
        }

        boolean isOwner = channelMemberRepository.existsByChannelIdAndUserIdAndRoleId(
                channelId, ownerId, ownerRole.getId());
        if (!isOwner) {
            return;
        }

        Role guestRole = roleRepository.findByName("GUEST").orElse(null);
        if (guestRole == null) {
            return;
        }

        ChannelMember membership = channelMemberRepository.findByChannelIdAndUserIdAndRoleId(channelId, guestId, guestRole.getId())
                .orElse(null);
        if (membership == null) {
            return;
        }

        boolean isPromotingOwner = channelMemberRepository.existsByChannelIdAndUserIdAndRoleId(
                channelId, guestId, ownerRole.getId());
        if (isPromotingOwner) {
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        if (adminRole == null) {
            return;
        }

        membership.setRole(adminRole);
        channelMemberRepository.save(membership);
    }

    public List<ChannelMemberDTO> getChannelMembers(Long channelId) {
        return channelMemberRepository.findByChannelId(channelId)
                .stream()
                .map(this::mapToChannelMemberDTO)
                .collect(Collectors.toList());
    }

    private ChannelDTO mapToChannelDTO(Channel channel) {
        ChannelDTO dto = new ChannelDTO();
        dto.setId(channel.getId());
        dto.setName(channel.getName());
        dto.setIsDeleted(channel.getIsDeleted());
        return dto;
    }

    private ChannelDTO mapToChannelDTO(Channel channel, Long roleId) {
        ChannelDTO dto = new ChannelDTO();
        dto.setId(channel.getId());
        dto.setName(channel.getName());
        dto.setIsDeleted(channel.getIsDeleted());
        dto.setRoleId(roleId);
        return dto;
    }

    private UserDTO mapUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getIsDeleted(),
                null,
                null
        );
    }

    private RoleDTO mapToRoleDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setRoleName(role.getName());
        return dto;
    }

    private ChannelMemberDTO mapToChannelMemberDTO(ChannelMember channelMembership) {
        ChannelMemberDTO dto = new ChannelMemberDTO();
        dto.setChannel(mapToChannelDTO(channelMembership.getChannel()));
        dto.setUser(mapUserDTO(channelMembership.getUser()));
        dto.setRole(mapToRoleDTO(channelMembership.getRole()));
        return dto;
    }
}

