package it.davideorlandi.lithophanizer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;

/**
 * Generates a cylindrical lithophane .stl file.
 */
public class Lithophanizer
{
    /** Outoput buffer size in bytes */
    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    private File imagePath;

    private File outputPath;

    private double diameter;

    private double radius;

    private double minThickness;

    private double maxThickness;

    private double topBorderThickness;

    private double topBorderHeight;

    private double topBorderTransition;

    private double bottomBorderThickness;

    private double bottomBorderHeight;

    private double bottomBorderTransition;

    private boolean flatInside;

    private int imageWidth;

    private int imageHeight;

    private BufferedImage image;

    private double angleStep;

    private double pixelStep;

    private Stl stl;

    /** Precalculated cosine for each column. */
    private double [] cos;

    /** Precalculated sine for each column. */
    private double [] sin;

    /**
     * Costruttore.
     * @param imagePath path of the source png image.
     * @param outputPath path of the destination .stl file.
     * @param diameter diameter of the lithophane (measured on the flat face).
     * @param minThickness minimum (lighttest) thickness.
     * @param maxThickness maximum (darkest) thickness.
     * @param topBorderThickness thickness of the top border.
     * @param topBorderHeight height of the top border; if 0 there will be no top border.
     * @param topBorderTransition length of the top border transition band; ignored if
     *        topBorderHeight<=0.
     * @param bottomBorderThickness thickness of the bottom border.
     * @param bottomBorderHeight height of the nottom border; if 0 there will be no bottom border.
     * @param bottomBorderTransition length of the bottom border transition band; ignored if
     *        bottomBorderHeight<=0.
     * @param flatInside if true the flat face will be on the inside; if false it will be on the
     *        outside.
     */
    public Lithophanizer(final File imagePath, final File outputPath, final double diameter,
            final double minThickness, final double maxThickness, final double topBorderThickness,
            final double topBorderHeight, final double topBorderTransition,
            final double bottomBorderThickness, final double bottomBorderHeight,
            final double bottomBorderTransition, final boolean flatInside)
    {
        this.imagePath = imagePath;
        this.outputPath = outputPath;
        this.diameter = diameter;
        this.radius = this.diameter / 2.0;
        this.minThickness = minThickness;
        this.maxThickness = maxThickness;
        this.topBorderThickness = topBorderThickness;
        this.topBorderHeight = topBorderHeight;
        this.topBorderTransition = topBorderTransition;
        this.bottomBorderThickness = bottomBorderThickness;
        this.bottomBorderHeight = bottomBorderHeight;
        this.bottomBorderTransition = bottomBorderTransition;
        this.flatInside = flatInside;
    }

    /**
     * Generates the .stl file.
     * @throws IOException errore di lettura o scrittura.
     */
    public void generateLithophane() throws IOException
    {
        if ( ! imagePath.exists())
            throw new IllegalArgumentException(
                    String.format("Image path \"%s\" not found.", imagePath));
        if ( ! imagePath.isFile())
            throw new IllegalArgumentException(
                    String.format("Image path \"%s\" is not a file.", imagePath));
        if ( ! (diameter > 0.0))
            throw new IllegalArgumentException("Diameter must be greater than zero");
        if ( ! (minThickness > 0.0))
            throw new IllegalArgumentException("Minimum thickness must be greater than zero");
        if ( ! (maxThickness > minThickness))
            throw new IllegalArgumentException(
                    "Mmaximum thickness must be greater than minimum thickness");

        this.image = ImageIO.read(imagePath);
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();

        this.angleStep = (2.0 * Math.PI) / imageWidth;
        this.pixelStep = (Math.PI * diameter) / imageWidth;

        System.out.format(Locale.US, "Diameter: %.1f mm; Height: %.1f mm; Pixel size: %.2f mm%n",
                diameter, imageHeight * pixelStep, pixelStep);

        // precalculate cos and sin
        this.cos = new double [imageWidth];
        this.sin = new double [imageWidth];
        for (int col = 0; col < imageWidth; col++)
        {
            double a = col * angleStep;
            cos[col] = Math.cos(a);
            sin[col] = Math.sin(a);
        }

        stl = new Stl(String.format("Cylindrical lithophane from %s", imagePath.getName()));

        // Layer bottomLayer = createBorderLayer(0.0, bottomBorderThickness);
        // Layer topLayer = createBorderLayer(bottomBorderHeight, bottomBorderThickness);
        // writeHorizontalSurface(bottomLayer, false);
        // writeVerticalSurface(bottomLayer, topLayer);
        // writeHorizontalSurface(topLayer, true);

        Layer previousLayer = createLithophaneLayer(0, 0.0);
        writeHorizontalSurface(previousLayer, false);
        for (int i = 1; i < imageHeight; i++)
        {
            Layer currentLayer = createLithophaneLayer(i, pixelStep);
            writeVerticalSurface(previousLayer, currentLayer);
            previousLayer = currentLayer;
        }
        writeHorizontalSurface(previousLayer, true);

        try (BufferedOutputStream stream = new BufferedOutputStream(
                new FileOutputStream(outputPath), BUFFER_SIZE))
        {
            stl.writeBinary(stream);
        }
    }

    /**
     * Adds lithophane layers for the specified image rows to the STL model.
     * @param previousLayer previous layer to build up from.
     * @param firstRow index of the first row of the image to generate.
     * @param lastRow index of the last row of the image to generate (inclusive).
     * @return the last generated layer.
     */
    private Layer createLithophaneBody(final Layer previousLayer, final int firstRow,
            final int lastRow)
    {
        Layer lastLayer = previousLayer;
        for (int i = firstRow; i <= lastRow; i++)
        {
            Layer currentLayer = createLithophaneLayer(i, pixelStep);
            writeVerticalSurface(lastLayer, currentLayer);
            lastLayer = currentLayer;
        }
        return lastLayer;
    }

    /**
     * Returns the B component of the HSB model from a pixel in 0-1 scale.
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @return brighntss (0-1)
     */
    private float getPixelBrighness(final int x, final int y)
    {
        Color c = new Color(image.getRGB(x, imageHeight - 1 - y));
        return Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null)[2];
    }

    /**
     * Creates a lithphane layer.
     * @param row image row.
     * @param zoffset vertical absolute coordinate of row 0 (not the current row).
     * @return created layer.
     */
    private Layer createLithophaneLayer(final int row, final double zoffset)
    {
        double [] [] outer = new double [imageWidth] [];
        double [] [] inner = new double [imageWidth] [];
        for (int col = 0; col < imageWidth; col++)
        {
            double brightness = getPixelBrighness(col, row);

            // brighter = thinner
            double thickness = ((1 - brightness) * (maxThickness - minThickness)) + minThickness;

            inner[col] = innerPoint(col, (row * pixelStep) + zoffset, thickness);
            outer[col] = outerPoint(col, (row * pixelStep) + zoffset, thickness);
        }

        return new Layer(outer, inner);
    }

    /**
     * Creates a "border" (fixed thickness) layer.
     * @param z vertical absolute coordinate.
     * @param borderThickness thickness of the border.
     * @return created layer.
     */
    private Layer createBorderLayer(final double z, final double borderThickness)
    {
        double [] [] outer = new double [imageWidth] [];
        double [] [] inner = new double [imageWidth] [];
        for (int col = 0; col < imageWidth; col++)
        {
            inner[col] = innerPoint(col, z, borderThickness);
            outer[col] = outerPoint(col, z, borderThickness);
        }

        return new Layer(outer, inner);
    }

    /**
     * Calculates an outer shell point.
     * @param col column of the point in image coordinates.
     * @param z vertical absolute coordinate.
     * @param thickness radial displacement from the base radius.
     * @return point vector.
     */
    private double [] outerPoint(final int col, final double z, final double thickness)
    {
        if (flatInside)
        {
            return new double [] { cos[col] * (radius + thickness), sin[col] * (radius + thickness),
                    z };
        }
        else
        {
            return new double [] { cos[col] * radius, sin[col] * radius, z };
        }
    }

    /**
     * Calculates an inner shell point.
     * @param col column of the point in image coordinates.
     * @param z vertical absolute coordinate.
     * @param thickness radial displacement from the base radius.
     * @return point vector.
     */
    private double [] innerPoint(final int col, final double z, final double thickness)
    {
        if (flatInside)
        {
            return new double [] { cos[col] * radius, sin[col] * radius, z };
        }
        else
        {
            return new double [] { cos[col] * (radius - thickness), sin[col] * (radius - thickness),
                    z };
        }
    }

    /**
     * Writes an horizontal surface to sìthe Stl stream.
     * @param layer outer and inner perimeters.
     * @param top if true a top surface is written, if false a bottom surface.
     */
    private void writeHorizontalSurface(final Layer layer, final boolean top)
    {
        int l = layer.innerPerimeter().length;
        for (int i = 0; i < l; i++)
        {
            // j = next point (may wrap)
            int j = (i + 1) % l;

            // a bottom surface by default, normal inverted if top
            stl.addTriangle(new Triangle(layer.innerPerimeter()[i], layer.outerPerimeter()[j],
                    layer.outerPerimeter()[i], top));
            stl.addTriangle(new Triangle(layer.innerPerimeter()[i], layer.innerPerimeter()[j],
                    layer.outerPerimeter()[j], top));
        }
    }

    /**
     * Writes a vertical mesh for the outer surface and one for the inner surface
     * @param layer1 lower layer points.
     * @param layer2 higher level points.
     */
    private void writeVerticalSurface(final Layer layer1, final Layer layer2)
    {
        int l = layer1.innerPerimeter().length;
        for (int i = 0; i < l; i++)
        {
            // j = next point (may wrap)
            int j = (i + 1) % l;

            // outer surface
            stl.addTriangle(new Triangle(layer1.outerPerimeter()[i], layer2.outerPerimeter()[j],
                    layer2.outerPerimeter()[i]));
            stl.addTriangle(new Triangle(layer1.outerPerimeter()[i], layer1.outerPerimeter()[j],
                    layer2.outerPerimeter()[j]));

            // inner surface
            stl.addTriangle(new Triangle(layer1.innerPerimeter()[i], layer2.innerPerimeter()[i],
                    layer2.innerPerimeter()[j]));
            stl.addTriangle(new Triangle(layer1.innerPerimeter()[i], layer2.innerPerimeter()[j],
                    layer1.innerPerimeter()[j]));

        }
    }

}
