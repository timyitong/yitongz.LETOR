/** This is one dimension or say one element of a document score vector,
    very simple structure:
      index----feature id
      score----feature score
*/
package com.yitongz.svm;
public class DocElement{
	public int index;
	public double score;
	public DocElement(int index, double score){
		this.index=index;
		this.score=score;
	}
}