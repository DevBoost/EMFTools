/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package org.emftext.language.mecore.resource.mecore.analysis;

public class MAnnotationAnnotationDefinitionReferenceResolver implements org.emftext.language.mecore.resource.mecore.IMecoreReferenceResolver<org.emftext.language.mecore.MAnnotation, org.emftext.language.mecore.MAnnotationDefinition> {
	
	private org.emftext.language.mecore.resource.mecore.analysis.MecoreDefaultResolverDelegate<org.emftext.language.mecore.MAnnotation, org.emftext.language.mecore.MAnnotationDefinition> delegate = new org.emftext.language.mecore.resource.mecore.analysis.MecoreDefaultResolverDelegate<org.emftext.language.mecore.MAnnotation, org.emftext.language.mecore.MAnnotationDefinition>();
	
	public void resolve(String identifier, org.emftext.language.mecore.MAnnotation container, org.eclipse.emf.ecore.EReference reference, int position, boolean resolveFuzzy, final org.emftext.language.mecore.resource.mecore.IMecoreReferenceResolveResult<org.emftext.language.mecore.MAnnotationDefinition> result) {
		delegate.resolve(identifier, container, reference, position, resolveFuzzy, result);
	}
	
	public String deResolve(org.emftext.language.mecore.MAnnotationDefinition element, org.emftext.language.mecore.MAnnotation container, org.eclipse.emf.ecore.EReference reference) {
		return delegate.deResolve(element, container, reference);
	}
	
	public void setOptions(java.util.Map<?,?> options) {
		// save options in a field or leave method empty if this resolver does not depend
		// on any option
	}
	
}
