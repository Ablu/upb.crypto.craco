package de.upb.crypto.craco.sig.bbs;

import de.upb.crypto.math.factory.BilinearGroup;
import de.upb.crypto.math.factory.BilinearGroupFactory;
import de.upb.crypto.math.structures.zn.HashIntoZn;

/**
 * Does the key generation for the BBS-B signature scheme respectively organization in the anonymous credential system
 *
 * @author Fabian Eidens
 */
public class BBSBKeyGen {
    private BBSBPublicParameter pp;
    private BilinearGroup group;

    public BBSBPublicParameter doKeyGen(int securityParameter) {
        return doKeyGen(securityParameter, false);
    }

    /**
     * Generates public parameters for BBSB
     *
     * @param securityParameter
     * @param debugMode         if set to true, uses insecure but fast groups.
     * @return
     */
    public BBSBPublicParameter doKeyGen(int securityParameter, boolean debugMode) {
        // First, get bilinear group from the factory
        BilinearGroupFactory fac = new BilinearGroupFactory(securityParameter);
        fac.setDebugMode(debugMode);
        fac.setRequirements(BilinearGroup.Type.TYPE_1);
        group = fac.createBilinearGroup();

        return doKeyGen(group);
    }

    public BBSBPublicParameter doKeyGen(BilinearGroup group) {
        pp = new BBSBPublicParameter();

        pp.setGroupG1(group.getG1());
        pp.setGroupG2(group.getG2());
        pp.setGroupGT(group.getGT());

        pp.setGroupHom(group.getHomomorphismG2toG1());
        pp.setHashIntoZp((HashIntoZn) group.getHashIntoZGroupExponent());
        pp.setBilinearMap(group.getBilinearMap());

        pp.setG2(pp.getGroupG1().getUniformlyRandomElement());
        pp.setG1(pp.getGroupHom().apply(pp.getG2()));

        return pp;
    }

    public BilinearGroup getGroup() {
        return group;
    }
}
