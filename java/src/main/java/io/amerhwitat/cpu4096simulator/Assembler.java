package io.amerhwitat.cpu4096simulator;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Small source assembler matching the supported syntax of src/assembler.js. */
public final class Assembler {
    private static final Map<String,Integer> OPCODES = Map.ofEntries(
        Map.entry("ADD",CpuCore.ADD),Map.entry("SUB",CpuCore.SUB),Map.entry("AND",CpuCore.AND),
        Map.entry("OR",CpuCore.OR),Map.entry("XOR",CpuCore.XOR),Map.entry("NOT",CpuCore.NOT),
        Map.entry("MOV",CpuCore.MOV),Map.entry("SHL",CpuCore.SHL),Map.entry("SHR",CpuCore.SHR),
        Map.entry("MUL",CpuCore.MUL),Map.entry("MULHI",CpuCore.MULHI),Map.entry("ROL",CpuCore.ROL),
        Map.entry("ROR",CpuCore.ROR),Map.entry("DIV",CpuCore.DIV),Map.entry("REM",CpuCore.REM),
        Map.entry("CMP",CpuCore.CMP),Map.entry("CMPEQ",CpuCore.CMPEQ),Map.entry("CMPLT",CpuCore.CMPLT),
        Map.entry("SYS_CALL",CpuCore.SYS_CALL),Map.entry("BARRIER",CpuCore.BARRIER),
        Map.entry("POLICY_UPDATE",CpuCore.POLICY_UPDATE),Map.entry("JMP",CpuCore.JMP),Map.entry("JZ",CpuCore.JZ),Map.entry("JNZ",CpuCore.JNZ));
    private static final Pattern REGISTER=Pattern.compile("^[RZ](\\d+)$",Pattern.CASE_INSENSITIVE);
    private static final BigInteger U64_MAX=BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
    private Assembler() {}

    public static int parseRegister(String token) {
        Matcher m=REGISTER.matcher(token.trim());
        if(!m.matches()) throw new IllegalArgumentException("invalid register: "+token);
        int n;
        try { n=Integer.parseInt(m.group(1)); }
        catch(NumberFormatException e) { throw new IllegalArgumentException("register must be 0..1023",e); }
        if(n<0||n>1023) throw new IllegalArgumentException("register must be 0..1023");
        return n;
    }
    public static BigInteger parseImmediate(String token) {
        String t=token.trim();
        BigInteger n;
        try {
            boolean negative=t.startsWith("-");
            String unsigned=(t.startsWith("-")||t.startsWith("+"))?t.substring(1):t;
            int radix=10;
            if(unsigned.startsWith("0x")||unsigned.startsWith("0X")) { radix=16; unsigned=unsigned.substring(2); }
            else if(unsigned.startsWith("0b")||unsigned.startsWith("0B")) { radix=2; unsigned=unsigned.substring(2); }
            if(unsigned.isEmpty()) throw new NumberFormatException("empty literal");
            n=new BigInteger(unsigned,radix);
            if(negative)n=n.negate();
        } catch(NumberFormatException e) { throw new IllegalArgumentException("invalid immediate: "+token,e); }
        if(n.signum()<0||n.compareTo(U64_MAX)>0) throw new IllegalArgumentException("immediate must be an unsigned 64-bit value");
        return n;
    }
    public static InstructionCodec.Instruction assembleLine(String sourceLine) {
        if(sourceLine==null) throw new IllegalArgumentException("line must not be null");
        String clean=sourceLine.replaceFirst("[;#].*$","").trim();
        if(clean.isEmpty()) return null;
        String[] tokens=clean.replace(',',' ').trim().split("\\s+");
        String mnemonic=tokens[0].toUpperCase(Locale.ROOT);
        Integer opcode=OPCODES.get(mnemonic);
        if(opcode==null) throw new IllegalArgumentException("unknown mnemonic: "+tokens[0]);
        List<String> args=new ArrayList<>(List.of(tokens).subList(1,tokens.length));
        int dst=0,srcA=0,srcB=0;
        BigInteger immediate=BigInteger.ZERO;
        boolean branch=List.of("JMP","JZ","JNZ").contains(mnemonic);
        if(branch) { if(args.size()!=1) throw new IllegalArgumentException(mnemonic+" requires one byte-address or label"); immediate=parseImmediate(args.get(0)); return new InstructionCodec.Instruction(opcode,0,0,0,immediate); }
        boolean unary=List.of("NOT","MOV","SYS_CALL","BARRIER","POLICY_UPDATE").contains(mnemonic);
        if(unary) {
            if(args.size()>0) dst=parseRegister(args.get(0));
            if(args.size()>1) {
                if(REGISTER.matcher(args.get(1)).matches()) srcA=parseRegister(args.get(1));
                else immediate=parseImmediate(args.get(1));
            }
            if(args.size()>2) throw new IllegalArgumentException("too many operands for "+mnemonic);
        } else {
            if(args.size()>0) dst=parseRegister(args.get(0));
            if(args.size()>1) srcA=parseRegister(args.get(1));
            if(args.size()>2) {
                if(REGISTER.matcher(args.get(2)).matches()) srcB=parseRegister(args.get(2));
                else immediate=parseImmediate(args.get(2));
            }
            if(args.size()>3) throw new IllegalArgumentException("too many operands for "+mnemonic);
        }
        return new InstructionCodec.Instruction(opcode,dst,srcA,srcB,immediate);
    }
    public static byte[] assemble(String source) {
        if(source==null) throw new IllegalArgumentException("source must not be null");
        String[] raw=source.split("\\R");
        Map<String,Integer> labels=new java.util.LinkedHashMap<>();
        List<String> instructions=new ArrayList<>();
        int offset=0;
        for(String rawLine:raw) {
            String clean=rawLine.replaceFirst("[;#].*$","").trim();
            if(clean.isEmpty()) continue;
            Matcher label=Pattern.compile("^([A-Za-z_][A-Za-z0-9_]*):").matcher(clean);
            if(label.find()) {
                String name=label.group(1);
                if(labels.putIfAbsent(name,offset)!=null) throw new IllegalArgumentException("duplicate label: "+name);
                clean=clean.substring(label.end()).trim();
                if(clean.isEmpty()) continue;
            }
            instructions.add(clean);
            offset+=InstructionCodec.INSTRUCTION_BYTES;
        }
        ByteArrayOutputStream output=new ByteArrayOutputStream();
        for(String line:instructions) {
            String resolved=line;
            Matcher branch=Pattern.compile("^(JMP|JZ|JNZ)\\s+([A-Za-z_][A-Za-z0-9_]*)$",Pattern.CASE_INSENSITIVE).matcher(line);
            if(branch.matches()) {
                Integer address=labels.get(branch.group(2));
                if(address==null) throw new IllegalArgumentException("undefined label: "+branch.group(2));
                resolved=branch.group(1)+" "+address;
            }
            InstructionCodec.Instruction instruction=assembleLine(resolved);
            if(instruction!=null) output.writeBytes(InstructionCodec.encode(instruction));
        }
        return output.toByteArray();
    }
    public static List<String> disassemble(byte[] bytes) {
        if(bytes==null) throw new IllegalArgumentException("bytes must not be null");
        if(bytes.length%InstructionCodec.INSTRUCTION_BYTES!=0)
            throw new IllegalArgumentException("machine code length must be a multiple of 16 bytes");
        List<String> lines=new ArrayList<>();
        for(int offset=0;offset<bytes.length;offset+=InstructionCodec.INSTRUCTION_BYTES) {
            byte[] chunk=java.util.Arrays.copyOfRange(bytes,offset,offset+InstructionCodec.INSTRUCTION_BYTES);
            lines.add(InstructionCodec.disassemble(InstructionCodec.decode(chunk)));
        }
        return List.copyOf(lines);
    }
}
