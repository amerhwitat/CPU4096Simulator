# CPU4096Simulator JavaFX workbench

Requirements: JDK 21+ and Maven 3.9+.

Run:

```bash
cd java
mvn --batch-mode clean verify
mvn javafx:run
```

The workbench assembles source using the repository's current Java assembler, executes the encoded 16-byte instruction stream against the Java host-side CPU model, and reports the PC, flags, step count, and a compact view of registers R0–R15. Execution is capped at 1,000 steps to prevent a branch loop from hanging the UI.

Scope: this is a deterministic software model for the currently implemented opcode subset. It is not hardware execution, not a full ISA implementation, and the 8192-bit profile is a software width profile rather than a physical CPU width. Complete flags/register-effect conformance and exhaustive ISA fixtures remain outstanding.
