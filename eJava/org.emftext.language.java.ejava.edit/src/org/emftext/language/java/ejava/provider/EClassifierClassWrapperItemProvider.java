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
package org.emftext.language.java.ejava.provider;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.provider.EcoreEditPlugin;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.emftext.language.java.classifiers.ClassifiersPackage;
import org.emftext.language.java.classifiers.provider.ClassItemProvider;
import org.emftext.language.java.ejava.EClassifierWrapper;
import org.emftext.language.java.ejava.EjavaFactory;
import org.emftext.language.java.ejava.EjavaPackage;
import org.emftext.language.java.members.MembersPackage;

/**
 * This is the item provider adapter for a
 * {@link org.emftext.language.java.ejava.EClassifierClassWrapper} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 *
 * @generated
 */
public class EClassifierClassWrapperItemProvider extends ClassItemProvider
		implements IEditingDomainItemProvider, IStructuredItemContentProvider,
		ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EClassifierClassWrapperItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addEClassifierPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the EClassifier feature. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected void addEClassifierPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_EClassifierWrapper_eClassifier_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_EClassifierWrapper_eClassifier_feature", "_UI_EClassifierWrapper_type"),
				 EjavaPackage.Literals.ECLASSIFIER_WRAPPER__ECLASSIFIER,
				 true,
				 false,
				 true,
				 null,
				 null,
				 null));
	}

	/**
	 * This returns EClassifierInterfaceWrapper.gif. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, EcoreEditPlugin.INSTANCE
				.getImage("full/obj16/EClass"));
	}

	/**
	 * This returns the label text for the adapted class. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public String getText(Object object) {
		String label = ((EClassifierWrapper) object).getName();
		return label;
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s
	 * describing the children that can be created under this object. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(
			Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add
			(createChildParameter
				(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS,
				 EjavaFactory.eINSTANCE.createEClassifierClassWrapper()));

		newChildDescriptors.add
			(createChildParameter
				(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS,
				 EjavaFactory.eINSTANCE.createEClassifierInterfaceWrapper()));

		newChildDescriptors.add
			(createChildParameter
				(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS,
				 EjavaFactory.eINSTANCE.createEClassifierEnumerationWrapper()));

		newChildDescriptors.add
			(createChildParameter
				(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS,
				 EjavaFactory.eINSTANCE.createEStructuralFeatureGetWrapper()));

		newChildDescriptors.add
			(createChildParameter
				(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS,
				 EjavaFactory.eINSTANCE.createEStructuralFeatureSetWrapper()));

		newChildDescriptors.add
			(createChildParameter
				(MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS,
				 EjavaFactory.eINSTANCE.createEOperationWrapper()));

		newChildDescriptors.add
			(createChildParameter
				(MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS,
				 EjavaFactory.eINSTANCE.createEClassifierClassWrapper()));

		newChildDescriptors.add
			(createChildParameter
				(MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS,
				 EjavaFactory.eINSTANCE.createEClassifierInterfaceWrapper()));

		newChildDescriptors.add
			(createChildParameter
				(MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS,
				 EjavaFactory.eINSTANCE.createEClassifierEnumerationWrapper()));

		newChildDescriptors.add
			(createChildParameter
				(MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS,
				 EjavaFactory.eINSTANCE.createEStructuralFeatureGetWrapper()));

		newChildDescriptors.add
			(createChildParameter
				(MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS,
				 EjavaFactory.eINSTANCE.createEStructuralFeatureSetWrapper()));

		newChildDescriptors.add
			(createChildParameter
				(MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS,
				 EjavaFactory.eINSTANCE.createEOperationWrapper()));
	}

	/**
	 * This returns the label text for
	 * {@link org.eclipse.emf.edit.command.CreateChildCommand}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getCreateChildText(Object owner, Object feature,
			Object child, Collection<?> selection) {
		Object childFeature = feature;
		Object childObject = child;

		boolean qualify =
			childFeature == MembersPackage.Literals.MEMBER_CONTAINER__MEMBERS ||
			childFeature == MembersPackage.Literals.MEMBER_CONTAINER__DEFAULT_MEMBERS ||
			childFeature == ClassifiersPackage.Literals.IMPLEMENTOR__IMPLEMENTS ||
			childFeature == ClassifiersPackage.Literals.CLASS__EXTENDS;

		if (qualify) {
			return getString
				("_UI_CreateChild_text2",
				 new Object[] { getTypeText(childObject), getFeatureText(childFeature), getTypeText(owner) });
		}
		return super.getCreateChildText(owner, feature, child, selection);
	}

	/**
	 * Return the resource locator for this item provider's resources. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return EjavaEditPlugin.INSTANCE;
	}

}
