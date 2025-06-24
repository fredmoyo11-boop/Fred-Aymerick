package com.sep.backend.account.balance;

import com.sep.backend.HttpStatus;
import com.sep.backend.StringResponse;
import com.sep.backend.Tags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
            tags = {Tags.BALANCE},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "deposit successful",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})
    public StringResponse deposit(@RequestParam("amount") double amount, Principal principal) {
        balanceService.deposit(amount, principal);
        return new StringResponse("deposit successful");
    }

    @PostMapping("/withdrawal")
    @Operation(description = "Lets the user withdraw money from his account",
            tags = {Tags.BALANCE},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "withdrawal successful",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})
    public StringResponse withdraw(@RequestParam("amount") double amount, Principal principal) {
        balanceService.withdraw(amount, principal);
        return new StringResponse("withdraw successful");
    }

    @GetMapping("/history/current")
    @Operation(description = "Returns the previous transactions of the current account.",
            tags = {Tags.BALANCE},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Previous transactions retrieved successfully.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Transaction.class))))})
    public List<Transaction> getCurrentTransactions(Principal principal) {
        return balanceService.getCurrentTransactions(principal);
    }
}
