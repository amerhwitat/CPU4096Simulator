# Chimera II Web Runtime Architecture

This repository is the browser/Node.js realization of the CPU4096Simulator project. It combines the P4096 4096-bit research model with the current Chimera II 8192-bit Java/native semantic model as a portable web layer.

## Layers

1. **WideWord** — arbitrary-width arithmetic using JavaScript BigInt with modulo-2^N semantics.
2. **CpuCore** — 1024 architectural registers, program counter, privilege checks, arithmetic/logic/shift/rotate/multiply/divide/compare instructions, and canonical 16-byte instruction encoding.
3. **ISA catalog** — 284 opcode slots, with the currently implemented 21 semantic operations explicitly marked; remaining entries are catalogued rather than falsely implemented.
4. **Runtime catalog** — shells, GNU/uutils utilities, Microsoft PowerShell/Coreutils, language runtimes, networking, filesystems, and optional services discussed for Chimera II.
5. **Node service** — HTTP API for health, ISA, CPU state, runtime catalog, hashing, random values, and Ed25519 key generation.
6. **Browser UI** — zero-framework JavaScript dashboard for CPU state, ISA, runtime/service catalog and key generation.

## Compatibility boundary

The web runtime is an independent implementation. It does not modify or replace the native `amerhwitat/amerhwitat.github.io`, Java `amerhwitat/keygen`, or protected repositories. It models their public architectural contracts and web-appropriate services.

## Security

Key generation uses Node's built-in cryptographic implementation. Private keys are generated in process and returned only by the explicit local API request; production deployments should add authentication, TLS, rate limits, secure key storage, and never expose private-key endpoints publicly.
