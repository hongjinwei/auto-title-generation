package auto_title_generation.cnn.utils;

import java.io.PrintStream;

public class Log {
	static PrintStream stream = System.out;
	
	public static void i(String tag,String msg){
		stream.println(tag+"\t"+msg);
	}
	
	public static void i(String msg){
		stream.println(msg);
	}

}
