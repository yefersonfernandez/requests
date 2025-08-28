package com.powerup.consumer;

import com.powerup.consumer.dto.user.response.UserResponseDto;
import com.powerup.port.consumer.model.UserConsumer;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface UserConsumerMapper {
    UserConsumer toUserConsumer(UserResponseDto userResponseDto);
}
