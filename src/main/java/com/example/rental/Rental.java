package com.example.rental;

import javax.persistence.*;

import com.example.rental.external.Payment;
import com.example.rental.external.PaymentService;

import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Rental_table")
public class Rental {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long customerId;
    private Long productId;
    private int amt;
    private String address;
    private String status;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getAmt() {
        return this.amt;
    }

    public void setAmt(int amt) {
        this.amt = amt;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    
    
    // 1. pub/sub start
    // @PostPersist
    // public void onPostPersist(){
    //     RentalPlaced rentalPlaced = new RentalPlaced();
    //     BeanUtils.copyProperties(this, rentalPlaced);
    //     rentalPlaced.publishAfterCommit();


    // }
    // 1. pub/sub end

    // 2. req/res start
    @PostPersist
    public void callPaymentStart(){
        Payment payment = new Payment();
        payment.setRentalId(this.getId());
        payment.setProductId(this.getProductId());        
        payment.setStatus("Req/Res PAYMENT COMPLETED");
        payment.setAmt(this.getAmt());
        
        // start payment
        PaymentService paymentService = RentalApplication.applicationContext.getBean(PaymentService.class);
        paymentService.startPayment(payment);
    }
    // 2. req/res end

    @PreRemove
    public void onPreRemove(){
        RentalCanceled rentalCanceled = new RentalCanceled();
        BeanUtils.copyProperties(this, rentalCanceled);
        rentalCanceled.publishAfterCommit();


    }

    

}
