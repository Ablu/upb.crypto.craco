package de.upb.crypto.craco.enc.asym.elgamal;

import de.upb.crypto.craco.interfaces.*;
import de.upb.crypto.math.interfaces.structures.Group;
import de.upb.crypto.math.interfaces.structures.GroupElement;
import de.upb.crypto.math.serialization.ObjectRepresentation;
import de.upb.crypto.math.serialization.RepresentableRepresentation;
import de.upb.crypto.math.serialization.Representation;
import de.upb.crypto.math.structures.zn.Zn;
import de.upb.crypto.math.structures.zn.Zn.ZnElement;

import java.math.BigInteger;

/**
 * Encryption scheme originally presented by Elgamal in [1]. The key generation, encryption and decryption algorithm can
 * be described as follows:
 * <p>
 * Let G be a cyclic group of order $q$.
 * <p>
 * Key generation:
 * - Choose a \in Z_q = {0,1, ..., q-1} uniformly at random.
 * - Choose generator g of G uniformly at random.
 * - The private key is (G, g, a, h = g^a) and the public key is (G, g, h)
 * <p>
 * Encryption of message m \in G under public key pk = (G, g, h):
 * - Choose r \in Z_q uniformly at random.
 * - The ciphertext is c = (c_1, c_2) = (g^r, m * h^r)
 * <p>
 * Decryption of c = (c_1, c_2) under private key sk = (G, g, a, h):
 * - The message is m = c_2 * c_1^{-a}
 * <p>
 * <p>
 * [1] T. Elgamal, "A public key cryptosystem and a signature scheme based on discrete logarithms," in IEEE Transactions
 * on Information Theory, vol. 31, no. 4, pp. 469-472, July 1985.
 */
public class ElgamalEncryption implements AsymmetricEncryptionScheme {

    Group groupG;

    public ElgamalEncryption(Group groupG) {
        this.groupG = groupG;
    }

    public ElgamalEncryption(Representation repr) {
        groupG = (Group) repr.obj().get("groupG").repr().recreateRepresentable();
    }

    public Group getGroup() {
        return groupG;
    }

    @Override
    public CipherText encrypt(PlainText plainText, EncryptionKey publicKey) {
        //choose a random element random \in {0, ..., sizeG}
        BigInteger sizeG = groupG.size();

        Zn zn_random = new Zn(sizeG);
        ZnElement random_zn_element = zn_random.getUniformlyRandomElement();

        return this.encrypt(plainText, publicKey, random_zn_element.getInteger());
    }

    /**
     * Encrypt message under public key and use given randomness.
     * <p>
     * This function is used internally. Randomness is either uniform at random to obtain the standard Elgamal
     * Encryption.
     * But, e.g. for Fujisaki-Okamoto transform, the randomness is the result of mangling the message
     * with additional randomness.
     *
     * @param plainText
     * @param publicKey
     * @param random
     * @return
     */
    public CipherText encrypt(PlainText plainText, EncryptionKey publicKey, BigInteger random) {
        if (publicKey == null || plainText == null)
            throw new IllegalArgumentException("The arguments must not be null.");
        if (!(publicKey instanceof ElgamalPublicKey))
            throw new IllegalArgumentException("The specified public key is invalid.");
        if (!(plainText instanceof ElgamalPlainText))
            throw new IllegalArgumentException("The specified plaintext is invalid.");

        GroupElement groupElementPlaintext = ((ElgamalPlainText) plainText).getPlaintext();
        GroupElement g = ((ElgamalPublicKey) publicKey).getG();
        GroupElement h = ((ElgamalPublicKey) publicKey).getH();


        //c1 = g^r
        GroupElement c1 = g.pow(random);

        //c2 = h^r * plaintext
        GroupElement c2 = h.pow(random).op(groupElementPlaintext);

        return new ElgamalCipherText(c1, c2);
    }

    @Override
    public PlainText decrypt(CipherText cipherText, DecryptionKey privateKey) {
        if (privateKey == null || cipherText == null)
            throw new IllegalArgumentException("The arguments must not be null.");
        if (!(cipherText instanceof ElgamalCipherText))
            throw new IllegalArgumentException("The specified ciphertext is invalid.");
        if (!(privateKey instanceof ElgamalPrivateKey))
            throw new IllegalArgumentException("The specified private key is invalid.");

        ElgamalCipherText cpCipherText = (ElgamalCipherText) cipherText;
        ZnElement a = ((ElgamalPrivateKey) privateKey).getA();
        GroupElement u = cpCipherText.getC1().pow(a);
        GroupElement m = u.inv().op(cpCipherText.getC2());
        return new ElgamalPlainText(m);
    }

    /**
     * Generates a public/private-key pair for the specified group.
     *
     * @return A pair of a private key and the corresponding public key.
     */
    @Override
    public KeyPair generateKeyPair() {
        BigInteger sizeG = groupG.size();
        //choose a random element 'a' \in {0, ..., sizeG}
        Zn zn_random = new Zn(sizeG);
        ZnElement a = zn_random.getUniformlyRandomElement();

        //choose a random generator of the group
        GroupElement generator = groupG.getUniformlyRandomNonNeutral();

        GroupElement h = generator.pow(a);

        //create a elgamal private key
        ElgamalPrivateKey privateKey = new ElgamalPrivateKey(groupG, generator, a, h);

        //generate the public key (g, h)
        EncryptionKey publicKey = privateKey.getPublicKey();

        return new KeyPair(publicKey, privateKey);
    }

    @Override
    public PlainText getPlainText(Representation repr) {
        return new ElgamalPlainText(repr, groupG);
    }

    @Override
    public ElgamalCipherText getCipherText(Representation repr) {
        return new ElgamalCipherText(repr, groupG);
    }

    @Override
    public ElgamalPublicKey getEncryptionKey(Representation repr) {
        GroupElement g = groupG.getElement(repr.obj().get("g"));
        GroupElement h = groupG.getElement(repr.obj().get("h"));
        return new ElgamalPublicKey(groupG, g, h);

    }

    @Override
    public ElgamalPrivateKey getDecryptionKey(Representation repr) {
        //return new ElgamalPrivateKey(repr);
        ElgamalPublicKey pub = getEncryptionKey(repr.obj().get("publicKey"));

        Zn zn = new Zn(this.getGroup().size());
        ZnElement a = zn.getElement(repr.obj().get("a"));
        return new ElgamalPrivateKey(pub, a);
    }

    @Override
    public Representation getRepresentation() {
        ObjectRepresentation toReturn = new ObjectRepresentation();
        toReturn.put("groupG", new RepresentableRepresentation(groupG));
        return toReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ElgamalEncryption) {
            ElgamalEncryption other = (ElgamalEncryption) o;
            return groupG.equals(other.groupG);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return groupG.hashCode();
    }

}
