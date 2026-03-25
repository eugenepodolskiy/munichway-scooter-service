package com.example.munichway.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TopUpRequest {

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be strictly positive")
    private Double amount;

    public @NotNull(message = "Amount cannot be null") @Positive(message = "Amount must be strictly positive") Double getAmount() {
        return amount;
    }

    public void setAmount(@NotNull(message = "Amount cannot be null") @Positive(message = "Amount must be strictly positive") Double amount) {
        this.amount = amount;
    }
}