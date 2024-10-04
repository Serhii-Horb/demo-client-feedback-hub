package com.api.client_feedback_hub.mapper;

import com.api.client_feedback_hub.dto.FeedbackResponseDto;
import com.api.client_feedback_hub.model.Feedback;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public FeedbackResponseDto convertToDto(Feedback feedback) {
        return modelMapper.map(feedback, FeedbackResponseDto.class);
    }

}