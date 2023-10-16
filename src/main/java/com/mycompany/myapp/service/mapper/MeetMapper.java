package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Meet;
import com.mycompany.myapp.dto.GetMeetDto;

public class MeetMapper {

    public static GetMeetDto fromEntity(Meet meet) {
        return GetMeetDto
            .builder()
            .id(meet.getId())
            .userName(meet.getUser().getLogin())
            .description(meet.getDescription())
            .isEnabled(meet.getIsEnabled())
            .build();
    }
}
