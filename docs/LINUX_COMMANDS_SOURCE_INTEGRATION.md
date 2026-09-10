# Linux Commands Source Integration

Chimera II reference sources: GNU Coreutils, util-linux, iproute2/net-tools, sudo, POSIX shells, and Toybox. Preserve original licenses/SPDX metadata and integrate through explicit adapters.

Performance policy: parallelize independent simulation/command tasks with bounded workers; avoid oversubscription and preserve deterministic single-thread execution for debugging and conformance.

Canonical standalone and Aurora Web UI integration is maintained in `amerhwitat/ChimeraIIOS`.
