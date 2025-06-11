package com.sep.backend.account.transaction;

import com.sep.backend.HttpStatus;
import com.sep.backend.StringResponse;
import com.sep.backend.Tags;
import com.sep.backend.entity.DriverEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value = "/api/balance", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {

     public final TransactionService transactionService;


    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @PostMapping("/deposit")
    @Operation(description = "Lets the user deposit money on his account",
            tags = {Tags.TRANSACTION},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "deposit successful",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})
    public StringResponse deposit(@RequestParam("amount") double amount, Principal principal) {
        transactionService.deposit(amount, principal);
        return new StringResponse("deposit successful");
    }

    @PostMapping("/withdrawal")
    @Operation(description = "Lets the user withdraw money from his account",
            tags = {Tags.TRANSACTION},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "withdrawal successful",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})
    public StringResponse withdraw(@RequestParam("amount") double amount, Principal principal) {
        transactionService.withdraw(amount, principal);
        return new StringResponse("withdraw successful");
    }

    @PostMapping("/transaction")
    @Operation(description = "Lets the user transact money to a driver",
            tags = {Tags.TRANSACTION},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "transaction successful",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})
    public StringResponse transaction(@RequestParam("amount")double amount, Principal principal,@RequestBody DriverEntity driver) {
        transactionService.transaction(amount, principal, driver);
        return new StringResponse("transaction successful");
    }
}
