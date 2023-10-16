package it.davideorlandi.lithophanizer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

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
        this.vertices = vertices;
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
     * Writes a triangle to the specified writer.
     * @param writer output writer.
     * @throws IOException error while writing;
     */
    public void writeAscii(final PrintWriter writer) throws IOException
    {
        double [] normal = calculateNormal();
        writer.println(String.format(" facet normal %s", formatVector(normal)));
        writer.println("  outer loop");
        for (double [] vertex : vertices)
        {
            writer.println(String.format("   vertex %s", formatVector(vertex)));
        }
        writer.println("  endloop");
        writer.println(" endfacet");
    }

    /**
     * Formats a vector as three float numbers separated by spaces.
     * @param vector vector to be formatted.
     * @return formatted vector.
     */
    protected String formatVector(final double [] vector)
    {
        return String.format(Locale.US, "%.3f, %.3f, %.3f", vector[0], vector[1], vector[2]);
    }
}