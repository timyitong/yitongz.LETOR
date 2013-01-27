#include <fstream>
#include <iostream>
#include <string>
#include <vector>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

using namespace std;
void writeDATA(const string &s);
void writeResult(const float &t);

double diffclock(clock_t clock1,clock_t clock2){
	double diffticks=clock1-clock2;
	double diffms=(diffticks*1000000)/CLOCKS_PER_SEC;
	return diffms;
}

int main(){
	vector <string> c_list;
	ifstream infile("c.txt");
	string s;
	clock_t t1,t2;
	double wallclock;
	int i=0;
	while ((infile>>s)!=0){
		c_list.push_back(s);
		writeDATA(s);
		t1=time(0);
		system("sh run.sh");
		t2=time(0);
		wallclock=diffclock(t2,t1);
		system("sh eval.sh");
		writeResult(wallclock);
		i++;
	}
	return 0;
}
void writeDATA(const string &s){
	ofstream outfile("DATA.txt");
	outfile<<"train=../../data/train.txt\ntest=../../data/test.txt\n";
	outfile<<"c="<<s<<"\n";
	outfile.close();
}
void writeResult(const float &t){
	ifstream infile("eval_result.txt");
	string P10;
	string MAP;
	string NDCG;
	string s;
	int i=0;
	while((infile>>s)!=0){
		i++;
		if (i==11)
			P10=s;
		if (i==19)
			MAP=s;
		if (i==30)
			NDCG=s;
	}
	ofstream outfile("stat.txt",ios::app);
	outfile<<P10<<"\t"<<MAP<<"\t"<<NDCG<<"\t"<<t<<endl;
}