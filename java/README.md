# Java implementation

The Java 21 headless simulator core is a host-side semantic model for the repository's fixed 16-byte instruction envelope. It does not claim to be a standardized ISA or a hardware emulator.

## Components

- `WideWord`: unsigned fixed-width arithmetic, with widths that are positive multiples of 64.
- `InstructionCodec`: canonical little-endian encoding/decoding for opcode:u16, dst:u16, srcA:u16, srcB:u16, immediate:u64.
- `Assembler`: source-line parsing and machine-code emission for the currently supported mnemonic set, R/Z register aliases, decimal/hex/binary unsigned immediates, comments, and byte-aligned disassembly.
- `CpuCore`: deterministic host-side semantics for ADD/SUB/AND/OR/XOR/NOT/MOV/SHL/SHR/MUL/MULHI/ROL/ROR/DIV/REM/CMP/CMPEQ/CMPLT/SYS_CALL/BARRIER/POLICY_UPDATE, including comparison flags and privilege guards.
- `MemoryBus`: bounded byte-addressed memory, unsigned 64-bit data access, mapped permission regions, execute checks, and W^X enforcement for mapped regions.

## Build and test

Run from this directory:

```sh
mvn --batch-mode --no-transfer-progress clean verify
```

Java and Node share `../contracts/fixtures/cpu4096sim-runtime-v1.tsv`. The current 19 vectors cover arithmetic, shifts, bitwise operations, multiply/high-multiply, rotations, division/remainder, comparisons, snapshots, metadata, and trace order.

## Scope and remaining parity

This is a scoped parity milestone. Labels, branches/control-flow assembly, all 284 catalog entries, complete flag behavior, a full machine-code execution pipeline, real MMU/TLB/DMA, and hardware execution are not implemented by these classes. Documentation and passing contract tests do not imply native hardware readiness.
