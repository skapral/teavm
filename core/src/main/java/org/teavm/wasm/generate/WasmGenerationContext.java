/*
 *  Copyright 2016 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.wasm.generate;

import java.util.HashMap;
import java.util.Map;
import org.teavm.interop.Import;
import org.teavm.model.AnnotationReader;
import org.teavm.model.AnnotationValue;
import org.teavm.model.ClassReader;
import org.teavm.model.ClassReaderSource;
import org.teavm.model.MethodReader;
import org.teavm.model.MethodReference;

public class WasmGenerationContext {
    private ClassReaderSource classSource;
    private Map<MethodReference, ImportedMethod> importedMethods = new HashMap<>();

    public WasmGenerationContext(ClassReaderSource classSource) {
        this.classSource = classSource;
    }

    public ImportedMethod getImportedMethod(MethodReference reference) {
        return importedMethods.computeIfAbsent(reference, ref -> {
            ClassReader cls = classSource.get(ref.getClassName());
            if (cls == null) {
                return null;
            }
            MethodReader method = cls.getMethod(ref.getDescriptor());
            if (method == null) {
                return null;
            }
            AnnotationReader annotation = method.getAnnotations().get(Import.class.getName());
            if (annotation == null) {
                return null;
            }

            String name = annotation.getValue("name").getString();
            AnnotationValue moduleValue = annotation.getValue("module");
            String module = moduleValue != null ? moduleValue.getString() : null;
            if (module != null && module.isEmpty()) {
                module = null;
            }
            return new ImportedMethod(name, module);
        });
    }

    public class ImportedMethod {
        public final String name;
        public final String module;

        ImportedMethod(String name, String module) {
            this.name = name;
            this.module = module;
        }
    }
}
