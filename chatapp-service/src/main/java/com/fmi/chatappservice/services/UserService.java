package com.fmi.chatappservice.services;

import com.fmi.chatappservice.dto.UserDTO;
import com.fmi.chatappservice.data.User;
import com.fmi.chatappservice.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        user.setIsDeleted(false);

        return mapToUserDTO(userRepository.save(user));
    }

    public UserDTO getUserByEmailAndPassword(String email, String password) {
        User user = userRepository.findUserByEmailAndPassword(email, password)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return mapToUserDTO(user);
    }

    public List<UserDTO> searchUsers(String email) {
        List<UserDTO> userDTOs;
        if (email != null && !email.isEmpty()) {
            List<User> users = userRepository.findByEmailContaining(email);
            userDTOs = users.stream()
                    .map(this::mapToUserDTO)
                    .collect(Collectors.toList());
        } else {
            List<User> users = userRepository.findAll();
            userDTOs = users.stream()
                    .map(this::mapToUserDTO)
                    .collect(Collectors.toList());
        }
        return userDTOs;
    }

    public UserDTO mapToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getIsDeleted(),
                user.getFriendshipsInitiated()
                        .stream()
                        .map(friendship -> mapFriendToDTO(friendship.getFriend()))
                        .collect(Collectors.toList()),
                user.getFriendshipsReceived()
                        .stream()
                        .map(friendship -> mapFriendToDTO(friendship.getCurrentUser()))
                        .collect(Collectors.toList())
        );
    }

    private UserDTO mapFriendToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getIsDeleted(),
                null,
                null
        );
    }
}