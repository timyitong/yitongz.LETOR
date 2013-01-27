package com.yitongz.lr;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.StringTokenizer;
import java.util.ArrayList;
public class Train{
	private String train_url;
	private String test_url;
	private String result_url;
	private ParameterOperator operator;
	private String mode;
	public Train(String data_url, String conf_url){
		try{
		
		/*READ DATA.txt*/
		BufferedReader br=new BufferedReader(new FileReader(new File(data_url)));
		//read in the train url
		String s=br.readLine();
		train_url=s.substring(s.indexOf('=')+1,s.length());

		// read in the test url
		s=br.readLine();
		test_url=s.substring(s.indexOf('=')+1,s.length());

		// read in the c value
		s=br.readLine();
		Parameter.c=Double.parseDouble(s.substring(s.indexOf('=')+1, s.length()));

		
		/*READ CONF.txt*/
		br=new BufferedReader(new FileReader(new File(conf_url)));
		//read in the result url
		s=br.readLine();
		result_url=s.substring(s.indexOf('=')+1,s.length());

		// read in the x0
		s=br.readLine();
		Parameter.x0=Double.parseDouble(s.substring(s.indexOf('=')+1, s.length()));

		/*SET MODE to Batch Version*/
		mode="batch";

		br.close();
		}catch(IOException e){
			e.printStackTrace();
		}

		operator=Parameter.init_Parameters();
		start_train();
		//check_w();
		start_test();
	}

	/*Train DATA*/
	private void start_train(){
		try{
		BufferedReader br=new BufferedReader(new FileReader(new File(train_url)));
		String s=null;
		DocVector doc=null;
		String tmp=null;
		int len=0;
		int doc_category=0;

			ArrayList<DocVector> doc_list=new ArrayList<DocVector>();
			while((s=br.readLine())!=null && len>-1){
				StringTokenizer st=new StringTokenizer(s);

				//set the doc category
				doc_category=Integer.parseInt(st.nextToken()); //doc catergory should be 1 or -1
				if (doc_category==0)
					doc_category=-1;
				doc=new DocVector(doc_category);

				//set the qid of the document
				tmp=st.nextToken();
				int qid=Integer.parseInt(tmp.substring(tmp.indexOf(':')+1,tmp.length()));
				doc.setQID(qid);

				// begin the score reading
				while(st.hasMoreTokens()){
					tmp=st.nextToken();
					int split=tmp.indexOf(':');
					if (split!=-1)
						doc.addX(Integer.parseInt(tmp.substring(0,split)),  Double.parseDouble(tmp.substring(split+1,tmp.length())) );
					else{ // we have the doc id token
						tmp=st.nextToken();  //skip the #docid
						tmp=st.nextToken();  //skip the =, then we get the realpart
						doc.setDocID(Integer.parseInt(tmp));
					}
				}
				doc.addCustomizedScores(); // generate the customized scores
				doc.normalizeScores(); // this step is to normalize scores as instructed in the readme.txt of the data package
				doc_list.add(doc);
				len++;
			}
			doc_list=PairWise.crossProduct(doc_list);
			operator.evolveW(doc_list,mode);

		br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	/*TEST DATA*/
	private void start_test(){
		try{
		BufferedReader br=new BufferedReader(new FileReader(new File(test_url)));
		String s=null;
		DocVector doc=null;
		String tmp=null;
		int doc_category=0;
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File(result_url)));
		while((s=br.readLine())!=null){
			StringTokenizer st=new StringTokenizer(s);

			// set the doc category
			String old_id=st.nextToken();
			doc_category=Integer.parseInt(old_id);
			if (doc_category==0)
				doc_category=-1;
			doc=new DocVector(doc_category);

			// set the qid
			tmp=st.nextToken();
			int qid=Integer.parseInt(tmp.substring(tmp.indexOf(':')+1,tmp.length()));
			doc.setQID(qid);

			// set the score reading
			while(st.hasMoreTokens()){
				tmp=st.nextToken();
				int split=tmp.indexOf(':');
				if (split!=-1){
					doc.addX(Integer.parseInt(tmp.substring(0,split)),  Double.parseDouble(tmp.substring(split+1,tmp.length())) );
				}else{
					tmp=st.nextToken();  //skip the #docid
					tmp=st.nextToken();  //skip the =, then we get the realpart
					doc.setDocID(Integer.parseInt(tmp));
				}
			}
			doc.addCustomizedScores();  // generate the customized scores
			doc.normalizeScores();      // normalize the scores
			double score=operator.calculate_pr(1,doc);  // calculate_pr(category,doc), because we only have 1 category in ranking, so it always 1
			bw.write(Double.toString(score));
			bw.newLine();
			System.out.println(score);  //output the scores to STDOUT
		}
		br.close();
		bw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	private void check_w(){  // test for current w values
		for (int k=1;k<Parameter.categories;k++){
			System.out.println(k+"=============");
			for (int i=0;i<Parameter.dimensions;i++){
				System.out.print("w"+"("+i+")"+operator.getW(k,i)+"  ");
			}
		}
	}
}