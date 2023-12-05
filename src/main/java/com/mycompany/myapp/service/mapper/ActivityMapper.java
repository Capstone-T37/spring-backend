package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Activity;
import com.mycompany.myapp.dto.GetActivityDto;
import org.springframework.stereotype.Service;

@Service
public class ActivityMapper {

    public static GetActivityDto fromEntity(Activity activity) {
        return GetActivityDto
            .builder()
            .id(activity.getId())
            .userName(activity.getUser().getLogin())
            .title(activity.getTitle())
            .description(activity.getDescription())
            .date(activity.getDate())
            .imageUrl(activity.getUser().getImageUrl())
            .build();
    }
}
