/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2016-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.hk2.stub.generator;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.inject.Named;
import javax.inject.Scope;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.metadata.generator.ServiceUtilities;
import org.glassfish.hk2.utilities.Stub;
import org.glassfish.hk2.utilities.general.GeneralUtilities;
import org.jvnet.hk2.annotations.ContractsProvided;

/**
 * @author jwells
 *
 */
@SupportedAnnotationTypes("org.glassfish.hk2.utilities.Stub")
public class StubProcessor extends AbstractProcessor {
    private final static String NAMED_ANNO = Named.class.getName();
    private final static String EXCEPTIONS = "EXCEPTIONS";
    private final static String PROVIDED_ANNO = ContractsProvided.class.getName();
    
    /**
     * Gets rid of warnings and this code should work with all source versions
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    /* (non-Javadoc)
     * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set, javax.annotation.processing.RoundEnvironment)
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv) {
        MultiException me = null;
        
        for (TypeElement annotation : annotations) {
            Set<? extends Element> clazzes = roundEnv.getElementsAnnotatedWith(annotation);
            
            for (Element clazzElement : clazzes) {
                if (!(clazzElement instanceof TypeElement)) continue;
                
                TypeElement clazz = (TypeElement) clazzElement;
                
                try {
                    writeStub(clazz);
                }
                catch (IOException ioe) {
                    if (me == null) {
                        me = new MultiException(ioe);
                    }
                    else {
                        me.addError(ioe);
                    }
                    
                }
            }
            
        }
        
        if (me != null) {
            processingEnv.getMessager().printMessage(Kind.ERROR, me.getMessage());
            me.printStackTrace();
            return true;
        }
        
        return true;
    }
    
    private static boolean isScopeAnnotation(AnnotationMirror annotation) {
        DeclaredType dt = annotation.getAnnotationType();
        TypeElement asElement = (TypeElement) dt.asElement();
        Scope scope = asElement.getAnnotation(Scope.class);
        
        return (scope != null);
    }
    
    private void getAllGenericMappings(TypeElement clazz,
            Map<String, Map<String, String>> retVal,
            Map<String, String> subclassMap) {
        Types types = processingEnv.getTypeUtils();
        
        TypeMirror clazzTypeMirror = clazz.asType();
        List<? extends TypeMirror> extendList = types.directSupertypes(clazzTypeMirror);
        
        for (TypeMirror tm : extendList) {
            if (!TypeKind.DECLARED.equals(tm.getKind())) continue;
            
            DeclaredType dt = (DeclaredType) tm;
            
            List<? extends TypeMirror> mirrorTypes = dt.getTypeArguments();
            
            TypeElement te = (TypeElement) dt.asElement();
            String extenderName = ServiceUtilities.nameToString(te.getQualifiedName());
            
            if (Object.class.getName().equals(extenderName) || retVal.containsKey(extenderName)) {
                // We have already seen this one
                continue;
            }
            
            Map<String, String> typeParamToTypeMap = new HashMap<String, String>();
            retVal.put(extenderName, typeParamToTypeMap);
            
            List<? extends TypeParameterElement> typeParameters = te.getTypeParameters();
            
            if (mirrorTypes.size() != typeParameters.size()) {
                throw new AssertionError("The mirror types and parameter types of " +
                  te.getQualifiedName() + " should match, they are " + mirrorTypes.size() +
                  " and " + typeParameters.size());
            }
            
            for (int lcv = 0; lcv < typeParameters.size(); lcv++) {
                TypeMirror mirror = mirrorTypes.get(lcv);
                TypeParameterElement typeParameter = typeParameters.get(lcv);
                
                String typeParameterName = ServiceUtilities.nameToString(typeParameter.getSimpleName());
                String typeName = getTypeName(mirror, subclassMap);
                
                typeParamToTypeMap.put(typeParameterName, typeName);
            }
            
            getAllGenericMappings(te, retVal, typeParamToTypeMap);
        }
    }
    
    private static boolean hasTypeParameters(TypeElement clazz) {
        List<? extends TypeParameterElement> typeParams = clazz.getTypeParameters();
        if (typeParams == null || typeParams.isEmpty()) return false;
        return true;
    }
    
    @SuppressWarnings("unchecked")
    private void writeStub(TypeElement clazz) throws IOException {
        Map<String, Map<String, String>> genericMapping = new HashMap<String, Map<String, String>>();
        
        if (!hasTypeParameters(clazz)) {
            getAllGenericMappings(clazz, genericMapping, new HashMap<String, String>());
        }
        
        Elements elementUtils = processingEnv.getElementUtils();
        
        Set<ExecutableElement> abstractMethods = new LinkedHashSet<ExecutableElement>();
        List<? extends Element> enclosedElements = elementUtils.getAllMembers(clazz);
        Set<ExecutableElementDuplicateFinder> dupFinder = new HashSet<ExecutableElementDuplicateFinder>();
        for (Element enclosedElement : enclosedElements) {
            if (!ElementKind.METHOD.equals(enclosedElement.getKind())) continue;
            
            Set<Modifier> modifiers = enclosedElement.getModifiers();
            if (!modifiers.contains(Modifier.ABSTRACT)) continue;
            
            ExecutableElement executableMethod = (ExecutableElement) enclosedElement;
            
            ExecutableElementDuplicateFinder eedf = new ExecutableElementDuplicateFinder(executableMethod);
            if (dupFinder.contains(eedf)) {
                continue;
            }
            dupFinder.add(eedf);
            
            abstractMethods.add(executableMethod);
        }
        
        boolean exceptions = false;
        String name = null;
        List<TypeElement> contractsProvided = null;
        String scope = null;
        
        List<? extends AnnotationMirror> annotationMirrors = elementUtils.getAllAnnotationMirrors(clazz);
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            DeclaredType annoType = annotationMirror.getAnnotationType();
            TypeElement annoElement = (TypeElement) annoType.asElement();
            String annoQualifiedName = ServiceUtilities.nameToString(annoElement.getQualifiedName());
            if (annoQualifiedName.equals(NAMED_ANNO)) {
            
                Map<? extends ExecutableElement, ? extends AnnotationValue> values = annotationMirror.getElementValues();
                AnnotationValue value = null;
                for (AnnotationValue v : values.values()) {
                    value = v;
                    break;
                }
            
                if (value == null) {
                    name = ServiceUtilities.nameToString(clazz.getSimpleName());
                }
                else {
                    name = (String) value.getValue();
                }
            }
            else if (annoQualifiedName.equals(Stub.class.getName())) {
                Map<? extends ExecutableElement, ? extends AnnotationValue> values = annotationMirror.getElementValues();
                
                AnnotationValue value = null;
                for (AnnotationValue v : values.values()) {
                    value = v;
                    break;
                }
                
                if (value != null) {
                    VariableElement ve = (VariableElement) value.getValue();
                    String stubType = ServiceUtilities.nameToString(ve.getSimpleName());
                    exceptions = EXCEPTIONS.equals(stubType);
                }
            }
            else if (annoQualifiedName.equals(PROVIDED_ANNO)) {
                Map<? extends ExecutableElement, ? extends AnnotationValue> values = annotationMirror.getElementValues();
                AnnotationValue value = null;
                for (AnnotationValue v : values.values()) {
                    value = v;
                    break;
                }
                
                if (value != null) {
                    List<? extends AnnotationValue> contracts = (List<? extends AnnotationValue>) value.getValue();
                    
                    contractsProvided = new LinkedList<TypeElement>();
                    for (AnnotationValue contract : contracts) {
                        DeclaredType dt = (DeclaredType) contract.getValue();
                        TypeElement te = (TypeElement) dt.asElement();
                        contractsProvided.add(te);
                    }
                }
            }
            else if (isScopeAnnotation(annotationMirror)) {
                // Must be copied to the top of the stub
                scope = "@" + annoQualifiedName;
            }
        }
        
        writeJavaFile(clazz, abstractMethods, name, exceptions, contractsProvided, scope, genericMapping);
    }
    
    private final static String STUB_EXTENSION = "_hk2Stub";
    
    private String getFullyQualifiedStubName(TypeElement clazz) {
        Elements elementUtils = processingEnv.getElementUtils();
        
        String clazzSimpleName = ServiceUtilities.nameToString(clazz.getSimpleName());
        
        Element enclosingElement = clazz.getEnclosingElement();
        ElementKind kind = enclosingElement.getKind();
        
        PackageElement packageElement = elementUtils.getPackageOf(clazz);
        String packageName = ServiceUtilities.nameToString(packageElement.getQualifiedName());
        
        if (ElementKind.PACKAGE.equals(kind)) {
            if (packageName == null || packageName.isEmpty()) {
                return clazzSimpleName;
            }
            
            return packageName + "." + clazzSimpleName + STUB_EXTENSION;
        }
        
        String enclosingName = ServiceUtilities.nameToString(enclosingElement.getSimpleName());
        
        // There is an enclosing element
        if (packageName == null || packageName.isEmpty()) {
            return enclosingName + "_" + clazzSimpleName + STUB_EXTENSION;
        }
        
        return packageName + "." + enclosingName + "_" + clazzSimpleName + STUB_EXTENSION;
        
    }
    
    private static String getJustClassPart(String fullyQualifiedFileNameWithDots) {
        int index = fullyQualifiedFileNameWithDots.lastIndexOf('.');
        if (index < 0) {
            return fullyQualifiedFileNameWithDots;
        }
        
        return fullyQualifiedFileNameWithDots.substring(index + 1);
    }
    
    private void writeJavaFile(TypeElement clazz, Set<ExecutableElement> abstractMethods,
            String name,
            boolean exceptions,
            List<TypeElement> contractsProvided,
            String scope,
            Map<String, Map<String,String>> genericMapper) throws IOException {
        Elements elementUtils = processingEnv.getElementUtils();
        
        PackageElement packageElement = elementUtils.getPackageOf(clazz);
        String packageName = ServiceUtilities.nameToString(packageElement.getQualifiedName());
        String clazzQualifiedName = ServiceUtilities.nameToString(clazz.getQualifiedName());
        String fullyQualifiedStubName = getFullyQualifiedStubName(clazz);
        String clazzSimpleName = ServiceUtilities.nameToString(clazz.getSimpleName());
        
        // String stubClazzName = ServiceUtilities.nameToString(clazz.getSimpleName()) + STUB_EXTENSION;
        String stubClazzName = getJustClassPart(fullyQualifiedStubName);
        
        Filer filer = processingEnv.getFiler();
        
        JavaFileObject jfo = filer.createSourceFile(fullyQualifiedStubName, clazz);
        
        Writer writer = jfo.openWriter();
        try {
            writer.append("package " + packageName + ";\n\n");
            
            writer.append("import javax.annotation.Generated;\n");
            if (name != null) {
                writer.append("import javax.inject.Named;\n");
            }
            writer.append("import org.jvnet.hk2.annotations.Service;\n");
            if (contractsProvided != null) {
                writer.append("import org.jvnet.hk2.annotations.ContractsProvided;\n");
            }
            writer.append("import " + clazzQualifiedName + ";\n\n");
            
            writer.append("@Service\n@Generated(\"org.glassfish.hk2.stub.generator.StubProcessor\")\n");
            if (name != null) {
                writer.append("@Named(\"" + name + "\")\n");
            }
            if (contractsProvided != null) {
                writer.append("@ContractsProvided({");
                boolean first = true;
                for (TypeElement contract : contractsProvided) {
                    if (first) {
                        first = false;
                    }
                    else {
                        writer.append(",\n    ");
                    }
                    
                    String cName = ServiceUtilities.nameToString(contract.getQualifiedName()) + ".class";
                    writer.append(cName);
                }
                writer.append("})\n");
            }
            if (scope != null) {
                writer.append(scope + "\n");
            }
            writer.append("public class " + stubClazzName + " extends " + clazzSimpleName + " {\n");
            
            for (ExecutableElement abstractMethod : abstractMethods) {
                writeAbstractMethod(abstractMethod, genericMapper, writer, exceptions); 
            }
            
            writer.append("}\n");
        }
        finally {
            writer.close();
        }
    }
    
    private void writeAbstractMethod(ExecutableElement abstractMethod,
            Map<String, Map<String, String>> genericMapper,
            Writer writer, boolean exceptions) throws IOException {
        Set<Modifier> modifiers = abstractMethod.getModifiers();
        
        TypeElement element = (TypeElement) abstractMethod.getEnclosingElement();
        String elementQName = ServiceUtilities.nameToString(element.getQualifiedName());
        Map<String, String> typeMapper = genericMapper.get(elementQName);
        
        writer.append("    ");
        
        if (modifiers.contains(Modifier.PUBLIC)) {
            writer.append("public ");
        }
        else if (modifiers.contains(Modifier.PROTECTED)) {
            writer.append("protected ");
        }
        
        TypeMirror returnType = abstractMethod.getReturnType();
        TypeMirrorOutputs returnOutputs = typeMirrorToString(returnType, false, typeMapper);
        
        writer.append(returnOutputs.leftHandSide + " " + abstractMethod.getSimpleName() + "(");
        
        List<? extends VariableElement> parameterElements = abstractMethod.getParameters();
        int numParams = parameterElements.size();
        int lcv = 0;
        
        for (VariableElement variable : parameterElements) {
            TypeMirror variableAsType = variable.asType();
            
            boolean varArgs = abstractMethod.isVarArgs() && ((lcv + 1) == numParams);
            
            TypeMirrorOutputs paramOutputs = typeMirrorToString(variableAsType, varArgs, typeMapper);
            if (lcv > 0) {
                writer.append(", ");
            }
            
            writer.append(paramOutputs.leftHandSide);
            
            if (varArgs) {
                writer.append("...");
            }
            
            writer.append(" p" + lcv);
            lcv++;
        }
        
        if (exceptions) {
            writer.append(") {\n        throw new UnsupportedOperationException(\"" + abstractMethod + "\");\n    }\n\n");
        }
        else {
            writer.append(") {\n        return " + returnOutputs.body + ";\n    }\n\n");
        }
    }
    
    private TypeMirrorOutputs typeMirrorToString(TypeMirror mirror,
            boolean varArg,
            Map<String, String> typeMap) throws IOException {
        Types typeUtils = processingEnv.getTypeUtils();
        
        TypeKind returnKind = mirror.getKind();
        
        switch (returnKind) {
        case ARRAY:
            return new TypeMirrorOutputs(arrayTypeToString((ArrayType) mirror, varArg, typeMap), "null");
        case VOID:
            return new TypeMirrorOutputs("void", "");
        case BOOLEAN:
            return new TypeMirrorOutputs("boolean", "true");
        case BYTE:
            return new TypeMirrorOutputs("byte", "0");
        case CHAR:
            return new TypeMirrorOutputs("char", "0");
        case DOUBLE:
            return new TypeMirrorOutputs("double", "(double) 0.0");
        case FLOAT:
            return new TypeMirrorOutputs("float", "(float) 0.0");
        case INT:
            return new TypeMirrorOutputs("int", "0");
        case LONG:
            return new TypeMirrorOutputs("long", "0");
        case SHORT:
            return new TypeMirrorOutputs("short", "0");
        case DECLARED:
            TypeElement element = (TypeElement) typeUtils.asElement(mirror);
            return new TypeMirrorOutputs(ServiceUtilities.nameToString(element.getQualifiedName()), "null");
        case TYPEVAR:
            if (typeMap == null) {
                return new TypeMirrorOutputs("Object", "null");
            }
            
            TypeVariable tv = (TypeVariable) mirror;
            Element typeElement = tv.asElement();
            String typeElementName = ServiceUtilities.nameToString(typeElement.getSimpleName());
            
            String leftSide = typeMap.get(typeElementName);
            if (leftSide == null) leftSide = "Object";
            
            return new TypeMirrorOutputs(leftSide, "null");
        default:
            throw new IOException("Unknown kind: " + returnKind);
        }
        
    }
    
    private String arrayTypeToString(ArrayType arrayType, boolean varArgs, Map<String, String> typeMapper) throws IOException {
        int numBraces = (varArgs) ? 0 : 1 ;
        
        TypeMirror arrayOfType = arrayType.getComponentType();
        while (arrayOfType instanceof ArrayType) {
            numBraces++;
            
            arrayOfType = ((ArrayType) arrayOfType).getComponentType();
        }
        
        TypeMirrorOutputs underlyingType = typeMirrorToString(arrayOfType, false, typeMapper);
        
        StringBuffer sb = new StringBuffer(underlyingType.leftHandSide);
        for (int lcv = 0; lcv < numBraces; lcv++) {
            sb.append("[]");
        }
        
        return sb.toString();
        
    }
    
    private static class TypeMirrorOutputs {
        private final String leftHandSide;
        private final String body;
        
        private TypeMirrorOutputs(String leftHandSide, String body) {
            this.leftHandSide = leftHandSide;
            this.body = body;
        }
    }
    
    private static String getTypeName(TypeMirror mirror) {
        return getTypeName(mirror, null);
    }
    
    private static String getTypeName(TypeMirror mirror, Map<String, String> typeMap) {
        switch (mirror.getKind()) {
        case DECLARED:
            DeclaredType dt = (DeclaredType) mirror;
            TypeElement te = (TypeElement) dt.asElement();
            return ServiceUtilities.nameToString(te.getQualifiedName());
        case ARRAY:
            ArrayType at = (ArrayType) mirror;
            TypeMirror atm = at.getComponentType();
            return getTypeName(atm) + "[]";
        case TYPEVAR:
            if (typeMap == null) return Object.class.getName();
            
            TypeVariable tv = (TypeVariable) mirror;
            String tvSimpleName = ServiceUtilities.nameToString(tv.asElement().getSimpleName());
            
            String retVal = typeMap.get(tvSimpleName);
            if (retVal == null) return Object.class.getName();
            
            return retVal;
        case BOOLEAN:
            return "boolean";
        case BYTE:
            return "byte";
        case CHAR:
            return "char";
        case DOUBLE:
            return "double";
        case FLOAT:
            return "float";
        case INT:
            return "int";
        case LONG:
            return "long";
        case SHORT:
            return "short";
        case VOID:
            return "void";
        default:
            return "";
        }
    }
    
    private static class ExecutableElementDuplicateFinder {
        private final ExecutableElement executableElement;
        private final int hash;
        
        private ExecutableElementDuplicateFinder(ExecutableElement executableElement) {
            this.executableElement = executableElement;
            
            int localHash = 0;
            
            Name name = executableElement.getSimpleName();
            localHash ^= name.hashCode();
            
            TypeMirror returnMirror = executableElement.getReturnType();
            localHash ^= getTypeName(returnMirror).hashCode();
            
            for (VariableElement ve : executableElement.getParameters()) {
                TypeMirror asType = ve.asType();
                
                localHash ^= getTypeName(asType).hashCode();
            }
            
            this.hash = localHash;
        }
        
        
        
        @Override
        public int hashCode() {
            return hash;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!(o instanceof ExecutableElementDuplicateFinder)) {
                return false;
            }
            
            ExecutableElementDuplicateFinder other = (ExecutableElementDuplicateFinder) o;
            
            Name name = executableElement.getSimpleName();
            Name otherName = other.executableElement.getSimpleName();
            
            if (!GeneralUtilities.safeEquals(name, otherName)) {
                return false;
            }
            
            TypeMirror returnMirror = executableElement.getReturnType();
            TypeMirror otherReturnMirror = other.executableElement.getReturnType();
            
            String returnMirrorAsString = getTypeName(returnMirror);
            String otherReturnMirrorAsString = getTypeName(otherReturnMirror);
            
            if (!GeneralUtilities.safeEquals(returnMirrorAsString, otherReturnMirrorAsString)) {
                return false;
            }
            
            List<? extends VariableElement> params = executableElement.getParameters();
            List<? extends VariableElement> otherParams = other.executableElement.getParameters();
            
            if (params.size() != otherParams.size()) {
                return false;
            }
            
            for (int lcv = 0; lcv < params.size(); lcv++) {
                VariableElement ve = params.get(lcv);
                VariableElement otherVE = otherParams.get(lcv);
                
                
                TypeMirror asType = ve.asType();
                TypeMirror otherAsType = otherVE.asType();
                
                
                String asStringType = getTypeName(asType);
                String otherAsStringType = getTypeName(otherAsType);
                
                if (!GeneralUtilities.safeEquals(asStringType, otherAsStringType)) {
                    return false;
                }
            }
            
            return true;
        }
    }
}
