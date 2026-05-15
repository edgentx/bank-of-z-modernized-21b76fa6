package com.example.api.terminal;

import com.example.application.AggregateNotFoundException;
import com.example.api.terminal.dto.ScreenField;
import com.example.api.terminal.dto.ScreenInputPayload;
import com.example.api.terminal.dto.ScreenMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/terminal/screens", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Terminal", description = "3270 screen maps for the teller workstation")
public class TerminalController {
  private static final Map<String, ScreenMap> SCREENS = Map.of(
      "MAINMENU",
      screen(
          "MAINMENU",
          "Bank-of-Z Main Menu",
          field("hdr", 1, 24, "BANK OF Z TELLER WORKSTATION", "BRIGHT"),
          field("line1", 4, 8, "1  Account list", "NORMAL"),
          field("line2", 5, 8, "2  Account detail", "NORMAL"),
          field("line3", 6, 8, "3  Transaction list", "NORMAL"),
          field("line4", 7, 8, "4  Sign on", "NORMAL"),
          input("option", 10, 8, 1, ""),
          field("prompt", 10, 12, "Enter option and press ENTER", "NORMAL"),
          field("keys", 23, 2, "F3=EXIT  ESC=CLEAR", "REVERSE")),
      "SIGNON",
      screen(
          "SIGNON",
          "Bank-of-Z Sign On",
          field("hdr", 1, 29, "BANK OF Z SIGN ON", "BRIGHT"),
          field("userLabel", 6, 20, "User ID", "NORMAL"),
          input("userId", 6, 32, 12, "TELLER001"),
          field("passLabel", 8, 20, "Password", "NORMAL"),
          input("password", 8, 32, 12, ""),
          field("branchLabel", 10, 20, "Branch", "NORMAL"),
          input("branch", 10, 32, 8, "NYC-1"),
          field("prompt", 14, 20, "Press ENTER to continue", "NORMAL"),
          field("keys", 23, 2, "F3=EXIT  ESC=CLEAR", "REVERSE")),
      "ACCTLIST",
      screen(
          "ACCTLIST",
          "Account List",
          field("hdr", 1, 30, "ACCOUNT LIST", "BRIGHT"),
          field("cols", 3, 4, "Acct No     Customer       Type       Status    Balance", "REVERSE"),
          field("empty", 6, 4, "No accounts are currently loaded.", "NORMAL"),
          field("keys", 23, 2, "F3=BACK", "REVERSE")),
      "ACCTDET",
      screen(
          "ACCTDET",
          "Account Detail",
          field("hdr", 1, 30, "ACCOUNT DETAIL", "BRIGHT"),
          field("acctLabel", 5, 8, "Account #", "NORMAL"),
          input("accountNumber", 5, 22, 12, ""),
          field("prompt", 8, 8, "Enter an account number and press ENTER", "NORMAL"),
          field("keys", 23, 2, "F3=BACK  ESC=CLEAR", "REVERSE")),
      "TXLIST",
      screen(
          "TXLIST",
          "Transaction List",
          field("hdr", 1, 29, "TRANSACTION LIST", "BRIGHT"),
          field("acctLabel", 5, 8, "Account #", "NORMAL"),
          input("accountNumber", 5, 22, 12, ""),
          field("prompt", 8, 8, "Enter an account number and press ENTER", "NORMAL"),
          field("keys", 23, 2, "F3=BACK  ESC=CLEAR", "REVERSE")));

  @GetMapping("/{screenId}")
  @Operation(summary = "Return a 3270 screen map")
  public ScreenMap getScreen(@PathVariable String screenId) {
    ScreenMap screen = SCREENS.get(normalize(screenId));
    if (screen == null) {
      throw new AggregateNotFoundException("Terminal screen", screenId);
    }
    return screen;
  }

  @PostMapping(value = "/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Submit a terminal screen and return the next screen")
  public ScreenMap submit(@RequestBody ScreenInputPayload payload) {
    if ("SIGNON".equals(normalize(payload.screenId()))) {
      return getScreen("MAINMENU");
    }
    return getScreen(payload.screenId());
  }

  private static ScreenMap screen(String screenId, String title, ScreenField... fields) {
    return new ScreenMap(screenId, title, 24, 80, List.of(fields));
  }

  private static ScreenField field(String name, int row, int col, String label, String highlight) {
    return new ScreenField(name, row, col, label.length(), label, null, true, highlight);
  }

  private static ScreenField input(String name, int row, int col, int length, String value) {
    return new ScreenField(name, row, col, length, null, value, false, "BRIGHT");
  }

  private static String normalize(String screenId) {
    return screenId == null ? "" : screenId.toUpperCase(Locale.ROOT);
  }
}
