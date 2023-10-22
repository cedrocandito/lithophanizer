package it.davideorlandi.lithophanizer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
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
     * Writes the model to a binary STL file.
     * @param stream destination stream.
     * @throws IOException error while writing.
     */
    public void writeBinary(final OutputStream stream) throws IOException
    {
        String truncatedName = name.length() <= 80 ? name : name.substring(0, 80);
        ByteBuffer header = ByteBuffer.allocate(80 + 4).order(ByteOrder.LITTLE_ENDIAN);
        header.put(String.format("%-80s", truncatedName).getBytes(StandardCharsets.US_ASCII));
        header.putInt(triangles.size());
        stream.write(header.array());
        for (Triangle triangle : triangles)
        {
            triangle.writeBinary(stream);
        }
        stream.flush();
    }
}
