package id.ezra.spearswap;

import java.nio.charset.StandardCharsets;

/** Build signature, revealed only by the hidden "/ssf credits" subcommand. */
final class Watermark {
    private static final byte[] BLOB = {17, 10, 70, 72, 9, 5, 0, 95, 67, 80, 90, 83, 95};
    private static final byte[] MASK = "ssf-swap-1.21".getBytes(StandardCharsets.UTF_8);

    private Watermark() {}

    static String reveal() {
        byte[] out = new byte[BLOB.length];
        for (int i = 0; i < out.length; i++) out[i] = (byte) (BLOB[i] ^ MASK[i % MASK.length]);
        return new String(out, StandardCharsets.UTF_8);
    }
}
