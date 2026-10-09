import test from 'node:test';
import assert from 'node:assert/strict';
import { readFile } from 'node:fs/promises';
import { WideWord, CpuCore, OPCODES } from '../src/chimera.js';

test('Java and Node simulator use the same deterministic contract vectors', async () => {
  const text = await readFile(new URL('../contracts/fixtures/cpu4096sim-runtime-v1.tsv', import.meta.url), 'utf8');
  const rows = text.split(/\r?\n/).filter(line => line && !line.startsWith('#')).map(line => line.split('\t'));
  assert.equal(rows.length, 7);
  for (const [kind, widthText, lhsText, rhsText, operandText, expected, pcText] of rows) {
    const width = Number(widthText);
    if (kind === 'metadata') {
      const envelope = { schema: 'chimera.cpu.workload', version: 1, kind: 'register-snapshot', payload: 'width=128;words=2' };
      assert.equal([envelope.schema, envelope.version, envelope.kind].join('|'), expected);
      assert.deepEqual(envelope, { schema: 'chimera.cpu.workload', version: 1, kind: 'register-snapshot', payload: 'width=128;words=2' });
      continue;
    }
    if (kind === 'snapshot') {
      assert.equal(WideWord.fromBigInt(BigInt(lhsText), width).hex(), expected);
      continue;
    }
    const cpu = new CpuCore(width, 32);
    cpu.regs[1] = WideWord.fromBigInt(BigInt(lhsText), width);
    cpu.regs[2] = WideWord.fromBigInt(BigInt(rhsText), width);
    const immediate = Number(operandText);
    if (kind === 'trace-order') {
      cpu.execute({ opcode: OPCODES.ADD, dst: 0, srcA: 1, srcB: 2, immediate: 0n });
      cpu.execute({ opcode: OPCODES.ADD, dst: 3, srcA: 0, srcB: 2, immediate: 0n });
      assert.equal(cpu.regs[0].toBigInt().toString() + ',' + cpu.regs[3].toBigInt().toString(), expected);
    } else {
      const opcode = kind === 'add' || kind === 'wrap-add' ? OPCODES.ADD : kind === 'shift-left' ? OPCODES.SHL : OPCODES.SHR;
      const out = cpu.execute({ opcode, dst: 0, srcA: 1, srcB: 2, immediate: BigInt(immediate) });
      assert.equal(out.toBigInt().toString(), expected);
    }
    assert.equal(cpu.pc.toString(), pcText);
  }
});
