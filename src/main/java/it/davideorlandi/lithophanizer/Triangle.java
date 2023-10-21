package it.davideorlandi.lithophanizer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Model of a STL's triangle.
 */
public class Triangle
{
    /** Array triangle's three vertices (each one a 3 float vector). */
    protected final double [] [] vertices;

    /**
     * Constructor.
     * @param vertices array of 3 vertices vectors.
     */
    public Triangle(final double [] [] vertices)
    {
        this(vertices, false);
    }

    /**
     * Constructor.
     * <p>
     * Note: it's not enough to invert the normal in the output .stl file; the order of the vertices
     * must be inverted too.
     * </p>
     * @param vertices array of 3 vertices vectors. The array will be copied.
     * @param invertNormal if true, the normal will be inverted (left-handed).
     */
    public Triangle(final double [] [] vertices, final boolean invertNormal)
    {
        this.vertices = new double [3] [];
        if (invertNormal)
        {
            this.vertices[0] = vertices[2];
            this.vertices[1] = vertices[1];
            this.vertices[2] = vertices[0];
        }
        else
        {
            this.vertices[0] = vertices[0];
            this.vertices[1] = vertices[1];
            this.vertices[2] = vertices[2];
        }
    }

    /**
     * Constructor.
     * @param v1 first vertex.
     * @param v2 second vertex.
     * @param v3 third vertex.
     */
    public Triangle(final double [] v1, final double [] v2, final double [] v3)
    {
        this(new double [] [] { v1, v2, v3 });
    }

    /**
     * Constructor.
     * @param v1 first vertex.
     * @param v2 second vertex.
     * @param v3 third vertex.
     * @param invertNormal if true, the normal will be inverted (left-handed).
     */
    public Triangle(final double [] v1, final double [] v2, final double [] v3,
            final boolean invertNormal)
    {
        this(new double [] [] { v1, v2, v3 }, invertNormal);
    }

    /**
     * Calculates the normal of the triangle.
     * @return normal vector.
     */
    protected double [] calculateNormal()
    {
        double [] v1 = new double [] { vertices[1][0] - vertices[0][0],
                vertices[1][1] - vertices[0][1], vertices[1][2] - vertices[0][2] };
        double [] v2 = new double [] { vertices[2][0] - vertices[0][0],
                vertices[2][1] - vertices[0][1], vertices[2][2] - vertices[0][2] };
        double [] n = new double [] { (v1[1] * v2[2]) - (v1[2] * v2[1]),
                (v1[2] * v2[0]) - (v1[0] * v2[2]), (v1[0] * v2[1]) - (v1[1] * v2[0]) };
        // normalize
        double l = Math.sqrt((n[0] * n[0]) + (n[1] * n[1]) + (n[2] * n[2]));
        n[0] /= l;
        n[1] /= l;
        n[2] /= l;

        return n;
    }

    /**
     * Writes this triangle to a binary stream.
     * @param stream outout stream (buffered is better).
     * @throws IOException error writing data.
     */
    public void writeBinary(final OutputStream stream) throws IOException
    {
        ByteBuffer b = ByteBuffer.allocate(50).order(ByteOrder.LITTLE_ENDIAN);
        putVectorIntoByteBuffer(b, calculateNormal());
        for (int v = 0; v < 3; v++)
        {
            putVectorIntoByteBuffer(b, vertices[v]);
        }
        b.putShort((short) 0);

        stream.write(b.array());
    }

    /**
     * Put a vector of 3 double as 3 floats in a ByteBuffer.
     * @param bb destination buffer.
     * @param vector array of x, y, z.
     */
    private void putVectorIntoByteBuffer(final ByteBuffer bb, final double [] vector)
    {
        for (int i = 0; i < 3; i++)
        {
            bb.putFloat((float) vector[i]);
        }
    }
}
