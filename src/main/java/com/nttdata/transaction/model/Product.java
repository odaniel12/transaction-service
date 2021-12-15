package com.nttdata.transaction.model;

import lombok.Data;

@Data
public class Product {

    private String id;
    private String typeProduct;
    private String nameProduct;
    private String idClient;
    private Double amount;
    private Integer transactionAmount;
    private Double limitCredit;

}
