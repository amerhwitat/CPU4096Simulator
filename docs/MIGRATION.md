# Java/Python → Node.js Mapping

| Existing ecosystem | Web runtime |
|---|---|
| Register8192 / 4096-bit register model | `WideWord` |
| Instruction | plain JS instruction object + 16-byte codec |
| ChimeraCpu | `CpuCore` |
| Koronos/128D concepts | runtime/ISA extension points |
| Desktop profiles/progress | browser dashboard extension point |
| Runtime compatibility catalogs | `RuntimeCatalog` |
| Keygen Java service | Node `crypto` Ed25519 API |
| Python research tools | browser-safe API/plugin boundary |
| Native C/C++ CPU | authoritative native implementation; web layer mirrors contracts |

The Python side is intentionally represented through a clean web/plugin boundary rather than embedding a guessed or unrelated Python codebase.
