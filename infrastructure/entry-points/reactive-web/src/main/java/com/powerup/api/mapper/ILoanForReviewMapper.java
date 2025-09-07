package com.powerup.api.mapper;

import com.powerup.api.dto.response.LoanForReviewResponseDto;
import com.powerup.model.LoanForReview;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ILoanForReviewMapper {
    LoanForReviewResponseDto toResponseDto(LoanForReview loanForReview);
}