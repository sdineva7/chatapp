package com.fmi.chatappservice.controllers;

import com.fmi.chatappservice.dto.MessageDTO;
import com.fmi.chatappservice.services.MessageService;
import com.fmi.chatappservice.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/messages")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping()
    public ResponseEntity<?> sendMessage(
            @RequestBody MessageDTO messageDTO) {
        try {
            var message = messageService.sendMessage(messageDTO);
            return Response.buildResponse(HttpStatus.CREATED, message);
        } catch (IllegalArgumentException ex) {
            return Response.buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/channels/{channelId}/messages")
    public ResponseEntity<?> getMessagesByChannel(@PathVariable Long channelId) {
        try {
            var messages = messageService.getMessagesByChannel(channelId);
            return Response.buildResponse(HttpStatus.OK, messages);
        } catch (IllegalArgumentException ex) {
            return Response.buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
