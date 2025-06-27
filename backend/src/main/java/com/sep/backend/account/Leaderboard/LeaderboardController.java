package com.sep.backend.account.Leaderboard;

import com.sep.backend.HttpStatus;
import com.sep.backend.Tags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/api/driver-leaderboard", produces = MediaType.APPLICATION_JSON_VALUE)
public class LeaderboardController {
    private final LeaderboardService leaderboardService;
    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    @Operation(description="Gibt die Liste der im System registrierten Fahrer mit  deren  Fahrtinformationen( ",
            tags={Tags.ACCOUNT},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK,description="Liste der Fahrer erfolgreich geladen!",
                            content= @Content(array= @ArraySchema( schema = @Schema(implementation= Leaderboard.class))))
            })
    public ResponseEntity<List<Leaderboard>> getDriverLeaderboards() {
        return ResponseEntity.ok(leaderboardService.getDriverLeaderboards());
    }
}
