/*******************************************************************************
 * Copyright (c) 2006-2012
 * Software Technology Group, Dresden University of Technology
 * DevBoost GmbH, Berlin, Amtsgericht Charlottenburg, HRB 140026
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Software Technology Group - TU Dresden, Germany;
 *   DevBoost GmbH - Berlin, Germany
 *      - initial API and implementation
 ******************************************************************************/
package de.devboost.emfcustomize;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.language.java.annotations.AnnotationInstance;
import org.emftext.language.java.classifiers.Class;
import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.generics.QualifiedTypeArgument;
import org.emftext.language.java.members.ClassMethod;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.modifiers.AnnotationInstanceOrModifier;
import org.emftext.language.java.modifiers.Public;
import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.resource.java.mopp.JavaResource;
import org.emftext.language.java.types.NamespaceClassifierReference;
import org.emftext.language.java.types.PrimitiveType;
import org.emftext.language.java.types.Type;
import org.emftext.language.java.types.TypeReference;

public class EcoreModelRefactorer {

	public void propagateEOperations(JavaResource resource, GenClass genClass) {
		GenPackage genPackage = genClass.getGenPackage();
		EPackage ePackage = genPackage.getEcorePackage();
		if (resource.getContents().isEmpty() || !(resource.getContents().get(0) instanceof CompilationUnit)) {
			return;
		}
		CompilationUnit cu = (CompilationUnit) resource.getContents().get(0);
		Class customClass = (Class) cu.getClassifiers().get(0);
		EClass eClass = genClass.getEcoreClass();

		if (eClass == null) {
			return;
		}

		clearExistingOperations(eClass);

		for (Method method : customClass.getMethods()) {
			if(canOperationForMethodBeGenerated(method, eClass)){
				for (AnnotationInstanceOrModifier modifier : method.getAnnotationsAndModifiers()) {
					if (modifier instanceof Public) {
						EOperation newEOperation = EcoreFactory.eINSTANCE.createEOperation();
						annotateAsGenerated(newEOperation);
						newEOperation.setName(method.getName());
						Type opType = method.getTypeReference().getTarget();
						newEOperation.setEType(eClassifierForCustomClass(opType,
								method.getTypeReference(), ePackage));
						if (isMulti(opType)) {
							newEOperation.setUpperBound(-1);
						}
						for (Parameter parameter : method.getParameters()) {
							EParameter newEParameter = EcoreFactory.eINSTANCE.createEParameter();
							newEParameter.setName(parameter.getName());
							Type paramType = parameter.getTypeReference().getTarget();
							newEParameter.setEType(eClassifierForCustomClass(paramType,
									parameter.getTypeReference(), ePackage));
							//TODO generics, ...
							newEOperation.getEParameters().add(newEParameter);
						}
						for (AnnotationInstanceOrModifier annotationInstance : method.getAnnotationsAndModifiers()) {
							if (annotationInstance instanceof AnnotationInstance) {
								Classifier javaAnnotation = ((AnnotationInstance) annotationInstance).getAnnotation();
								if (javaAnnotation.eIsProxy()) {
									continue;
								}
								EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
								eAnnotation.setSource(javaAnnotation.getContainingCompilationUnit(
										).getNamespacesAsString() + javaAnnotation.getName());
								newEOperation.getEAnnotations().add(eAnnotation);
							}
						}

						eClass.getEOperations().add(newEOperation);
						break;
					}
				}
			}
		}

		try {
			Resource ecoreResource = ePackage.eResource();
			URI originalURI = ecoreResource.getURI();
			if (originalURI.isFile()) {
				String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
				URI platformURI = URI.createPlatformResourceURI(originalURI.toFileString().substring(workspacePath.length()), true);
				ecoreResource.setURI(platformURI);
			}
			new ResourceSaver().saveResource(ecoreResource);
			ecoreResource.setURI(originalURI);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean canOperationForMethodBeGenerated(Method method, EClass ecoreClass){
		// structural features?
		if(method.getName().startsWith("get") || method.getName().startsWith("set")){
			String realName = method.getName().substring(3);
			char[] stringArray = realName.toCharArray();
			stringArray[0] = Character.toUpperCase(stringArray[0]);
			String realNameLowerCase = new String(stringArray);
			List<EStructuralFeature> structuralFeatures = ecoreClass.getEAllStructuralFeatures();
			for (EStructuralFeature structuralFeature : structuralFeatures) {
				if(structuralFeature.getName().equals(realNameLowerCase) || structuralFeature.getName().equals(realName)){
					return false;
				}
			}
		}
		// super method?
		List<AnnotationInstanceOrModifier> annotationsAndModifiers = method.getAnnotationsAndModifiers();
		for (AnnotationInstanceOrModifier annotationInstanceOrModifier : annotationsAndModifiers) {
			if(annotationInstanceOrModifier instanceof AnnotationInstance){
				AnnotationInstance annotationInstance = (AnnotationInstance) annotationInstanceOrModifier;
				Classifier classifier = annotationInstance.getAnnotation();
				String name = classifier.getName();
				List<String> packageName = classifier.getContainingPackageName();
				if("Override".equals(name) && packageName.size() == 2 && "java".equals(packageName.get(0)) && "lang".equals(packageName.get(1))){
					//modelled operation?
					List<EOperation> operations = ecoreClass.getEOperations();
					for (EOperation operation : operations) {
						if(operation.getName().equals(method.getName())){
							if(operation.getEAnnotation(EcoreModelRefactorer.class.getName()) == null){
								// TODO check signature
								
							}
							return true;
						}
					}
					return false;
				}
			}
		}
		return true;
	}

	private void clearExistingOperations(EClass eClass) {
		for (Iterator<EOperation> i = eClass.getEOperations().iterator(); i.hasNext();) {
			if (i.next().getEAnnotation(EcoreModelRefactorer.class.getName()) != null) {
				i.remove();
			}		
		}
	}

	private void annotateAsGenerated(EOperation newEOperation) {
		EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
		eAnnotation.setSource(EcoreModelRefactorer.class.getName());
		newEOperation.getEAnnotations().add(eAnnotation);
	}

	private EClassifier eClassifierForCustomClass(Type type, TypeReference typeReference, EPackage startEPackage) {
		Set<EPackage> allEPackages = new LinkedHashSet<EPackage>();
		collectAllEPackages(startEPackage, allEPackages);

		for (EPackage ePackage : allEPackages) {
			if (type instanceof ConcreteClassifier) {
				if (((ConcreteClassifier) type).getName().equals("EList")) {
					QualifiedTypeArgument typeArgument = (QualifiedTypeArgument) ((NamespaceClassifierReference) typeReference).getClassifierReferences().get(0).getTypeArguments().get(0);
					type = typeArgument.getTypeReference().getTarget();
				}
				String className = eClassNameForCustomClassName(((ConcreteClassifier) type).getName());
				for (EClassifier eClassifier : ePackage.getEClassifiers()) {
					if (eClassifier.getName().equals(className)) {
						return eClassifier;
					}
				}
				if (className.equals("Class")) {
					className = "JavaClass"; //TODO type parameters
				}
				className = "E" + className;
				for (EClassifier typeFromEcore : EcorePackage.eINSTANCE.getEClassifiers()) {
					if (typeFromEcore.getName().equals(className)) {
						return typeFromEcore;
					}
				}
			} else if (type instanceof PrimitiveType) {
				String primitiveTypeName = "E" + type.eClass().getName();
				for (EClassifier typeFromEcore : EcorePackage.eINSTANCE.getEClassifiers()) {
					if (typeFromEcore.getName().equals(primitiveTypeName)) {
						return typeFromEcore;
					}
				}
			}
		}

		return null;
	}

	private boolean isMulti(Type type) {
		if (type instanceof ConcreteClassifier) {
			String className = ((ConcreteClassifier) type).getName();
			if (className.equals("EList")) {
				return true;
			}
		}
		return false;
	}

	private void collectAllEPackages(EPackage startEPackage, Set<EPackage> allEPackages) {
		if (allEPackages.contains(startEPackage)) {
			return;
		}
		allEPackages.add(startEPackage);
		for (EClassifier eClassifier : startEPackage.getEClassifiers()) {
			if (eClassifier instanceof EClass) {
				for (EClass superType : ((EClass) eClassifier).getESuperTypes()) {
					if (!superType.eIsProxy()) {
						collectAllEPackages(superType.getEPackage(), allEPackages);
					}
				}
			}
		}
	}

	private String eClassNameForCustomClassName(String name) {
		int idx = name.indexOf(GeneratedFactoryRefactorer.CUSTOM_CLASS_SUFFIX);
		if (idx != -1) {
			return name.substring(0, idx);
		}
		return name;
	}
}
