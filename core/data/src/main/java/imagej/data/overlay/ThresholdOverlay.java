/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2012 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package imagej.data.overlay;

import net.imglib2.Positionable;
import net.imglib2.RealPositionable;
import net.imglib2.img.ImgPlus;
import net.imglib2.meta.AxisType;
import net.imglib2.ops.condition.WithinRangeCondition;
import net.imglib2.ops.function.Function;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.pointset.ConditionalPointSet;
import net.imglib2.ops.pointset.HyperVolumePointSet;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.ops.pointset.PointSetRegionOfInterest;
import net.imglib2.roi.RegionOfInterest;
import net.imglib2.type.numeric.RealType;
import imagej.ImageJ;
import imagej.data.AbstractData;
import imagej.util.ColorRGB;

/**
 * 
 * @author Barry DeZonia
 *
 */
public class ThresholdOverlay extends AbstractData implements Overlay {

	private final ImgPlus<? extends RealType<?>> imgPlus;
	private final ConditionalPointSet points;
	private final WithinRangeCondition<? extends RealType<?>> condition;
	private final RegionOfInterest regionAdapter;

	public ThresholdOverlay(ImageJ context, ImgPlus<? extends RealType<?>> imgPlus) {
		setContext(context);
		this.imgPlus = imgPlus;
		Function<long[],? extends RealType<?>> function = new RealImageFunction(imgPlus, imgPlus.firstElement());
		condition = new WithinRangeCondition(function, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		long[] dims = new long[imgPlus.numDimensions()];
		imgPlus.dimensions(dims);
		HyperVolumePointSet volume = new HyperVolumePointSet(dims);
		points = new ConditionalPointSet(volume, condition);
		regionAdapter = new PointSetRegionOfInterest(points);
	}
	
	public ThresholdOverlay(ImageJ context, ImgPlus<? extends RealType<?>> imgPlus, double min, double max) {
		this(context, imgPlus);
		setRange(min,max);
	}
	
	public void setRange(double min, double max) {
		condition.setMin(min);
		condition.setMax(max);
		points.setCondition(condition); // this lets PointSet know it is changed
	}
	
	public void resetThreshold() {
		setRange(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	public PointSet getPoints() {
		return points;
	}
	
	@Override
	public void update() {
		// TODO
		// force the redraw of the associated jhotdraw figure
	}

	@Override
	public void rebuild() {
		// TODO Auto-generated method stub
		// update(); // would this be okay?
	}

	@Override
	public boolean isDiscrete() {
		return true;
	}

	@Override
	public int getAxisIndex(AxisType axis) {
		return imgPlus.getAxisIndex(axis);
	}

	@Override
	public AxisType axis(int d) {
		return imgPlus.axis(d);
	}

	@Override
	public void axes(AxisType[] axes) {
		imgPlus.axes(axes);
	}

	@Override
	public void setAxis(AxisType axis, int d) {
		imgPlus.setAxis(axis, d);
	}

	@Override
	public double calibration(int d) {
		return imgPlus.calibration(d);
	}

	@Override
	public void calibration(double[] cal) {
		for (int i = 0; i < cal.length; i++) cal[i] = calibration(i);
	}

	@Override
	public void calibration(float[] cal) {
		for (int i = 0; i < cal.length; i++) cal[i] = (float) calibration(i);
	}

	@Override
	public void setCalibration(double cal, int d) {
		if (cal == 1 && (d == 0 || d == 1)) return;
		throw new IllegalArgumentException("Cannot set calibration of a ThresholdOverlay");
	}

	@Override
	public void setCalibration(double[] cal) {
		for (int i = 0; i < cal.length; i++) setCalibration(cal[i], i);
	}

	@Override
	public void setCalibration(float[] cal) {
		for (int i = 0; i < cal.length; i++) setCalibration(cal[i], i);
	}

	@Override
	public int numDimensions() {
		return points.numDimensions();
	}

	@Override
	public long min(int d) {
		return points.min(d);
	}

	@Override
	public void min(long[] min) {
		for (int i = 0; i < min.length; i++) min[i] = min(i);
	}

	@Override
	public void min(Positionable min) {
		for (int i = 0; i < min.numDimensions(); i++) min.setPosition(min(i), i);
	}

	@Override
	public long max(int d) {
		return points.max(d);
	}

	@Override
	public void max(long[] max) {
		for (int i = 0; i < max.length; i++) max[i] = max(i);
	}

	@Override
	public void max(Positionable max) {
		for (int i = 0; i < max.numDimensions(); i++) max.setPosition(max(i), i);
	}

	@Override
	public double realMin(int d) {
		return min(d);
	}

	@Override
	public void realMin(double[] min) {
		for (int i = 0; i < min.length; i++) min[i] = realMin(i);
	}

	@Override
	public void realMin(RealPositionable min) {
		for (int i = 0; i < min.numDimensions(); i++) min.setPosition(realMin(i),i);
	}

	@Override
	public double realMax(int d) {
		return max(d);
	}

	@Override
	public void realMax(double[] max) {
		for (int i = 0; i < max.length; i++) max[i] = realMax(i);
	}

	@Override
	public void realMax(RealPositionable max) {
		for (int i = 0; i < max.numDimensions(); i++) max.setPosition(realMax(i),i);
	}

	@Override
	public void dimensions(long[] dimensions) {
		points.dimensions(dimensions);
	}

	@Override
	public long dimension(int d) {
		return points.dimension(d);
	}

	@Override
	public RegionOfInterest getRegionOfInterest() {
		return regionAdapter;
	}

	private int alpha = 0;
	
	@Override
	public int getAlpha() {
		return alpha;
	}

	@Override
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	private ColorRGB fillColor = new ColorRGB(255, 0, 0);

	@Override
	public ColorRGB getFillColor() {
		return fillColor;
	}

	@Override
	public void setFillColor(ColorRGB fillColor) {
		this.fillColor = fillColor;
	}

	private ColorRGB lineColor = new ColorRGB(255, 0, 0);

	@Override
	public ColorRGB getLineColor() {
		return lineColor;
	}

	@Override
	public void setLineColor(ColorRGB lineColor) {
		this.lineColor = lineColor;
	}

	@Override
	public double getLineWidth() {
		return 1; // or zero?
	}

	@Override
	public void setLineWidth(double lineWidth) {
		// ignore
	}

	private LineStyle lineStyle = LineStyle.NONE;
	
	@Override
	public LineStyle getLineStyle() {
		return lineStyle;
	}

	@Override
	public void setLineStyle(LineStyle style) {
		this.lineStyle = style;
	}

	private ArrowStyle lineStartArrowStyle = ArrowStyle.NONE;
	
	@Override
	public ArrowStyle getLineStartArrowStyle() {
		return lineStartArrowStyle;
	}

	@Override
	public void setLineStartArrowStyle(ArrowStyle style) {
		this.lineStartArrowStyle = style;
	}

	private ArrowStyle lineEndArrowStyle = ArrowStyle.NONE;
	
	@Override
	public ArrowStyle getLineEndArrowStyle() {
		return lineEndArrowStyle;
	}

	@Override
	public void setLineEndArrowStyle(ArrowStyle style) {
		lineEndArrowStyle = style;
	}

	@Override
	public ThresholdOverlay duplicate() {
		return new ThresholdOverlay(getContext(), imgPlus, condition.getMin(), condition.getMax());
	}

	@Override
	public void move(double[] deltas) {
		// do nothing - thresholds don't move though space
	}
}
