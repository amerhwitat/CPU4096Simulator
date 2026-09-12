# CPU4096Simulator — Chimera II Web Edition

This repository is the Node.js + JavaScript web/runtime implementation layer for the Chimera II research architecture. It covers 4096/8192-bit arithmetic, the 1024-register CPU model, ISA inventory, memory/MMIO, kernel/runtime models, networking, Koronos 128D research state, robotics HAL and the browser dashboard.

## Build and run

Use the unified build layer first:

```bash
npm ci
npm test
npm run lint
npm run smoke
npm start
```

On Windows, `build-tools\build.bat --only node` provides the same dependency/build progress path. Open the local dashboard on the port printed by the application.

## Cross-language integration

The simulator is part of the unified Python/Node.js/Java/C++ solution. Node.js remains the web/runtime source here, while Python, Java and C++ consume the same deterministic JSON/JSONL vectors and reproducibility metadata. The companion `node/README.md` documents the integration boundary.

The 284-opcode catalog remains an inventory; catalogued opcodes are not automatically represented as implemented semantics.

## Important boundary

The 8192-bit datapath, 128 logical 64-bit lanes and brain-network model are software/research targets, not claims of physical silicon or biological equivalence. Network, filesystem and service catalogs are integration targets, not arbitrary host-daemon execution.

Crypto/AI workloads use public or synthetic material. Address-targeted private-key enumeration, seed guessing and unauthorized wallet access are excluded.

## API

The existing API covers health, ISA, runtime, CPU, kernel, memory, network, brain, robotics, Koronos, assembler, compiler, simulation, hashing, HMAC, secure randomness and Ed25519 key generation. See `docs/` for the detailed API and implementation status.
