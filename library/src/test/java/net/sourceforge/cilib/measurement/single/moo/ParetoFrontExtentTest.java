/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.single.moo;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.moo.archive.Archive;
import net.sourceforge.cilib.problem.solution.Fitnesses;
import net.sourceforge.cilib.problem.solution.MOFitness;
import net.sourceforge.cilib.problem.solution.MinimisationFitness;
import net.sourceforge.cilib.problem.solution.OptimisationSolution;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.type.types.Type;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import static org.mockito.Mockito.*;

/**
 *
 */
@Ignore
public class ParetoFrontExtentTest {

    @Test
    public void results() {
        Algorithm algorithm = mock(Algorithm.class);

        MOFitness fitness01 = Fitnesses.create(new MinimisationFitness(0.0), new MinimisationFitness(1.0));
        final Type type01 = mock(Type.class, "type_01");

        MOFitness fitness02 = Fitnesses.create(new MinimisationFitness(0.5), new MinimisationFitness(0.5));
        final Type type02 = mock(Type.class, "type_02");

        MOFitness fitness03 = Fitnesses.create(new MinimisationFitness(1.0), new MinimisationFitness(0.0));
        final Type type03 = mock(Type.class, "type_03");

        // Cloning returns the mocked objects themselves.
        when(type01.getClone()).thenReturn(type01);
        when(type02.getClone()).thenReturn(type02);
        when(type03.getClone()).thenReturn(type03);

        OptimisationSolution solution01 = new OptimisationSolution(type01, fitness01);
        OptimisationSolution solution02 = new OptimisationSolution(type02, fitness02);
        OptimisationSolution solution03 = new OptimisationSolution(type03, fitness03);

        Archive archive = Archive.Provider.get();
        archive.add(solution01);
        archive.add(solution02);
        archive.add(solution03);

        Real extent = new ParetoFrontExtent().getValue(algorithm);
        Assert.assertEquals(Math.sqrt(2.0), extent.doubleValue(), 0.00001);
    }

    @AfterClass
    public static void teardown() {
        Archive.Provider.get().clear();
    }
}