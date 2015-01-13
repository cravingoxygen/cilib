/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.entity.initialisation;

import java.util.ArrayList;
import net.sourceforge.cilib.controlparameter.ConstantControlParameter;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.entity.Property;
import net.sourceforge.cilib.math.random.ProbabilityDistributionFunction;
import net.sourceforge.cilib.math.random.UniformDistribution;
import net.sourceforge.cilib.type.types.Type;
import net.sourceforge.cilib.type.types.Bounds;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.type.types.container.Vector;
import net.sourceforge.cilib.math.random.generator.Rand;


/**
 *
 * @param <E> The entity type.
 */
public class OrthogonalInitialisationStrategy<E extends Entity> implements InitialisationStrategy<E> {

    private static final long serialVersionUID = -7926839076670354209L;
	
    private ProbabilityDistributionFunction random;
	
	protected int numBasisVectors = -1;
	
	private ArrayList<Vector> basisVectors = null;
	private ArrayList<Bounds> scaleBoundsPerBasisVector = null;

	
	//Wait for the boundsPerDimension and numBasisVectors to be set.
	//Only then can the BasisVectors be generated and the centers & scaleBoundsPerBasisVector be calculated.
	public OrthogonalInitialisationStrategy() {
        this.random = new UniformDistribution();
	}

    public OrthogonalInitialisationStrategy(OrthogonalInitialisationStrategy copy) {
        this.random = copy.random;
		this.numBasisVectors = copy.numBasisVectors;
        this.basisVectors = copy.basisVectors;
        this.scaleBoundsPerBasisVector = copy.scaleBoundsPerBasisVector;
    }

    @Override
    public OrthogonalInitialisationStrategy getClone() {
        return new OrthogonalInitialisationStrategy(this);
    }

    @Override
    public void initialise(Property key, E entity) {
        Type type = entity.get(key);
        Vector vector = (Vector) type;
		
		if (basisVectors == null) {
			generateBasisVectors(vector);
		}

		//Is the Rand.nextInt sufficiently random?
		int basisIndex = Rand.nextInt(basisVectors.size());
		Vector basisVector = basisVectors.get(basisIndex); //Choose a random basisVector

		double lower = scaleBoundsPerBasisVector.get(basisIndex).getLowerBound();
		double higher = scaleBoundsPerBasisVector.get(basisIndex).getUpperBound();
		double scale = random.getRandomNumber(lower, higher);
		
        for (int i = 0; i < vector.size(); i++) {
            vector.setReal(i, basisVector.getReal(i)*scale);
        }
    }
	
	public void setNumBasisVectors(int numBasisVectors)
	{
		this.numBasisVectors = numBasisVectors;
	}
	
	public int getNumBasisVectors()
	{
		return numBasisVectors;
	}

	private void generateBasisVectors(Vector vector) {
		
		int nDims = vector.size();
		Vector[] xs = new Vector[numBasisVectors];
		double[] center = new double[nDims];
		//Use the builder to produced randomised reals (each real created with bounds accordingly)
		Vector.Builder builder = Vector.newBuilder();
		
		//Build vector at the center of the search space and initialize the VectorBuilder.
		for (int n = 0; n < nDims; n++) {
			Bounds bounds = vector.get(n).getBounds();
			center[n] = (bounds.getLowerBound()+bounds.getUpperBound())/2.0;
			builder.add(Real.valueOf(center[n], bounds));
		}
		
		//Create set of seed vectors
		for (int k = 0; k < numBasisVectors; k++) {
			xs[k] = builder.buildRandom();
		}
		
		//Use seed vectors to create numBasisVector-many orthogonal vectors
		basisVectors = new ArrayList();
		for (int k = 0; k < numBasisVectors; k++) {
			basisVectors.add(xs[k].orthogonalize(basisVectors));
		}
		
		//Now generate bounds for the scaling constants (one set of bounds for each basis vector)
		scaleBoundsPerBasisVector = new ArrayList();
		for (int k = 0; k < numBasisVectors; k++) {
			scaleBoundsPerBasisVector.add(generateScalarBounds(center, basisVectors.get(k), vector));
		}
	}
	
	//c = center/origin of line; d = direction
	private Bounds generateScalarBounds(double[] c, Vector d, Vector vector) {
		if (c.length != d.size()) {
			System.out.println("==========ERROR========== \n"+"Dimensions of center and direction vectors don't match");
		}
		
		boolean allZero = true;
		for (int i = 0; i < c.length; i++) {
			if (d.getReal(i) == 0) {
				continue;
			}
			allZero = false;
			Bounds bounds = vector.get(i).getBounds();
			//Find the line's pt of intersection with the High for ith dimension:
			double tmi = (bounds.getUpperBound()  - c[i]) / d.getReal(i);
			//Check whether the point of intersection violates any of the other boundary constraints
			if (!validIntersectionPoint(c, d, tmi, i, vector)) {
				continue;
			}
			
			//Find the line's pt of intersection with the Low for ith dimension:
			double tli = (bounds.getLowerBound() - c[i]) /  d.getReal(i);

			if (!validIntersectionPoint(c, d, tli, i, vector)) {
				continue;
			}
			//Maximally 2 valid points of intersection. If we have found them, then we are done.
			//(Intersection in corners will result in the same values for t)
			if (tli < tmi) {
				return new Bounds(tli, tmi);
			} else {
				return new Bounds(tmi, tli);
			}
		}
		if (allZero){
			return vector.get(0).getBounds();
		}
		
		System.out.println("==========ERROR========== \n"+"No point of intersection was found");
		return null;
	}
	
	
	private boolean validIntersectionPoint(double[] c, Vector d, double scale, int index, Vector vector) {
	
		for (int k = 0; k < c.length; k++) {
			if (k != index) {
				double xk = c[k] + scale* d.getReal(k);
				Bounds bounds = vector.get(k).getBounds();
				if (xk > bounds.getUpperBound() || xk < bounds.getLowerBound()) {
					return false;
				}
			}
		}
	
		return true;
	
	}
}
