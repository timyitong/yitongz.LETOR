/** This is the Vector of one document, which means for each line of input data,
    we store it as a document vector.
	
	a document vector is made of (DocElement 1, DocElemment 2, .... DocElement N)
    NOTICE:
      the iteration of elements is highly optimized
      we do not store elements with a score of 0
*/
package com.yitongz.lr;
import java.util.ArrayList;
import java.util.Iterator;
public class DocVector{
	private int category;
	private int doc_id; // the doc id
	private int qid;   // the query id
	public ArrayList<DocElement> x_list=new ArrayList<DocElement>(); // only store scores not equal to 0
	private double[] x;
	private Iterator <DocElement> iterator=null;  // the iterator
	private DocElement next_doc=null;
	private int next_x_index=0;
	public int x_index=0;
	private double x0=Parameter.x0;
	public DocVector(int category){
		this.category=category;
	}
	public double multiply(double [] w){  // calculate w^T * x
		double r=w[0]*x0;
		for (int i=0;i<x_list.size();i++){
			DocElement de=x_list.get(i);
			r+=w[de.index]*de.score;
		}
		return r;
	}
	public double index(int index){   //get the score of an DocElement at the index 
		x_index=index;
		if (index==0){
			iterator=x_list.iterator();
			next_doc=iterator.next();
			next_x_index=next_doc.index;
			return x0;			
		}
		if (index<next_x_index){
			return 0;
		}else if (index==next_x_index){
			DocElement old_doc=next_doc;
			if (iterator.hasNext())
			{	next_doc=iterator.next();
				next_x_index=next_doc.index;
			}else{
				next_x_index=-1;
			}
			return old_doc.score;
		}else{
			return 0;
		}
	}
	public boolean hasNextX(){  // hasNextX(), and getNextX() is made for iterating the only the x>0 directly
		if (next_x_index!=-1)
			return true;
		else 
			return false;
	}
	public void normalizeScores(){  //normalize the scores into the same scale
		Iterator <DocElement> it=x_list.iterator();
		DocElement tmp=null;
		double sum=0;
		while(it.hasNext()){
			tmp=it.next();
			sum+=tmp.score*tmp.score;
		}
		sum=Math.sqrt(sum);
		it=x_list.iterator();
		while (it.hasNext()){
			tmp=it.next();
			tmp.score=tmp.score/sum;
		}

	}
	public double getNextX(){
		return (index(next_x_index));
	}
	public void resetIterator(){   // reset the iterator to the beginning place
		next_x_index=0;
		next_doc=null;
		iterator=null;
	}
	public int getY(int k){
		return (category==k) ? 1 : -1; //In current case, if match yi=1 else yi=-1
	}
	public void addX(int x_index,double x_score){
		x_list.add(new DocElement(x_index,x_score));
	}
	public void setCategory(int category){
		this.category=category;
	}
	public int getCategory(){
		return category;
	}
	public void setDocID(int id){
		this.doc_id=id;
	}
	public int getDocID(){
		return this.doc_id;
	}
	public void setQID(int id){
		this.qid=id;
	}
	public int getQID(){
		return this.qid;
	}
	public DocVector minus(DocVector doc2,int pos_or_neg){  // generate the vector of this vector minus b vector
		Iterator <DocElement>it_a=this.x_list.iterator();
		Iterator <DocElement>it_b=doc2.x_list.iterator();
		DocVector res=new DocVector(this.category);
		DocElement a=null;
		DocElement b=null;
		while (it_a.hasNext() && it_b.hasNext()){
			a=it_a.next();
			b=it_b.next();
			res.addX(a.index, a.score-b.score);
		}
		return res;
	}

	/*Generate the customized scores*/
	public void addCustomizedScores(){
		double [] scores=new double[45];
		scores[0]=-1;
		// we only use the 1...44 array;
		int i=1;
		this.getNextX(); // skip the x0
		while (this.hasNextX() && i<=44){
			scores[i]=getNextX();
			i++;
		}
		this.resetIterator();


		//score1:
		double score1=(scores[6]+1)*(scores[13]+1)*(scores[32]+2.5)*(480*scores[40]+1.2);
		// hits of authority * sitemap based propaganta * tfidf body * weighted-out link
		this.addX(45,score1);

		//score2:
		double score2=1000000*((0.63*scores[14]+1)*(scores[36]+1)-0.999999)*(scores[8]/0.0001);  
		// pagerank *topical_pagerank* hostrank
		this.addX(46,score2);

		//score3:
		double score3=Math.pow((scores[29]+0.7),2);
		// square of tf_of_anchor
		this.addX(47,score3);

	}
}