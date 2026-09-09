# Chimera II Cross-Implementation Mapping

## Purpose

This document records how the Node.js/JavaScript web layer maps the existing Chimera II ecosystem without modifying the native, Java, protected, or unidentified Python sources.

## Java → Node.js

| Java / research component | Web implementation |
|---|---|
| `Register8192` / wide register model | `WideWord` + `CpuCore` |
| `Instruction` | JS instruction object + current 16-byte codec |
| `ChimeraCpu` | `CpuCore` |
| `Vector128D` | `Vector128D` / `State128D` |
| Koronos learning concepts | `LearningKernel128D` and web research state |
| Trust store | `TrustStore` |
| Desktop profiles | `DESKTOP_PROFILES` |
| Boot/desktop progress | `BootDesktopProgress` |
| Runtime catalogs | `RuntimeCatalog` |
| Java key generation | Node built-in `crypto` Ed25519 API |
| Persistent Java learning DB | Not embedded; web layer remains stateless unless a persistence backend is added |

## Native C/C++ → Node.js

| Native architectural area | Web representation |
|---|---|
| 8192-bit registers | `WideWord`/`CpuCore` |
| Instruction decoder/executor | `CpuCore` semantic subset |
| Memory/MMIO | `MemoryBus` |
| Virtual memory | `VirtualMemory` |
| Kernel scheduling | `Scheduler` |
| Capabilities/syscalls | `CapabilitySet` / `SyscallTable` |
| IPC | `IpcBus` |
| Services | `ServiceManager` |
| Networking | `PacketBus`, `UdpStack`, `TcpStateMachine`, `DnsTable`, `NetlinkModel` |
| Brain/network research | `BrainNetwork`, `State128D`, `DeterministicTrace` |
| Robotics HAL | `RoboticsHAL`, `SwarmScheduler` |
| Assembler/disassembler | `assembler.js` / `toolchain.js` |

The native repository remains the authoritative source for native ABI and hardware-facing behavior. The web layer mirrors public contracts and provides deterministic browser/Node equivalents.

## Python → Node.js boundary

The project discussions reference Python research tooling, including neural/vision/research workflows, but no authoritative Python repository was identified for this integration. Therefore the web implementation uses an explicit plugin/API boundary instead of inventing or copying a Python codebase.

A future Python service can integrate through a documented HTTP/JSON or process boundary without changing the browser architecture.

## 128D Framework mapping

The user's preferred 128D conceptual model is represented as a bounded vector/graph research layer. The implementation treats it as a computational abstraction rather than a claim that 128 dimensions or an 8192-bit register inherently produces intelligence.

## Broader specification alignment

The research specification calls for an emulator, bootable x86-64 kernel, UEFI loader, LLVM target/toolchain, networking, BrainNetwork/virtual CPUs, robotics HAL, and eventually an FPGA banked datapath. fileciteturn149file4L407-L420

The current web repository implements the software-model portions appropriate for Node/browser and leaves hardware/bootloader/LLVM/host-kernel work in their respective source-of-record projects.

## ABI and compatibility policy

- Preserve existing Java/native ABI contracts unless an explicit versioned change is approved.
- Preserve the current 16-byte web instruction codec.
- Keep the 284-entry ISA inventory stable while adding semantics incrementally.
- Do not silently reinterpret catalogued opcodes as implemented.
- Keep host-specific services behind adapters.
- Keep security-sensitive operations explicit and authenticated in production deployments.
