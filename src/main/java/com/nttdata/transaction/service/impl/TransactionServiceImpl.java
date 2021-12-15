package com.nttdata.transaction.service.impl;

import com.nttdata.transaction.model.Product;
import com.nttdata.transaction.model.Transaction;
import com.nttdata.transaction.repository.TransactionRepository;
import com.nttdata.transaction.service.TransactionService;
import com.nttdata.transaction.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Calendar;
import java.util.Date;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override public Flux<Transaction> findAllService() {
	return transactionRepository.findAll();
    }

    @Override
    public Mono<Transaction> createTransactionService(Transaction transaction) {

        if (transaction.getProductOriginId().equals(transaction.getProductDestinationId()) && transaction.getTransactionType().equals(Constant.TYPE_TRANSACTION_TRANSFER)) {
            return Mono.error(new Exception("Las cuentas de origen y destino no pueden ser las mismas"));
	}
		Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date tempDate = cal.getTime();
        cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - 5);
        tempDate = cal.getTime();
        transaction.setRegistrationDate(tempDate);

	return findProductById(transaction.getProductOriginId())
			.flatMap(prodOrigin -> {
			    Double amount;

			    if (transaction.getTransactionType().equals(Constant.TYPE_TRANSACTION_RETIREMENT)) {
				if (transaction.getAmount() > prodOrigin.getAmount()) {
				    return Mono.error(new Exception("No dispone con saldo suficiente para RETIRO"));
				}else {
				    amount = prodOrigin.getAmount() - transaction.getAmount();
				    prodOrigin.setAmount(amount);
				    return updateProduct(prodOrigin).flatMap(x -> {
					return transactionRepository.save(transaction);
				    });

				}
			    }else if (transaction.getTransactionType().equals(Constant.TYPE_TRANSACTION_DEPOSIT)) {
				amount = prodOrigin.getAmount() + transaction.getAmount();
				prodOrigin.setAmount(amount);
				return updateProduct(prodOrigin).flatMap(x -> {
				    return transactionRepository.save(transaction);
				});

			    }else if (transaction.getTransactionType().equals(Constant.TYPE_TRANSACTION_TRANSFER)) {
				if (transaction.getAmount() > prodOrigin.getAmount()) {
				    return Mono.error(new Exception("No dispose con saldo suficiente para TRANSFERENCIA"));
				}else {
				    return findProductById(transaction.getProductDestinationId())
						    .flatMap(prodDestination -> {

							Double amountOrigin;
							amountOrigin = prodOrigin.getAmount() - transaction.getAmount();
							prodOrigin.setAmount(amountOrigin);

							return updateProduct(prodOrigin).flatMap( x -> {
							    Double amountDestination;
							    amountDestination = prodDestination.getAmount() + transaction.getAmount();
							    prodDestination.setAmount(amountDestination);
							    return updateProduct(prodDestination).flatMap(y -> {
								return transactionRepository.save(transaction);
							    });
							});


						    }).switchIfEmpty(Mono.error(new Exception("Producto de destino no existe")));

				}
			    }else {
				return Mono.error(new Exception("Tipo de transaccion invalida"));
			    }
			})
			.switchIfEmpty(Mono.error(new Exception("Producto no existe")));
    }

    public Mono<Product> findProductById(String id) {

	String url = "http://localhost:8089/product/findById/" + id;

	return WebClient.create()
			.get()
			.uri(url)
			.retrieve()
			.bodyToMono(Product.class);
    }

    public Mono<Product> updateProduct(Product product) {

	String url = "http://localhost:8089/product/update";

	return WebClient.create()
			.put()
			.uri(url)
			.body(Mono.just(product), Product.class)
			.retrieve()
			.bodyToMono(Product.class);
    }
}
