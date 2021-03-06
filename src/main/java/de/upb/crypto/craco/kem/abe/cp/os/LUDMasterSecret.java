package de.upb.crypto.craco.kem.abe.cp.os;

import de.upb.crypto.craco.interfaces.pe.MasterSecret;
import de.upb.crypto.math.serialization.Representation;
import de.upb.crypto.math.structures.zn.Zp.ZpElement;


/**
 * Class for master secret key \alpha\in Z_p.
 *
 * @author peter.guenther
 */
public class LUDMasterSecret implements MasterSecret {

    private ZpElement alpha;

    public LUDMasterSecret(ZpElement alpha) {
        this.alpha = alpha;
    }

    public ZpElement getSecretExponent() {
        return alpha;
    }

    @Override
    public Representation getRepresentation() {
        return alpha.getRepresentation();
    }
}
