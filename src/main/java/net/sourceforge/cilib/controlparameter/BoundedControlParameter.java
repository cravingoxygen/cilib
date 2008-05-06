/*
 * Copyright (C) 2003 - 2008
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science
 * University of Pretoria
 * South Africa
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.cilib.controlparameter;

import net.sourceforge.cilib.type.DomainParser;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * 
 * @author Gary Pampara
 *
 */
public abstract class BoundedControlParameter implements ControlParameter {
	
	protected Real parameter;
	protected String range = "";
	
	
	/**
	 * Create an instance of the {@linkplain BoundedControlParameter}.
	 */
	public BoundedControlParameter() {
		this.parameter = new Real();
	}
	
	
	/**
	 * Copy constructor.
	 * @param copy The instance which to copy.
	 */
	public BoundedControlParameter(BoundedControlParameter copy) {
		this.parameter = copy.parameter.getClone();
		this.range = new String(copy.range);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public abstract BoundedControlParameter getClone();

	
	/**
	 * {@inheritDoc} 
	 */
	public double getParameter() {
		return parameter.getReal();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public double getParameter(double min, double max) {
		throw new UnsupportedOperationException("");
	}

	
	/**
	 * {@inheritDoc}
	 */
	public void setParameter(double value) {
		this.parameter.setReal(value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateParameter() {
		update();
		clamp();
	}
	
	/**
	 * Update the {@linkplain ControlParameter}.
	 */
	protected abstract void update();
	
	/**
	 * Clamp the current paramter vaue between the lower and upper bound values.
	 */
	protected void clamp() {
		if (this.parameter.getReal() < this.parameter.getLowerBound())
			this.parameter.setReal(this.parameter.getLowerBound());
		else if (this.parameter.getReal() > this.parameter.getUpperBound())
			this.parameter.setReal(this.parameter.getUpperBound());
	}
	
	
	
	/**
	 * Get the lower bound of the {@linkplain ControlParameter}.
	 * @return The lower bound value.
	 */
	public double getLowerBound() {
		return this.parameter.getLowerBound();
	}
	
	
	/**
	 * Set the value of the lower bound.
	 * @param lower The value to set.
	 */
	public void setLowerBound(double lower) {
		this.parameter.setLowerBound(lower);
	}
	
	
	/**
	 * Get the upper bound for the {@linkplain ControlParameter}.
	 * @return The upper bound value.
	 */
	public double getUpperBound() {
		return this.parameter.getUpperBound();
	}
	
	
	/**
	 * Set the value for the upper bound. 
	 * @param value The value to set.
	 */
	public void setUpperBound(double value) {
		this.parameter.setUpperBound(value);
	}

	
	/**
	 * Get the range of the {@linkplain BoundedControlParameter}.
	 * @return The string representing the range of the parameter.
	 */
	public String getRange() {
		return range;
	}

	
	/**
	 * Set the range of the parameter.
	 * @param range The domain string representing the range.
	 */
	public void setRange(String range) {
		this.range = range;
		DomainParser parser = DomainParser.getInstance();
		parser.parse(this.range);
		Vector v = (Vector) parser.getBuiltRepresentation();
		
		if (v.getDimension() != 1) 
			throw new RuntimeException("Range incorrect in BoundedUpdateStrategy! Please correct");
		else
			this.parameter = (Real) v.get(0);
	}
	
}
