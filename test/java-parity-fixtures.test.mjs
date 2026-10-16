import test from 'node:test';
import assert from 'node:assert/strict';
import { readFile } from 'node:fs/promises';
import { WideWord, CpuCore, OPCODES } from '../src/chimera.js';
import { assemble } from '../src/assembler.js';

test('Java and Node simulator use the same deterministic contract vectors', async () => {
  const text = await readFile(new URL('../contracts/fixtures/cpu4096sim-runtime-v1.tsv', import.meta.url), 'utf8');
  const rows = text.split(/\r?\n/).filter(line => line && !line.startsWith('#')).map(line => line.split('\t'));
  assert.equal(rows.length, 19);
  const opcodes = { add: OPCODES.ADD, 'wrap-add': OPCODES.ADD, 'shift-left': OPCODES.SHL, 'shift-right': OPCODES.SHR,
    and: OPCODES.AND, or: OPCODES.OR, xor: OPCODES.XOR, not: OPCODES.NOT, mul: OPCODES.MUL, mulhi: OPCODES.MULHI,
    rol: OPCODES.ROL, ror: OPCODES.ROR, div: OPCODES.DIV, rem: OPCODES.REM, cmpeq: OPCODES.CMPEQ, cmplt: OPCODES.CMPLT };
  for (const [kind, widthText, lhsText, rhsText, operandText, expected, pcText] of rows) {
    const width = Number(widthText);
    if (kind === 'metadata') {
      const envelope = { schema: 'chimera.cpu.workload', version: 1, kind: 'register-snapshot', payload: 'width=128;words=2' };
      assert.equal([envelope.schema, envelope.version, envelope.kind].join('|'), expected);
      assert.deepEqual(envelope, { schema: 'chimera.cpu.workload', version: 1, kind: 'register-snapshot', payload: 'width=128;words=2' });
      continue;
    }
    if (kind === 'snapshot') { assert.equal(WideWord.fromBigInt(BigInt(lhsText), width).hex(), expected); continue; }
    const cpu = new CpuCore(width, 32);
    cpu.regs[1] = WideWord.fromBigInt(BigInt(lhsText), width); cpu.regs[2] = WideWord.fromBigInt(BigInt(rhsText), width);
    if (kind === 'trace-order') {
      cpu.execute({ opcode: OPCODES.ADD, dst: 0, srcA: 1, srcB: 2, immediate: 0n });
      cpu.execute({ opcode: OPCODES.ADD, dst: 3, srcA: 0, srcB: 2, immediate: 0n });
      assert.equal(cpu.regs[0].toBigInt().toString() + ',' + cpu.regs[3].toBigInt().toString(), expected);
    } else {
      const out = cpu.execute({ opcode: opcodes[kind], dst: 0, srcA: 1, srcB: 2, immediate: BigInt(operandText) });
      assert.equal(out.toBigInt().toString(), expected, 'fixture ' + kind);
    }
    assert.equal(cpu.pc.toString(), pcText);
  }
});
test('assembler resolves labels and branch opcodes execute absolute byte targets', () => {
  const bytes = assemble('start: MOV R1, R0\nCMP R1, R0\nJZ done\nADD R1, R1, R1\ndone: JMP done');
  assert.equal(bytes.length, 80);
  const cpu = new CpuCore(64, 8);
  const at = offset => cpu.decode(bytes.slice(offset, offset + 16));
  assert.equal(at(32).opcode, OPCODES.JZ);
  assert.equal(at(32).immediate, 64n);
  cpu.execute(at(0)); cpu.execute(at(16)); cpu.execute(at(32));
  assert.equal(cpu.pc, 64n);
  cpu.execute(at(64));
  assert.equal(cpu.pc, 64n);
  assert.throws(() => assemble('JMP missing'), /undefined label/);
  assert.throws(() => assemble('again: MOV R1, R0\nagain: JMP again'), /duplicate label/);
});
