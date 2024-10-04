package com.api.client_feedback_hub.mapper;

import com.api.client_feedback_hub.dto.UserResponseDto;
import com.api.client_feedback_hub.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public UserResponseDto convertToDto(User user) {
        return modelMapper.map(user, UserResponseDto.class);
    }

}
