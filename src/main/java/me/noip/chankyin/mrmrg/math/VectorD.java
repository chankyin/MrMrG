package me.noip.chankyin.mrmrg.math;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.noip.chankyin.mrmrg.utils.ImmutableArrayD;
import me.noip.chankyin.mrmrg.utils.Utils;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
@EqualsAndHashCode
public class VectorD{
	public final static VectorD ZERO_2 = new VectorD(0, 0);
	public final static VectorD ZERO_3 = new VectorD(0, 0, 0);
	public final static VectorD I_2 = new VectorD(1, 0);
	public final static VectorD J_2 = new VectorD(0, 1);
	public final static VectorD I_3 = new VectorD(1, 0, 0);
	public final static VectorD J_3 = new VectorD(0, 1, 0);
	public final static VectorD K_3 = new VectorD(0, 0, 1);

	@Getter private final ImmutableArrayD coords;

	public VectorD(double... coords){
		this(new ImmutableArrayD(coords));
	}

	public int getDimension(){
		return coords.length;
	}

	public VectorD add(VectorD that){
		throwDim(that);
		return new VectorD(Utils.arrayMap(in -> in[0] + in[1], coords.toArray(), that.coords.toArray()));
	}

	public VectorD subtract(VectorD that){
		throwDim(that);
		return new VectorD(Utils.arrayMap(in -> in[0] - in[1], coords.toArray(), that.coords.toArray()));
	}

	public VectorD multiply(double d){
		return new VectorD(Utils.arrayMap(in -> in[0] * d, coords.toArray()));
	}

	public VectorD divide(double d){
		return new VectorD(Utils.arrayMap(in -> in[0] / d, coords.toArray()));
	}

	public double modulusSquared(){
		double out = 0d;
		for(Double coord : coords){
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
		for(int i = 0; i < coords.length; i++){
			dot += coords.get(i) * that.coords.get(i);
		}
		return dot;
	}

	@Override
	public String toString(){
		return "{" + StringUtils.join(coords.toArray(), ',') + "}";
	}

	private void throwDim(VectorD that){
		if(coords.length != that.coords.length){
			throw new IllegalArgumentException(String.format(
					"Attempt to operate vectors of different dimensions (%d, %d)", coords.length, that.coords.length));
		}
	}

	private void throwMinDim(int minDim){
		if(coords.length < minDim){
			throw new IllegalArgumentException(String.format(
					"Attempt to access %d-dimensional vector, %d+ dimensions required", coords.length, minDim));
		}
	}

	private void throwEqDim(int required){
		if(coords.length != required){
			throw new IllegalArgumentException(String.format(
					"Passed %d-dimensional vector, %d-dimensional vector required", coords.length, required));
		}
	}

	public double getX(){
		throwMinDim(1);
		return getCoords().get(0);
	}

	public double getY(){
		throwMinDim(2);
		return getCoords().get(1);
	}

	public double getZ(){
		throwMinDim(3);
		return getCoords().get(2);
	}

	public VectorD setX(double x){
		throwMinDim(1);
		double[] data = getCoords().toArray();
		data[0] = x;
		return new VectorD(data);
	}

	public VectorD setY(double y){
		throwMinDim(2);
		double[] data = getCoords().toArray();
		data[1] = y;
		return new VectorD(data);
	}

	public VectorD setZ(double z){
		throwMinDim(3);
		double[] data = getCoords().toArray();
		data[2] = z;
		return new VectorD(data);
	}

	public double getCoords(int i){
		return coords.get(i);
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
		return getDirectionBearing(0,1);
	}

	@Radians
	public double getDirectionBearing(int xOrd, int yOrd){
		throwMinDim(Math.min(xOrd, yOrd));
		double x = getCoords().get(xOrd);
		double y = getCoords().get(yOrd);
		return Math.atan2(yOrd, xOrd);
	}
}
