# Java implementation

JavaFX visualization layer for simulator traces, benchmark dashboards, deterministic vectors, and cross-language experiment metadata.


## Java 21 headless simulator core

The tracked Java directory previously contained only this README; it did not contain the documented JavaFX trace/dashboard source. This milestone adds a real headless semantic core: `WideWord` fixed-width arithmetic, a small deterministic `CpuCore` supporting ADD/SUB/MOV/SHL/SHR, canonical 16-byte little-endian instruction encoding, ordered register snapshots, and a stable workload-envelope boundary. Shared vectors live in `../contracts/fixtures/cpu4096sim-runtime-v1.tsv`; run `mvn --batch-mode --no-transfer-progress clean verify` from this directory. This does not claim the JavaFX UI or full opcode catalog is implemented.
