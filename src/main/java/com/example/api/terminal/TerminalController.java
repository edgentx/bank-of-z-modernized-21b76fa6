package com.example.api.terminal;

import com.example.api.account.dto.AccountTransactionSummary;
import com.example.api.terminal.dto.ScreenField;
import com.example.api.terminal.dto.ScreenInputPayload;
import com.example.api.terminal.dto.ScreenMap;
import com.example.application.AggregateNotFoundException;
import com.example.application.account.AccountAppService;
import com.example.domain.account.model.AccountAggregate;
import com.example.infrastructure.mongo.customer.CustomerDocument;
import com.example.infrastructure.mongo.customer.CustomerMongoDataRepository;
import com.example.infrastructure.mongo.transaction.TransactionDocument;
import com.example.infrastructure.mongo.transaction.TransactionMongoDataRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
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
  private static final int ROWS = 24;
  private static final int COLS = 80;
  private static final int ROW_LENGTH = 74;

  private final AccountAppService accounts;
  private final CustomerMongoDataRepository customers;
  private final TransactionMongoDataRepository transactions;

  public TerminalController(
      AccountAppService accounts,
      CustomerMongoDataRepository customers,
      TransactionMongoDataRepository transactions) {
    this.accounts = accounts;
    this.customers = customers;
    this.transactions = transactions;
  }

  @GetMapping("/{screenId}")
  @Operation(summary = "Return a 3270 screen map")
  public ScreenMap getScreen(@PathVariable String screenId) {
    return switch (normalize(screenId)) {
      case "MAINMENU" -> mainMenu(null);
      case "SIGNON" -> signon();
      case "ACCTLIST" -> accountList(null);
      case "ACCTDET" -> accountDetail(null, "", null);
      case "TXLIST" -> transactionList(null, null);
      default -> throw new AggregateNotFoundException("Terminal screen", screenId);
    };
  }

  @PostMapping(value = "/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Submit a terminal screen and return the next screen")
  public ScreenMap submit(@RequestBody ScreenInputPayload payload) {
    String screenId = normalize(payload.screenId());
    Map<String, String> values = payload.values() == null ? Map.of() : payload.values();
    return switch (screenId) {
      case "SIGNON" -> mainMenu("SIGNED ON AS " + defaultIfBlank(values.get("userId"), "TELLER001"));
      case "MAINMENU" -> submitMainMenu(values);
      case "ACCTLIST" -> accountList("REFRESHED");
      case "ACCTDET" -> submitAccountDetail(values);
      case "TXLIST" -> submitTransactionList(values);
      default -> getScreen(payload.screenId());
    };
  }

  private ScreenMap submitMainMenu(Map<String, String> values) {
    return switch (defaultIfBlank(values.get("option"), "").trim()) {
      case "1" -> accountList(null);
      case "2" -> accountDetail(null, "", null);
      case "3" -> transactionList(null, null);
      case "4" -> signon();
      default -> mainMenu("INVALID OPTION");
    };
  }

  private ScreenMap submitAccountDetail(Map<String, String> values) {
    String accountNumber = defaultIfBlank(values.get("accountNumber"), "").trim();
    if (accountNumber.isBlank()) {
      return accountDetail(null, "", "ENTER ACCOUNT NUMBER");
    }
    try {
      return accountDetail(accounts.findById(accountNumber), accountNumber, "READY");
    } catch (AggregateNotFoundException ex) {
      return accountDetail(null, accountNumber, "ACCOUNT NOT FOUND");
    }
  }

  private ScreenMap submitTransactionList(Map<String, String> values) {
    String accountNumber = defaultIfBlank(values.get("accountNumber"), "").trim();
    if (accountNumber.isBlank()) {
      return transactionList(null, "ENTER ACCOUNT NUMBER");
    }
    try {
      accounts.findById(accountNumber);
      return transactionList(accountNumber, "READY");
    } catch (AggregateNotFoundException ex) {
      return transactionList(accountNumber, "ACCOUNT NOT FOUND");
    }
  }

  private ScreenMap mainMenu(String status) {
    return screen(
        "MAINMENU",
        "Bank-of-Z Main Menu",
        field("hdr", 1, 24, "BANK OF Z TELLER WORKSTATION", "BRIGHT"),
        field("line1", 4, 8, "1  Account list", "NORMAL"),
        field("line2", 5, 8, "2  Account detail", "NORMAL"),
        field("line3", 6, 8, "3  Transaction list", "NORMAL"),
        field("line4", 7, 8, "4  Sign on", "NORMAL"),
        input("option", 10, 8, 1, ""),
        field("prompt", 10, 12, "Enter option and press ENTER", "NORMAL"),
        field("status", 22, 2, defaultIfBlank(status, "READY"), "NORMAL"),
        field("keys", 23, 2, "F1=HELP  F3=BACK  ENTER=SUBMIT  ESC=CLEAR", "REVERSE"));
  }

  private ScreenMap signon() {
    return screen(
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
        field("status", 22, 2, "MOCK SIGNON - ENTER CONTINUES", "NORMAL"),
        field("keys", 23, 2, "F3=BACK  ENTER=SUBMIT  ESC=CLEAR", "REVERSE"));
  }

  private ScreenMap accountList(String status) {
    List<ScreenField> fields = new ArrayList<>();
    fields.add(field("hdr", 1, 30, "ACCOUNT LIST", "BRIGHT"));
    fields.add(field("cols", 3, 4, "Acct No    Customer            Type       Status        Balance", "REVERSE"));

    List<AccountAggregate> page = accounts.list(null, null, null, PageRequest.of(0, 6)).getContent();
    if (page.isEmpty()) {
      fields.add(output("empty", 5, 4, "No accounts are currently loaded."));
    } else {
      for (int i = 0; i < page.size(); i++) {
        AccountAggregate account = page.get(i);
        fields.add(output("row_" + (i + 1), 5 + i, 4, accountRow(account)));
      }
    }

    fields.add(field("status", 22, 2, defaultIfBlank(status, "ALL ACCOUNTS LOADED"), "NORMAL"));
    fields.add(field("keys", 23, 2, "F3=BACK  F5=REFRESH", "REVERSE"));
    return screen("ACCTLIST", "Account List", fields);
  }

  private ScreenMap accountDetail(AccountAggregate account, String accountNumber, String status) {
    String selectedAccountNumber = account != null ? account.id() : defaultIfBlank(accountNumber, "");
    List<ScreenField> fields = new ArrayList<>();
    fields.add(field("hdr", 1, 30, "ACCOUNT DETAIL", "BRIGHT"));
    fields.add(field("acctLabel", 4, 8, "Account #", "NORMAL"));
    fields.add(input("accountNumber", 4, 22, 12, selectedAccountNumber));
    fields.add(field("prompt", 6, 8, "Enter an account number and press ENTER", "NORMAL"));

    if (account != null) {
      fields.add(output("customer", 8, 8, "Customer: " + customerName(account.getCustomerId()) + " (" + account.getCustomerId() + ")"));
      fields.add(output("type", 9, 8, "Type:     " + account.getAccountType()));
      fields.add(output("statusValue", 10, 8, "Status:   " + account.getStatus()));
      fields.add(output("branch", 11, 8, "Branch:   " + account.getSortCode()));
      fields.add(output("balance", 12, 8, "Balance:  " + money(account.getInitialDeposit())));
      fields.add(output("available", 13, 8, "Available:" + money(account.getInitialDeposit())));
      fields.add(output("overdraft", 14, 8, "Overdraft:GBP 0.00"));
    }

    fields.add(field("status", 22, 2, defaultIfBlank(status, "READY"), "NORMAL"));
    fields.add(field("keys", 23, 2, "F3=BACK  ENTER=SUBMIT  ESC=CLEAR", "REVERSE"));
    return screen("ACCTDET", "Account Detail", fields);
  }

  private ScreenMap transactionList(String accountNumber, String status) {
    List<ScreenField> fields = new ArrayList<>();
    fields.add(field("hdr", 1, 29, "TRANSACTION LIST", "BRIGHT"));
    fields.add(field("acctLabel", 4, 8, "Account #", "NORMAL"));
    fields.add(input("accountNumber", 4, 22, 12, defaultIfBlank(accountNumber, "")));
    fields.add(field("cols", 6, 4, "Txn ID          Type         Amount       Description", "REVERSE"));

    if (accountNumber == null || accountNumber.isBlank()) {
      fields.add(output("hint", 8, 4, "Enter an account number and press ENTER"));
    } else {
      List<TransactionDocument> page = transactions.findByAccountId(accountNumber, PageRequest.of(0, 8)).getContent();
      if (page.isEmpty()) {
        fields.add(output("empty", 8, 4, "No transactions posted for this account."));
      } else {
        for (int i = 0; i < page.size(); i++) {
          fields.add(output("row_" + (i + 1), 8 + i, 4, transactionRow(page.get(i))));
        }
      }
    }

    fields.add(field("status", 22, 2, defaultIfBlank(status, "READY"), "NORMAL"));
    fields.add(field("keys", 23, 2, "F3=BACK  F5=REFRESH  ENTER=SUBMIT  ESC=CLEAR", "REVERSE"));
    return screen("TXLIST", "Transaction List", fields);
  }

  private String accountRow(AccountAggregate account) {
    return fit(String.format(
        "%-10s %-19s %-10s %-9s %12s",
        account.id(),
        fit(customerName(account.getCustomerId()), 19),
        fit(account.getAccountType(), 10),
        account.getStatus(),
        money(account.getInitialDeposit())),
        ROW_LENGTH);
  }

  private String transactionRow(TransactionDocument transaction) {
    String kind = defaultIfBlank(transaction.getKind(), "transaction");
    return fit(String.format(
        "%-14s %-10s %12s  %-30s",
        transaction.getId(),
        fit(kind.toUpperCase(Locale.ROOT), 10),
        money(AccountTransactionSummary.toMinorUnits(transaction.getAmount())),
        kind),
        ROW_LENGTH);
  }

  private String customerName(String customerId) {
    return customers.findById(customerId)
        .map(CustomerDocument::getFullName)
        .filter(name -> !name.isBlank())
        .orElse(customerId);
  }

  private static ScreenMap screen(String screenId, String title, ScreenField... fields) {
    return screen(screenId, title, List.of(fields));
  }

  private static ScreenMap screen(String screenId, String title, List<ScreenField> fields) {
    return new ScreenMap(screenId, title, ROWS, COLS, fields);
  }

  private static ScreenField field(String name, int row, int col, String label, String highlight) {
    return new ScreenField(name, row, col, label.length(), label, null, true, highlight);
  }

  private static ScreenField output(String name, int row, int col, String value) {
    return new ScreenField(name, row, col, ROW_LENGTH, fit(value, ROW_LENGTH), null, true, "NORMAL");
  }

  private static ScreenField input(String name, int row, int col, int length, String value) {
    return new ScreenField(name, row, col, length, null, value, false, "BRIGHT");
  }

  private static String normalize(String screenId) {
    return screenId == null ? "" : screenId.toUpperCase(Locale.ROOT);
  }

  private static String money(long minorUnits) {
    return String.format(Locale.UK, "GBP %,.2f", minorUnits / 100.0);
  }

  private static String fit(String value, int length) {
    if (value == null) {
      return "";
    }
    return value.length() <= length ? value : value.substring(0, length);
  }

  private static String defaultIfBlank(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }
}
