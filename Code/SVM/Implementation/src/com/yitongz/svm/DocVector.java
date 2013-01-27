/** This is the Vector of one document, which means for each line of input data,
    we store it as a document vector.
	
	a document vector is made of (DocElement 1, DocElemment 2, .... DocElement N)
    NOTICE:
      the iteration of elements is highly optimized
      we do not store elements with a score of 0
*/
package com.yitongz.svm;
import java.util.ArrayList;
import java.util.Iterator;
public class DocVector{
	public ArrayList<DocElement> x_list=new ArrayList<DocElement>();
	public int category; // the category of a document, in this case it only equals to 1: relevant or 0: irrelevant
	private int qid;   // the query_id of a document
	public DocVector(int category){
		this.category=category;
	}
	public void setQID(int qid){
		this.qid=qid;
	}
	public int getQID(){
		return this.qid;
	}
	public int getY(int k){
		return this.category==k ? 1 : -1;
	}
	public void addX(int index, double score){
		x_list.add(new DocElement(index,score));
	}
	public void addCustomizedScores(){
		double [] scores=new double[45];
		scores[0]=-1;
		Iterator <DocElement> it=x_list.iterator();
		// we only use the 1...41 array;
		int i=1;
		while (it.hasNext() && i<=44){
			scores[i]=it.next().score;
			i++;
		}


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
		// square of tf of anchor
		this.addX(47,score3);

	}
	public void normalizeScores(){ // normalize the scores
		Iterator <DocElement> it=x_list.iterator();
		DocElement tmp=null;
		double sum=0;
		while(it.hasNext()){
			tmp=it.next();
			sum+=tmp.score*tmp.score;
		}
		sum=Math.sqrt(sum);
		it=x_list.iterator();
		while(it.hasNext()){
			tmp=it.next();
			tmp.score=tmp.score/sum;
		}
	}
	public DocVector minus(DocVector list2, int pos_or_neg){   // make this vector minus list2 vector and return the result
		Iterator <DocElement> it_a=this.x_list.iterator();
		Iterator <DocElement> it_b=list2.x_list.iterator();
		DocVector doc=new DocVector(this.category);
		DocElement tmp1=null;
		DocElement tmp2=null;
		while(it_a.hasNext() && it_b.hasNext()){
			tmp1=it_a.next();
			tmp2=it_b.next();
			doc.addX(tmp1.index,tmp1.score-tmp2.score);
		}
		return doc;
	} 
}