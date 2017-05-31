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
package org.glassfish.hk2.configuration.api;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Scope;

/**
 * This annotation is put onto classes to indicate that
 * they should be created based on the availability of
 * instances of a specify type of configuration in the 
 * {@link org.glassfish.hk2.configuration.hub.api.Hub}
 * 
 * @author jwells
 *
 */
@Documented
@Scope
@Retention(RUNTIME)
@Target(TYPE)
public @interface ConfiguredBy {
    /**
     * A service is created for each instance of this type,
     * with a name taken from the key of the instance
     * 
     * @return the name of the type to base instances
     * of this service on
     */
    public String value();
    
    /**
     * Specifies the creation policy for configured services
     * based on type instances.  The values it can take are:
     * <UL>
     * <LI>ON_DEMAND - Services are created when user code creates demand (via lookup or injection)</LI>
     * <LI>EAGER - Services are created as soon as configured instances become available</LI>
     * </UL>
     * The default value is ON_DEMAND
     * 
     * @return The creation policy for services configured by this type
     */
    public CreationPolicy creationPolicy() default CreationPolicy.ON_DEMAND;
    
    public enum CreationPolicy {
        /**
         * Instances of services with this policy will
         * be created when some user code creates explicit
         * demand for the service.  This is similar to most
         * other hk2 services
         */
        ON_DEMAND,
        
        /**
         * Instances of services with this policy will
         * be created as soon as their backing instances
         * become available
         */
        EAGER
    }

}
