package com.sep.backend.account;

import com.sep.backend.HttpStatus;
import com.sep.backend.StringResponse;
import com.sep.backend.Tags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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


    @Operation(description = "Suche nach Benutzerprofilen basierend auf einem Suchbegriff ( username-part)",
            tags = {Tags.ACCOUNT},
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of userprofile.",
                                 content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccountDTO.class)) ))})
    @GetMapping("/search")
    public ResponseEntity<List<AccountDTO>> SearchUserProfiles(@RequestParam String part) {
        return ResponseEntity.ok(accountService.SearchUser(part));
    }

    @Operation(summary = "Aktualisiert das Benutzerprofil", description = "Profildaten werden als multipart/form-data gesendet, wobei das JSON-Objekt unter 'data' und das optionale Bild unter 'file' übermittelt wird."
            ,tags = {Tags.ACCOUNT} ,
             responses = {
          @ApiResponse(responseCode = HttpStatus.OK, description = "Profil erfolgreich aktualisiert.",
                       content = @Content(mediaType = "text/plain", schema = @Schema(type = "String")))})
    @PutMapping(value ="/{email}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateAccountProfile(@PathVariable String email, @RequestPart("data") UpdateAccountDTO updateAccountDTO, @RequestPart(value = "file", required = false) MultipartFile file) {
        accountService.saveAccountChanges(email, updateAccountDTO, file);
        return ResponseEntity.ok("Profil erfolgreich aktualisiert!");
    }


    @Operation(summary = "Gibt das Benutzerprofil zurück", description = "Liefert die vollständigen Profildaten eines Benutzers basierend auf der email.",
            tags = {Tags.ACCOUNT},
            responses = {
            @ApiResponse(responseCode = HttpStatus.OK, description = "Benutzerprofil erfolgreich geladen.",
                         content = @Content(schema = @Schema(implementation = AccountDTO.class)))}
    )
    @GetMapping("/{email}")
    public ResponseEntity<AccountDTO> getAccountprofile(@PathVariable String email) {
        return ResponseEntity.ok(accountService.getAccountprofile(email));
    }
}
