/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.hk2.utilities.general.test;

import java.util.Random;

import org.glassfish.hk2.utilities.general.GeneralUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class GeneralUtilitiesTest {
    private final static String RANDOM_RESULT =
            "Total buffer length: 128\n" +
            "00000000 88 18 B9 9F E8 54 2F 53  36 5A 99 D8 9E 24 14 EA \n" +
            "00000010 22 E8 90 24 67 DB 7E 4B  5E 71 05 60 65 40 F5 A3 \n" +
            "00000020 E5 32 CE FF 7D 58 77 2E  A5 21 80 72 17 7F 7B B4 \n" +
            "00000030 8D B7 1B B6 9C 32 28 BB  5C 7C 04 3D D2 D1 41 A1 \n" +
            "00000040 3F 44 6C 27 84 EF AE 06  82 01 2E F6 1C 24 FF F3 \n" +
            "00000050 DE BB 0A 54 0C F1 42 8A  32 17 80 61 4A 70 36 0C \n" +
            "00000060 9E 9C 14 5B 22 BA B9 FA  5C 04 69 80 26 36 A9 60 \n" +
            "00000070 D6 DB FD C5 10 00 9C 66  B7 62 6D 31 CC 37 28 F2 ";
    
    private final Random random = new Random(1967L);
    
    @Test
    public void testPrintOutBytes() {
        byte buffer[] = new byte[128];
        
        random.nextBytes(buffer);
        
        String asString = GeneralUtilities.prettyPrintBytes(buffer);
        
        Assert.assertEquals(RANDOM_RESULT, asString);
    }

}
