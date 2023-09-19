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
            .firstName(activity.getUser().getFirstName())
            .title(activity.getTitle())
            .description(activity.getDescription())
            .address(activity.getAddress())
            .date(activity.getDate())
            .maximum(activity.getMaximum())
            .build();
    }
}
