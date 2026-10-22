# SoloRecord TODO

## Verify before relying on it
- [ ] Confirm `@EnableMethodSecurity` is on `SecurityConfig` (without it, every `@PreAuthorize` is silently ignored)
- [ ] Check patient token on doctor lookup returns 403, doctor token on `/approve` returns 403
- [ ] Check login error bodies are identical for unknown email and wrong password (no email enumeration)
- [ ] Full manual run of access grants and lookup once a frontend exists

## Patient module cleanup
- [ ] Move `TooManyLookupAttemptsException` from `identity.exception` to `patient.exception`
- [ ] Add `@PreAuthorize("hasRole('PATIENT')")` to `/complete-profile` and `/profile-status`
- [ ] Add `@Valid` to `completeProfile` request body
- [ ] Switch `PatientController` from `getCurrentUser()` to `@AuthenticationPrincipal` (consistency with access-grant controller)
- [ ] Make `nationalId` unique and indexed on `Patient`

## Cross-module work
- [ ] Access-check method on the patient module: does this doctor have an active grant at this facility for this patient (needed by clinical and lab)
- [ ] Notification listener for `AccessRequestedEvent` (`@TransactionalEventListener(AFTER_COMMIT)` + `@Async`, `@EnableAsync` in `events.config`)
- [ ] Sender interface + stub (no PII in logs), real SMTP/SMS last
- [ ] Audit module, then retrofit `AuditLogService.log(...)` / event subscription into patient, facility, clinical, lab, outbreak (consider synchronous audit writes in the same transaction)

## Testing
- [ ] JUnit tests for `NameMasker` (single letter, 2-letter, extra spaces, null/blank, two-part names)
- [ ] Service tests: duplicate request (409), another patient's grant (404), approving twice (409)
- [ ] Context-load test so missing or duplicate beans fail CI early

## Known limitations (note in docs)
- In-process events can be lost if the app crashes between commit and listener
- Lookup rate limiter key could lose its expiry if the app dies between `increment` and `expire`
- `nationalId` stored unencrypted
- Kafka skipped for now; `EventPublisher` interface allows a Kafka implementation later

## Remaining modules
- [ ] facility
- [ ] clinical
- [ ] lab
- [ ] outbreak
- [ ] audit
- [ ] notification (full module, in-app notifications + real delivery)