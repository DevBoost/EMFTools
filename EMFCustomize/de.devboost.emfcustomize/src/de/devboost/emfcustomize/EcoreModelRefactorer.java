/*******************************************************************************
 * Copyright (c) 2006-2013
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.classifiers.Class;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.generics.QualifiedTypeArgument;
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

	public static final String MODEL_ANNOTATION			= "@model";
	private static final char[] VALID_PREFIX_CHARACTERS	= new char[]{'*'};
	
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

		Set<Method> annotatedMethods = getAnnotatedMethods(customClass);

		for (Method method : annotatedMethods) {
			for (AnnotationInstanceOrModifier modifier : method.getAnnotationsAndModifiers()) {
				if (modifier instanceof Public) {
					EOperation newEOperation = EcoreFactory.eINSTANCE.createEOperation();
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
						newEParameter.setEType(eClassifierForCustomClass(paramType, parameter.getTypeReference(), ePackage));
						//TODO generics, ...
						newEOperation.getEParameters().add(newEParameter);
					}
					// TODO @jendrik: why is that needed?
//					for (AnnotationInstanceOrModifier annotationInstance : method.getAnnotationsAndModifiers()) {
//						if (annotationInstance instanceof AnnotationInstance) {
//							Classifier javaAnnotation = ((AnnotationInstance) annotationInstance).getAnnotation();
//							if (javaAnnotation.eIsProxy()) {
//								continue;
//							}
//							EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
//							eAnnotation.setSource(javaAnnotation.getContainingCompilationUnit(
//									).getNamespacesAsString() + javaAnnotation.getName());
//							newEOperation.getEAnnotations().add(eAnnotation);
//						}
//					}
					boolean operationAlreadyExists = false;
					List<EOperation> operations = eClass.getEOperations();
					List<EOperation> existingOperations = new ArrayList<EOperation>(operations);
					// must be done here already for ensuring that compared operations have the same parent
					eClass.getEOperations().add(newEOperation);
					for (EOperation existingOperation : existingOperations) {
						boolean removed = removeAnnotation(existingOperation);
						if(EcoreUtil.equals(existingOperation, newEOperation)){
							operationAlreadyExists = true;
							removeAnnotation(existingOperation);
							annotateAsGenerated(existingOperation);
							break;
						}
						if(removed){
							annotateAsGenerated(existingOperation);
						}
					}
					if(!operationAlreadyExists){
						annotateAsGenerated(newEOperation);
					} else {
						eClass.getEOperations().remove(newEOperation);
					}
					break;
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

	private boolean removeAnnotation(EOperation existingOperation) {
		List<EAnnotation> annotations = existingOperation.getEAnnotations();
		EAnnotation annotationToRemove = null;
		for (EAnnotation annotation : annotations) {
			if(annotation.getSource().equals(EcoreModelRefactorer.class.getName())){
				annotationToRemove = annotation;
			}
		}
		if(annotationToRemove != null){
			return annotations.remove(annotationToRemove);
		}
		return false;
	}

	public Set<Method> getAnnotatedMethods(Class customClass) {
		Set<Method> annotatedMethods = new HashSet<Method>();
		
		List<Method> methods = customClass.getMethods();
		for (Method method : methods) {
			List<String> comments = method.getComments();
			if(comments != null && comments.size() > 0){
				for (String comment : comments) {
					String[] lines = comment.split("[\\r\\n]+");
					for (String line : lines) {
						String deleteWhitespace = StringUtils.deleteWhitespace(line);
						if(StringUtils.endsWith(deleteWhitespace, MODEL_ANNOTATION)){
							String difference = StringUtils.removeEnd(deleteWhitespace, MODEL_ANNOTATION);
							if(StringUtils.containsOnly(difference, VALID_PREFIX_CHARACTERS) || difference.isEmpty()){
								annotatedMethods.add(method);
							}
						}
					}
				}
			}
		}
		return annotatedMethods;
	}

//	private void clearExistingOperations(EClass eClass) {
//		for (Iterator<EOperation> i = eClass.getEOperations().iterator(); i.hasNext();) {
//			if (i.next().getEAnnotation(EcoreModelRefactorer.class.getName()) != null) {
//				i.remove();
//			}		
//		}
//	}

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
