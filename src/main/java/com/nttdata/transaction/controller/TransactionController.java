package com.nttdata.transaction.controller;

import com.nttdata.transaction.model.Transaction;
import com.nttdata.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService saveProductService;

    @GetMapping("/findAll")
    private Flux<Transaction> findAllController() {
	return saveProductService.findAllService();
    }

    @PostMapping("/save")
    private Mono<Transaction> saveController(@RequestBody Transaction transaction) {
	return saveProductService.createTransactionService(transaction);
    }

}
