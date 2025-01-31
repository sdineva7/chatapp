package com.fmi.chatappservice.services;

import com.fmi.chatappservice.data.Channel;
import com.fmi.chatappservice.dto.MessageDTO;
import com.fmi.chatappservice.dto.UserDTO;
import com.fmi.chatappservice.data.Message;
import com.fmi.chatappservice.data.User;
import com.fmi.chatappservice.repo.ChannelRepository;
import com.fmi.chatappservice.repo.MessageRepository;
import com.fmi.chatappservice.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    public MessageDTO sendMessage(MessageDTO messageDTO) {
        Channel channel = channelRepository.findById(messageDTO.getChannelId())
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));
        User user = userRepository.findById(messageDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setChannel(channel);
        message.setUser(user);
        message = messageRepository.save(message);

        return mapToMessageDTO(message);
    }

    public List<MessageDTO> getMessagesByChannel(Long channelId) {
        List<Message> messages = messageRepository.findByChannelIdOrderByTimestampAsc(channelId);
        return messages.stream()
                .map(this::mapToMessageDTO)
                .collect(Collectors.toList());
    }

    private MessageDTO mapToMessageDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getContent(),
                message.getTimestamp(),
                message.getChannel().getId(),
                message.getUser().getId(),
                mapUserDTO(message.getUser())
        );
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
}

