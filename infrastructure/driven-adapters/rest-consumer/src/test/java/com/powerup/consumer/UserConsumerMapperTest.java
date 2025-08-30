package com.powerup.consumer;

import com.powerup.consumer.dto.user.response.UserResponseDto;
import com.powerup.port.consumer.model.UserConsumer;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserConsumerMapperTest {

    private final UserConsumerMapper mapper = Mappers.getMapper(UserConsumerMapper.class);

    @Test
    void testToUserConsumer() {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(1L);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setBirthDate(LocalDate.of(1990, 5, 20));
        dto.setAddress("123 Main St");
        dto.setPhone("1234567890");
        dto.setIdentityDocument("ID12345");
        dto.setEmail("john.doe@test.com");
        dto.setBaseSalary(BigDecimal.valueOf(3500.50));

        UserConsumer consumer = mapper.toUserConsumer(dto);

        assertNotNull(consumer);
        assertEquals(dto.getId(), consumer.getId());
        assertEquals(dto.getFirstName(), consumer.getFirstName());
        assertEquals(dto.getLastName(), consumer.getLastName());
        assertEquals(dto.getBirthDate(), consumer.getBirthDate());
        assertEquals(dto.getAddress(), consumer.getAddress());
        assertEquals(dto.getPhone(), consumer.getPhone());
        assertEquals(dto.getIdentityDocument(), consumer.getIdentityDocument());
        assertEquals(dto.getEmail(), consumer.getEmail());
        assertEquals(dto.getBaseSalary(), consumer.getBaseSalary());
    }
}
