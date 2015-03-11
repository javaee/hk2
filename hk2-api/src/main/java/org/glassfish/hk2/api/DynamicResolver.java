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
package org.glassfish.hk2.api;

import org.jvnet.hk2.annotations.Contract;

/**
 * This class allows users to provide a custom injection target for &#64;Inject.
 * <p>
 * Using Dynamic Resolvers one can perform three types of dynamic injection
 * resolution:
 * </p>
 * <ol>
 * <li>Dynamic Type Injection (dynamic resolution of a specific type)</li>
 * <li>Dynamic Qualified Injection (dynamic resolution of any type with a
 * specific qualifier(s))</li>
 * <li>Dynamic Qualified Type Injection (dynamic resolution of a specific type
 * with a specific qualifier(s)</li>
 * </ol>
 *
 * <p>
 * Note that for dynamic qualified injection the qualifier must be placed on the
 * DynamicResolver implementation.
 * </p>
 *
 * <p>
 * An implementation of DynamicResolver must be in the Singleton scope.
 * Implementations of DynamicResolver will be instantiated as soon as they are
 * added to HK2 in order to avoid deadlocks and circular references. Therefore
 * it is recommended that implementations of InjectionResolver make liberal use
 * of {@link javax.inject.Provider} or {@link IterableProvider} when injecting
 * dependent services so that these services are not instantiated when the
 * DynamicResolver is created
 * </p>
 *
 * @author Sharmarke Aden
 * @param <T> This must be the class of the dynamic injection type that this
 * resolver will handle.
 */
@Contract
public interface DynamicResolver<T> {

    /**
     * This method will return the object that should be injected into the given
     * dynamic injection point. It is the responsibility of the implementation
     * to ensure that the object returned can be safely injected into the
     * injection point.
     * <p>
     * This method should not do the injection themselves
     *
     * @param injectee The dynamic injection point this value is being injected
     * into
     *
     * @return the object that will be injected.
     */
    public T resolve(DynamicInjectee<T> injectee);

}
