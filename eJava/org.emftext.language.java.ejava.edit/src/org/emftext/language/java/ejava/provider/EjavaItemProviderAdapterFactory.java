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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.emftext.language.java.ejava.util.EjavaAdapterFactory;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc --> <!--
 * end-user-doc -->
 * @generated
 */
public class EjavaItemProviderAdapterFactory extends EjavaAdapterFactory
		implements ComposeableAdapterFactory, IChangeNotifier, IDisposable {
	/**
	 * This keeps track of the root adapter factory that delegates to this adapter factory.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected ComposedAdapterFactory parentAdapterFactory;

	/**
	 * This is used to implement
	 * {@link org.eclipse.emf.edit.provider.IChangeNotifier}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected IChangeNotifier changeNotifier = new ChangeNotifier();

	/**
	 * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	protected Collection<Object> supportedTypes = new ArrayList<Object>();

	/**
	 * This constructs an instance. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @generated
	 */
	public EjavaItemProviderAdapterFactory() {
		supportedTypes.add(IEditingDomainItemProvider.class);
		supportedTypes.add(IStructuredItemContentProvider.class);
		supportedTypes.add(ITreeItemContentProvider.class);
		supportedTypes.add(IItemLabelProvider.class);
		supportedTypes.add(IItemPropertySource.class);
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.emftext.language.java.ejava.EPackageWrapper} instances. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected EPackageWrapperItemProvider ePackageWrapperItemProvider;

	/**
	 * This creates an adapter for a
	 * {@link org.emftext.language.java.ejava.EPackageWrapper}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Adapter createEPackageWrapperAdapter() {
		if (ePackageWrapperItemProvider == null) {
			ePackageWrapperItemProvider = new EPackageWrapperItemProvider(this);
		}

		return ePackageWrapperItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.emftext.language.java.ejava.EClassifierClassWrapper} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClassifierClassWrapperItemProvider eClassifierClassWrapperItemProvider;

	/**
	 * This creates an adapter for a
	 * {@link org.emftext.language.java.ejava.EClassifierClassWrapper}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Adapter createEClassifierClassWrapperAdapter() {
		if (eClassifierClassWrapperItemProvider == null) {
			eClassifierClassWrapperItemProvider = new EClassifierClassWrapperItemProvider(this);
		}

		return eClassifierClassWrapperItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.emftext.language.java.ejava.EClassifierInterfaceWrapper} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClassifierInterfaceWrapperItemProvider eClassifierInterfaceWrapperItemProvider;

	/**
	 * This creates an adapter for a
	 * {@link org.emftext.language.java.ejava.EClassifierInterfaceWrapper}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Adapter createEClassifierInterfaceWrapperAdapter() {
		if (eClassifierInterfaceWrapperItemProvider == null) {
			eClassifierInterfaceWrapperItemProvider = new EClassifierInterfaceWrapperItemProvider(this);
		}

		return eClassifierInterfaceWrapperItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.emftext.language.java.ejava.EClassifierEnumerationWrapper} instances.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  protected EClassifierEnumerationWrapperItemProvider eClassifierEnumerationWrapperItemProvider;

  /**
	 * This creates an adapter for a {@link org.emftext.language.java.ejava.EClassifierEnumerationWrapper}.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public Adapter createEClassifierEnumerationWrapperAdapter()
  {
		if (eClassifierEnumerationWrapperItemProvider == null) {
			eClassifierEnumerationWrapperItemProvider = new EClassifierEnumerationWrapperItemProvider(this);
		}

		return eClassifierEnumerationWrapperItemProvider;
	}

  /**
	 * This keeps track of the one adapter used for all {@link org.emftext.language.java.ejava.EStructuralFeatureGetWrapper} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EStructuralFeatureGetWrapperItemProvider eStructuralFeatureGetWrapperItemProvider;

	/**
	 * This creates an adapter for a {@link org.emftext.language.java.ejava.EStructuralFeatureGetWrapper}.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createEStructuralFeatureGetWrapperAdapter() {
		if (eStructuralFeatureGetWrapperItemProvider == null) {
			eStructuralFeatureGetWrapperItemProvider = new EStructuralFeatureGetWrapperItemProvider(this);
		}

		return eStructuralFeatureGetWrapperItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.emftext.language.java.ejava.EStructuralFeatureSetWrapper} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EStructuralFeatureSetWrapperItemProvider eStructuralFeatureSetWrapperItemProvider;

	/**
	 * This creates an adapter for a {@link org.emftext.language.java.ejava.EStructuralFeatureSetWrapper}.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createEStructuralFeatureSetWrapperAdapter() {
		if (eStructuralFeatureSetWrapperItemProvider == null) {
			eStructuralFeatureSetWrapperItemProvider = new EStructuralFeatureSetWrapperItemProvider(this);
		}

		return eStructuralFeatureSetWrapperItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.emftext.language.java.ejava.EEnumLiteralWrapper} instances.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  protected EEnumLiteralWrapperItemProvider eEnumLiteralWrapperItemProvider;

  /**
	 * This creates an adapter for a {@link org.emftext.language.java.ejava.EEnumLiteralWrapper}.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  @Override
  public Adapter createEEnumLiteralWrapperAdapter()
  {
		if (eEnumLiteralWrapperItemProvider == null) {
			eEnumLiteralWrapperItemProvider = new EEnumLiteralWrapperItemProvider(this);
		}

		return eEnumLiteralWrapperItemProvider;
	}

  /**
	 * This keeps track of the one adapter used for all
	 * {@link org.emftext.language.java.ejava.EOperationWrapper} instances. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected EOperationWrapperItemProvider eOperationWrapperItemProvider;

	/**
	 * This creates an adapter for a
	 * {@link org.emftext.language.java.ejava.EOperationWrapper}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Adapter createEOperationWrapperAdapter() {
		if (eOperationWrapperItemProvider == null) {
			eOperationWrapperItemProvider = new EOperationWrapperItemProvider(this);
		}

		return eOperationWrapperItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.emftext.language.java.ejava.EObjectInstantiation} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EObjectInstantiationItemProvider eObjectInstantiationItemProvider;

	/**
	 * This creates an adapter for a
	 * {@link org.emftext.language.java.ejava.EObjectInstantiation}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Adapter createEObjectInstantiationAdapter() {
		if (eObjectInstantiationItemProvider == null) {
			eObjectInstantiationItemProvider = new EObjectInstantiationItemProvider(this);
		}

		return eObjectInstantiationItemProvider;
	}

	/**
	 * This returns the root adapter factory that contains this factory. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ComposeableAdapterFactory getRootAdapterFactory() {
		return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
	}

	/**
	 * This sets the composed adapter factory that contains this factory. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setParentAdapterFactory(
			ComposedAdapterFactory parentAdapterFactory) {
		this.parentAdapterFactory = parentAdapterFactory;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object type) {
		return supportedTypes.contains(type) || super.isFactoryForType(type);
	}

	/**
	 * This implementation substitutes the factory itself as the key for the adapter.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter adapt(Notifier notifier, Object type) {
		return super.adapt(notifier, this);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object adapt(Object object, Object type) {
		if (isFactoryForType(type)) {
			Object adapter = super.adapt(object, type);
			if (!(type instanceof Class<?>) || (((Class<?>)type).isInstance(adapter))) {
				return adapter;
			}
		}

		return null;
	}

	/**
	 * This adds a listener.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void addListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.addListener(notifyChangedListener);
	}

	/**
	 * This removes a listener.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void removeListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.removeListener(notifyChangedListener);
	}

	/**
	 * This delegates to {@link #changeNotifier} and to
	 * {@link #parentAdapterFactory}. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @generated
	 */
	public void fireNotifyChanged(Notification notification) {
		changeNotifier.fireNotifyChanged(notification);

		if (parentAdapterFactory != null) {
			parentAdapterFactory.fireNotifyChanged(notification);
		}
	}

	/**
	 * This disposes all of the item providers created by this factory. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void dispose() {
		if (ePackageWrapperItemProvider != null) ePackageWrapperItemProvider.dispose();
		if (eClassifierClassWrapperItemProvider != null) eClassifierClassWrapperItemProvider.dispose();
		if (eClassifierInterfaceWrapperItemProvider != null) eClassifierInterfaceWrapperItemProvider.dispose();
		if (eClassifierEnumerationWrapperItemProvider != null) eClassifierEnumerationWrapperItemProvider.dispose();
		if (eStructuralFeatureGetWrapperItemProvider != null) eStructuralFeatureGetWrapperItemProvider.dispose();
		if (eStructuralFeatureSetWrapperItemProvider != null) eStructuralFeatureSetWrapperItemProvider.dispose();
		if (eEnumLiteralWrapperItemProvider != null) eEnumLiteralWrapperItemProvider.dispose();
		if (eOperationWrapperItemProvider != null) eOperationWrapperItemProvider.dispose();
		if (eObjectInstantiationItemProvider != null) eObjectInstantiationItemProvider.dispose();
	}

}
