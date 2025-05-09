package com.sep.backend.account;

import com.sep.backend.HttpStatus;
import com.sep.backend.StringResponse;
import com.sep.backend.Tags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @GetMapping("/health")
    @Operation(description = "Returns the status of the account controller.",
            tags = {Tags.ACCOUNT},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Account controller healthy.",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})

    public StringResponse health() {
        return new StringResponse("OK");
    }


    @Operation(description = "Suche nach Benutzerprofilen basierend auf einem Suchbegriff ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of userprofile.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class)))})
    @GetMapping("/search")
    public ResponseEntity<List<AccountDTO>> userSearch(@RequestParam String part) {

        return ResponseEntity.ok(accountService.userSearch(part));
    }


    @Operation(summary = "Aktualisiert das Benutzerprofil", description = "Aktualisiert das Profil eines Benutzers basierend auf dem aktuellen Benutzernamen, der in der URL angegeben wird. Die neuen Profildaten  werden im JSON-Format(außer Bild) im Anfrage body übergeben."
    )
    @ApiResponse(responseCode = "200", description = "Profil erfolgreich aktualisiert.",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "String")))
    @IsOwner
    @PutMapping(value ="/{username}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateAccount(@PathVariable String username, @RequestPart("data") UpdateAccountDTO updateAccountDTO, @RequestPart(value = "file", required = false) MultipartFile file) {

        accountService.updateAccount(username, updateAccountDTO, file);
        return ResponseEntity.ok("Profil erfolgreich aktualisiert!");
    }


    @Operation(summary = "Gibt das Benutzerprofil zurück", description = "Liefert die vollständigen Profildaten eines Benutzers basierend auf dem Benutzernamen."
    )
    @ApiResponse(responseCode = "200", description = "Benutzerprofil erfolgreich geladen.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class)
            )
    )
    @GetMapping("/{username}")
    public ResponseEntity<AccountDTO> getAccountprofil(@PathVariable String username) {
        return ResponseEntity.ok(accountService.getAccountprofil(username));
    }
}
