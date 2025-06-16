package com.sep.backend.account.transaction;

import com.sep.backend.HttpStatus;
import com.sep.backend.StringResponse;
import com.sep.backend.Tags;
import com.sep.backend.entity.DriverEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/balance", produces = MediaType.APPLICATION_JSON_VALUE)
public class BalanceController {

     public final BalanceService balanceService;


    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }


    @PostMapping("/deposit")
    @Operation(description = "Lets the user deposit money on his account",
            tags = {Tags.TRANSACTION},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "deposit successful",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})
    public StringResponse deposit(@RequestParam("amount") double amount, Principal principal) {
        balanceService.deposit(amount, principal);
        return new StringResponse("deposit successful");
    }

    @PostMapping("/withdrawal")
    @Operation(description = "Lets the user withdraw money from his account",
            tags = {Tags.TRANSACTION},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "withdrawal successful",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})
    public StringResponse withdraw(@RequestParam("amount") double amount, Principal principal) {
        balanceService.withdraw(amount, principal);
        return new StringResponse("withdraw successful");
    }

    @PostMapping("/transaction")
    @Operation(description = "Lets the user transact money to a driver",
            tags = {Tags.TRANSACTION},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "transfer successful",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})
    public StringResponse transaction(@RequestParam("amount")double amount, Principal principal,@RequestBody DriverEntity driver) {
        balanceService.transfer(amount, principal, driver);
        return new StringResponse("transfer successful");
    }
    @PostMapping("/history")
    @Operation(description = "shows history of transactions",
            tags = {Tags.TRANSACTION},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "history retrieve successful",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})
    public StringResponse getHistory(Principal principal){
         balanceService.getHistory(principal);
         return new StringResponse("history transactions retrieve successful");
    }
}
