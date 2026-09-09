# CPU4096Simulator — Chimera II Web Edition

This repository is the independent **Node.js + JavaScript web/runtime implementation layer** for the Chimera II research architecture. It consolidates the web-appropriate portions of the 4096-bit lineage, the 8192-bit Java/native semantic model, the 128D/Koronos research layer, kernel/runtime models, networking, assembler/toolchain, memory/MMIO, deterministic brain-network simulation, and robotics HAL.

## Current implementation

- Configurable 4096-bit and 8192-bit arbitrary-width arithmetic using JavaScript `BigInt`.
- Chimera II CPU model with 1024 architectural wide registers, privilege state, program counter, and canonical 16-byte instruction encoding.
- **284 opcode inventory** preserved as the architectural catalog. The currently executable semantic subset is explicitly marked; undefined/catalogued opcodes are not represented as implemented.
- Arithmetic, logic, move, shifts/rotates, multiply/high-multiply, divide/remainder, comparisons, and privileged system operations in the current semantic CPU subset.
- Memory bus with MMIO mapping, read-only/executable protection, W^X policy, memory faults, snapshots, and a simple virtual-memory/page model.
- Kernel/runtime model: process contexts, capabilities, priority scheduler, IPC, syscall table, and service manager.
- Networking model: packet bus, UDP abstraction, TCP state machine, DNS table, and Netlink-like family model.
- 128D/Koronos research layer: bounded vector state, deterministic graph/network stepping, trust store, desktop profiles, and boot/desktop progress model.
- Robotics HAL: sensors, actuators, and deterministic swarm scheduling.
- Toolchain layer: source assembly, instruction disassembly, IR compilation, linking/object inspection, and ISA JSON export.
- Runtime compatibility catalog for Linux/Unix, Windows/Server, macOS, WSL/WSL2/WSLg, x86-64 and ARM64.
- Shell/utility metadata covering Bash, Zsh, Fish, PowerShell, GNU/uutils-style utilities, awk/gawk, sed, grep, findutils, util-linux, procps, iproute2, jq/yq, compression, build and debugger tools.
- Runtime metadata for Python, Java, .NET, Node.js, Perl, Ruby and PHP.
- Filesystem/service/network compatibility metadata including OpenSSH, Samba, NFS, DNS, web/mail/time/logging services, desktop/system services, virtualization/containers, databases and MQTT.
- Node HTTP API for CPU, ISA, memory, kernel, networking, brain, robotics, assembly, compilation, simulation, hashing, secure random data and Ed25519 key generation.
- Zero-framework browser dashboard for the research runtime.
- Node built-in test/smoke suite with no third-party npm runtime dependencies.

## Important implementation boundary

Chimera II is a research/emulation platform, not a claim of existing 8192-bit silicon or biological intelligence. The 8192-bit datapath, 128 logical 64-bit lanes, accelerator concepts, and brain-network model are software/research targets. Physical performance, energy, frequency, silicon area, and biological-equivalence claims require measured implementations. The 284-opcode inventory is not equivalent to 284 fully implemented instruction semantics.

The networking, filesystem, service, shell, compiler, and compatibility catalogs describe integration targets and simulation interfaces. The browser cannot directly install or execute arbitrary host daemons or replace a host operating system.

## Repository boundary

Only this repository is the web implementation target. The following repositories remain untouched by this project:

- `amerhwitat/amerhwitat.github.io` — native/source-of-record work
- `amerhwitat/keygen` — Java implementation and key-generation work
- `amerhwitat/ChimeraIIOS` — protected original Chimera II repository
- `amerhwitat/test` — protected test repository

The Python portion is represented by a clean integration/plugin boundary because no authoritative Python repository was identified; no unrelated Python code is invented or copied.

## Run

```bash
npm test
npm run lint
npm run smoke
npm start
```

Open `http://localhost:3000`.

Node.js 24 LTS or newer is recommended.

## API surface

| Endpoint | Purpose |
|---|---|
| `/api/health` | Runtime health/status |
| `/api/isa` | ISA inventory |
| `/api/isa.json` | Machine-readable ISA inventory |
| `/api/runtime/catalog` | Compatibility catalog |
| `/api/cpu/state` | CPU snapshot |
| `/api/kernel/state` | Scheduler/IPC/syscall/service state |
| `/api/memory/state` | Memory/VM state |
| `/api/network/state` | Packet/DNS/TCP/Netlink state |
| `/api/brain/state` | 128D graph state |
| `/api/robotics/state` | Robotics HAL/swarm state |
| `/api/koronos/state` | Koronos/desktop/trust state |
| `/api/assemble` | Assemble source into instruction bytes |
| `/api/compile` | Compile simple Chimera IR |
| `/api/sim/step` | Execute a simulation step |
| `/api/keygen` | Generate Ed25519 key material |
| `/api/random` | Secure random bytes |
| `/api/hash` | Hash data |
| `/api/hmac` | HMAC data |

See the documents under `docs/` for architecture, implementation status, service boundaries, API details, and Java/Python/native mapping.
