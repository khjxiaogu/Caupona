package com.teammoeg.caupona.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.datagen.FileGenerator.FileOutput;

public class JavaFileOutput {
	public class LineBuilder{
		public class ParamBuilder<T>{
			boolean isFirst=true;
			T par;
			
			public ParamBuilder(T par) {
				super();
				this.par = par;
			}
			public ParamBuilder<T> paramLiteral(String s) {
				param();
				literal(s);
				return this;
			}
			public ParamBuilder<T> paramString(String s) {
				param();
				string(s);
				return this;
			}
			public ParamBuilder<T> generic(Class<?>...classes) {
				LineBuilder.this.generic(classes);
				return this;
			}
			public ParamBuilder<ParamBuilder<T>> paramNewInst(String s) {
				param();
				newInst(s);
				return new ParamBuilder<>(this);
			}
			public ParamBuilder<ParamBuilder<T>> paramCall(String s) {
				param();
				literal(s);
				return new ParamBuilder<>(this);
			}
			
			public ParamBuilder<ParamBuilder<T>> paramNewInst(Class<?> s) {
				param();
				newInst(s);
				return new ParamBuilder<>(this);
			}
			public ParamBuilder<T> param() {
				if(!isFirst) 
					comma();
				else
					startP();
				isFirst=false;
				return this;
			}
			public T complete() {
				if(isFirst)
					startP();
				endP();
				return par;
			}
		}
		private int parentheses;
		private boolean isLastLiteral=false;
		private StringBuilder current=new StringBuilder();
		public LineBuilder startP() {
			parentheses++;
			current.append("(");
			isLastLiteral=false;
			return this;
		}
		public LineBuilder string(String str) {
			if(isLastLiteral) {
				current.append(" ");
			}
			current.append("\"").append(str.replaceAll("\"","\\\"")).append("\"");
			return this;
		}
		public LineBuilder endP() {
			parentheses--;
			current.append(")");
			isLastLiteral=false;
			return this;
		}
		public LineBuilder type(Class<?> cls) {
			literal(cls.getSimpleName());
			return this;
		}
		public LineBuilder assign(Class<?> cls,String name) {
			return type(cls).literal(name).literal("=");
		}
		public ParamBuilder<LineBuilder> call(String lit) {
			literal(lit);
			isLastLiteral=false;
			return new ParamBuilder<>(this);
		}
		public LineBuilder literal(String lit) {
			if(isLastLiteral) {
				current.append(" ");
			}
			int cnt=0;
			int st=0;
			while((st=lit.indexOf("(",st+1))>=0) {
				cnt++;
			}
			st=0;
			while((st=lit.indexOf(")",st+1))>=0) {
				cnt--;
			}
			isLastLiteral=true;
			parentheses+=cnt;
			current.append(lit);
			return this;
		}
		public LineBuilder dot() {
			current.append(".");
			isLastLiteral=false;
			return this;
		}
		public LineBuilder generic(Class<?>...classes) {
			current.append("<");
			boolean isFirst=true;
			isLastLiteral=false;
			for(Class<?> c:classes) {
				if(isFirst) {
					isFirst=false;
				}else comma();
				literal(c.getSimpleName());
			}
			current.append(">");
			isLastLiteral=true;
			return this;
		}
		public ParamBuilder<LineBuilder> newInst(String cls) {
			literal("new").literal(cls);
			return new ParamBuilder<>(this);
		}
		public ParamBuilder<LineBuilder> newInst(Class<?> cls) {
			literal("new").type(cls);
			return new ParamBuilder<>(this);
		}
		public LineBuilder comma() {
			isLastLiteral=true;
			current.append(",");
			return this;
		}
		public void endLine() {
			while(parentheses>0)
				endP();
			println(current.toString());
		}
		public void end() {
			while(parentheses>0)
				endP();
			println(current.toString()+";");
		}
	}
	
	private final FileOutput fo;
	private int indent = 0;

	
	private final List<String> imports=new ArrayList<>();
	private final List<String> lines=new ArrayList<>();
	private boolean startedComment;
	private boolean isDocument;
	
	public JavaFileOutput(FileGenerator fg, String name) {
		fo = fg.createOutput(fg.getClassPath(name));
		int idx = name.lastIndexOf(".");
		startComment(true,"Copyright (c) 2022 TeamMoeg");;
		println();
		println();
		println("This file is part of "+CPMain.MODNAME+".");
		println();
		println(CPMain.MODNAME+" is free software: you can redistribute it and/or modify");
		println("it under the terms of the GNU General Public License as published by");
		println("the Free Software Foundation, version 3.");
		println();
		println(CPMain.MODNAME+" is distributed in the hope that it will be useful,");
		println("but WITHOUT ANY WARRANTY; without even the implied warranty of");
		println("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the");
		println("GNU General Public License for more details.");
		println();
		println("Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.");
		println("Any mods or plugins can also use apis provided by forge or api using GPL or open source.");
		println();
		println("You should have received a copy of the GNU General Public License");
		println("along with "+CPMain.MODNAME+". If not, see <https://www.gnu.org/licenses/>.");
		endComment();
		println();
		line().literal("package").literal(name.substring(0, idx)).end();
		println();
		asImport();
		defineBlock("public class " + name.substring(idx + 1));
	}

	public void addImport(Class<?> cls) {
		if(cls.getCanonicalName()==null)
			throw new IllegalArgumentException("Local or anonymous class is not allowed in import");
		imports.add("import " + cls.getCanonicalName()+";");
	}

	public void addImport(String cls) {
		imports.add("import " + cls+";");
	}
	public void addImportDelimeter() {
		imports.add("");
	}

	public void addImportLine(String line) {
		imports.add(line);
	}
	public void println() {
		println("");
	}
	public void println(String line) {
		lines.add("\t".repeat(indent)+(startedComment&&isDocument?"* ":"") + line);
	}
	public LineBuilder line() {
		return new LineBuilder();
	}
	
	public void comment(String line) {
		lines.add("// "+line);
	}

	public void createMap(String modifier,String name,@SuppressWarnings("rawtypes") Class<? extends Map> class1,Class<?> kt,Class<?> vt) {
		line().literal(modifier).type(Map.class).generic(kt,vt).literal(name).literal("=").newInst(class1).generic().complete().end();
	}
	public void startComment(boolean isDocument,String line) {
		String s="/*";
		if(isDocument)
			s+="*";
		println(s+" "+line);
		startedComment=true;
		this.isDocument=isDocument;
	}
	public void asImport() {
		finishAll();
		imports.addAll(lines);
		lines.clear();
	}
	public void endComment() {
		isDocument=false;
		startedComment=false;
		println("*/");
	}
	public void defineBlock(String signature) {
		if(startedComment)
			throw new IllegalStateException("Cannot define block in comment!");
		println(signature + " {");
		indent++;
	}

	public void endBlock() {
		if(startedComment)
			throw new IllegalStateException("Cannot end block in comment!");
		indent--;
		println("}");
	}
	public void finishAll() {
		if(startedComment)
			endComment();
		while(indent>0)
			endBlock();
	}
	public FileOutput complete() {
		finishAll();
		for(String s:imports)
			fo.getPrint().println(s);
		fo.getPrint().println("/* This file is generated by data generators, any modification by hand would lost. */");
		for(String s:lines)
			fo.getPrint().println(s);
		return fo;
	}
}
