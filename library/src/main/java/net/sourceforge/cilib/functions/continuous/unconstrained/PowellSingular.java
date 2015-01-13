/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.type.types.container.Vector;

import com.google.common.base.Preconditions;

/**
 * <p><b>Powell.</b></p>
 *
 * <p>Minimum:
 * <ul>
 * <li> &fnof;(<b>x</b>*) = 0.0</li>
 * <li> <b>x</b>* = (3, -1, 0, 1,..., 3, -1, 0, 1)</li>
 * <li> for x<sub>i</sub> in [-4, -5]</li>
 * </ul>
 * </p>
 *
 * <p>Characteristics:
 * <ul>
 * <li>Only defined for 4*N dimensions</li>
 * </ul>
 * </p>
 *
 * R(-4, 5)^32
 *
 */
public class PowellSingular extends ContinuousFunction {

    /**
     * {@inheritDoc}
     */
    @Override
    public Double f(Vector input) {
        Preconditions.checkArgument(input.size() > 2, "PowellSingular function is only meaningful for more than 2 dimensions");

		double result = 0.0;
		for (int k = 1; k < input.size()-2; k++) {
			result += Math.pow(input.doubleValueOf(k-1) + 10*input.doubleValueOf(k), 2);
			result += 5*Math.pow(input.doubleValueOf(k+1) - input.doubleValueOf(k+2), 2);
			result += Math.pow(input.doubleValueOf(k) - 2* input.doubleValueOf(k+1), 4);
			result += 10*Math.pow(input.doubleValueOf(k-1)-input.doubleValueOf(k+2), 4);
		}
	
		return result;
    }
}
