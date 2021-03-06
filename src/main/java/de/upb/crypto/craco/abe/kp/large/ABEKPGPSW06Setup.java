package de.upb.crypto.craco.abe.kp.large;

import de.upb.crypto.craco.common.WatersHash;
import de.upb.crypto.math.factory.BilinearGroup;
import de.upb.crypto.math.factory.BilinearGroupFactory;
import de.upb.crypto.math.interfaces.hash.HashIntoStructure;
import de.upb.crypto.math.interfaces.structures.GroupElement;
import de.upb.crypto.math.structures.zn.Zp;
import de.upb.crypto.math.structures.zn.Zp.ZpElement;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ABEKPGPSW06Setup {

    private ABEKPGPSW06PublicParameters pp;

    private ABEKPGPSW06MasterSecret msk;

    /**
     * Generate public parameters and the master secret.
     * <p>
     * Sets up public parameters and the master secret for a given security
     * parameter securityParameter. The parameter n specifies the maximum number
     * of attributes per cipher text. The parameter watersHash selects between
     * two possible hash functions: Waters Hash function or the hash function
     * from the group factory. The former yields a secure construction in the
     * standard model while the latter might be only secure in the random oracle
     * model. Typically, setting watersHash to false provides much faster
     * implementations.
     * <p>
     * To enable debugging modus, set debug to true. WARNING: This results in an
     * insecure instantiation of the underlying groups.
     *
     * @param securityParameter
     * @param n                 - maximum number of attributes per cipher text.
     * @param watersHash        - the hash function
     * @param debug             - enable debugging.
     */
    public void doKeyGen(int securityParameter, int n, boolean watersHash, boolean debug) {
        // Generate bilinear group
        BilinearGroupFactory fac = new BilinearGroupFactory(securityParameter);
        fac.setDebugMode(debug);
        fac.setRequirements(BilinearGroup.Type.TYPE_1, !watersHash, false, false);
        BilinearGroup group = fac.createBilinearGroup();

        doKeyGen(group, n, watersHash);
    }

    /**
     * Setup with pre-made group
     *
     * @param group      group used in the scheme
     * @param n
     * @param watersHash true: waters hash function; false: default hash function defined in the group
     */
    public void doKeyGen(BilinearGroup group, int n, boolean watersHash) {
        pp = new ABEKPGPSW06PublicParameters();
        pp.setN(BigInteger.valueOf(n));

        pp.setGroupG1(group.getG1());
        pp.setGroupG2(group.getG2());
        pp.setGroupGT(group.getGT());

        if (!watersHash) {
            pp.setHashToG1(group.getHashIntoG1());
        } else {
            HashIntoStructure hashToGroup = new WatersHash(pp.getGroupG1(), n + 1);
            pp.setHashToG1(hashToGroup);
        }

        Zp zp = new Zp(group.getG1().size());
        ZpElement y = zp.getUniformlyRandomUnit();

        pp.setE(group.getBilinearMap());

        // g_1 <- G_1 \setminus {1}
        pp.setG1_generator(pp.getGroupG1().getUniformlyRandomNonNeutral());

        // Y = e(g,g)^y = e(g^y, g)
        pp.setY(pp.getE().apply(pp.getG1_generator().pow(y), pp.getG1_generator()));

        msk = new ABEKPGPSW06MasterSecret(y);

        // T_i = g^t_i
        Map<BigInteger, GroupElement> T = new HashMap<>();

        for (int i = 0; i < n + 1; i++) {
            T.put(BigInteger.valueOf(i), pp.getG1_generator().pow(zp.getUniformlyRandomUnit()));
        }
        pp.setT(T);
    }

    public ABEKPGPSW06PublicParameters getPublicParameters() {
        return pp;
    }

    public ABEKPGPSW06MasterSecret getMasterSecret() {
        return msk;
    }
}
