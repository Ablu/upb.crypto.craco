package de.upb.crypto.craco.abe.fuzzy.small;

import de.upb.crypto.craco.interfaces.abe.Attribute;
import de.upb.crypto.craco.interfaces.pe.MasterSecret;
import de.upb.crypto.math.serialization.Representation;
import de.upb.crypto.math.serialization.annotations.AnnotatedRepresentationUtil;
import de.upb.crypto.math.serialization.annotations.Represented;
import de.upb.crypto.math.serialization.annotations.RepresentedMap;
import de.upb.crypto.math.structures.zn.Zp;
import de.upb.crypto.math.structures.zn.Zp.ZpElement;

import java.util.Map;

/**
 * The {@link MasterSecret} for the {@link IBEFuzzySW05Small} generated
 * in the {@link IBEFuzzySW05SmallSetup}.
 *
 * @author Mirko Jürgens
 */
public class IBEFuzzySW05SmallMasterSecret implements MasterSecret {

    @Represented(structure = "zp", recoveryMethod = ZpElement.RECOVERY_METHOD)
    private ZpElement y;

    @RepresentedMap(keyRestorer = @Represented, valueRestorer = @Represented(structure = "zp", recoveryMethod =
            ZpElement.RECOVERY_METHOD))
    private Map<Attribute, ZpElement> t;

    @SuppressWarnings("unused")
    private Zp zp;

    public IBEFuzzySW05SmallMasterSecret(ZpElement y, Map<Attribute, ZpElement> t2) {
        this.y = y;
        this.t = t2;
    }

    public IBEFuzzySW05SmallMasterSecret(Representation repr, IBEFuzzySW05SmallPublicParameters kpp) {
        zp = new Zp(kpp.getGroupG1().size());
        AnnotatedRepresentationUtil.restoreAnnotatedRepresentation(repr, this);
    }

    @Override
    public Representation getRepresentation() {
        return AnnotatedRepresentationUtil.putAnnotatedRepresentation(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IBEFuzzySW05SmallMasterSecret other = (IBEFuzzySW05SmallMasterSecret) obj;
        if (t == null) {
            if (other.t != null)
                return false;
        } else if (!t.equals(other.t))
            return false;
        if (y == null) {
            if (other.y != null)
                return false;
        } else if (!y.equals(other.y))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((t == null) ? 0 : t.hashCode());
        result = prime * result + ((y == null) ? 0 : y.hashCode());
        return result;
    }

    public ZpElement getY() {
        return y;
    }

    public Map<Attribute, ZpElement> getT() {
        return t;
    }

}
