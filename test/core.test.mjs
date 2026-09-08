import test from 'node:test';
import assert from 'node:assert/strict';
import { WideWord, CpuCore, OPCODES, RuntimeCatalog } from '../src/chimera.js';

test('4096-bit arithmetic is modulo 2^4096', () => {
  const a = WideWord.fromBigInt((1n << 4095n) + 5n, 4096);
  const b = WideWord.fromBigInt(7n, 4096);
  assert.equal(a.add(b).toBigInt(), (1n << 4095n) + 12n);
});

test('8192-bit core executes wide ADD', () => {
  const cpu = new CpuCore(8192);
  cpu.regs[1] = WideWord.fromBigInt(10n, 8192);
  cpu.regs[2] = WideWord.fromBigInt(32n, 8192);
  cpu.execute({ opcode: OPCODES.ADD, dst: 0, srcA: 1, srcB: 2, immediate: 0n });
  assert.equal(cpu.regs[0].toBigInt(), 42n);
});

test('instruction encoding is canonical 16-byte little-endian', () => {
  const cpu = new CpuCore(8192);
  const bytes = cpu.encode({ opcode: OPCODES.MOV, dst: 7, srcA: 9, srcB: 0, immediate: 123n });
  assert.deepEqual(cpu.decode(bytes), { opcode: OPCODES.MOV, dst: 7, srcA: 9, srcB: 0, immediate: 123n });
});

test('runtime catalog exposes shells, utilities and optional services', () => {
  const c = new RuntimeCatalog();
  assert.ok(c.commands.includes('bash'));
  assert.ok(c.commands.includes('pwsh'));
  assert.ok(c.commands.includes('awk'));
  assert.ok(c.services.includes('sshd'));
  assert.ok(c.services.includes('samba'));
});
