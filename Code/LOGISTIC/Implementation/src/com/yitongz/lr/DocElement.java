/** This is one dimension or say one element of a document score vector,
    very simple structure:
      index----feature id
      score----feature score
*/
package com.yitongz.lr;
public class DocElement{
	public int index;
	public double score;
	public DocElement(int x, double score){
		this.index=x;
		this.score=score;
	}
}