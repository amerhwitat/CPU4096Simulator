# Performance & Concurrency Policy

Chimera II simulator components should use concurrency where it improves measured throughput without changing observable ISA behavior.

- Keep the Node.js event loop non-blocking.
- Move genuinely CPU-heavy, long-running simulation jobs to bounded worker threads or processes instead of blocking HTTP handlers.
- Do not create a worker per request; use bounded pools and backpressure.
- Keep shared simulator state isolated or synchronized; prefer message passing and immutable snapshots.
- Cap concurrency from hardware capacity and workload size, with an explicit deterministic single-thread mode for tests.
- Avoid oversubscription when native libraries already parallelize.
- Benchmark latency and throughput before/after changes.

All optimizations must preserve deterministic ISA semantics, API compatibility, and security properties.
