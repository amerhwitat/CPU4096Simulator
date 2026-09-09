# Chimera II Web Service and Compatibility Catalog

## Scope

This document distinguishes **catalogued compatibility targets** from services that are actually executable inside the Node/browser implementation. The web runtime does not bundle or impersonate complete third-party operating systems, daemons, compilers, or host utilities.

The broader Chimera II specification places shells/POSIX-style APIs, VFS, networking, IPC, device management and storage above the microkernel. fileciteturn149file0L24-L32

## 1. Executable web/runtime models

| Area | Current web implementation |
|---|---|
| CPU | WideWord + CpuCore |
| ISA inventory | 284 catalogued slots; current semantic subset explicitly implemented |
| Assembly | Defined subset assembler |
| Disassembly | Current 16-byte web instruction decoder |
| ISA JSON | Machine-readable catalog export |
| Memory | RAM, MMIO, protection, W^X, VM model |
| Kernel | Process, scheduler, capabilities, IPC, syscalls, services |
| Networking | Packet bus, UDP model, TCP state machine, DNS, Netlink-like model |
| 128D/Koronos | Vector state, deterministic graph stepping, trust/desktop/boot models |
| Robotics | Sensor/actuator HAL and swarm scheduler |
| Toolchain | Simple IR compiler/link/object metadata |
| Cryptography | Node built-in hashing, HMAC, random data and Ed25519 generation |

## 2. Shells and command families

Catalogued compatibility targets include:

- Bash, Zsh, Fish;
- PowerShell / `pwsh`;
- GNU coreutils and uutils/coreutils-style utilities;
- awk/gawk, sed, grep, findutils;
- util-linux, procps, psmisc;
- iproute2 and iputils;
- curl, wget, rsync, OpenSSH client;
- jq/yq;
- diff/patch;
- tar/cpio and gzip/bzip2/xz/zstd;
- make, binutils, GCC, GDB, autoconf, automake and libtool;
- screen, nano, less and Emacs;
- OpenSSL/GnuTLS;
- SQLite;
- Python, Java, .NET, Node.js, Rust, Perl, Ruby and PHP.

These entries describe compatibility/integration targets. They do not mean the browser has reimplemented each program.

## 3. Network and service targets

The catalog includes:

- IPv4 and IPv6;
- TCP, UDP and DNS;
- routing, Netlink-style control and packet interfaces;
- SMB/SMB3 and NFS/NFS4;
- WireGuard/OpenVPN integration targets;
- OpenSSH/sshd;
- Samba;
- BIND/dnsmasq;
- Apache, nginx and Caddy;
- Postfix and Dovecot;
- chrony/NTP;
- rsyslog/syslog-ng;
- cron/systemd/D-Bus;
- Avahi/CUPS/BlueZ;
- libvirt;
- Podman/Docker/containerd/k3s;
- PostgreSQL/MariaDB/Redis;
- Mosquitto/MQTT.

The current web implementation models selected networking semantics for deterministic research/testing; it does not expose host-level daemons or raw network administration through the browser.

## 4. Filesystem targets

Compatibility metadata covers FAT, exFAT, NTFS, ReFS, ext4, XFS, Btrfs, ZFS, NFS4, SMB3, UFS, APFS, HFS+, tmpfs and overlayfs.

The research specification additionally calls for a VFS/storage layer and MMIO/shared-memory concepts. fileciteturn149file0L24-L32

## 5. Cross-platform targets

The compatibility model covers Linux/Unix, Windows/Server, macOS, WSL/WSL2/WSLg, x86-64 and ARM64, plus the experimental Chimera R8192/C8192 environment.

The standards-oriented material defines the project as a cross-architecture system spanning ARM Cortex-M, x86-64 and Chimera R8192/C8192. fileciteturn149file2L265-L271

## 6. Service implementation policy

Every catalog entry follows one of three states:

1. **Executable model** — deterministic code exists in this repository.
2. **Integration target** — interfaces/metadata are represented, but the external service remains host-provided.
3. **Future subsystem** — the research specification requires it, but implementation has not yet been completed.

This policy prevents the documentation from turning a compatibility catalog into a false claim of complete operating-system reimplementation.
