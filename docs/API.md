# Chimera II Web API

The Node service exposes deterministic inspection and research endpoints. Start with `npm start` and use the local server at port 3000 by default.

## Read-only/state endpoints

- `GET /api/health` — runtime health and component status.
- `GET /api/isa` — JSON ISA inventory.
- `GET /api/isa.json` — machine-readable ISA inventory alias.
- `GET /api/runtime/catalog` — shell, utility, runtime, filesystem, network and service catalog.
- `GET /api/cpu/state` — CPU/register/PC/privilege snapshot.
- `GET /api/kernel/state` — processes, scheduler, IPC, syscalls and service state.
- `GET /api/memory/state` — physical-memory and virtual-memory state.
- `GET /api/network/state` — packet, UDP, TCP, DNS and Netlink-like state.
- `GET /api/brain/state` — 128D graph state.
- `GET /api/robotics/state` — sensors, actuators and swarm state.
- `GET /api/koronos/state` — Koronos, trust, desktop profiles and boot/desktop progress.

## Transformation/simulation endpoints

- `POST /api/assemble` — assemble supported source instructions into the current 16-byte web instruction format.
- `POST /api/compile` — compile the supported Chimera IR representation.
- `POST /api/sim/step` — execute a controlled simulation step.
- `GET /api/random` — generate cryptographically secure random data.
- `GET /api/hash` — hash supplied data.
- `GET /api/hmac` — calculate an HMAC.
- `GET /api/keygen` — generate Ed25519 key material.

Exact request fields should be treated as implementation-level API and may evolve under version control. Sensitive endpoints must not be exposed to untrusted clients without authentication, TLS, rate limiting and secure key handling.

## Architectural rule

The API exposes **models**, not privileged host administration. It does not grant browser code direct access to host filesystems, kernel Netlink, arbitrary daemons, raw NIC DMA, or host process execution.
