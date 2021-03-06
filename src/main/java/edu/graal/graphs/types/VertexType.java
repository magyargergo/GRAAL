package edu.graal.graphs.types;

import javaslang.collection.Array;
import javaslang.collection.Set;

import static java.lang.Math.max;
import static java.lang.Math.pow;

public enum VertexType implements IVertexType {
	DECL(1),
	ASSIGN(1),
	CTRL(2),
	CALL(3),
	RETURN(4),
	BREAK(5),
	CONTINUE(6),
	CONN(7);

	private int value;
	VertexType(int value) {
		this.value = value;
	}

	public static final double PENALTY_CONSTANT = 1.0;
	public static final double MAX_PENALTY = 999999;

	public static double getPenalty(PDGVertex v1, PDGVertex v2) {
		double totalPenalty = 0.0;

		if(v1.getType().getValue() != v2.getType().getValue()) {
			totalPenalty += MAX_PENALTY;
		}
		else if((v1.getType().equals(DECL) && v2.getType().equals(ASSIGN)) ||
				(v2.getType().equals(ASSIGN) && v2.getType().equals(DECL))) {
			totalPenalty += PENALTY_CONSTANT;
		}

		Set<? extends IVertexSubtype> v1UniqueSubTypes = v1.subTypes().diff(v2.subTypes());
		Set<? extends IVertexSubtype> v2UniqueSubTypes = v2.subTypes().diff(v1.subTypes());

		if(v1UniqueSubTypes.isEmpty() || v2UniqueSubTypes.isEmpty()) {
			totalPenalty += pow(max(v1UniqueSubTypes.size(), v2UniqueSubTypes.size()), 2) * PENALTY_CONSTANT;
		}

		totalPenalty +=  Array.ofAll(v1UniqueSubTypes).crossProduct(v2UniqueSubTypes)
				.map(VertexSubtype::getPenalty)
				.sum().doubleValue();

		return totalPenalty;
	}

	@Override
	public int getValue() {
		return value;
	}
}
