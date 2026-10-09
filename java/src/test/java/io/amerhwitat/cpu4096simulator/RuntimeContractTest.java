package io.amerhwitat.cpu4096simulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class RuntimeContractTest {
    private static final Path FIXTURES = Path.of("..", "contracts", "fixtures", "cpu4096sim-runtime-v1.tsv");

    @Test
    void sharedArithmeticSnapshotAndMetadataVectorsPass() throws Exception {
        int count = 0;
        for (String line : Files.readAllLines(FIXTURES, StandardCharsets.UTF_8)) {
            if (line.isBlank() || line.startsWith("#")) continue;
            String[] f = line.split("\\t", -1);
            assertEquals(7, f.length, "malformed fixture: " + line);
            switch (f[0]) {
                case "metadata" -> {
                    CpuCore cpu = new CpuCore(8192, 32);
                    assertEquals("{\"schema\":\"chimera.cpu.workload\",\"version\":1,\"kind\":\"register-snapshot\",\"payload\":\"width=128;words=2\"}",
                        cpu.workloadEnvelope("register-snapshot", "width=128;words=2"));
                    assertEquals(f[5], "chimera.cpu.workload|1|register-snapshot");
                }
                case "snapshot" -> {
                    WideWord word = new WideWord(new BigInteger(f[2]), Integer.parseInt(f[1]));
                    assertEquals(f[5], word.hex());
                }
                default -> {
                    int bits = Integer.parseInt(f[1]);
                    CpuCore cpu = new CpuCore(bits, 32);
                    cpu.setRegister(1, new WideWord(new BigInteger(f[2]), bits));
                    cpu.setRegister(2, new WideWord(new BigInteger(f[3]), bits));
                    int shift = Integer.parseInt(f[4]);
                    int opcode = switch (f[0]) {
                        case "add", "wrap-add" -> CpuCore.ADD;
                        case "shift-left" -> CpuCore.SHL;
                        case "shift-right" -> CpuCore.SHR;
                        default -> throw new AssertionError("unknown fixture: " + f[0]);
                    };
                    WideWord result = cpu.execute(opcode, 0, 1, 2, shift);
                    assertEquals(new BigInteger(f[5]), result.toBigInteger());
                    assertEquals(Long.parseLong(f[6]), cpu.pc());
                }
            }
            count++;
        }
        assertEquals(6, count);
    }

    @Test
    void shiftConstraintsAndInstructionEncodingAreDeterministic() {
        WideWord one = new WideWord(BigInteger.ONE, 128);
        assertEquals(BigInteger.ZERO, one.shiftLeft(128).toBigInteger());
        assertEquals(BigInteger.ZERO, one.shiftRight(128).toBigInteger());
        assertThrows(IllegalArgumentException.class, () -> one.shiftLeft(-1));
        CpuCore cpu = new CpuCore(8192, 32);
        byte[] encoded = cpu.encode(CpuCore.MOV, 7, 9, 0, 123);
        assertEquals(16, encoded.length);
        assertEquals(7, Byte.toUnsignedInt(encoded[2]));
        assertEquals(123, Byte.toUnsignedInt(encoded[8]));
    }
}
