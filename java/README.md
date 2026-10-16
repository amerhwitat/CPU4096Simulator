# Java implementation

The Java 21 headless simulator core is a host-side semantic model for the repository's fixed 16-byte instruction envelope. It does not claim to be a standardized ISA or a hardware emulator.

## Components

- `WideWord`: unsigned fixed-width arithmetic, with widths that are positive multiples of 64.
- `InstructionCodec`: canonical little-endian encoding/decoding for opcode:u16, dst:u16, srcA:u16, srcB:u16, immediate:u64.
- `Assembler`: source-line parsing and machine-code emission for the currently supported mnemonic set, R/Z register aliases, decimal/hex/binary unsigned immediates, comments, two-pass labels, and absolute byte-address branches (JMP, JZ, JNZ).
- `CpuCore`: deterministic host-side semantics for arithmetic/bitwise operations, comparisons, privilege guards, and absolute byte-address control flow (JMP, JZ, JNZ). Branch targets must be 16-byte aligned and fit the signed 64-bit PC range.
- `MemoryBus`: bounded byte-addressed memory, unsigned 64-bit data access, mapped permission regions, execute checks, and W^X enforcement for mapped regions.

## Build and test

Run from this directory:

```sh
mvn --batch-mode --no-transfer-progress clean verify
```

Java and Node share `../contracts/fixtures/cpu4096sim-runtime-v1.tsv`. The current 19 vectors cover arithmetic, shifts, bitwise operations, multiply/high-multiply, rotations, division/remainder, comparisons, snapshots, metadata, and trace order.

## Scope and remaining parity

This is a scoped parity milestone. All 284 catalog entries, complete flag behavior, a full machine-code execution pipeline, real MMU/TLB/DMA, and hardware execution are not implemented by these classes. Branching is a limited host-model extension, not proof of complete ISA or hardware control-flow parity. Documentation and passing contract tests do not imply native hardware readiness.
