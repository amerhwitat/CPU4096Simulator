import net from 'node:net';
import { createHash } from 'node:crypto';

export class Chimera128DState {
  constructor(values = {}, extensions = {}) { this.values = values; this.extensions = extensions; }
  envelope() { return { schema: 'chimera-128d/v1', values: this.values, extensions: this.extensions }; }
}

export class ReplayGuard {
  constructor() { this.highest = new Map(); }
  accept(peerId, sequence) {
    const old = this.highest.get(peerId) ?? -1;
    if (sequence <= old) return false;
    this.highest.set(peerId, sequence); return true;
  }
}

export function frame(peerId, sequence, kind, payload, ttlMs = 60000) {
  const canonical = JSON.stringify(payload, Object.keys(payload).sort());
  const payload_sha256 = createHash('sha256').update(canonical).digest('hex');
  return JSON.stringify({ protocol:'chimera-p2p/v1', peer_id:peerId, sequence, kind,
    expires_at:Date.now()+ttlMs, payload, payload_sha256 }) + '\n';
}

export function createServer({ host='127.0.0.1', port=0, onMessage=()=>{} } = {}) {
  const guard = new ReplayGuard();
  return net.createServer(socket => {
    let buffer='';
    socket.on('data', chunk => {
      buffer += chunk.toString(); let i;
      while ((i=buffer.indexOf('\n')) >= 0) {
        const line=buffer.slice(0,i); buffer=buffer.slice(i+1);
        try {
          const m=JSON.parse(line); if (m.protocol!=='chimera-p2p/v1' || m.expires_at<Date.now()) continue;
          if (!guard.accept(String(m.peer_id), Number(m.sequence))) continue;
          const canonical=JSON.stringify(m.payload, Object.keys(m.payload).sort());
          if (createHash('sha256').update(canonical).digest('hex')!==m.payload_sha256) continue;
          onMessage(m);
        } catch { /* malformed frames are discarded */ }
      }
    });
  }).listen(port, host);
}
