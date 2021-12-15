package com.nttdata.transaction.service;

import com.nttdata.transaction.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {

    public Flux<Transaction> findAllService();
    public Mono<Transaction> createTransactionService(Transaction transaction);

}
