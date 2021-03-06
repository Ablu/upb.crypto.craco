package de.upb.crypto.craco.kdf.uextr;

import de.upb.crypto.craco.interfaces.kdf.HashFamily;
import de.upb.crypto.math.serialization.BigIntegerRepresentation;
import de.upb.crypto.math.serialization.ObjectRepresentation;
import de.upb.crypto.math.serialization.Representation;
import de.upb.crypto.math.serialization.StringRepresentation;
import de.upb.crypto.math.structures.polynomial.Seed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An implementation of a q-wise d-independent hash family described in [1].
 * This implementation implements a random variable on an \epsilon -biased
 * source that was described in [2].
 * <p>
 * <p>
 * [1] Corollary 2.1 in Celis et. al., 2011, Balls and Bins: Smaller Hash
 * Families and Faster Evaluation
 * (https://www.microsoft.com/en-us/research/wp-content/uploads/2011/01/Main.pdf)
 * <p>
 * <p>
 * [2] Construction 5 in Alon et. al., 2002, Simple Constructions of Almost
 * k-wise Independent Random Variables
 * (http://www.math.tau.ac.il/~nogaa/PDFS/aghp4.pdf)
 *
 * @author Mirko Jürgens, refactoring: Denis Diemert
 */
public class KWiseDeltaDependentHashFamily implements HashFamily {

    private static final Logger logger = LogManager.getLogger("KWiseDeltaDependentHashFamilyLogger");

    private int inputLength;

    private int outputLength;

    private double k;

    private double delta;

    private int seedLength;

    /**
     * Sets up a K-Wise Delta-Dependent Hash-Family. This family can yield a
     * seeded {@link KWiseDeltaDependentHashFunction}.
     *
     * @param k
     * @param logDelta
     * @param inputLength  the input-length of a hash function
     * @param outputLength the input-length of a hash function
     */
    public KWiseDeltaDependentHashFamily(double k, double logDelta, int inputLength, int outputLength) {
        super();
        logger.debug("Setting up a KWiseDeltaDependentHashFamily with k: " + k + " logDelta: " + logDelta
                + " inputLength: " + inputLength + " outputLength: " + outputLength);
        this.inputLength = inputLength;
        this.outputLength = outputLength;
        this.k = k;
        this.delta = logDelta;

        int sampleLength = inputLength * outputLength;

        // double temp = Math.pow(2, (-k * outputLength * 0.5));
        double logTemp = -k * outputLength * 0.5;

        // double epsilon = delta * temp;
        double logEpsilon = logDelta + logTemp;

        // double temp2 = Math.pow(2, -logEpsilon) / (sampleLength - 1);
        double m = -logEpsilon + (Math.log(sampleLength - 1) / Math.log(2));

        // double spampleSpace = 1 / temp2;
        seedLength = (int) (2 * m);
        logger.debug("Calculated seed-length of: " + seedLength);
    }

    public KWiseDeltaDependentHashFamily(Representation repr) {
        ObjectRepresentation obj = (ObjectRepresentation) repr;
        inputLength = obj.get("inputLength").bigInt().getInt();
        outputLength = obj.get("outputLength").bigInt().getInt();
        k = new Double(obj.get("k").str().get());
        delta = new Double(obj.get("delta").str().get());
        seedLength = obj.get("seedLength").bigInt().getInt();
    }

    @Override
    public Representation getRepresentation() {
        ObjectRepresentation obj = new ObjectRepresentation();
        obj.put("inputLength", new BigIntegerRepresentation(inputLength));
        obj.put("outputLength", new BigIntegerRepresentation(outputLength));
        obj.put("k", new StringRepresentation(Double.toString(k)));
        obj.put("delta", new StringRepresentation(Double.toString(delta)));
        obj.put("seedLength", new BigIntegerRepresentation(seedLength));
        return obj;
    }

    @Override
    public int getOutputLength() {
        return outputLength;
    }

    @Override
    public int seedLength() {
        return seedLength;
    }

    @Override
    public KWiseDeltaDependentHashFunction seedFunction(Seed seed) {
        return new KWiseDeltaDependentHashFunction(this, seed);
    }

    @Override
    public int getInputLength() {
        return inputLength;
    }

    public double getK() {
        return k;
    }

    public double getDelta() {
        return delta;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(delta);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + inputLength;
        temp = Double.doubleToLongBits(k);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + outputLength;
        result = prime * result + seedLength;
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
        KWiseDeltaDependentHashFamily other = (KWiseDeltaDependentHashFamily) obj;
        if (Double.doubleToLongBits(delta) != Double.doubleToLongBits(other.delta))
            return false;
        if (inputLength != other.inputLength)
            return false;
        if (Double.doubleToLongBits(k) != Double.doubleToLongBits(other.k))
            return false;
        if (outputLength != other.outputLength)
            return false;
        if (seedLength != other.seedLength)
            return false;
        return true;
    }

    @Override
    public KWiseDeltaDependentHashFunction getHashFunction(Representation repr) {
        return new KWiseDeltaDependentHashFunction(repr);
    }

}
