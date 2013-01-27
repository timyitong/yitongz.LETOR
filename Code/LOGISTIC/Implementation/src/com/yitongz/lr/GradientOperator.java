package com.yitongz.lr;
import java.util.ArrayList;
import java.util.Iterator;
public class GradientOperator implements ParameterOperator{
	private double [][] w;
	private double c=Parameter.c;
	private double pace=1e-4;  //The Epsilon  1e-4
	private double threshold=5e-4; //when to converge 5e-4
	GradientOperator(double [][] new_w){
		w=new_w;
	}
	public double sigema(double z){    // calculate the sigema function
		return 1/(1+Math.exp(0-z));
	}
	public double getW(int k, int j){  // get a w_j for the Kth category
		return w[k-1][j];
	}
	public double [] getVector(int k){ // get the Kth w vector
		return w[k-1];
	}
	public void setW(int k, int j, double val){ // update a w_j for the Kth catergory
		w[k-1][j]=val;
	}

	/*------Batch Version----------*/
	private void evolveW(int k, ArrayList<DocVector> list){
		double gradient_mod=1;      // The mod of vector dl/dw to judge whether it is converged

		double[] step=new double[Parameter.dimensions+1];  // the sum of dl/dw for each dimension
		for (int j=0;j<step.length;j++){
			step[j]=0;   // make the orignial value to be 0 
		}
		Iterator <DocVector> it=null;
		DocVector doc=null;
		while(gradient_mod>threshold){ 
			//first combine and calculate the batch
			it=list.iterator();
			int doc_index=0;
			gradient_mod=0;
			while (it.hasNext()){
				doc=it.next();
				double wt_x=doc.multiply(getVector(k));
				int y=doc.getY(k);
				double y_times_sigema=(y*sigema(-y*wt_x)); //y-sigema(w^t*x)
				double mod=0;
				while(doc.hasNextX()){
					double x=doc.getNextX();
					double delta=y_times_sigema*x;
					step[doc.x_index]+=delta;
					mod+=delta*delta;
				}
				doc_index++;
				doc.resetIterator();
			}
			for (int j=0;j<step.length;j++){
				double w_j=getW(k,j);
				double new_w_j=w_j+pace*step[j];
				setW(k,j,new_w_j);
				gradient_mod+=step[j]*step[j];      //Gradient_v1: first sum vector then calculate mod
			
				step[j]=0-c*new_w_j;   // after using them, make the orignial value to be -c*w_j
			}
			gradient_mod=Math.sqrt(gradient_mod)/(list.size());   //Gradient_v1: first sum vector then calculate mod
			//System.out.println(gradient_mod);
		}
	}

	public double calculate_pr(int k, DocVector x){ // this is for getting the score of a document
		double under=1;
		/* This is for the single probability calculation: */
		return sigema(x.multiply(this.getVector(k)));
	}
	public void checkCategory(DocVector x){  // this is for checking the category of a document
		double [] pr_list=new double[Parameter.categories];
		int max_category=1;
		for (int i=1;i<=Parameter.categories;i++){
			pr_list[i-1]=calculate_pr(i,x);
			if (pr_list[max_category-1]<pr_list[i-1])
				max_category=i;
		}
		x.setCategory(max_category);
	}
	/*--------Batch Version End----------*/







	// NOTICE:  This method is for the stochastic version, currently not in use
	private double evolveW(int k, DocVector x){
		double wt_x=x.multiply(getVector(k));
		double y=x.getY(k);
		double y_minus_sigema=(y-sigema(wt_x));
		double gradient_mod=0;

		for (int i=0;i<Parameter.dimensions;i++){
			double w_i=getW(k,i);
			double step=x.index(i)*y_minus_sigema;
			gradient_mod+=step*step;
			setW(k,i,w_i+pace*(step+c*w_i));
		}
		gradient_mod=Math.sqrt(gradient_mod);
		x.resetIterator();
		return gradient_mod;
	}
	// NOTICE:  This method is for the stochastic version, currently not in use
	public void evolveW(DocVector x){  
		for (int i=1;i<=Parameter.categories;i++)
			evolveW(i,x);
	}
	// NOTICE:  This method is for the stochastic version, currently not in use
	public void evolveW(ArrayList<DocVector> list,String mode){
		if (mode.equals("batch")){
			for (int k=1;k<=Parameter.categories;k++)
				evolveW(k,list);
		}else{	// this is the stochastic version
			double old_mod=0;
			double mod=1;
			Iterator <DocVector> it=list.iterator();
			for (int k=1;k<=Parameter.categories;k++){
				mod=1;
				while (mod>threshold){
					old_mod=mod;
					mod=0;
					while(it.hasNext())
						mod+=evolveW(k,it.next());
					mod=mod/list.size();
						it=list.iterator();
						mod=1;
				}
			}
		}
	}

}