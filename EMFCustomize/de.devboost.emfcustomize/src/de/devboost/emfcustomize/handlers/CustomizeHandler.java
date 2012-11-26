package de.devboost.emfcustomize.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;

import de.devboost.emfcustomize.GeneratedFactoryRefactorer;

public class CustomizeHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
		if(currentSelection instanceof IStructuredSelection){
			Object firstElement = ((IStructuredSelection) currentSelection).getFirstElement();
			if(firstElement instanceof GenClass){
				process((GenClass) firstElement);
			}
		}
		return null;
	}

	
	private void process(GenClass genClass){
		ConcreteClassifier customClass = new GeneratedFactoryRefactorer().createInitialCustomClass(genClass);
		URI uri = customClass.eResource().getURI();
		CompilationUnit compilationUnit = customClass.getContainingCompilationUnit();
		List<String> namespaces = compilationUnit.getNamespaces();
		URI srcFolderUri = uri.trimSegments(1 + namespaces.size());
		String platformString = srcFolderUri.toPlatformString(true);
		IResource member = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);
		IProject project = member.getProject();
		IJavaProject javaProject = JavaCore.create(project);
		IFolder folder = (IFolder) member;
		try {
			IClasspathEntry classPath = JavaCore.newSourceEntry(folder.getFullPath());
			IClasspathEntry[] classpathEntries = javaProject.getRawClasspath();
			boolean found = false;
			for (IClasspathEntry entry : classpathEntries) {
				if(entry.equals(classPath)){
					found = true;
					break;
				}
			}
			if(!found){
				List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
				list.addAll(Arrays.asList(classpathEntries));
				list.add(classPath);
				javaProject.setRawClasspath(list.toArray(new IClasspathEntry[0]), new NullProgressMonitor());
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
}
