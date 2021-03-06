package de.upb.crypto.craco.enc.sym.streaming.aes;

import de.upb.crypto.craco.interfaces.*;
import de.upb.crypto.craco.interfaces.pe.CiphertextIndex;
import de.upb.crypto.craco.interfaces.pe.KeyIndex;
import de.upb.crypto.craco.prf.PrfImage;
import de.upb.crypto.craco.prf.PrfKey;
import de.upb.crypto.craco.prf.PrfPreimage;
import de.upb.crypto.math.interfaces.hash.ByteAccumulator;
import de.upb.crypto.math.interfaces.hash.UniqueByteRepresentable;
import de.upb.crypto.math.random.interfaces.RandomGenerator;
import de.upb.crypto.math.random.interfaces.RandomGeneratorSupplier;
import de.upb.crypto.math.serialization.ByteArrayRepresentation;
import de.upb.crypto.math.serialization.ObjectRepresentation;
import de.upb.crypto.math.serialization.Representation;

import java.util.Arrays;

/**
 * A simple implementation of an representable byte array. This byte array can
 * be a plain text or a cipher text or an encryption key and/or a decryption key
 *
 * @author Mirko Jürgens
 */
public class ByteArrayImplementation implements PlainText, CipherText, DecryptionKey, EncryptionKey, SymmetricKey,
        KeyIndex, CiphertextIndex, PrfKey, PrfPreimage, PrfImage, UniqueByteRepresentable {

    private byte[] data;

    public ByteArrayImplementation(byte[] bytes) {
        this.data = bytes;
    }

    public ByteArrayImplementation(Representation repr) {
        byte[] representatedBytes = repr.obj().get("data").bytes().get();
        this.data = representatedBytes;
    }

    /**
     * Creates a ByteArrayImplementation filled with [length] bytes of randomness
     *
     * @param numberBytes number of random bytes / length of resulting ByteArrayImplementation
     */
    public static ByteArrayImplementation fromRandom(int numberBytes) {
        RandomGenerator rng = RandomGeneratorSupplier.getRnd();
        return new ByteArrayImplementation(rng.getRandomByteArray(numberBytes));
    }

    public byte[] getData() {
        return data;
    }

    /**
     * Create new byte array as concatination of this with a.
     *
     * @param a - the array to append
     * @return
     */
    public ByteArrayImplementation append(ByteArrayImplementation a) {
        byte[] result = new byte[data.length + a.getData().length];
        System.arraycopy(data, 0, result, 0, data.length);
        System.arraycopy(a, 0, result, data.length, a.getData().length);
        return new ByteArrayImplementation(result);
    }

    /**
     * The length of this byte array.
     *
     * @return
     */
    public int length() {
        return data.length;
    }

    /**
     * Compute exclusive or of two byte arrays.
     * <p>
     * Returns a new byte array where the i-th entry is the exclusive or of this
     * byte array's i-th entry and a's i-th entry.
     *
     * @param a
     * @return
     */
    public ByteArrayImplementation xor(ByteArrayImplementation a) {
        int min = this.length() < a.length() ? this.length() : a.length();
        int max = this.length() > a.length() ? this.length() : a.length();

        byte[] result = new byte[max];
        for (int i = 0; i < min; i++) {
            result[i] = (byte) (this.getData()[i] ^ a.getData()[i]);
        }
        return new ByteArrayImplementation(result);
    }

    @Override
    public Representation getRepresentation() {
        ObjectRepresentation toReturn = new ObjectRepresentation();
        toReturn.put("data", new ByteArrayRepresentation(data));
        return toReturn;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ByteArrayImplementation other = (ByteArrayImplementation) obj;
        if (!Arrays.equals(data, other.data))
            return false;
        return true;
    }

    @Override
    public String toString() {
        String result = "[";
        for (int i = 0; i < this.getData().length; i++) {
            result += String.format("%d", Byte.toUnsignedInt(this.getData()[i]));
            if (i < this.getData().length - 1)
                result += ",";
        }
        result += "]";
        return result;
    }

    @Override
    public ByteAccumulator updateAccumulator(ByteAccumulator accumulator) {
        accumulator.escapeAndAppend(data);
        return accumulator;
    }

}
