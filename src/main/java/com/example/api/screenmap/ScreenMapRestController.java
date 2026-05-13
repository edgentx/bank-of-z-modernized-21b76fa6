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
  private static final String ACCTLIST = "ACCTLIST";
  private static final String ACCTDET = "ACCTDET";
  private static final String TXLIST = "TXLIST";

  private final Map<String, ScreenMapResponse> seeded;

  /** Trivial submit-routing table; replace with ValidateScreenInputCmd dispatch. */
  private final Map<String, String> next =
      Map.of(
          SIGNON, MAINMENU,
          MAINMENU, ACCTLIST,
          ACCTLIST, ACCTDET,
          ACCTDET, TXLIST,
          TXLIST, MAINMENU);

  public ScreenMapRestController() {
    this.seeded =
        Map.of(
            SIGNON, signonScreen(),
            MAINMENU, mainMenuScreen(),
            ACCTLIST, accountListScreen(),
            ACCTDET, accountDetailScreen(),
            TXLIST, transactionListScreen());
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

  private static ScreenMapResponse accountListScreen() {
    List<ScreenFieldDto> fields =
        List.of(
            ScreenFieldDto.bright("title", 2, 28, "BANK OF Z — ACCOUNT LIST"),
            ScreenFieldDto.label("cust_label", 4, 10, "Customer ID.....:"),
            ScreenFieldDto.input("customer_id", 4, 29, 12),
            ScreenFieldDto.bright("hdr_sel", 7, 4, "SEL"),
            ScreenFieldDto.bright("hdr_acct", 7, 9, "ACCOUNT NO"),
            ScreenFieldDto.bright("hdr_type", 7, 28, "TYPE"),
            ScreenFieldDto.bright("hdr_status", 7, 40, "STATUS"),
            ScreenFieldDto.bright("hdr_balance", 7, 56, "BALANCE"),
            // Demo rows
            ScreenFieldDto.input("sel_1", 9, 5, 1),
            ScreenFieldDto.label("row_1_acct", 9, 9, "1000000000000001"),
            ScreenFieldDto.label("row_1_type", 9, 28, "CHECKING"),
            ScreenFieldDto.label("row_1_status", 9, 40, "OPEN"),
            ScreenFieldDto.label("row_1_balance", 9, 56, "$  12,450.78"),
            ScreenFieldDto.input("sel_2", 10, 5, 1),
            ScreenFieldDto.label("row_2_acct", 10, 9, "1000000000000002"),
            ScreenFieldDto.label("row_2_type", 10, 28, "SAVINGS"),
            ScreenFieldDto.label("row_2_status", 10, 40, "OPEN"),
            ScreenFieldDto.label("row_2_balance", 10, 56, "$ 187,432.11"),
            ScreenFieldDto.label("hint", 19, 10, "Mark SEL with X and press ENTER to view detail."),
            ScreenFieldDto.label("status", 22, 2, "F3=MENU  ENTER=DETAIL  F7=PGUP  F8=PGDN"));
    return new ScreenMapResponse(
        ACCTLIST,
        "ACCOUNT LIST",
        ScreenMapResponse.DEFAULT_ROWS,
        ScreenMapResponse.DEFAULT_COLS,
        fields);
  }

  private static ScreenMapResponse accountDetailScreen() {
    List<ScreenFieldDto> fields =
        List.of(
            ScreenFieldDto.bright("title", 2, 27, "BANK OF Z — ACCOUNT DETAIL"),
            ScreenFieldDto.label("acct_label", 4, 4, "Account No......:"),
            ScreenFieldDto.label("acct_value", 4, 23, "1000000000000001"),
            ScreenFieldDto.label("type_label", 5, 4, "Type............:"),
            ScreenFieldDto.label("type_value", 5, 23, "CHECKING"),
            ScreenFieldDto.label("status_label", 6, 4, "Status..........:"),
            ScreenFieldDto.label("status_value", 6, 23, "OPEN"),
            ScreenFieldDto.label("cust_label", 7, 4, "Customer........:"),
            ScreenFieldDto.label("cust_value", 7, 23, "C00042 — JANE DOE"),
            ScreenFieldDto.label("opened_label", 8, 4, "Opened..........:"),
            ScreenFieldDto.label("opened_value", 8, 23, "2024-08-12"),
            ScreenFieldDto.label("ledger_label", 10, 4, "Ledger Balance..:"),
            ScreenFieldDto.bright("ledger_value", 10, 23, "$    12,450.78"),
            ScreenFieldDto.label("avail_label", 11, 4, "Available.......:"),
            ScreenFieldDto.bright("avail_value", 11, 23, "$    11,950.78"),
            ScreenFieldDto.label("pending_label", 12, 4, "Holds...........:"),
            ScreenFieldDto.label("pending_value", 12, 23, "$       500.00"),
            ScreenFieldDto.label("legacy_label", 14, 4, "Legacy Source...:"),
            ScreenFieldDto.label("legacy_value", 14, 23, "DB2 (history bridge)"),
            ScreenFieldDto.label("status", 22, 2, "F3=BACK  F4=TXLIST  F5=REFRESH  F12=CANCEL"));
    return new ScreenMapResponse(
        ACCTDET,
        "ACCOUNT DETAIL",
        ScreenMapResponse.DEFAULT_ROWS,
        ScreenMapResponse.DEFAULT_COLS,
        fields);
  }

  private static ScreenMapResponse transactionListScreen() {
    List<ScreenFieldDto> fields =
        List.of(
            ScreenFieldDto.bright("title", 2, 27, "BANK OF Z — TRANSACTION LIST"),
            ScreenFieldDto.label("acct_label", 4, 4, "For Account.....:"),
            ScreenFieldDto.label("acct_value", 4, 23, "1000000000000001"),
            ScreenFieldDto.bright("hdr_date", 6, 4, "DATE"),
            ScreenFieldDto.bright("hdr_kind", 6, 16, "TYPE"),
            ScreenFieldDto.bright("hdr_amount", 6, 28, "AMOUNT"),
            ScreenFieldDto.bright("hdr_balance", 6, 46, "BALANCE"),
            ScreenFieldDto.bright("hdr_ref", 6, 64, "REF"),
            ScreenFieldDto.label("row_1_date", 8, 4, "2026-05-12"),
            ScreenFieldDto.label("row_1_kind", 8, 16, "DEPOSIT"),
            ScreenFieldDto.label("row_1_amount", 8, 28, "$  +1,200.00"),
            ScreenFieldDto.label("row_1_balance", 8, 46, "$  12,450.78"),
            ScreenFieldDto.label("row_1_ref", 8, 64, "T-9001"),
            ScreenFieldDto.label("row_2_date", 9, 4, "2026-05-11"),
            ScreenFieldDto.label("row_2_kind", 9, 16, "WITHDRAW"),
            ScreenFieldDto.label("row_2_amount", 9, 28, "$    -200.00"),
            ScreenFieldDto.label("row_2_balance", 9, 46, "$  11,250.78"),
            ScreenFieldDto.label("row_2_ref", 9, 64, "T-8997"),
            ScreenFieldDto.label("row_3_date", 10, 4, "2026-05-10"),
            ScreenFieldDto.label("row_3_kind", 10, 16, "DEPOSIT"),
            ScreenFieldDto.label("row_3_amount", 10, 28, "$    +500.00"),
            ScreenFieldDto.label("row_3_balance", 10, 46, "$  11,450.78"),
            ScreenFieldDto.label("row_3_ref", 10, 64, "T-8965"),
            ScreenFieldDto.label("status", 22, 2, "F3=BACK  F7=PGUP  F8=PGDN  F12=CANCEL"));
    return new ScreenMapResponse(
        TXLIST,
        "TRANSACTION LIST",
        ScreenMapResponse.DEFAULT_ROWS,
        ScreenMapResponse.DEFAULT_COLS,
        fields);
  }
}
