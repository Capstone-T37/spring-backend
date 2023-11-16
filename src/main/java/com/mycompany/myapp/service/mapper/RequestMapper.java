package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Activity;
import com.mycompany.myapp.domain.Request;
import com.mycompany.myapp.dto.GetActivityDto;
import com.mycompany.myapp.dto.GetRequestDto;
import org.springframework.stereotype.Service;

@Service
public class RequestMapper {

    public static GetRequestDto fromEntity(Request request) {
        return GetRequestDto.builder().id(request.getId()).userName(request.getUser().getLogin()).build();
    }
}
