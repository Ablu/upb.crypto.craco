package de.upb.crypto.craco.abe.fuzzy.small;

import de.upb.crypto.craco.abe.fuzzy.large.Identity;
import de.upb.crypto.craco.interfaces.DecryptionKey;
import de.upb.crypto.craco.interfaces.abe.SetOfAttributes;
import de.upb.crypto.math.interfaces.structures.Group;
import de.upb.crypto.math.interfaces.structures.GroupElement;
import de.upb.crypto.math.serialization.Representation;
import de.upb.crypto.math.serialization.annotations.AnnotatedRepresentationUtil;
import de.upb.crypto.math.serialization.annotations.Represented;
import de.upb.crypto.math.serialization.annotations.RepresentedMap;

import java.math.BigInteger;
import java.util.Map;

/**
 * The {@link DecryptionKey} for the {@link IBEFuzzySW05Small}.
 * <p>
 * This key is generated by
 * {@link IBEFuzzySW05Small#generateDecryptionKey(de.upb.crypto.pg.interfaces.pe.MasterSecret, Identity)}
 * .
 *
 * @author Mirko Jürgens
 */
public class IBEFuzzySW05SmallDecryptionKey implements DecryptionKey {

    @Represented
    private SetOfAttributes identity;

    @RepresentedMap(keyRestorer = @Represented, valueRestorer = @Represented(structure = "groupG1", recoveryMethod =
            GroupElement.RECOVERY_METHOD))
    private Map<BigInteger, GroupElement> d;

    @SuppressWarnings("unused")
    private Group groupG1;

    public IBEFuzzySW05SmallDecryptionKey(SetOfAttributes id, Map<BigInteger, GroupElement> d) {
        this.identity = id;
        this.d = d;
    }

    public IBEFuzzySW05SmallDecryptionKey(Representation repr, IBEFuzzySW05SmallPublicParameters kpp) {
        groupG1 = kpp.getGroupG1();
        AnnotatedRepresentationUtil.restoreAnnotatedRepresentation(repr, this);
    }

    @Override
    public Representation getRepresentation() {
        return AnnotatedRepresentationUtil.putAnnotatedRepresentation(this);
    }

    public SetOfAttributes getIdentity() {
        return identity;
    }

    public Map<BigInteger, GroupElement> getD() {
        return d;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((d == null) ? 0 : d.hashCode());
        result = prime * result + ((identity == null) ? 0 : identity.hashCode());
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
        IBEFuzzySW05SmallDecryptionKey other = (IBEFuzzySW05SmallDecryptionKey) obj;
        if (d == null) {
            if (other.d != null)
                return false;
        } else if (!d.equals(other.d))
            return false;
        if (identity == null) {
            if (other.identity != null)
                return false;
        } else if (!identity.equals(other.identity))
            return false;
        return true;
    }

}
