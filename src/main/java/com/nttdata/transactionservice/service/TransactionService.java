package com.nttdata.transactionservice.service;

import com.nttdata.transactionservice.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {

    public Flux<Transaction> findAllService();
    public Mono<Transaction> createTransactionService(Transaction transaction);

}
