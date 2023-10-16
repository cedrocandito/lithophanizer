package it.davideorlandi.lithophanizer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

/**
 * Generates a cylindrical lithophane .stl file.
 */
public class Lithophanizer
{
    private File imagePath;

    private File outputPath;

    private double diameter;

    private double radius;

    private double minThickness;

    private double maxThickness;

    private double topBorderThickness;

    private double topBorderHeight;

    private double bottomBorderThickness;

    private double bottomBorderHeight;

    private boolean flatInside;

    private int imageWidth;

    private int imageHeight;

    private BufferedImage image;

    private double angleStep;

    private Stl stl;

    /**
     * Costruttore.
     * @param imagePath path of the source png image.
     * @param outputPath path of the destination .stl file.
     * @param diameter diameter of the lithophane (measured on the flat face).
     * @param minThickness minimum (lighttest) thickness.
     * @param maxThickness maximum (darkest) thickness.
     * @param topBorderThickness thickness of the top border.
     * @param topBorderHeight height of the top border; if 0 there will be no top border.
     * @param bottomBorderThickness thickness of the bottom border.
     * @param bottomBorderHeight height of the nottom border; if 0 there will be no bottom border.
     * @param flatInside if true the flat face will be on the inside; if false it will be on the
     *        outside.
     */
    public Lithophanizer(final File imagePath, final File outputPath, final double diameter,
            final double minThickness, final double maxThickness, final double topBorderThickness,
            final double topBorderHeight, final double bottomBorderThickness,
            final double bottomBorderHeight, final boolean flatInside)
    {
        this.imagePath = imagePath;
        this.outputPath = outputPath;
        this.diameter = diameter;
        this.radius = this.diameter / 2.0;
        this.minThickness = minThickness;
        this.maxThickness = maxThickness;
        this.topBorderThickness = topBorderThickness;
        this.topBorderHeight = topBorderHeight;
        this.bottomBorderThickness = bottomBorderThickness;
        this.bottomBorderHeight = bottomBorderHeight;
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

        stl = new Stl("lithophane");

        // ?????
        // System.out.println(image.getWidth() + " x " + image.getHeight());
        // System.out.println("primo pixel: " + new Color(image.getRGB(0, 0)));
        // ?????

        this.angleStep = (2.0 * Math.PI) / imageWidth;

        for (double a = 0.0; a < (2.0 * Math.PI); a += angleStep)
        {
            bottomCell(a, bottomBorderThickness);
        }

        stl.writeAscii(new PrintWriter(System.out));
    }

    protected void bottomCell(final double angle, final double thickness)
    {
        double nextAngle = angle + angleStep;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double nextCos = Math.cos(nextAngle);
        double nextSin = Math.sin(nextAngle);
        double p1[] = new double [] { cos * radius, sin * radius, 0.0 };
        double p2[] = new double [] { cos * (radius + thickness), sin * (radius + thickness), 0.0 };
        double p3[] = new double [] { nextCos * (radius + thickness),
                nextSin * (radius + thickness), 0.0 };
        double p4[] = new double [] { nextCos * (radius), nextSin * (radius), 0.0 };

        stl.addTriangle(new Triangle(p1, p2, p4));
        stl.addTriangle(new Triangle(p2, p3, p4));
    }
}
