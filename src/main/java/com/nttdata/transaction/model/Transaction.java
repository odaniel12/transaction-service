package com.nttdata.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "transaction")
public class Transaction {

    @Id
    private String transactionId;
    private String productOriginId;
    private String productDestinationId;
    private Double amount;
    private String transactionType;
    private Date registrationDate;

}
