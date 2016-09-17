package me.noip.chankyin.mrmrg.geometry;

import lombok.NoArgsConstructor;

import org.apache.commons.lang3.StringUtils;

import me.noip.chankyin.mrmrg.utils.Utils;
import me.noip.chankyin.mrmrg.utils.io.SavedObject;
import me.noip.chankyin.mrmrg.utils.io.SavedProperty;

@SavedObject(1)
@NoArgsConstructor
public class VectorD{
	@SavedProperty(1) private double[] v;

	public VectorD(VectorD copy){
		v = copy.v;
	}

	public VectorD(double... v){
		this.v = v;
	}

	public int dimension(){
		return v.length;
	}

	public double[] toArray(){
		return v.clone();
	}

	public VectorD add(VectorD that){
		throwDim(that);
		return new VectorD(Utils.arrayMap(in -> in[0] + in[1], v, that.v));
	}

	public VectorD subtract(VectorD that){
		throwDim(that);
		return new VectorD(Utils.arrayMap(in -> in[0] - in[1], v, that.v));
	}

	public VectorD multiply(double d){
		return new VectorD(Utils.arrayMap(in -> in[0] * d, v));
	}

	public VectorD divide(double d){
		return new VectorD(Utils.arrayMap(in -> in[0] / d, v));
	}

	public double modulusSquared(){
		double out = 0d;
		for(Double coord : v){
			out += coord * coord;
		}
		return out;
	}

	public double modulus(){
		return Math.sqrt(modulusSquared());
	}

	public VectorD unit(){
		return multiply(1 / modulus());
	}

	public double distanceSquared(VectorD that){
		return subtract(that).modulusSquared();
	}

	public double distance(VectorD that){
		return Math.sqrt(distanceSquared(that));
	}

	public double dot(VectorD that){
		throwDim(that);
		double dot = 0d;
		for(int i = 0; i < v.length; i++){
			dot += v[i] * that.v[i];
		}
		return dot;
	}

	@Override
	public String toString(){
		return "{" + StringUtils.join(v, ',') + "}";
	}

	private void throwDim(VectorD that){
		if(v.length != that.v.length){
			throw new IllegalArgumentException(String.format(
					"Attempt to operate vectors of different dimensions (%d, %d)", v.length, that.v.length));
		}
	}

	private void throwMinDim(int minDim){
		if(v.length < minDim){
			throw new IllegalArgumentException(String.format(
					"Attempt to access %d-dimensional vector, %d+ dimensions required", v.length, minDim));
		}
	}

	private void throwEqDim(int required){
		if(v.length != required){
			throw new IllegalArgumentException(String.format(
					"Passed %d-dimensional vector, %d-dimensional vector required", v.length, required));
		}
	}

	public double getX(){
		throwMinDim(1);
		return v[0];
	}

	public double getY(){
		throwMinDim(2);
		return v[1];
	}

	public double getZ(){
		throwMinDim(3);
		return v[2];
	}

	public VectorD setX(double x){
		throwMinDim(1);
		double[] data = toArray();
		data[0] = x;
		return new VectorD(data);
	}

	public VectorD setY(double y){
		throwMinDim(2);
		double[] data = toArray();
		data[1] = y;
		return new VectorD(data);
	}

	public VectorD setZ(double z){
		throwMinDim(3);
		double[] data = toArray();
		data[2] = z;
		return new VectorD(data);
	}

	public double getCoords(int i){
		return v[i];
	}

	public static double pointLineDistanceSquared2D(
			@PositionVector @Dimension(2) VectorD lineFrom, @PositionVector @Dimension(2) VectorD lineTo,
			@PositionVector @Dimension(2) VectorD point){
		lineFrom.throwEqDim(2);
		lineTo.throwEqDim(2);
		point.throwEqDim(2);

		// bad implementation: Heron's formula
		double lineLengthSq = lineTo.distanceSquared(lineFrom);
		double pointFromLengthSq = lineFrom.distanceSquared(point);
		double pointToLengthSq = lineTo.distanceSquared(point);

		double d = pointFromLengthSq + pointToLengthSq - lineLengthSq;
		double areaSq = 0.0625d * (4 * pointFromLengthSq * pointToLengthSq - d * d);

		return areaSq / lineLengthSq;
	}

	public static double pointLineDistance2D(
			@PositionVector @Dimension(2) VectorD lineFrom, @PositionVector @Dimension(2) VectorD lineTo,
			@PositionVector @Dimension(2) VectorD point){
		return Math.sqrt(pointLineDistanceSquared2D(lineFrom, lineTo, point));
	}

	@Radians
	public double getDirectionBearing(){
		return getDirectionBearing(0, 1);
	}

	@Radians
	public double getDirectionBearing(int xOrd, int yOrd){
		throwMinDim(Math.min(xOrd, yOrd));
		return Math.atan2(v[yOrd], v[xOrd]);
	}
}
