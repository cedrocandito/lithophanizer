package it.davideorlandi.lithophanizer;

import java.io.File;

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

    private static final String DEFAULT_MIN_THICKNESS = "0.6";

    private static final String DEFAULT_MAX_THICKNESS = "3.0";

    private static final String DEFAULT_TOP_BORDER_HEIGHT = "3.0";

    private static final String DEFAULT_TOP_BORDER_THICKNESS = "3.0";

    private static final String DEFAULT_TOP_BORDER_TRANSITION = "2.0";

    private static final String DEFAULT_BOTTOM_BORDER_HEIGHT = "3.0";

    private static final String DEFAULT_BOTTOM_BORDER_THICKNESS = "3.0";

    private static final String DEFAULT_BOTTOM_BORDER_TRANSITION = "2.0";

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

    private static final String TOP_BORDER_TRANSITION_LONG_OPTION = "top-bottom-transition";

    private static final String BOTTOM_BORDER_HEIGHT_LONG_OPTION = "border-bottom-height";

    private static final String BOTTOM_BORDER_THICKNESS_LONG_OPTION = "border-bottom-thickness";

    private static final String BOTTOM_BORDER_TRANSITION_LONG_OPTION = "border-bottom-transition";

    private static final String FLAT_INSIDE_LONG_OPTION = "flat-inside";

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
                                        Double.class).build());

        op.addOption(Option.builder().option(MIN_THICKNESS_SHORT_OPTION).longOpt(
                MIN_THICKNESS_LONG_OPTION).desc(
                        "Minimum (ligthtest) thickness of the lithophane; default "
                                + DEFAULT_MIN_THICKNESS + ".").hasArg().argName("number").type(
                                        Double.class).build());

        op.addOption(Option.builder().option(MAX_THICKNESS_SHORT_OPTION).longOpt(
                MAX_THICKNESS_LONG_OPTION).desc(
                        "Maximum (darkest) thickness of the lithophane; default "
                                + DEFAULT_MAX_THICKNESS + ".").hasArg().argName("number").type(
                                        Double.class).build());

        op.addOption(Option.builder().longOpt(TOP_BORDER_HEIGHT_LONG_OPTION).desc(
                "Height of top border (0 = no border); default " + DEFAULT_TOP_BORDER_HEIGHT
                        + ".").hasArg().argName("number").type(Double.class).build());

        op.addOption(Option.builder().longOpt(TOP_BORDER_THICKNESS_LONG_OPTION).desc(
                "Thickness of top border; default " + DEFAULT_TOP_BORDER_THICKNESS
                        + ".").hasArg().argName("number").type(Double.class).build());

        op.addOption(Option.builder().longOpt(TOP_BORDER_TRANSITION_LONG_OPTION).desc(
                "Lenght of the bottom transition band between top border and lithophane, ignored if border disable; default "
                        + DEFAULT_TOP_BORDER_TRANSITION + ".").hasArg().argName("number").type(
                                Double.class).build());

        op.addOption(Option.builder().longOpt(BOTTOM_BORDER_HEIGHT_LONG_OPTION).desc(
                "Height of bottom border (0 = no border); default " + DEFAULT_BOTTOM_BORDER_HEIGHT
                        + ".").hasArg().argName("number").type(Double.class).build());

        op.addOption(Option.builder().longOpt(BOTTOM_BORDER_THICKNESS_LONG_OPTION).desc(
                "Thickness of bottom border; default " + DEFAULT_BOTTOM_BORDER_THICKNESS
                        + ".").hasArg().argName("number").type(Double.class).build());

        op.addOption(Option.builder().longOpt(BOTTOM_BORDER_TRANSITION_LONG_OPTION).desc(
                "Lenght of the bottom transition band between bottom border and lithophane, ignored if border disable; default "
                        + DEFAULT_BOTTOM_BORDER_TRANSITION + ".").hasArg().argName("number").type(
                                Double.class).build());

        op.addOption(Option.builder().longOpt(FLAT_INSIDE_LONG_OPTION).desc(
                "Makes the patterned face outside and the flat face inside (default is flat outside).").build());

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

                File imagePath = new File(cmd.getOptionValue(IMAGE_LONG_OPTION));
                File outputPath = new File(cmd.getOptionValue(OUTPUT_LONG_OPTION));
                double diameter = Double.valueOf(
                        cmd.getOptionValue(DIAMETER_LONG_OPTION, DEFAULT_DIAMETER));
                double minThickness = Double.valueOf(
                        cmd.getOptionValue(MIN_THICKNESS_LONG_OPTION, DEFAULT_MIN_THICKNESS));
                double maxThickness = Double.valueOf(
                        cmd.getOptionValue(MAX_THICKNESS_LONG_OPTION, DEFAULT_MAX_THICKNESS));
                double topBorderThickness = Double.valueOf(cmd.getOptionValue(
                        TOP_BORDER_THICKNESS_LONG_OPTION, DEFAULT_TOP_BORDER_THICKNESS));
                double topBorderHeight = Double.valueOf(cmd.getOptionValue(
                        TOP_BORDER_HEIGHT_LONG_OPTION, DEFAULT_TOP_BORDER_HEIGHT));
                double topBorderTransition = Double.valueOf(cmd.getOptionValue(
                        TOP_BORDER_TRANSITION_LONG_OPTION, DEFAULT_TOP_BORDER_TRANSITION));
                double bottomBorderThickness = Double.valueOf(cmd.getOptionValue(
                        BOTTOM_BORDER_THICKNESS_LONG_OPTION, DEFAULT_BOTTOM_BORDER_THICKNESS));
                double bottomBorderHeight = Double.valueOf(cmd.getOptionValue(
                        BOTTOM_BORDER_HEIGHT_LONG_OPTION, DEFAULT_BOTTOM_BORDER_HEIGHT));
                double bottomBorderTransition = Double.valueOf(cmd.getOptionValue(
                        BOTTOM_BORDER_TRANSITION_LONG_OPTION, DEFAULT_BOTTOM_BORDER_TRANSITION));
                boolean flatInside = cmd.hasOption(FLAT_INSIDE_LONG_OPTION);

                Lithophanizer lithophanizer = new Lithophanizer(imagePath, outputPath, diameter,
                        minThickness, maxThickness, topBorderThickness, topBorderHeight,
                        topBorderTransition, bottomBorderThickness, bottomBorderHeight,
                        bottomBorderTransition, flatInside);
                lithophanizer.generateLithophane();
            }
        }
        catch (ParseException | IllegalArgumentException simpleException)
        {
            System.err.println(simpleException.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
