# Chimera 128D + authenticated P2P integration

The simulator's CPU, kernel, memory, network and Koronos state can be serialized into the shared Chimera multidimensional envelope. The 128D baseline covers geometry, temporal state, observer/perspective, light/material response, events, objects, properties and interaction rules, with an extensible cognitive overlay.

P2P is opt-in and authenticated. Nodes exchange identity/version/capabilities, then use request/response, pub/sub, snapshot/delta and content-addressed synchronization. Sequence numbers and payload hashes provide deterministic ordering and integrity checks; stronger authentication/encryption is supplied by the host deployment.

Node.js/JavaScript is the primary implementation in this repository. Other language implementations consume the same JSON/JSONL conformance vectors. The protocol does not perform unsolicited scanning or remote execution.
