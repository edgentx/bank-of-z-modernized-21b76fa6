export default function AccountsPage() {
  return (
    <section>
      <h2 className="text-2xl font-semibold text-teller">Accounts</h2>
      <p className="mt-2 text-slate-600">
        Account listing and detail views attach here. The Account aggregate ports are exposed by the
        Spring Boot core via REST; this page will bind to <code>api.accounts.list()</code> once
        downstream stories ship the handlers.
      </p>
    </section>
  );
}
