[//]: # " DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER. "
[//]: # "  "
[//]: # " Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved. "
[//]: # "  "
[//]: # " The contents of this file are subject to the terms of either the GNU "
[//]: # " General Public License Version 2 only (''GPL'') or the Common Development "
[//]: # " and Distribution License(''CDDL'') (collectively, the ''License'').  You "
[//]: # " may not use this file except in compliance with the License.  You can "
[//]: # " obtain a copy of the License at "
[//]: # " https://oss.oracle.com/licenses/CDDL+GPL-1.1 "
[//]: # " or LICENSE.txt.  See the License for the specific "
[//]: # " language governing permissions and limitations under the License. "
[//]: # "  "
[//]: # " When distributing the software, include this License Header Notice in each "
[//]: # " file and include the License file at LICENSE.txt. "
[//]: # "  "
[//]: # " GPL Classpath Exception: "
[//]: # " Oracle designates this particular file as subject to the ''Classpath'' "
[//]: # " exception as provided by Oracle in the GPL Version 2 section of the License "
[//]: # " file that accompanied this code. "
[//]: # "  "
[//]: # " Modifications: "
[//]: # " If applicable, add the following below the License Header, with the fields "
[//]: # " enclosed by brackets [] replaced by your own identifying information: "
[//]: # " ''Portions Copyright [year] [name of copyright owner]'' "
[//]: # "  "
[//]: # " Contributor(s): "
[//]: # " If you wish your version of this file to be governed by only the CDDL or "
[//]: # " only the GPL Version 2, indicate your decision by adding ''[Contributor] "
[//]: # " elects to include this software in this distribution under the [CDDL or GPL "
[//]: # " Version 2] license.''  If you don't indicate a single choice of license, a "
[//]: # " recipient has the option to distribute your version of this file under "
[//]: # " either the CDDL, the GPL Version 2 or to extend the choice of license to "
[//]: # " its licensees as provided above.  However, if you add GPL Version 2 code "
[//]: # " and therefore, elected the GPL Version 2 license, then the option applies "
[//]: # " only if the new code is made subject to such option by the copyright "
[//]: # " holder. "

HK2 is an implementation of JSR-330 in a JavaSE environment.


[JSR-330](http://jcp.org/aboutJava/communityprocess/final/jsr330/) defines services and injection points that can be dynamically discovered at runtime and which allow for Inversion of Control (IoC) and dependency injection (DI).


HK2 provides an API for control over its operation and has the ability to automatically load services into the container.


It is the foundation for the GlassFish V3 and V4 application servers as well as other products.


HK2 also has powerful features that can be used to perform tasks such as looking up services or customizing you injections, as well as several extensibility features allowing the users to connect with other container technologies


The following list gives an overview of some of the things that can be customized or extended with HK2:
- Custom lifecycles and scopes
- Events
- AOP and other proxies
- Custom injection resolution
- Assisted injection
- Just In Time injection resolution
- Custom validation and security
- Run Level Services


Getting started
----------------

Read the [introduction](introduction.html) and [get started](getting-started.html) with HK2.


API overview
------------

[Learn](api-overview.html) more about the HK2 API, or [browse](apidocs/index.html) the javadoc.


Features
--------

[Learn](extensibility.html) more about the features of HK2


Integration
-----------

HK2 is well integrated with [GlassFish](http://glassfish.org), [Spring](http://www.springsource.org) and others !
