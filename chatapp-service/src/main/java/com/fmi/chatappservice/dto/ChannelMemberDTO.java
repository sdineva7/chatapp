package com.fmi.chatappservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelMemberDTO {
    private ChannelDTO channel;
    private UserDTO user;
    private RoleDTO role;
}
