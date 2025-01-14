package com.example.demo.model;



import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory_transactions")
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    private int quantity;

    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private String suppliedTo;
    @Column(nullable = true)
    private Double sellingPrice;
    private int quantityOnHand;

    public enum TransactionType {
        INBOUND,   // stock addition
        OUTBOUND   // stock reduction
    }
}

