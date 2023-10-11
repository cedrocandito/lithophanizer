package it.davideorlandi.lithophanizer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Main class of the application.
 */
public class Main
{
    // default option values

    private static final String DEFAULT_DIAMETER = "150.0";

    private static final String DEFAULT_MIN_THICKNESS = "1.0";

    private static final String DEFAULT_MAX_THICKNESS = "5.0";

    private static final String DEFAULT_TOP_BORDER_HEIGHT = "4.0";

    private static final String DEFAULT_TOP_BORDER_THICKNESS = "3.0";

    private static final String DEFAULT_BOTTOM_BORDER_HEIGHT = "4.0";

    private static final String DEFAULT_BOTTOM_BORDER_THICKNESS = "3.0";

    // CLI options

    private static final String IMAGE_SHORT_OPTION = "i";

    private static final String IMAGE_LONG_OPTION = "image";

    private static final String OUTPUT_SHORT_OPTION = "o";

    private static final String OUTPUT_LONG_OPTION = "output";

    private static final String DIAMETER_SHORT_OPTION = "d";

    private static final String DIAMETER_LONG_OPTION = "diameter";

    private static final String MIN_THICKNESS_SHORT_OPTION = "n";

    private static final String MIN_THICKNESS_LONG_OPTION = "thickness-min";

    private static final String MAX_THICKNESS_SHORT_OPTION = "x";

    private static final String MAX_THICKNESS_LONG_OPTION = "thickness-max";

    private static final String TOP_BORDER_HEIGHT_LONG_OPTION = "border-top-height";

    private static final String TOP_BORDER_THICKNESS_LONG_OPTION = "border-top-thickness";

    private static final String BOTTOM_BORDER_HEIGHT_LONG_OPTION = "border-bottom-height";

    private static final String BOTTOM_BORDER_THICKNESS_LONG_OPTION = "border-bottom-thickness";

    /**
     * Entry point.
     * @param args command line arguments.
     */
    public static void main(final String [] args)
    {
        Options op = new Options();

        op.addOption(Option.builder().option(IMAGE_SHORT_OPTION).longOpt(IMAGE_LONG_OPTION).desc(
                "Path to png image file (required).").hasArg().argName("path").required().type(
                        String.class).build());
        op.addOption(Option.builder().option(OUTPUT_SHORT_OPTION).longOpt(OUTPUT_LONG_OPTION).desc(
                "Path to destination .stl file (required).").hasArg().argName(
                        "path").required().type(String.class).build());
        op.addOption(
                Option.builder().option(DIAMETER_SHORT_OPTION).longOpt(DIAMETER_LONG_OPTION).desc(
                        "Diameter of the lithophane cylinder, in millimeters, measured on the flat surface; default "
                                + DEFAULT_DIAMETER + ".").hasArg().argName("number").type(
                                        Float.class).build());

        op.addOption(Option.builder().option(MIN_THICKNESS_SHORT_OPTION).longOpt(
                MIN_THICKNESS_LONG_OPTION).desc(
                        "Minimum (ligthtest) thickness of the lithophane; default "
                                + DEFAULT_MIN_THICKNESS + ".").hasArg().argName("number").type(
                                        Float.class).build());

        op.addOption(Option.builder().option(MAX_THICKNESS_SHORT_OPTION).longOpt(
                MAX_THICKNESS_LONG_OPTION).desc(
                        "Maximum (darkest) thickness of the lithophane; default "
                                + DEFAULT_MAX_THICKNESS + ".").hasArg().argName("number").type(
                                        Float.class).build());

        op.addOption(Option.builder().longOpt(TOP_BORDER_HEIGHT_LONG_OPTION).desc(
                "Height of top border (0 = no border); default " + DEFAULT_TOP_BORDER_HEIGHT
                        + ".").hasArg().argName("number").type(Float.class).build());

        op.addOption(Option.builder().longOpt(TOP_BORDER_THICKNESS_LONG_OPTION).desc(
                "Thickness of top border; default " + DEFAULT_TOP_BORDER_THICKNESS
                        + ".").hasArg().argName("number").type(Float.class).build());

        op.addOption(Option.builder().longOpt(BOTTOM_BORDER_HEIGHT_LONG_OPTION).desc(
                "Height of bottom border (0 = no border); default " + DEFAULT_BOTTOM_BORDER_HEIGHT
                        + ".").hasArg().argName("number").type(Float.class).build());

        op.addOption(Option.builder().longOpt(BOTTOM_BORDER_THICKNESS_LONG_OPTION).desc(
                "Thickness of bottom border; default " + DEFAULT_BOTTOM_BORDER_THICKNESS
                        + ".").hasArg().argName("number").type(Float.class).build());

        try
        {
            if (args.length == 0)
            {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("lithophanizer", op, true);
            }
            else
            {
                CommandLineParser parser = new DefaultParser();
                CommandLine cmd = parser.parse(op, args);

                String imagePath = cmd.getOptionValue(IMAGE_LONG_OPTION);
                String outputPath = cmd.getOptionValue(OUTPUT_LONG_OPTION);
                Float diameter = Float.valueOf(
                        cmd.getOptionValue(DIAMETER_LONG_OPTION, DEFAULT_DIAMETER));
                Float minThickness = Float.valueOf(
                        cmd.getOptionValue(MIN_THICKNESS_LONG_OPTION, DEFAULT_MIN_THICKNESS));
                Float maxThickness = Float.valueOf(
                        cmd.getOptionValue(MAX_THICKNESS_LONG_OPTION, DEFAULT_MAX_THICKNESS));
                Float topBorderThickness = Float.valueOf(cmd.getOptionValue(
                        TOP_BORDER_THICKNESS_LONG_OPTION, DEFAULT_TOP_BORDER_THICKNESS));
                Float topBorderHeight = Float.valueOf(cmd.getOptionValue(
                        TOP_BORDER_HEIGHT_LONG_OPTION, DEFAULT_TOP_BORDER_HEIGHT));
                Float bottomBorderThickness = Float.valueOf(cmd.getOptionValue(
                        BOTTOM_BORDER_THICKNESS_LONG_OPTION, DEFAULT_BOTTOM_BORDER_THICKNESS));
                Float bottomBorderHeight = Float.valueOf(cmd.getOptionValue(
                        BOTTOM_BORDER_HEIGHT_LONG_OPTION, DEFAULT_BOTTOM_BORDER_HEIGHT));

                System.out.println(String.format(
                        "Image=%s, output=%s, diameter=%f, minThickness=%f, maxThickness=%f, topBorderHeight=%f, topBorderThickness=%f, bottomBorderHeight=%f, bottomBorderThickness=%f",
                        imagePath, outputPath, diameter, minThickness, maxThickness,
                        topBorderHeight, topBorderThickness, bottomBorderHeight,
                        bottomBorderThickness));
            }
        }
        catch (ParseException pe)
        {
            System.err.println(pe.getMessage());
        }
    }
}
