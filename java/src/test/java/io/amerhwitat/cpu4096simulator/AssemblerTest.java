package io.amerhwitat.cpu4096simulator;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;
class AssemblerTest {
 @Test void assemblesCommentsRegistersAndLiterals() {
  byte[] code=Assembler.assemble("; header\n MOV r1, z2 # copy\nADD R3, R1, R2\nSHL R4, R3, 0x40\n");
  assertEquals(48,code.length);
  assertEquals(new InstructionCodec.Instruction(CpuCore.MOV,1,2,0,BigInteger.ZERO),InstructionCodec.decode(java.util.Arrays.copyOfRange(code,0,16)));
  assertEquals(new InstructionCodec.Instruction(CpuCore.ADD,3,1,2,BigInteger.ZERO),InstructionCodec.decode(java.util.Arrays.copyOfRange(code,16,32)));
  assertEquals(BigInteger.valueOf(64),InstructionCodec.decode(java.util.Arrays.copyOfRange(code,32,48)).immediate());
 }
 @Test void validatesRegistersImmediatesAndMnemonics() {
  assertEquals(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE),Assembler.parseImmediate("0xffffffffffffffff"));
  assertEquals(1023,Assembler.parseRegister("z1023"));
  assertThrows(IllegalArgumentException.class,()->Assembler.parseRegister("r1024"));
  assertThrows(IllegalArgumentException.class,()->Assembler.parseImmediate("-1"));
  assertThrows(IllegalArgumentException.class,()->Assembler.assembleLine("MAGIC r1, r2"));
  assertThrows(IllegalArgumentException.class,()->Assembler.assembleLine("ADD r1, r2, r3, 4"));
  assertThrows(IllegalArgumentException.class,()->Assembler.disassemble(new byte[17]));
 }
 @Test void executesExtendedOpcodeAndPrivilegeContracts() {
  CpuCore cpu=new CpuCore(64,8);
  cpu.setRegister(1,new WideWord(BigInteger.TEN,64)); cpu.setRegister(2,new WideWord(BigInteger.valueOf(6),64));
  assertEquals(BigInteger.valueOf(12),cpu.execute(CpuCore.XOR,3,1,2,0).toBigInteger());
  assertEquals(BigInteger.valueOf(60),cpu.execute(CpuCore.MUL,3,1,2,0).toBigInteger());
  assertEquals(BigInteger.ZERO,cpu.execute(CpuCore.CMPLT,3,1,2,0).toBigInteger());
  cpu.execute(CpuCore.CMP,0,1,2,0); assertEquals(new CpuCore.Flags(false,false,true,false),cpu.flags());
  long before=cpu.pc(); assertThrows(IllegalStateException.class,()->cpu.execute(CpuCore.SYS_CALL,0,0,0,0)); assertEquals(before,cpu.pc());
  cpu.setPrivilege(1); assertNull(cpu.execute(CpuCore.SYS_CALL,0,0,0,0));
  assertThrows(IllegalStateException.class,()->cpu.execute(CpuCore.POLICY_UPDATE,0,0,0,0));
  cpu.setPrivilege(3); assertNull(cpu.execute(CpuCore.POLICY_UPDATE,0,0,0,0));
 }
 @Test void divisionByZeroDoesNotAdvancePc() {
  CpuCore cpu=new CpuCore(64,4); long before=cpu.pc();
  assertThrows(ArithmeticException.class,()->cpu.execute(CpuCore.DIV,0,1,2,0)); assertEquals(before,cpu.pc());
 }
}