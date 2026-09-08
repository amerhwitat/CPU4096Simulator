# CPU4096Simulator — Chimera II Web Edition

This repository started as a 4096-bit CPU simulator specification and is now the independent **Node.js + JavaScript web implementation layer** for the Chimera II research architecture.

## What is included

- P4096 4096-bit arbitrary-width CPU model.
- Chimera II 8192-bit model with 1024 architectural registers.
- JavaScript `BigInt` wide-word arithmetic with modulo-2^N semantics.
- Canonical 16-byte instruction encoding used by the current Chimera II semantic layer.
- 284-opcode ISA inventory; 21 currently implemented semantic operations are explicitly marked, while the remaining entries are catalogued rather than falsely claimed as executable.
- Privilege checks for system-call and policy-update operations.
- Runtime compatibility catalog covering Linux, Windows/Server, macOS, WSL/WSL2/WSLg, x86-64 and ARM64.
- Shells and utilities: Bash, Zsh, Fish, PowerShell, GNU/uutils-style core utilities, awk/gawk, sed, grep, findutils, util-linux, procps, iproute2, jq/yq, compression, build and debugger tools.
- Runtime catalog for Python, Java, .NET, Node.js, Perl, Ruby and PHP.
- Networking/filesystem catalog and optional service catalog: OpenSSH, Samba, NFS, BIND/dnsmasq, Apache/nginx/Caddy, mail, time/logging, desktop/system services, virtualization/containers, databases and MQTT.
- Node HTTP API for health, ISA, CPU state, hashing, secure random data and Ed25519 key generation.
- Browser dashboard with CPU, ISA, services and keygen panels.
- Test and smoke-test suite with zero third-party npm dependencies.

## Run

```bash
npm test
npm run lint
npm start
```

Open `http://localhost:3000`.

Node.js 24 LTS or newer is recommended. The official Node.js release page currently lists v24.20.0 as LTS and v26.8.1 as Current. citeturn0search0turn0search5

## Architecture boundary

This repository is intentionally independent. It does **not** modify:

- `amerhwitat/amerhwitat.github.io`
- `amerhwitat/keygen`
- `amerhwitat/ChimeraIIOS`
- `amerhwitat/test`

The web layer mirrors public architectural contracts from the native and Java work. The Python portion is exposed as a clean integration boundary rather than inventing or copying an unidentified Python repository.

See:

- `docs/ARCHITECTURE.md`
- `docs/SERVICES.md`
- `docs/MIGRATION.md`
