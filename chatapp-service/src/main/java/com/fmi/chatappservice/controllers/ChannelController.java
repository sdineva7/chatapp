package com.fmi.chatappservice.controllers;

import com.fmi.chatappservice.dto.ChannelDTO;
import com.fmi.chatappservice.services.ChannelService;
import com.fmi.chatappservice.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/channels")
public class ChannelController {
    private final ChannelService channelService;

    @Autowired
    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping()
    public ResponseEntity<?> createChannel(@RequestParam Long userId, @RequestBody ChannelDTO channelRequest) {
        try {
            ChannelDTO result = channelService.createChannel(userId, channelRequest);
            return Response.buildResponse(HttpStatus.CREATED, result);
        } catch (IllegalArgumentException ex) {
            return Response.buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping()
    public ResponseEntity<?> editChannelName(
            @RequestParam Long ownerId,
            @RequestBody ChannelDTO channel) {
        try {
            channelService.editChannelName(ownerId, channel.getId(), channel.getName());
            return Response.buildResponse(HttpStatus.OK, "Channel name updated successfully");
        } catch (IllegalArgumentException ex) {
            return Response.buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<?> getUserChannels(@PathVariable Long ownerId) {
        try {
            var channels = channelService.getUserChannels(ownerId);
            return Response.buildResponse(HttpStatus.OK, channels);
        } catch (IllegalArgumentException ex) {
            return Response.buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/{channelId}/members")
    public ResponseEntity<?> getChannelMembers(@PathVariable Long channelId) {
        try {
            var members = channelService.getChannelMembers(channelId);
            return Response.buildResponse(HttpStatus.OK, members);
        } catch (IllegalArgumentException ex) {
            return Response.buildResponse(HttpStatus.NOT_FOUND, "Channel not found");
        } catch (Exception ex) {
            return Response.buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<?> deleteChannel(@PathVariable Long channelId, @RequestParam Long userId) {
        try {
            channelService.deleteChannel(userId, channelId);
            return Response.buildResponse(HttpStatus.OK, "Channel deleted successfully");
        } catch (IllegalArgumentException ex) {
            return Response.buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("/{channelId}/add-guest")
    public ResponseEntity<?> addGuestToChannel(
            @PathVariable Long channelId,
            @RequestParam Long userId,
            @RequestParam Long guestId) {
        try {
            channelService.addGuestToChannel(userId, channelId, guestId);
            return Response.buildResponse(HttpStatus.CREATED, "Guest added successfully");
        } catch (IllegalArgumentException ex) {
            return Response.buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @DeleteMapping("/{channelId}/remove-guest")
    public ResponseEntity<?> removeGuestFromChannel(
            @PathVariable Long channelId,
            @RequestParam Long ownerId,
            @RequestParam Long guestId) {
        try {
            channelService.removeGuestFromChannel(ownerId, channelId, guestId);
            return Response.buildResponse(HttpStatus.OK, "Guest removed successfully");
        } catch (IllegalArgumentException ex) {
            return Response.buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("/{channelId}/promote-to-admin")
    public ResponseEntity<?> promoteToAdmin(
            @PathVariable Long channelId,
            @RequestParam Long ownerId,
            @RequestParam Long guestId) {
        try {
            channelService.promoteToAdmin(ownerId, channelId, guestId);
            return Response.buildResponse(HttpStatus.OK, "User promoted to ADMIN successfully");
        } catch (IllegalArgumentException ex) {
            return Response.buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
