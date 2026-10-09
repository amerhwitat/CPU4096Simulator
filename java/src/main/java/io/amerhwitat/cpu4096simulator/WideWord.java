package io.amerhwitat.cpu4096simulator;

import java.math.BigInteger;
import java.util.Objects;

/** Unsigned N-bit datapath value, matching src/chimera.js WideWord modulo semantics. */
public final class WideWord {
    private final int bits;
    private final BigInteger value;

    public WideWord(BigInteger value, int bits) {
        if (bits <= 0 || bits % 64 != 0) throw new IllegalArgumentException("width must be a positive multiple of 64");
        this.bits = bits;
        this.value = Objects.requireNonNull(value, "value").mod(BigInteger.ONE.shiftLeft(bits));
    }
    public static WideWord fromBigInteger(BigInteger value, int bits) { return new WideWord(value, bits); }
    public static WideWord zero(int bits) { return new WideWord(BigInteger.ZERO, bits); }
    public int bits() { return bits; }
    public BigInteger toBigInteger() { return value; }
    private void sameWidth(WideWord other) {
        Objects.requireNonNull(other, "other");
        if (bits != other.bits) throw new IllegalArgumentException("word widths differ");
    }
    public WideWord add(WideWord other) { sameWidth(other); return new WideWord(value.add(other.value), bits); }
    public WideWord subtract(WideWord other) { sameWidth(other); return new WideWord(value.subtract(other.value), bits); }
    public WideWord and(WideWord other) { sameWidth(other); return new WideWord(value.and(other.value), bits); }
    public WideWord or(WideWord other) { sameWidth(other); return new WideWord(value.or(other.value), bits); }
    public WideWord xor(WideWord other) { sameWidth(other); return new WideWord(value.xor(other.value), bits); }
    public WideWord not() { return new WideWord(value.xor(BigInteger.ONE.shiftLeft(bits).subtract(BigInteger.ONE)), bits); }
    public WideWord shiftLeft(int count) {
        if (count < 0) throw new IllegalArgumentException("negative shift");
        return new WideWord(count >= bits ? BigInteger.ZERO : value.shiftLeft(count), bits);
    }
    public WideWord shiftRight(int count) {
        if (count < 0) throw new IllegalArgumentException("negative shift");
        return new WideWord(count >= bits ? BigInteger.ZERO : value.shiftRight(count), bits);
    }
    public String hex() { return String.format("%" + (bits / 4) + "s", value.toString(16)).replace(' ', '0'); }
}
