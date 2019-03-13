/*******************************************************************************
 * Copyright 2019 AT&T Intellectual Property.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.onap.optf.utilities;
/**
 * Utility to split a single set of changes into multiple 'micro-commits'. Helpful for initial commits of new components
 * into the repo
 * 
 * This will 
 *  - determine how many commits are necessary
 *  - create working folders and clone the repo 'N' times
 *  - do git remote add gerrit 
 *  - copy each subset of files to the respective working clones
 *  - git add the new (updated?) files
 *  - git commit -m 
 *  
 *  Since this is a work in progess the git commit -as and git review are still manual to double check the results  
 *  
 *   
 *  
 * 
 * This code currently assumes a repos structure that containes ONLY the files to be committed. There is no attempt 
 * to identify new/changed files to commit. Preparation of the list of files is manual.
 * 
 * This does not make any attempt to determine if this will break the build. 
 *  
 * Caution. This code was developed and tested in Windows. WIndows paths to GIT bash, etc. are hard coded.     
 *  
 */

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;




public class OnapInitialCommit 
{
	CommandLine cmdline = null;
	Long lpc = 1000l;
	
	Map<String, Long> fileMap = new TreeMap<String, Long>();
	List<List<String>> commitLists = new ArrayList<>();
	public static void main(String []argv)
	{
		Options options = new Options();
		options.addRequiredOption("r", "repoFolder", true, "Folder containing all of the files to commit.");
		options.addRequiredOption("f", "filesFolder", true, "Folder containing all files to commit - relative to repo folder");
		options.addRequiredOption("w", "commitReposFolder", true, "Folder where all of the commit repos are created");
		options.addRequiredOption("c", "cloneCommand", true, "Clone with commit hooks command - with credentials");
		options.addRequiredOption("g", "remoteAddGerrit", true, "git remote add gerrit command");
		options.addRequiredOption("i", "issue", true, "Issue ID");
		options.addRequiredOption("m", "message", true, "Commit message");
		options.addRequiredOption("p", "clonePath", true, "Path created in commitRepoFolder by the clone command");
		
		options.addOption("l", "linesPerCommit", true, "lines to commit");
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse( options, argv);
			OnapInitialCommit oic = new OnapInitialCommit(cmd);
			oic.execute();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "OnapIntialCommit", options );
			
		}
	}


	public OnapInitialCommit(CommandLine cmd) 
	{
		cmdline = cmd;
	}
	private void execute() 
	{
		buildCommitLists();

		int i = 0;
		for (List<String> list : commitLists)
		{
			i++;
			commit(list, i);
		}
		
	}


	private void commit(List<String> list, int i) 
	{
		if (list.size() == 0)
			return;
		System.out.println("\n\n");
		for (String name : list)
		{
			System.out.println(name + ":" + fileMap.get(name));
		}
		File cloneFolder = new File(cmdline.getOptionValue("w") + File.separator + "commit" + i);
		cloneFolder.mkdirs();
		cloneCode(cloneFolder, i);
		System.out.println("\n\n");
		File fromFolder = new File(cmdline.getOptionValue("r"));
		List<String> filesToAdd = new ArrayList<>();
		for (String fromFile : list)
		{
			String toPath = fromFile.replace(fromFolder.getAbsolutePath(), "");
			
			Path toFile = Paths.get(cloneFolder + File.separator + "cmso" +  toPath); 
			System.out.println(fromFile  + ":" + toFile);
			try {
				toFile.toFile().mkdirs();
				Files.copy(Paths.get(fromFile), toFile, StandardCopyOption.REPLACE_EXISTING);
				filesToAdd.add(toPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		commitFiles(cloneFolder, filesToAdd, i);
	}


	private void cloneCode(File cloneFolder, int i) 
	{
		String unixCloneFolder = FilenameUtils.separatorsToUnix(cloneFolder.getAbsolutePath());
		unixCloneFolder = unixCloneFolder.replace("c:", "/c");
		StringBuilder shell = new StringBuilder();
		shell.append("cd \"").append(unixCloneFolder).append("\"\n");
		shell.append("export PATH=$PATH:\"/C/Program Files/Git/mingw64/bin\"").append("\n");
		shell.append(cmdline.getOptionValue("c")).append("\n");
		shell.append("cd ").append(cmdline.getOptionValue("p")).append("\n");
		shell.append(cmdline.getOptionValue("g")).append("\n");
		File shellFile = new File("oic.sh");
		File shellFileOut = new File("oic.log." + i);
		try {
			Files.write(Paths.get(shellFile.getAbsolutePath()), shell.toString().getBytes());
			String command = "\"C:\\Program Files\\Git\\bin\\bash.exe\" -x " + shellFile.getAbsolutePath(); 
			
			ProcessBuilder builder = new ProcessBuilder("C:\\Program Files\\Git\\bin\\bash.exe", "-x", shellFile.getAbsolutePath());
			builder.redirectOutput(shellFileOut);
			builder.redirectError(shellFileOut);
			Process p = builder.start(); // may throw IOException
			p.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	private void commitFiles(File cloneFolder, List<String> list, int i) 
	{
		String unixCloneFolder = FilenameUtils.separatorsToUnix(cloneFolder.getAbsolutePath());
		unixCloneFolder = unixCloneFolder.replace("c:", "/c");
		StringBuilder shell = new StringBuilder();
		shell.append("cd \"").append(unixCloneFolder).append("\"\n");
		shell.append("export PATH=$PATH:\"/C/Program Files/Git/mingw64/bin\"").append("\n");
		shell.append("cd ").append(cmdline.getOptionValue("p")).append("\n");
		for (String name : list)
		{
			name = FilenameUtils.separatorsToUnix(name);
			name = name.replaceAll("^/", "");
			shell.append("git add ").append(name).append("\n");
		}
		shell.append("git commit -m \"Commit ").append(i).append(" for ").append(cmdline.getOptionValue("m"));
		shell.append("\" -m \"Multiple commits required due to commit size limitation.\" -m \"");
		shell.append("Issue-ID: ").append(cmdline.getOptionValue("i")).append("\"\n");
		
		File shellFile = new File("addFiles" + i + ".sh");
		File shellFileOut = new File("addFiles" + i + ".log");
		try {
			Files.write(Paths.get(shellFile.getAbsolutePath()), shell.toString().getBytes());
			String command = "\"C:\\Program Files\\Git\\bin\\bash.exe\" -x " + shellFile.getAbsolutePath(); 
			
			ProcessBuilder builder = new ProcessBuilder("C:\\Program Files\\Git\\bin\\bash.exe", "-x", shellFile.getAbsolutePath());
			builder.redirectOutput(shellFileOut);
			builder.redirectError(shellFileOut);
			Process p = builder.start(); // may throw IOException
			p.waitFor();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


	private void buildCommitLists() 
	{
		File files = new File(cmdline.getOptionValue("r") + File.separator + cmdline.getOptionValue("f"));
		if (files.isDirectory())
		{
			buildCommitMap(files);
			List<String> l = new ArrayList<>();
			long size = 0;
			for (String name : fileMap.keySet())
			{
				Long thisSize = fileMap.get(name);
				System.out.println(thisSize + " : " + name);
				if ((size +  thisSize) > lpc )
				{
					commitLists.add(l);
					l = new ArrayList<>();
					size = 0;
				}
				size+=thisSize;
				l.add(name);
			}
			commitLists.add(l);
		}
	}


	private void buildCommitMap(File files) 
	{
		for (File thisOne : files.listFiles())
		{
			if (thisOne.isDirectory())
				buildCommitMap(thisOne);
			else
				updateMap(thisOne);
		}
		
	}


	private void updateMap(File thisOne) 
	{
		//System.out.println(thisOne.getAbsolutePath());
		Path path = Paths.get(thisOne.getAbsolutePath());
		long lineCount = -1;
		Charset[] csList = {
				StandardCharsets.UTF_8,
				StandardCharsets.ISO_8859_1,
				StandardCharsets.US_ASCII,
				StandardCharsets.UTF_16,
		};
		for (Charset cs : csList)
		{
			try {
				lineCount = Files.lines(path, cs).count();
				if (lineCount > 1)
					break;
			} catch (Exception e) {
				//System.out.println(thisOne.getAbsolutePath());
			}
		}
		if (lineCount > -1)
		{
			fileMap.put(thisOne.getAbsolutePath(), lineCount);
		}
		else
		{
			System.out.println("Skipping " +  thisOne.getAbsolutePath());
		}
	}
	
	
}
