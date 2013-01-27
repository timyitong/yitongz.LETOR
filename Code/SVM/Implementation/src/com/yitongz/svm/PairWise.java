package com.yitongz.svm;
import java.util.ArrayList;
import java.util.Iterator;
public class PairWise{
	public static ArrayList <DocVector> crossProduct(ArrayList <DocVector> list){   // form the pairwise vectors
		ArrayList <DocVector> new_list=new ArrayList <DocVector>();
		Iterator <DocVector> it_a=list.iterator();
		DocVector tmp1=null;
		DocVector tmp2=null;
		while(it_a.hasNext()){
			tmp1=it_a.next();
			if (tmp1.getY(1)==1){
				Iterator <DocVector> it_b=list.iterator();
				while (it_b.hasNext()){
					tmp2=it_b.next();
					if (tmp1.getQID()==tmp2.getQID() && tmp2.getY(1)==-1){
						DocVector doc=tmp1.minus(tmp2,1);
						new_list.add(doc);
						/*
						DocVector doc2=tmp2.minus(tmp1,-1);
						new_list.add(doc2);*/
					}
				}
			}
		}
		return new_list;
	}
}