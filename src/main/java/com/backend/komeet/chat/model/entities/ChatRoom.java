package com.backend.komeet.chat.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import com.backend.komeet.user.model.entities.User;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    private User recipient;

    @Setter
    private Boolean isVisibleToSender;

    @Setter
    private Boolean isVisibleToRecipient;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Chat> chats;

    /**
     * 채팅방 팩토리 메서드
     */
    public static ChatRoom from(
            User sender,
            User recipient
    ) {
        return ChatRoom.builder()
                .sender(sender)
                .recipient(recipient)
                .isVisibleToRecipient(true)
                .isVisibleToSender(true)
                .build();
    }
}
