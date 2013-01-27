package com.yitongz.lr;
import java.util.ArrayList;
import java.util.Iterator;
public class PairWise{
	public static ArrayList <DocVector> crossProduct(ArrayList <DocVector> list){
		DocVector tmp=null;
		DocVector tmp2=null;
		Iterator <DocVector>it_a=list.iterator();
		ArrayList <DocVector> new_list=new ArrayList <DocVector>();
		while(it_a.hasNext()){
			tmp=it_a.next();
			if (tmp.getY(1)==1){
				Iterator <DocVector>it_b=list.iterator();
				while (it_b.hasNext()){
					tmp2=it_b.next();
					if (tmp.getQID()==tmp2.getQID() && tmp2.getY(1)==-1){
						DocVector d=tmp.minus(tmp2,1); // only calculate D+ - D-, do not calculate the inverse pairs
						new_list.add(d);	// add the DocVector into the new DocVector list					
					}

				}
			}
		}
		return new_list;
	}
}