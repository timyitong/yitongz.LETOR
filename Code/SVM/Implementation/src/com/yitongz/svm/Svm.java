package com.yitongz.svm;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
public class Svm{
	private String c;
	private String train_url;
	private String test_url;
	private ArrayList <DocVector> doc_list=new ArrayList<DocVector>();
	private String result_url="Implementation/result";
	private String input_url=result_url+"/input";  // default directory for temporary data file storage
	private String svm_url="Implementation/lib/svm_light";
	public Svm(){
		try{
		readConfig();  // read in config file
		readInput(train_url); // read the train data, customize and normalize
		crossProduct(); // do the D+ minus D-
		writeInput("train"); // store the train data file
		train();  // do training

		doc_list=new ArrayList<DocVector>();  // clean the doc_list
		readInput(test_url);  // read the test data, customize and normalize
		writeInput("test");   // store the test data file
		test();    // do testing
		}catch(Exception e){e.printStackTrace();}
	}
	private void readConfig(){ //read the configuration file
		try{
			BufferedReader br=new BufferedReader(new FileReader(new File("DATA.txt")));
			String s=null;
			s=br.readLine();
			train_url=s.substring(s.indexOf('=')+1,s.length());

			s=br.readLine();
			test_url=s.substring(s.indexOf('=')+1,s.length());

			s=br.readLine();
			c=s.substring(s.indexOf('=')+1, s.length());

			br.close();
		}catch(IOException e){e.printStackTrace();}
	}
	private void readInput(String url){ // read the input training data
		try{
			BufferedReader br=new BufferedReader(new FileReader(new File(url)));
			String s=null;
			String tmp=null;
			while((s=br.readLine())!=null){
				StringTokenizer st=new StringTokenizer(s);
				int category=Integer.parseInt(st.nextToken());
				if (category==0)
					category=-1;
				DocVector doc=new DocVector(category);
				tmp=st.nextToken(); //set the qid
				int qid=Integer.parseInt(tmp.substring(tmp.indexOf(':')+1,tmp.length()));
				doc.setQID(qid);
				while (st.hasMoreTokens()){
					tmp=st.nextToken();
					int split=tmp.indexOf(':');
					if (split!=-1){
						int index=Integer.parseInt(tmp.substring(0,split));
						double score=Double.parseDouble(tmp.substring(split+1,tmp.length()));
						doc.addX(index,score);
					}
				}
				doc.addCustomizedScores();  // add the customized features
				doc.normalizeScores();      // normalize the scores
				doc_list.add(doc);
			}
		}catch(IOException e){e.printStackTrace();}
	}
	private void crossProduct(){ // generate pairwise vectors
		doc_list=PairWise.crossProduct(doc_list);
	}
	private void writeInput(String name){ // write down the new train data files
		try{
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(input_url+"."+name)));
			Iterator <DocVector> it=doc_list.iterator();
			DocVector doc=null;
			DocElement tmp=null;
			while(it.hasNext()){
				doc=it.next();
				Iterator <DocElement> itb=doc.x_list.iterator();
				bw.write(""+doc.getY(1));
				while(itb.hasNext()){
					tmp=itb.next();
					bw.write(" "+tmp.index+":"+tmp.score);
				}
				bw.newLine();
			}
			bw.close();
		}catch(IOException e){e.printStackTrace();}
	}

	/*Call the svm_light to train*/
	private void train(){ 
		try{
		Runtime x = Runtime.getRuntime();
		String s=svm_url+"/"+"./svm_learn "+"-c "+c+" -b 0 -v 0 "+input_url+".train "+input_url+".model";
		Process prcs=x.exec(s);
		prcs.waitFor();

		}catch(Exception e){e.printStackTrace();}
	}

	/*Call the svm_light to test*/
	private void test(){
		try{
		Runtime x = Runtime.getRuntime();
		String s=svm_url+"/"+"./svm_classify "+input_url+".test "+input_url+".model "+input_url+".result";
		Process prcs=x.exec(s);
		prcs.waitFor();
		BufferedReader br=new BufferedReader(new FileReader(new File(input_url+".result")));
		while ((s=br.readLine())!=null){
			System.out.println(s);
		}
		}catch(Exception e){e.printStackTrace();}
	}
}