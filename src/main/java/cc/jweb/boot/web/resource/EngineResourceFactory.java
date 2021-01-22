/*
 * Copyright  (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.jweb.boot.web.resource;

import cc.jweb.boot.config.JwebBootAppConfig;
import cc.jweb.boot.utils.lang.StringUtils;
import com.jfinal.server.undertow.ResourceManagerBuilder;
import com.jfinal.template.source.ISource;
import com.jfinal.template.source.ISourceFactory;
import io.jboot.Jboot;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.handlers.resource.URLResource;
import io.undertow.util.ETag;
import io.undertow.util.MimeMappings;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;


/**
 * 模板引擎资源工厂
 *
 * @author imlzw@vip.qq.com
 * @platform www.jweb.cc
 */
public class EngineResourceFactory implements ISourceFactory {

    private ResourceManager build;

    public EngineResourceFactory() {
        String resourcePath = Jboot.configValue("undertow.resourcePath");
        if (StringUtils.isBlank(resourcePath)) {
            resourcePath = "src/main/webapp, WebRoot, WebContent, classpath:webapp, webapp";
        }
        build = new ResourceManagerBuilder().build(resourcePath, JwebBootAppConfig.class.getClassLoader());
    }

    @Override
    public ISource getSource(String baseTemplatePath, String fileName, String encoding) {
        try {
            // 路径规范化
            String path = new URI(fileName).normalize().getPath();
            Resource resource = build.getResource(path);
            if (resource == null) {
                throw new RuntimeException("Resource is not found: " + fileName);
            }
            return new ReourceSource(resource, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static class ReourceSource implements ISource {

        private ResourceWrap resource;
        private String fileName;
        private String encoding;
        private long lastModified;

        public ReourceSource(Resource resource, String encoding) {
            this.resource = new ResourceWrap(resource);
            this.fileName = this.resource.getName();
            this.encoding = encoding;
        }

        public static StringBuilder loadFile(File file, InputStream fileInputStream, String encoding) {
            StringBuilder ret = new StringBuilder(file != null ? (int) file.length() + 3 : 64);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream, encoding))) {
                String line = br.readLine();
                if (line != null) {
                    ret.append(line);
                } else {
                    return ret;
                }

                while ((line = br.readLine()) != null) {
                    ret.append('\n').append(line);
                }
                return ret;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public boolean isModified() {
            File file = resource.getFile();
            if (file == null || !file.exists()) {
                return false;
            }
            return lastModified != file.lastModified();
        }

        public String getCacheKey() {
            return fileName;
        }

        public String getEncoding() {
            return encoding;
        }

        public String getFileName() {
            return fileName;
        }

        public StringBuilder getContent() {
            File file = resource.getFile();
            InputStream fileInputStream = resource.getFileInputStream();
            // 极为重要，否则在开发模式下 isModified() 一直返回 true，缓存一直失效（原因是 lastModified 默认值为 0）
            this.lastModified = file != null ? file.lastModified() : 0;
            return loadFile(file, fileInputStream, encoding);
        }


        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Resource name: ").append(fileName).append("\n");
            sb.append("Final Resource name: ").append(resource != null ? resource.getName() : null).append("\n");
            sb.append("Last modified: ").append(lastModified).append("\n");
            return sb.toString();
        }

    }

    /**
     * 资源包装类
     */
    public static class ResourceWrap implements Resource {

        private Resource resource;

        public ResourceWrap(Resource resource) {
            this.resource = resource;
        }

        @Override
        public String getPath() {
            return resource.getPath();
        }

        @Override
        public Date getLastModified() {
            return this.resource.getLastModified();
        }

        @Override
        public String getLastModifiedString() {
            return this.resource.getLastModifiedString();
        }

        @Override
        public ETag getETag() {
            return this.resource.getETag();
        }

        @Override
        public String getName() {
            return this.resource.getName();
        }

        @Override
        public boolean isDirectory() {
            return this.resource.isDirectory();
        }

        @Override
        public List<Resource> list() {
            return this.resource.list();
        }

        @Override
        public String getContentType(MimeMappings mimeMappings) {
            return this.resource.getContentType(mimeMappings);
        }

        @Override
        public void serve(Sender sender, HttpServerExchange exchange, IoCallback completionCallback) {
            this.resource.serve(sender, exchange, completionCallback);
        }

        @Override
        public Long getContentLength() {
            return this.resource.getContentLength();
        }

        @Override
        public String getCacheKey() {
            return this.resource.getCacheKey();
        }

        public InputStream getFileInputStream() {
            File file = getFile();
            if (file != null && file.exists()) {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (this.resource instanceof URLResource) {
                boolean isJarFile = this.resource.getUrl().getPath().startsWith("jar:");
                try {
                    return this.resource.getUrl().openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public File getFile() {
            return this.resource.getFile();
        }

        @Override
        public Path getFilePath() {
            return this.resource.getFilePath();
        }

        @Override
        public File getResourceManagerRoot() {
            return this.resource.getResourceManagerRoot();
        }

        @Override
        public Path getResourceManagerRootPath() {
            return this.resource.getResourceManagerRootPath();
        }

        @Override
        public URL getUrl() {
            return this.resource.getUrl();
        }
    }

}
