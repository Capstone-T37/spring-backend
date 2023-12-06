package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Conversation;
import com.mycompany.myapp.domain.Meet;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.dto.GetConversationDto;
import com.mycompany.myapp.dto.GetMeetDto;

public class ConversationMapper {

    public static GetConversationDto fromEntity(Conversation conversation, User user) {
        return GetConversationDto.builder().id(conversation.getId()).userName(user.getLogin()).imageUrl(user.getImageUrl()).build();
    }
}
