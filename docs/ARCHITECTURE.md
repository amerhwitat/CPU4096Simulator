# Chimera II Web Runtime Architecture

## 1. Purpose

`CPU4096Simulator` is the independent Node.js/JavaScript implementation layer for the Chimera II research ecosystem. It turns the architectural contracts accumulated across the native, Java, and research specifications into browser-safe and Node-runtime components.

The library specification defines Chimera II as a software-defined/emulated research processor ecosystem: the 8192-bit datapath and related accelerator concepts are design targets, not existing silicon. It also distinguishes register width, instruction width, lane width, issue width, and memory bandwidth. fileciteturn149file0L17-L23

## 2. Layered architecture

1. **Wide-word layer** — `WideWord` provides modulo-2^N arbitrary-width arithmetic for P4096 and Chimera II 8192-bit modes.
2. **CPU layer** — `CpuCore` provides 1024 architectural registers, PC/privilege state, current semantic operations, and the canonical 16-byte instruction codec used by the current web model.
3. **ISA layer** — `isaCatalog` preserves all 284 catalogued opcode slots. Defined semantic operations are distinguished from reserved/catalogued entries.
4. **Memory layer** — `MemoryBus` models RAM, MMIO, access faults, read-only/executable regions, W^X policy and snapshots; `VirtualMemory` provides a simple page allocation/translation model.
5. **Kernel layer** — process contexts, capabilities, scheduler, IPC, syscall registration and service lifecycle are represented by `kernel.js`.
6. **Networking layer** — packet queues, UDP, TCP state transitions, DNS records and a Netlink-like control model are represented by `network.js`.
7. **Research/128D layer** — bounded 128D vector state, graph propagation and deterministic traces provide the computational research model; this is not a claim of biological brain emulation.
8. **Robotics layer** — sensor/actuator registration and swarm scheduling form a hardware-abstraction model.
9. **Toolchain layer** — assembler, disassembler, ISA JSON, simple IR compilation/linking and object inspection are exposed through `toolchain.js` and `assembler.js`.
10. **Runtime catalog** — shells, utilities, languages, filesystems, network protocols and optional services are described as compatibility/integration targets.
11. **HTTP API** — `server.js` exposes inspectable state and controlled simulation operations.
12. **Browser UI** — a zero-framework dashboard visualizes the runtime without requiring a browser framework or third-party npm runtime packages.

The underlying technical specification describes the broader OS layers as applications, user environment, services, microkernel, HAL, firmware and execution substrate, with emulator, QEMU/FPGA and eventual hardware paths. fileciteturn149file0L24-L32

## 3. Architectural register and instruction model

The target 8192-bit model uses 1024 GPRs, 128 logical 64-bit lanes, and a research-wide datapath. The supplied specification also describes separate predicate/mask, floating/vector and tensor register classes. fileciteturn149file0L33-L47

The web implementation currently models the GPR/CPU semantic subset and retains the wider ISA inventory for future incremental implementation. It must not be described as executing every catalogued opcode.

The current CPU codec is a 16-byte little-endian web encoding: opcode, destination, source A, source B, and an eight-byte immediate. This is the web model's current ABI/codec contract; it should not be silently conflated with the broader R8192/C8192 specification, which separately describes fixed and variable-length instruction families. The specification identifies C8192 packets from 64 through 4096 bits with prefix/length/operand decoding. fileciteturn149file0L48-L60

## 4. Memory and protection

The memory subsystem provides:

- byte-addressable simulated RAM;
- MMIO device mapping;
- access-fault reporting;
- read-only and executable region protection;
- W^X policy;
- snapshots;
- 4 KiB page-oriented virtual allocation/translation.

These correspond to the research specification's requirements for memory protection, MMIO and coherent memory while remaining a deterministic JavaScript simulation. The specification calls for 4 KiB, 2 MiB and 1 GiB page concepts and MMIO/coherent shared-memory regions. fileciteturn149file2L305-L313

## 5. Kernel/runtime model

`kernel.js` is a deterministic service model rather than a replacement kernel. It provides process contexts, priority scheduling, capabilities, IPC, syscall dispatch and service registration. The broader Chimera II specification requires a preemptive scheduler, zero-copy I/O, dual-stack networking, memory protection and an 8192-bit execution engine. fileciteturn149file2L297-L304

## 6. Networking model

`network.js` models packet ownership/queues, UDP endpoints, TCP state transitions, DNS records and Netlink-like families. It is suitable for deterministic testing and UI inspection. It is not a direct implementation of Linux kernel Netlink, raw Ethernet, NIC DMA, or host sockets.

The library specification identifies IPv4/IPv6, TCP/UDP, routing, ND, SLAAC and DHCPv6 as networking-guide targets. fileciteturn149file1L146-L157

## 7. 128D/Koronos and neural research

The 128D layer represents state vectors and graph propagation with bounded, deterministic updates. It is a research abstraction for hyperdimensional/vector computation and virtual computational nodes. The technical specification explicitly states that the neural concept is modeled as a graph of virtual computational nodes and that register width alone does not reproduce biological intelligence. fileciteturn149file0L17-L21

## 8. Toolchain

The current web toolchain includes:

- assembly parsing for the defined semantic instruction subset;
- instruction disassembly;
- machine-readable ISA JSON;
- simple Chimera IR;
- IR-to-assembly compilation;
- object/link metadata;
- inspection/disassembly helpers.

The research documents call for an assembler/compiler/linker/emulator stack and explicitly identify C++ → Chimera IR → R8192/C8192 assembly as a target. fileciteturn149file1L85-L117

## 9. Compatibility boundary

This repository is independent. It does not modify or replace:

- `amerhwitat/amerhwitat.github.io`;
- `amerhwitat/keygen`;
- `amerhwitat/ChimeraIIOS`;
- `amerhwitat/test`.

The Python integration remains a clean boundary because no authoritative Python repository has been identified. No unrelated or guessed Python code is embedded.

## 10. Security boundary

Key generation uses Node's built-in cryptographic implementation. Production deployments must add authentication, TLS, rate limiting, secure key storage and strict access control before exposing sensitive endpoints. Private-key generation should not be treated as a public service.

The wider specification identifies Secure Boot, signed/measured images, W^X, protected MMIO, DMA/IOMMU isolation, explicit packet ownership, capability-checked syscalls and constant-time cryptographic arithmetic as security goals. fileciteturn149file4L384-L387

## 11. Validation strategy

Validation should cover wide arithmetic, assembler/decoder round trips, memory/MMIO/W^X faults, scheduler/syscalls, packet/TCP state, deterministic neural traces and future boot-path/toolchain integration. These areas match the specification's validation strategy. fileciteturn149file4L391-L406

## 12. Implementation status

**Implemented in the web repository:** CPU semantic subset, 284-entry inventory, memory/VM model, kernel/runtime model, networking model, 128D graph model, robotics HAL, assembler/disassembler helpers, IR/toolchain helpers, compatibility catalog, HTTP API, browser dashboard and automated tests.

**Not claimed as complete:** all 284 opcode semantics, a production microkernel, a native Linux/Unix kernel, a complete TCP/IP implementation, real NIC DMA, full C/C++ compiler backend, full ELF toolchain, physical FPGA/silicon execution, or biological brain emulation.

This distinction keeps the web implementation synchronized with the research architecture without overstating its present maturity.
