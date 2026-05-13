/**
 * Bank-of-Z teller screen definitions for the canonical @vforce360/terminal
 * component (ported from dashboard/src/components/terminal/).
 *
 * Each screen is a {@link ScreenDefinition} — equivalent to a 3270 BMS map
 * with declared fields, function-key bindings, optional API submit mapping,
 * and optional menu-style navigation.
 *
 * Wiring:
 *  - SIGNON      — landing screen; ENTER advances to MAINMENU.
 *  - MAINMENU    — operator selects 1..5; menu maps selection → screen id.
 *  - ACCTLIST    — list of accounts for a customer id (calls /api/accounts).
 *  - ACCTDET     — single-account inquiry (calls /api/accounts/{accountId}).
 *  - TXLIST      — recent transactions for an account.
 *
 * Mock responses are baked in so the emulator stays usable when the
 * platform's read-side projections aren't wired yet — the real handlers
 * land later and the mockResponses entries become unused.
 */
import type { ScreenDefinition } from "@/components/terminal/terminal-types";

const SIGNON: ScreenDefinition = {
  screenId: "SIGNON",
  title: "BANK OF Z — SIGNON",
  fields: [
    { row: 2,  col: 30, name: "title",      length: 0,  type: "output", label: "BANK OF Z — SIGNON",        bright: true,  protected: true },
    { row: 5,  col: 10, name: "instruct",   length: 0,  type: "output", label: "Enter your teller id and press ENTER:", protected: true },
    { row: 8,  col: 10, name: "lab_teller", length: 0,  type: "output", label: "Teller ID ......:",         protected: true },
    { row: 8,  col: 29, name: "teller_id",  length: 8,  type: "input" },
    { row: 10, col: 10, name: "lab_pin",    length: 0,  type: "output", label: "PIN ............:",         protected: true },
    { row: 10, col: 29, name: "pin",        length: 6,  type: "input" },
    { row: 22, col: 2,  name: "statusline", length: 0,  type: "output", label: "F3=EXIT  ENTER=SIGNON",     protected: true },
  ],
  functionKeys: {
    F3:    { action: "exit",   label: "EXIT" },
    ENTER: { action: "submit", label: "SIGNON" },
  },
  apiMapping: {
    submit: { method: "POST", path: "/api/sessions" },
    responseMap: {},
    // Mock until the host wires sessions: any submit → land on MAINMENU.
    mockResponses: { "*": { ok: true } },
  },
  menu: { field: "teller_id", targets: { "*": "MAINMENU" } },
  statusLine: "TELLER WORKSTATION — SIGNON",
};

const MAINMENU: ScreenDefinition = {
  screenId: "MAINMENU",
  title: "BANK OF Z — MAIN MENU",
  fields: [
    { row: 2,  col: 28, name: "title",     length: 0, type: "output", label: "BANK OF Z — MAIN MENU",                  bright: true,  protected: true },
    { row: 4,  col: 10, name: "welcome",   length: 0, type: "output", label: "Welcome, TELLER-001 / BRANCH-NYC-1",     protected: true },
    { row: 8,  col: 12, name: "op1",       length: 0, type: "output", label: "1.  Account List",                       protected: true },
    { row: 9,  col: 12, name: "op2",       length: 0, type: "output", label: "2.  Customer Lookup",                    protected: true },
    { row: 10, col: 12, name: "op3",       length: 0, type: "output", label: "3.  Account Detail",                     protected: true },
    { row: 11, col: 12, name: "op4",       length: 0, type: "output", label: "4.  Transaction List",                   protected: true },
    { row: 12, col: 12, name: "op5",       length: 0, type: "output", label: "5.  Legacy Bridge (DB2 history)",        protected: true },
    { row: 16, col: 10, name: "lab_sel",   length: 0, type: "output", label: "Selection ......:",                      protected: true },
    { row: 16, col: 29, name: "selection", length: 1, type: "input" },
    { row: 22, col: 2,  name: "statusline", length: 0, type: "output", label: "F3=SIGNOFF  ENTER=SELECT  F12=CANCEL",  protected: true },
  ],
  functionKeys: {
    F3:    { action: "exit",   label: "SIGNOFF" },
    F12:   { action: "cancel", label: "CANCEL" },
    ENTER: { action: "submit", label: "SELECT" },
  },
  menu: {
    field: "selection",
    targets: {
      "1": "ACCTLIST",
      "2": "ACCTLIST",      // customers ride the same list for the demo
      "3": "ACCTDET",
      "4": "TXLIST",
      "5": "ACCTDET",
    },
  },
};

const ACCTLIST: ScreenDefinition = {
  screenId: "ACCTLIST",
  title: "BANK OF Z — ACCOUNT LIST",
  fields: [
    { row: 2,  col: 28, name: "title",       length: 0,  type: "output", label: "BANK OF Z — ACCOUNT LIST",   bright: true,  protected: true },
    { row: 4,  col: 10, name: "lab_cust",    length: 0,  type: "output", label: "Customer ID.....:",          protected: true },
    { row: 4,  col: 29, name: "customer_id", length: 12, type: "input" },
    { row: 7,  col: 4,  name: "hdr_sel",     length: 0,  type: "output", label: "SEL", bright: true,         protected: true },
    { row: 7,  col: 9,  name: "hdr_acct",    length: 0,  type: "output", label: "ACCOUNT NO", bright: true,  protected: true },
    { row: 7,  col: 28, name: "hdr_type",    length: 0,  type: "output", label: "TYPE", bright: true,        protected: true },
    { row: 7,  col: 40, name: "hdr_status",  length: 0,  type: "output", label: "STATUS", bright: true,      protected: true },
    { row: 7,  col: 56, name: "hdr_bal",     length: 0,  type: "output", label: "BALANCE", bright: true,     protected: true },
    { row: 9,  col: 5,  name: "sel_1",       length: 1,  type: "input" },
    { row: 9,  col: 9,  name: "r1_acct",     length: 0,  type: "output", label: "1000000000000001",          protected: true },
    { row: 9,  col: 28, name: "r1_type",     length: 0,  type: "output", label: "CHECKING",                   protected: true },
    { row: 9,  col: 40, name: "r1_status",   length: 0,  type: "output", label: "OPEN",                       protected: true },
    { row: 9,  col: 56, name: "r1_bal",      length: 0,  type: "output", label: "$  12,450.78",               protected: true },
    { row: 10, col: 5,  name: "sel_2",       length: 1,  type: "input" },
    { row: 10, col: 9,  name: "r2_acct",     length: 0,  type: "output", label: "1000000000000002",          protected: true },
    { row: 10, col: 28, name: "r2_type",     length: 0,  type: "output", label: "SAVINGS",                    protected: true },
    { row: 10, col: 40, name: "r2_status",   length: 0,  type: "output", label: "OPEN",                       protected: true },
    { row: 10, col: 56, name: "r2_bal",      length: 0,  type: "output", label: "$ 187,432.11",               protected: true },
    { row: 19, col: 10, name: "hint",        length: 0,  type: "output", label: "Mark SEL with X and press ENTER to view detail.", protected: true },
    { row: 22, col: 2,  name: "statusline",  length: 0,  type: "output", label: "F3=MENU  ENTER=DETAIL  F7=PGUP  F8=PGDN",         protected: true },
  ],
  functionKeys: {
    F3:    { action: "navigate", target: "MAINMENU", label: "MENU" },
    F7:    { action: "page_up",   label: "PGUP" },
    F8:    { action: "page_down", label: "PGDN" },
    ENTER: { action: "navigate", target: "ACCTDET", label: "DETAIL" },
  },
  apiMapping: {
    submit: { method: "GET", path: "/api/accounts" },
    responseMap: {},
    mockResponses: { "*": { items: [] } },
  },
};

const ACCTDET: ScreenDefinition = {
  screenId: "ACCTDET",
  title: "BANK OF Z — ACCOUNT DETAIL",
  fields: [
    { row: 2,  col: 27, name: "title",        length: 0, type: "output", label: "BANK OF Z — ACCOUNT DETAIL",          bright: true,  protected: true },
    { row: 4,  col: 4,  name: "lab_acct",     length: 0, type: "output", label: "Account No......:",                    protected: true },
    { row: 4,  col: 23, name: "acct_value",   length: 0, type: "output", label: "1000000000000001",                     protected: true },
    { row: 5,  col: 4,  name: "lab_type",     length: 0, type: "output", label: "Type............:",                    protected: true },
    { row: 5,  col: 23, name: "type_value",   length: 0, type: "output", label: "CHECKING",                              protected: true },
    { row: 6,  col: 4,  name: "lab_status",   length: 0, type: "output", label: "Status..........:",                    protected: true },
    { row: 6,  col: 23, name: "status_value", length: 0, type: "output", label: "OPEN",                                  protected: true },
    { row: 7,  col: 4,  name: "lab_cust",     length: 0, type: "output", label: "Customer........:",                    protected: true },
    { row: 7,  col: 23, name: "cust_value",   length: 0, type: "output", label: "C00042 — JANE DOE",                    protected: true },
    { row: 8,  col: 4,  name: "lab_open",     length: 0, type: "output", label: "Opened..........:",                    protected: true },
    { row: 8,  col: 23, name: "open_value",   length: 0, type: "output", label: "2024-08-12",                            protected: true },
    { row: 10, col: 4,  name: "lab_ledger",   length: 0, type: "output", label: "Ledger Balance..:",                    protected: true },
    { row: 10, col: 23, name: "ledger_value", length: 0, type: "output", label: "$    12,450.78", bright: true,         protected: true },
    { row: 11, col: 4,  name: "lab_avail",    length: 0, type: "output", label: "Available.......:",                    protected: true },
    { row: 11, col: 23, name: "avail_value",  length: 0, type: "output", label: "$    11,950.78", bright: true,         protected: true },
    { row: 12, col: 4,  name: "lab_pending",  length: 0, type: "output", label: "Holds...........:",                    protected: true },
    { row: 12, col: 23, name: "pending_value",length: 0, type: "output", label: "$       500.00",                       protected: true },
    { row: 14, col: 4,  name: "lab_legacy",   length: 0, type: "output", label: "Legacy Source...:",                    protected: true },
    { row: 14, col: 23, name: "legacy_value", length: 0, type: "output", label: "PostgreSQL (history bridge)",          protected: true },
    { row: 22, col: 2,  name: "statusline",   length: 0, type: "output", label: "F3=BACK  F4=TXLIST  F5=REFRESH  F12=CANCEL", protected: true },
  ],
  functionKeys: {
    F3:    { action: "navigate", target: "ACCTLIST", label: "BACK" },
    F4:    { action: "navigate", target: "TXLIST",   label: "TXLIST" },
    F5:    { action: "refresh",  label: "REFRESH" },
    F12:   { action: "cancel",   label: "CANCEL" },
    ENTER: { action: "navigate", target: "TXLIST",   label: "TXLIST" },
  },
  apiMapping: {
    submit: { method: "GET", path: "/api/accounts/1000000000000001" },
    responseMap: {},
    mockResponses: { "*": { accountId: "1000000000000001", balance: 12450.78 } },
  },
};

const TXLIST: ScreenDefinition = {
  screenId: "TXLIST",
  title: "BANK OF Z — TRANSACTION LIST",
  fields: [
    { row: 2,  col: 27, name: "title",       length: 0, type: "output", label: "BANK OF Z — TRANSACTION LIST", bright: true,  protected: true },
    { row: 4,  col: 4,  name: "lab_acct",    length: 0, type: "output", label: "For Account.....:",            protected: true },
    { row: 4,  col: 23, name: "acct_value",  length: 0, type: "output", label: "1000000000000001",             protected: true },
    { row: 6,  col: 4,  name: "hdr_date",    length: 0, type: "output", label: "DATE",    bright: true,        protected: true },
    { row: 6,  col: 16, name: "hdr_kind",    length: 0, type: "output", label: "TYPE",    bright: true,        protected: true },
    { row: 6,  col: 28, name: "hdr_amount",  length: 0, type: "output", label: "AMOUNT",  bright: true,        protected: true },
    { row: 6,  col: 46, name: "hdr_balance", length: 0, type: "output", label: "BALANCE", bright: true,        protected: true },
    { row: 6,  col: 64, name: "hdr_ref",     length: 0, type: "output", label: "REF",     bright: true,        protected: true },
    { row: 8,  col: 4,  name: "r1_date",     length: 0, type: "output", label: "2026-05-12",                   protected: true },
    { row: 8,  col: 16, name: "r1_kind",     length: 0, type: "output", label: "DEPOSIT",                       protected: true },
    { row: 8,  col: 28, name: "r1_amount",   length: 0, type: "output", label: "$  +1,200.00",                  protected: true },
    { row: 8,  col: 46, name: "r1_balance",  length: 0, type: "output", label: "$  12,450.78",                  protected: true },
    { row: 8,  col: 64, name: "r1_ref",      length: 0, type: "output", label: "T-9001",                        protected: true },
    { row: 9,  col: 4,  name: "r2_date",     length: 0, type: "output", label: "2026-05-11",                   protected: true },
    { row: 9,  col: 16, name: "r2_kind",     length: 0, type: "output", label: "WITHDRAW",                      protected: true },
    { row: 9,  col: 28, name: "r2_amount",   length: 0, type: "output", label: "$    -200.00",                  protected: true },
    { row: 9,  col: 46, name: "r2_balance",  length: 0, type: "output", label: "$  11,250.78",                  protected: true },
    { row: 9,  col: 64, name: "r2_ref",      length: 0, type: "output", label: "T-8997",                        protected: true },
    { row: 10, col: 4,  name: "r3_date",     length: 0, type: "output", label: "2026-05-10",                   protected: true },
    { row: 10, col: 16, name: "r3_kind",     length: 0, type: "output", label: "DEPOSIT",                       protected: true },
    { row: 10, col: 28, name: "r3_amount",   length: 0, type: "output", label: "$    +500.00",                  protected: true },
    { row: 10, col: 46, name: "r3_balance",  length: 0, type: "output", label: "$  11,450.78",                  protected: true },
    { row: 10, col: 64, name: "r3_ref",      length: 0, type: "output", label: "T-8965",                        protected: true },
    { row: 22, col: 2,  name: "statusline",  length: 0, type: "output", label: "F3=BACK  F7=PGUP  F8=PGDN  F12=CANCEL", protected: true },
  ],
  functionKeys: {
    F3:    { action: "navigate", target: "ACCTDET", label: "BACK" },
    F7:    { action: "page_up",   label: "PGUP" },
    F8:    { action: "page_down", label: "PGDN" },
    F12:   { action: "cancel",    label: "CANCEL" },
    ENTER: { action: "navigate", target: "ACCTDET", label: "BACK" },
  },
};

export const BANK_SCREENS: Record<string, ScreenDefinition> = {
  SIGNON,
  MAINMENU,
  ACCTLIST,
  ACCTDET,
  TXLIST,
};

export const BANK_INITIAL_SCREEN = "SIGNON";
