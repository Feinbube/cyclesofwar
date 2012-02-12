package cyclesofwar.codecheck;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.FieldOrMethod;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.ReferenceType;

import cyclesofwar.Arena;
import cyclesofwar.Player;

public class Checker {
	Set<JavaClass> alreadyChecked = new HashSet<JavaClass>();
	
	static void error(JavaClass c, FieldOrMethod n, String message){
		System.out.println(c.getClassName()+"."+n.getName() + ": " + message);
	}

	/* Classes that can be used in an unlimited fashion */
	static Set<String> unlimitedClasses = new HashSet<String>();
	static {
		/* Keep in alphabetical order per package */
		unlimitedClasses.addAll(Arrays.asList(new String[]{
				/* java.lang: reflection is banned */
				"java.lang.Double",
				"java.lang.Object",
				"java.lang.String",
				"java.lang.Math",
				/* java.util: Container API is ok; Random is not */
				"java.util.ArrayList",
				"java.util.Collections",
				"java.util.HashSet",
				"java.util.Iterator",
				"java.util.List",
				"java.util.Set",
				/* java.awt: Color is ok */
				"java.awt.Color",
		}));
	}
	/* Classes that don't need to be checked */
	static Set<String> knownGoodClasses = new HashSet<String>();
	static {
		knownGoodClasses.addAll(Arrays.asList(new String[]{
				"cyclesofwar.Player",
				"cyclesofwar.Planet",
				"cyclesofwar.Fleet",
		}));
	}
	
	/* Calls into the standard library are restricted;
	 * the code of the stdlib itself is not checked
	 */
	static boolean isStdLib(String name){
		return name.startsWith("java") || name.startsWith("javax") 
			|| name.startsWith("org");
	}
	
	void checkCall(JavaClass c, Method m, ReferenceType referenceType, String methodName) {
		/* This really should be a class to make a method call */
		ObjectType t = (ObjectType)referenceType;
		String name = t.getClassName();
		if (!isStdLib(name)) {
			/* For non-stdlib classes, recursively check classes being used */
			try {
				checkClass(Repository.lookupClass(name));
			} catch (ClassNotFoundException e) {
				error(c, m, "could not find class "+name);
			}
			return;
		}
		if (unlimitedClasses.contains(t.getClassName()))
			return;
		error(c, m, "disallowed call to "+t.getClassName()+"."+methodName);
		
	}

	void checkClass(JavaClass clazz)
	{
		if (clazz.isInterface())
			/* Interfaces do not contain code */
			return;
		if (alreadyChecked.contains(clazz))
			return;
		String name = clazz.getClassName();
		if (isStdLib(name))
			/* The standard library class code does not need to be checked */
			return;
		if (knownGoodClasses.contains(name))
			/* Non-stdlib class that is part of the CoW runtime */
			return;
		alreadyChecked.add(clazz);
		/* Static classes are banned because they may defy replays */
		for (Field f:clazz.getFields()){
			if (f.isStatic()){
				error(clazz, f, "static field");
			}
		}
		/* Check byte code: verify that calls either go to the CoW runtime,
		 * or into known-good standard library API.
		 */
		ConstantPoolGen cpg = new ConstantPoolGen(clazz.getConstantPool());
		for (Method m:clazz.getMethods()){
			if (m.isAbstract())
				continue;
			InstructionList a = new InstructionList(m.getCode().getCode());
			for (Instruction insn:a.getInstructions())
			{
				if (insn instanceof InvokeInstruction) {
					InvokeInstruction inv = (InvokeInstruction)insn;
					checkCall(clazz, m, inv.getReferenceType(cpg), inv.getMethodName(cpg));
				}
			}
		}
		
	}

	public static void main(String[] args) throws Exception
	{
		Checker c = new Checker();
		for (Player p:Arena.registeredPlayers()) {
			c.checkClass(Repository.lookupClass(p.getClass()));
		}
	}

}
