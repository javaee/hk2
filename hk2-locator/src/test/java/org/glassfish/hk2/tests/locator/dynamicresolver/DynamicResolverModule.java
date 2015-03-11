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
package org.glassfish.hk2.tests.locator.dynamicresolver;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicResolver;
import org.glassfish.hk2.tests.locator.dynamicresolver.contract.ContractInjectedService;
import org.glassfish.hk2.tests.locator.dynamicresolver.contract.DynamicContractResolver;
import org.glassfish.hk2.tests.locator.dynamicresolver.misc.NonDynamicTypeInjectedService;
import org.glassfish.hk2.tests.locator.dynamicresolver.misc.OptionalNonDynamicTypeInjectedService;
import org.glassfish.hk2.tests.locator.dynamicresolver.misc.OptionalQualifedInjectedService;
import org.glassfish.hk2.tests.locator.dynamicresolver.misc.OptionalQualifedTypeInjectedService;
import org.glassfish.hk2.tests.locator.dynamicresolver.misc.OptionalTypeInjectedService;
import org.glassfish.hk2.tests.locator.dynamicresolver.misc.UntypedAndUnqualifiedResolver;
import org.glassfish.hk2.tests.locator.dynamicresolver.qualifed.DynamicQualifedResolver;
import org.glassfish.hk2.tests.locator.dynamicresolver.qualifed.DynamicQualifiedQualifer;
import org.glassfish.hk2.tests.locator.dynamicresolver.qualifed.QualifedInjectedService;
import org.glassfish.hk2.tests.locator.dynamicresolver.qualifedType.DynamicQualifedTypeResolver;
import org.glassfish.hk2.tests.locator.dynamicresolver.qualifedType.DynamicQualifiedTypeQualifer;
import org.glassfish.hk2.tests.locator.dynamicresolver.qualifedType.QualifedTypeInjectedService;
import org.glassfish.hk2.tests.locator.dynamicresolver.type.DynamicTypeResolver;
import org.glassfish.hk2.tests.locator.dynamicresolver.type.TypeInjectedService;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 *
 * @author Sharmarke Aden (saden)
 */
public class DynamicResolverModule implements TestModule {

    @Override
    public void configure(DynamicConfiguration config) {
        //optional injection
        config.bind(BuilderHelper.link(OptionalNonDynamicTypeInjectedService.class)
                .build());

        config.bind(BuilderHelper.link(OptionalTypeInjectedService.class)
                .build());

        config.bind(BuilderHelper.link(OptionalQualifedInjectedService.class)
                .build());

        config.bind(BuilderHelper.link(OptionalQualifedTypeInjectedService.class)
                .build());

        //contract injection
        config.bind(BuilderHelper.link(DynamicContractResolver.class)
                .to(DynamicResolver.class)
                .build());

        config.bind(BuilderHelper.link(ContractInjectedService.class)
                .build());

        //type injection
        config.bind(BuilderHelper.link(DynamicTypeResolver.class)
                .to(DynamicResolver.class)
                .build());

        config.bind(BuilderHelper.link(TypeInjectedService.class)
                .build());

        //qualified injection
        config.bind(BuilderHelper.link(DynamicQualifedResolver.class)
                .to(DynamicResolver.class)
                .qualifiedBy(DynamicQualifiedQualifer.class.getName())
                .build());

        config.bind(BuilderHelper.link(QualifedInjectedService.class)
                .build());

        //qualified type injection
        config.bind(BuilderHelper.link(DynamicQualifedTypeResolver.class)
                .to(DynamicResolver.class)
                .qualifiedBy(DynamicQualifiedTypeQualifer.class.getName())
                .build());

        config.bind(BuilderHelper.link(QualifedTypeInjectedService.class)
                .build());

        //misc
        config.bind(BuilderHelper.link(NonDynamicTypeInjectedService.class)
                .build());

        config.bind(BuilderHelper.link(UntypedAndUnqualifiedResolver.class)
                .to(DynamicResolver.class)
                .build());

    }

}
