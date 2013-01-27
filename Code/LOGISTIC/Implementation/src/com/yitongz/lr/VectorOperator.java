package com.yitongz.lr;
public class VectorOperator{
	private static VectorOperator op=new VectorOperator();
	private VectorOperator(){

	}
	public static VectorOperator getOperator(){
		return op;
	}
}