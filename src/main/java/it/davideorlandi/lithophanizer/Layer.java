package it.davideorlandi.lithophanizer;

/**
 * A single model layer, with an outer and an inner perimeter.
 * @param outerPerimeter Points of the outer perimeter. Each point is a 3 float vector.
 * @param innerPerimeter Points of the inner perimeter. Each point is a 3 float vector.
 */
public record Layer(double [] [] outerPerimeter, double [] [] innerPerimeter)
{
    // nessun metodo
}
