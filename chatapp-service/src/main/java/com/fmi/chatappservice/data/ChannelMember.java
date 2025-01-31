package com.fmi.chatappservice.data;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"channel_id", "user_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChannelMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
