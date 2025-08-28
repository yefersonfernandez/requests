package com.powerup.api.mapper;

import com.powerup.api.dto.request.LoanRequestDto;
import com.powerup.api.dto.response.LoanResponseDto;
import com.powerup.model.loan.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ILoanMapper {
    LoanResponseDto toLoanResponseDto(Loan loan);
    Loan toModel(LoanRequestDto loanRequestDto);
}
