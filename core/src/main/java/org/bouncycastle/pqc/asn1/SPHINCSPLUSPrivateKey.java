package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.*;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

/**
 * See https://datatracker.ietf.org/doc/draft-uni-qsckeys-sphincsplus/00/ for details
 * ASN.1 Encoding for a
 * SphincsPlus private key for fully populated:
 * <pre>
 *   SPHINCSPLUSPrivateKey ::= SEQUENCE {
 *     version          INTEGER {v2(1)}     --syntax version 2 (round 3)
 *     skseed          OCTET STRING,        --n-byte private key seed
 *     skprf           OCTET STRING,        --n-byte private key seed
 *     PublicKey       SPHINCSPLUSPublicKey --public key
 *   }
 * </pre>
 */
public class SPHINCSPLUSPrivateKey
    extends ASN1Object
{

    private int version;
    private byte[] skseed;
    private byte[] skprf;
    private SPHINCSPLUSPublicKey PublicKey;

    public int getVersion()
    {
        return version;
    }

    public byte[] getSkseed()
    {
        return skseed;
    }

    public byte[] getSkprf()
    {
        return skprf;
    }

    public SPHINCSPLUSPublicKey getPublicKey()
    {
        return PublicKey;
    }

    public SPHINCSPLUSPrivateKey(int version, byte[] skseed, byte[] skprf)
    {
        this(version, skseed, skprf, null);
    }

    public SPHINCSPLUSPrivateKey(int version, byte[] skseed, byte[] skprf, SPHINCSPLUSPublicKey publicKey)
    {
        this.version = version;
        this.skseed = skseed;
        this.skprf = skprf;
        PublicKey = publicKey;
    }

    public SPHINCSPLUSPrivateKey(ASN1Sequence seq)
    {
        version = BigIntegers.intValueExact(ASN1Integer.getInstance(seq.getObjectAt(0)).getValue());
        if (version != 0)
        {
            throw new IllegalArgumentException("unrecognized version");
        }
        skseed = Arrays.clone(ASN1OctetString.getInstance(seq.getObjectAt(1)).getOctets());

        skprf = Arrays.clone(ASN1OctetString.getInstance(seq.getObjectAt(2)).getOctets());

        // todo optional publickey
        if(seq.size() == 4)
        {
            PublicKey = SPHINCSPLUSPublicKey.getInstance(seq.getObjectAt(3));
        }
    }

    public ASN1Primitive toASN1Primitive()
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(new ASN1Integer(version));
        v.add(new DEROctetString(skseed));
        v.add(new DEROctetString(skprf));

        // todo optional publickey
        if(PublicKey != null)
        {
            v.add(new SPHINCSPLUSPublicKey(PublicKey.getPkseed(), PublicKey.getPkroot()));
        }

        return new DERSequence(v);
    }
    public static  SPHINCSPLUSPrivateKey getInstance(Object o)
    {
        if (o instanceof SPHINCSPLUSPrivateKey)
        {
            return (SPHINCSPLUSPrivateKey)o;
        }
        else if (o != null)
        {
            return new SPHINCSPLUSPrivateKey(ASN1Sequence.getInstance(o));
        }

        return null;
    }
}
