/*
The MIT License (MIT)

Copyright (c) 2015 Sanketh P B

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.etas.jenkins.plugins.CreateTextFile;

import hudson.FilePath;
import hudson.remoting.Callable;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import jenkins.security.Roles;

import org.jenkinsci.remoting.RoleChecker;

public class CreateFileTask implements Serializable,Callable<Boolean,IOException> {

	private static final long serialVersionUID = 1L;
	private final String fileContent;
	private final String filePath;
	private final String fileOption;

	
	public CreateFileTask(String filePath,String fileContent,String fileOption){
		this.filePath = filePath;
		this.fileContent = fileContent;
		this.fileOption = fileOption;
	}
	
	@Override
	public void checkRoles(RoleChecker checker) throws SecurityException {
		checker.check(this, Roles.SLAVE);		
	}

	@Override
	public Boolean call() throws IOException {
		
		try {
			FilePath textFile = new FilePath(new File(filePath));
			String finalFileContent = "";
			String existingFileContents = "";
			String eol = System.getProperty("line.separator");
			
			if(!textFile.exists()){
				
				finalFileContent = fileContent;
			}			
			else{
				
				existingFileContents = textFile.readToString();
				
				if(fileOption.equalsIgnoreCase("overWrite")){
					
					finalFileContent = fileContent;
					
				}else if(fileOption.equalsIgnoreCase("appendToEnd")){
					
					finalFileContent = existingFileContents.concat(eol + fileContent);
					
				}else if(fileOption.equalsIgnoreCase("insertAtStart")){
									
					finalFileContent = fileContent.concat(eol + existingFileContents);

				}
				
				textFile.deleteContents();
				
			}
			
			finalFileContent = finalFileContent.replaceAll("\n", System.lineSeparator());
			
			textFile.write(finalFileContent, "UTF-8");
			
		} catch (InterruptedException e) {

			e.printStackTrace();
			return false;
		} catch (IOException e){
			
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
