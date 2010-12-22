/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.grapher;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.grapher.InjectorGrapher;
import com.google.inject.grapher.graphviz.GraphvizRenderer;
import com.google.inject.name.Named;

import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;

/**
 * A service which prints out a graph of the current injector to a configurable
 * file.
 *
 * @author Willi Schoenborn
 */
final class DefaultGrapherService implements GrapherService, Initializable {
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultGrapherService.class);

    private final GraphvizRenderer renderer;
    
    private File file = new File("graph.dot");
    
    private Charset encoding = Charsets.UTF_8;

    private final InjectorGrapher grapher;
    
    @Inject
    public DefaultGrapherService(GraphvizRenderer renderer, Injector injector, InjectorGrapher grapher) {
        this.renderer = Preconditions.checkNotNull(renderer, "Renderer");
        Preconditions.checkNotNull(injector, "Injector");
        this.grapher = Preconditions.checkNotNull(grapher, "Grapher").of(injector);
    }

    @Inject(optional = true)
    void setFile(@Named(GrapherServiceConfig.FILE) File file) {
        this.file = Preconditions.checkNotNull(file, "File");
    }

    @Inject(optional = true)
    void setEncoding(@Named(GrapherServiceConfig.ENCODING) Charset encoding) {
        this.encoding = Preconditions.checkNotNull(encoding, "Encoding");
    }
    
    @Override
    public void initialize() throws LifecycleException {
        final PrintWriter out;
        
        try {
            out = new PrintWriter(file, encoding.name());
        } catch (IOException e) {
            throw new LifecycleException(e);
        }
        
        renderer.setOut(out);
    }
    
    @Override
    public void execute() throws LifecycleException {
        try {
            LOG.debug("Graphing application");
            grapher.graph();
        } catch (IOException e) {
            throw new LifecycleException(e);
        }
    }
    
}
