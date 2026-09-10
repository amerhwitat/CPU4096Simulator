# Chimera II Cross-Architecture ISA Simulation Contract

The simulator now treats the Chimera C8192/R8192 extension catalog as semantic operations rather than architecture-specific encodings.

## Simulated classes

- BITMANIP: CLZ, CTZ, POPCNT, BEXT/BDEP-style operations
- ATOMICS: CAS, SWAP and fetch operations
- MEMORY_ORDER: FENCE, acquire/release and barrier semantics
- VECTOR: VADD, VMUL, VREDUCE, VPERM and scalable lane operations
- MATRIX_TENSOR: MADD and TCONTRACT-style operations
- CRYPTO: AES/SHA/CRC semantic operations
- STRING_MEMORY: bulk move/compare/checksum operations
- CACHE_MEMORY: prefetch and cache maintenance hints
- SYSTEM_CONTROL: traps, system calls, CSR/TLB style controls
- VIRTUALIZATION: VMRUN/VMEXIT guest transitions
- DECIMAL: packed-decimal compatibility semantics
- PREDICATE: mask-driven vector execution
- NETWORK: zero-copy queue operations for Spotnik simulation

The web UI should display these classes as capability groups and should not expose vendor encodings as if they were Chimera encodings.

## ABI rule

The simulator keeps the existing Chimera packet/ABI model. New semantic operations are extension-versioned and are mapped to generated opcode tables before being made architectural commitments.

## References

Intel SDM: https://www.intel.com/content/www/us/en/developer/articles/technical/intel-sdm.html
Arm A64: https://developer.arm.com/documentation/ddi0602/2026-06
RISC-V: https://docs.riscv.org/reference/isa/
OpenPOWER: https://openpowerfoundation.org/specifications/isa/
SPARC V9: https://docs.oracle.com/cd/E18752_01/html/816-1681/sparcv9-15322.html
PA-RISC: https://parisc.docs.kernel.org/en/latest/technical_documentation.html
