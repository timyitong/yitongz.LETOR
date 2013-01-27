package com.yitongz.lr;
/** This is Main Class of the Text Catergorization
*/
public class Main{
	public static void main(String argv[]){
		run_lr(argv);
	}
	private static void run_lr(String argv[]){
		if (argv.length!=2)
			new Train("DATA.txt", "Implementation/CONF.txt");
		else 
			new Train(argv[0],argv[1]);
	}
}