/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.hk2.utilities;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Set;
import org.glassfish.hk2.api.DynamicInjectee;
import org.glassfish.hk2.utilities.reflection.Pretty;

/**
 * This is an implementation of the DynamicInjectee interface. Using this
 * implementation may make your code more portable, as new methods added to the
 * interface will be reflected in this class.
 *
 * @author saden@author Sharmarke Aden (saden)
 * @param <T>
 *
 */
public class DynamicInjecteeImpl<T extends Type> implements DynamicInjectee<T> {

    private final Type type;
    private final Set<Annotation> qualifiers;
    private final Class<?> parentClass;
    private final AnnotatedElement parent;
    private int position = -1;
    private boolean optional = false;

    public DynamicInjecteeImpl(Type type,
            Set<Annotation> qualifiers,
            int position,
            boolean optional,
            Class<?> parentClass,
            AnnotatedElement parent) {
        this.type = type;
        this.qualifiers = qualifiers;
        this.parentClass = parentClass;
        this.parent = parent;
        this.position = position;
        this.optional = optional;
    }

    /**
     * This is the copy constructor, which will copy all the values from the
     * incoming dynamic instance.
     *
     * @param copyMe The non-null Injectee to copy the values from
     */
    public DynamicInjecteeImpl(DynamicInjectee<T> copyMe) {
        type = copyMe.getType();
        position = copyMe.getPosition();
        parent = copyMe.getParent();
        parentClass = copyMe.getParentClass();
        qualifiers = copyMe.getQualifiers();
        optional = copyMe.isOptional();
    }

    @Override
    public T getType() {
        return (T) type;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    @Override
    public <T extends Annotation> T getQualifier(Class<T> type) {
        for (Annotation qualifier : qualifiers) {
            if (qualifier.annotationType().equals(type)) {
                return (T) qualifier;
            }
        }

        return null;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public Class<?> getParentClass() {
        return parentClass;
    }

    @Override
    public AnnotatedElement getParent() {
        return parent;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public String toString() {
        return "DynamicImpl(type=" + Pretty.type(type)
                + ",parent=" + Pretty.clazz(parentClass)
                + ",qualifier=" + Pretty.collection(qualifiers)
                + ",position=" + position
                + ",optional=" + optional
                + "," + System.identityHashCode(this) + ")";
    }

}
