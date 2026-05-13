package com.example.api.screenmap;

import com.example.api.screenmap.dto.ScreenFieldDto;
import com.example.api.screenmap.dto.ScreenInputRequest;
import com.example.api.screenmap.dto.ScreenMapResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST face of the 3270 screen-map subsystem (BANK S-21 / S-22).
 *
 * Demo-readiness scaffold: returns hardcoded BMS-style screen maps for the
 * canonical teller flow (SIGNON → MAINMENU → ACCT_INQ). Submit echoes the
 * next screen back according to a small in-controller routing table.
 *
 * <p>Full implementation reads layouts from MongoDB via
 * {@link com.example.domain.screenmap.repository.ScreenMapRepository}
 * and routes via {@link com.example.domain.screenmap.model.ValidateScreenInputCmd}.
 * Follow-up story tracks moving the seeded layouts into the persistence
 * layer + adding the remaining BMS screens (CUST_ENROLL, TXN_POST, etc.).
 */
@RestController
@RequestMapping(value = "/api/terminal/screens", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Terminal", description = "3270 screen map render + submit")
public class ScreenMapRestController {

  private static final String SIGNON = "SIGNON";
  private static final String MAINMENU = "MAINMENU";
  private static final String ACCT_INQ = "ACCT_INQ";

  private final Map<String, ScreenMapResponse> seeded;

  /** Trivial submit-routing table; replace with ValidateScreenInputCmd dispatch. */
  private final Map<String, String> next =
      Map.of(
          SIGNON, MAINMENU,
          MAINMENU, ACCT_INQ,
          ACCT_INQ, MAINMENU);

  public ScreenMapRestController() {
    this.seeded =
        Map.of(
            SIGNON, signonScreen(),
            MAINMENU, mainMenuScreen(),
            ACCT_INQ, accountInquiryScreen());
  }

  @GetMapping("/{screenId}")
  @Operation(summary = "Fetch a screen map by its 3270 screen identifier")
  public ScreenMapResponse get(@PathVariable String screenId) {
    ScreenMapResponse map = seeded.get(screenId);
    if (map == null) {
      throw new ResponseStatusException(
          org.springframework.http.HttpStatus.NOT_FOUND,
          "Unknown screenId: " + screenId);
    }
    return map;
  }

  @PostMapping("/submit")
  @Operation(summary = "Submit a populated screen; returns the next screen to render")
  public ScreenMapResponse submit(@Valid @RequestBody ScreenInputRequest request) {
    if (!seeded.containsKey(request.screenId())) {
      throw new ResponseStatusException(
          org.springframework.http.HttpStatus.NOT_FOUND,
          "Unknown screenId: " + request.screenId());
    }
    // TODO(invariants): drive through ScreenMapAggregate.execute(ValidateScreenInputCmd)
    // so mandatoryFieldsValidated + bmsFieldLengthCompliant invariants gate the
    // transition. For now we just look up the next screen and return its layout.
    String nextScreenId = next.getOrDefault(request.screenId(), MAINMENU);
    return seeded.get(nextScreenId);
  }

  // ---------------------------------------------------------------------------
  // Seeded screens
  //   Layout positions follow standard BMS conventions: row/col are 1-indexed,
  //   length is the operator-typeable maximum, label rows are 0-length protected
  //   text. The point of these is enough chrome to make the terminal feel
  //   alive — exact pixel-fidelity to a specific CICS map is out of scope for
  //   this scaffold.
  // ---------------------------------------------------------------------------

  private static ScreenMapResponse signonScreen() {
    List<ScreenFieldDto> fields =
        List.of(
            ScreenFieldDto.bright("title", 2, 30, "BANK OF Z — SIGNON"),
            ScreenFieldDto.label("instruct", 5, 10, "Enter your teller id and press ENTER:"),
            ScreenFieldDto.label("teller_label", 8, 10, "Teller ID ......:"),
            ScreenFieldDto.input("teller_id", 8, 29, 8),
            ScreenFieldDto.label("pin_label", 10, 10, "PIN ............:"),
            ScreenFieldDto.input("pin", 10, 29, 6),
            ScreenFieldDto.label("status", 22, 2, "F3=EXIT  ENTER=SIGNON"));
    return new ScreenMapResponse(
        SIGNON,
        "TELLER SIGNON",
        ScreenMapResponse.DEFAULT_ROWS,
        ScreenMapResponse.DEFAULT_COLS,
        fields);
  }

  private static ScreenMapResponse mainMenuScreen() {
    List<ScreenFieldDto> fields =
        List.of(
            ScreenFieldDto.bright("title", 2, 28, "BANK OF Z — MAIN MENU"),
            ScreenFieldDto.label("welcome", 4, 10, "Welcome, TELLER-001 / BRANCH-NYC-1"),
            ScreenFieldDto.label("op1", 8, 12, "1.  Account Inquiry"),
            ScreenFieldDto.label("op2", 9, 12, "2.  Customer Lookup"),
            ScreenFieldDto.label("op3", 10, 12, "3.  Deposit / Withdrawal"),
            ScreenFieldDto.label("op4", 11, 12, "4.  Statement Generation"),
            ScreenFieldDto.label("op5", 12, 12, "5.  Legacy Bridge (DB2 history)"),
            ScreenFieldDto.label("prompt", 16, 10, "Selection ......:"),
            ScreenFieldDto.input("selection", 16, 29, 1),
            ScreenFieldDto.label("status", 22, 2, "F3=SIGNOFF  ENTER=SELECT  F12=CANCEL"));
    return new ScreenMapResponse(
        MAINMENU,
        "MAIN MENU",
        ScreenMapResponse.DEFAULT_ROWS,
        ScreenMapResponse.DEFAULT_COLS,
        fields);
  }

  private static ScreenMapResponse accountInquiryScreen() {
    List<ScreenFieldDto> fields =
        List.of(
            ScreenFieldDto.bright("title", 2, 28, "BANK OF Z — ACCOUNT INQUIRY"),
            ScreenFieldDto.label("acct_label", 5, 10, "Account No......:"),
            ScreenFieldDto.input("account_id", 5, 29, 16),
            ScreenFieldDto.label("ssn_label", 7, 10, "SSN (optional)..:"),
            ScreenFieldDto.input("ssn", 7, 29, 11),
            ScreenFieldDto.label("results_hdr", 11, 10, "Results"),
            ScreenFieldDto.label("results_underline", 12, 10, "-------"),
            ScreenFieldDto.label("hint", 14, 10, "(Press ENTER to look up; F3 returns to menu.)"),
            ScreenFieldDto.label("status", 22, 2, "F3=MENU  ENTER=LOOKUP  F5=REFRESH"));
    return new ScreenMapResponse(
        ACCT_INQ,
        "ACCOUNT INQUIRY",
        ScreenMapResponse.DEFAULT_ROWS,
        ScreenMapResponse.DEFAULT_COLS,
        fields);
  }
}
