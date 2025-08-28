package com.powerup.r2dbc;

import com.powerup.model.loantype.LoanType;
import com.powerup.model.loantype.gateways.ILoanTypeRepositoryPort;
import com.powerup.r2dbc.entity.LoanTypeEntity;
import com.powerup.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;

@Repository
public class LoanTypeRepositoryAdapter extends ReactiveAdapterOperations<LoanType, LoanTypeEntity, Long, ILoanTypeRepository>  implements ILoanTypeRepositoryPort {
    public LoanTypeRepositoryAdapter(ILoanTypeRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, LoanType.class), transactionalOperator);
    }
}
