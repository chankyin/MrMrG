package me.noip.chankyin.mrmrg;

import me.noip.chankyin.mrmrg.ui.ProjectScreen;

import java.io.File;

public class MrMrG{
	public final static String NAME = "MrMrG";
	public final static String VERSION = "1.0";

	public static File dataDir;

	public static void main(String[] args){
		dataDir = new File("data");
		if(args.length > 0){
			dataDir = new File(args[0]);
		}
		dataDir.mkdirs();
		if(!dataDir.isDirectory()){
			throw new RuntimeException("Could not create data directory: " + dataDir.getAbsolutePath());
		}

		new ProjectScreen().display();
	}
}
