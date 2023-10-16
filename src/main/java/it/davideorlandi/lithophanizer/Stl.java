package it.davideorlandi.lithophanizer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * STL model creation and serialization.
 */
public class Stl
{
    /** Name of this model. */
    protected final String name;

    /** All the triangles (facets) comprising the model. */
    protected final List<Triangle> triangles = new ArrayList<>();

    /**
     * Constructor.
     * @param name model name.
     */
    public Stl(final String name)
    {
        this.name = name;
    }

    /**
     * Adds a triangle to the STL model.
     * @param triangle trignale to be added.
     */
    public void addTriangle(final Triangle triangle)
    {
        triangles.add(triangle);
    }

    /**
     * Writes the model to an ASCII STL file.
     * @param writer destination writer.
     * @throws IOException error while writing.
     */
    public void writeAscii(final PrintWriter writer) throws IOException
    {
        writer.println(String.format("solid %s", name));
        for (Triangle triangle : triangles)
        {
            triangle.writeAscii(writer);
        }
        writer.println("endsolid");
        writer.flush();
    }
}
