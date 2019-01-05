/**
 *  Copyright 2019 The ModiTect authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.moditect.deptective.plugintest.analyzewhitelistallexternal;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.junit.Test;
import org.moditect.deptective.plugintest.PluginTestBase;
import org.moditect.deptective.plugintest.analyzewhitelistallexternal.bar.Bar;
import org.moditect.deptective.plugintest.analyzewhitelistallexternal.foo.Foo;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;

public class AnalyzeWhitelistAllExternalTest extends PluginTestBase {

    @Test
    public void shouldGenerateConfig() throws Exception {
        Compilation compilation = Compiler.javac()
            .withOptions(
                    "-Xplugin:Deptective",
                    "-Adeptective.mode=ANALYZE",
                    "-Adeptective.whitelisted=*ALL_EXTERNAL*"
            )
            .compile(
                    forTestClass(Bar.class),
                    forTestClass(Foo.class)
            );

        assertThat(compilation).succeeded();

        assertThat(compilation.notes()).hasSize(1);
        Diagnostic<? extends JavaFileObject> note = compilation.notes().get(0);
        String message = note.getMessage(null);
        String generatedConfig = message.substring(message.indexOf(System.lineSeparator()) + 1);

        String expectedConfig = "{\n" +
                "    \"packages\" : [ {\n" +
                "      \"name\" : \"org.moditect.deptective.plugintest.analyzewhitelistallexternal.bar\"\n" +
                "    }, {\n" +
                "      \"name\" : \"org.moditect.deptective.plugintest.analyzewhitelistallexternal.foo\",\n" +
                "      \"reads\" : [ \"org.moditect.deptective.plugintest.analyzewhitelistallexternal.bar\" ]\n" +
                "    } ],\n" +
                "    \"whitelisted\" : [ \"java.io\", \"java.math\", \"java.net\" ]\n" +
                "  }]";

        JSONAssert.assertEquals(expectedConfig, generatedConfig, JSONCompareMode.LENIENT);
    }
}
