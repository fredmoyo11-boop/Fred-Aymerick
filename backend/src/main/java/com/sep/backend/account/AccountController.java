package com.sep.backend.account;

import com.sep.backend.HttpStatus;
import com.sep.backend.StringResponse;
import com.sep.backend.Tags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    @GetMapping("/health")
    @Operation(description = "Returns the status of the account controller.",
            tags = {Tags.ACCOUNT},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Account controller healthy.",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})
    public StringResponse health() {
        return new StringResponse("OK");
    }

}
