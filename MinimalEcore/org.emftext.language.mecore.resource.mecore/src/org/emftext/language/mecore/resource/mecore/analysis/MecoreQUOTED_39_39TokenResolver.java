/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package org.emftext.language.mecore.resource.mecore.analysis;

public class MecoreQUOTED_39_39TokenResolver implements org.emftext.language.mecore.resource.mecore.IMecoreTokenResolver {
	
	private org.emftext.language.mecore.resource.mecore.analysis.MecoreDefaultTokenResolver defaultTokenResolver = new org.emftext.language.mecore.resource.mecore.analysis.MecoreDefaultTokenResolver(true);
	
	public String deResolve(Object value, org.eclipse.emf.ecore.EStructuralFeature feature, org.eclipse.emf.ecore.EObject container) {
		// By default token de-resolving is delegated to the DefaultTokenResolver.
		String result = defaultTokenResolver.deResolve(value, feature, container, "'", "'", null);
		return result;
	}
	
	public void resolve(String lexem, org.eclipse.emf.ecore.EStructuralFeature feature, org.emftext.language.mecore.resource.mecore.IMecoreTokenResolveResult result) {
		// By default token resolving is delegated to the DefaultTokenResolver.
		defaultTokenResolver.resolve(lexem, feature, result, "'", "'", null);
	}
	
	public void setOptions(java.util.Map<?,?> options) {
		defaultTokenResolver.setOptions(options);
	}
	
}
