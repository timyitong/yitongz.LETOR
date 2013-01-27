package com.yitongz.lr;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Parameter{
	public static int dimensions=44+3; // the 3 more dimensions are the self-customized scores
	public static int categories=1;
	public static double c;
	public static double x0;
	public static boolean isTraining=true; //true is training, false is testing
	private Parameter(){

	}
	public static ParameterOperator init_Parameters(){  //return a single instance of Parameter Operator
		double[][] w=new double[categories][dimensions+1];
		for (int i=0;i<categories;i++){
			for (int j=0;j<dimensions+1;j++)
				//w[i][j]=-0.40;
				w[i][j]=0;
		}
		ParameterOperator po=new GradientOperator(w);
		return po;
	}
}