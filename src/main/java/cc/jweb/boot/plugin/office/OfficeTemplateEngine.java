/*
 * Copyright (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
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

package cc.jweb.boot.plugin.office;

import cn.hutool.setting.Setting;
import cc.jweb.boot.web.render.JwebBootRenderFactory;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.jfinal.template.source.ClassPathSourceFactory;
import io.jboot.Jboot;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Docx文件模板Engine
 */
public class OfficeTemplateEngine {

    public static OfficeTemplateEngine instance;
    private Map<String, byte[]> fileCache = new HashMap<>();
    private Map<String, Template> fileTemplateCache = new HashMap<>();
    private Map<String, Properties> configCache = new HashMap<>();
    private Engine engine = null;

    private OfficeTemplateEngine() {
        initEngine();
    }

    public static boolean init(Setting setting) {
        instance = new OfficeTemplateEngine();
        return true;
    }

    public static boolean clear() {
        instance = null;
        return true;
    }

    /**
     * 将docx的document.xml转化为合法的jfinal模板内容
     *
     * @param xmlContent
     * @return
     */
    public static String correctXml4Jfinal(String xmlContent, char startFlag, char endFlag) {
        if (xmlContent == null) {
            return null;
        }
        xmlContent = xmlContent.replace("#", "[[@]]");
        StringBuilder sb = new StringBuilder();
        int templateStartFlag = -1;
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < xmlContent.length(); i++) {
            char c = xmlContent.charAt(i);
            if (c == startFlag) {
                templateStartFlag = i;
            }
            if (templateStartFlag == -1) {
                sb.append(c);
            } else {
                content.append(c);
            }
            if (c == endFlag) {
                templateStartFlag = -1;
                sb.append("#(").append(content.substring(1, content.length() - 1).replaceAll("<.*?>", "")).append(")");
                content.setLength(0);
            }
        }
        return sb.toString().replace("[[@]]", "#[[#]]#");
    }

    public static void main(String[] args) {
        char start = 12310;
        System.out.println(start);
    }

    private void initEngine() {
        engine = Engine.create("office-template");
        engine.setDevMode(Jboot.isDevMode());
        engine.setSourceFactory(new ClassPathSourceFactory());
        JwebBootRenderFactory.getInstance().initEngine(engine);
    }

    /**
     * 获取类加载器
     *
     * @return
     */
    protected ClassLoader getClassLoader() {
        ClassLoader ret = Thread.currentThread().getContextClassLoader();
        return ret != null ? ret : getClass().getClassLoader();
    }

    /**
     * 模板文件，根据数据渲染输出流
     *
     * @param officeTemplate
     * @param templateParams
     * @param mediaFiles
     * @param outputStream
     * @throws ZipException
     * @throws IOException
     */
    public void outputStream(String officeTemplate, Map templateParams, Map<String, byte[]> mediaFiles, OutputStream outputStream) {
        ZipFile zipFile = null;
        try {
            URL office = getClassLoader().getResource("/office/template/" + officeTemplate);
            URL officeCof = getClassLoader().getResource("/office/template/" + officeTemplate.substring(0, officeTemplate.indexOf(".")) + ".conf");
            Properties config = null;
            if (!engine.getDevMode()) {
                config = configCache.get(officeTemplate);
            }
            if (config == null) {
                config = new Properties();
                FileInputStream fileInputStream = null;
                InputStreamReader reader = null;
                try {
                    if (officeCof != null) {
                        fileInputStream = new FileInputStream(officeCof.getPath());
                        reader = new InputStreamReader(fileInputStream, "UTF-8");
                        config.load(reader);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                configCache.put(officeTemplate, config);
            }
            char templateStartFlag = config.getProperty("_templateStartFlag").charAt(0);
            char templateEndFlag = config.getProperty("_templateEndFlag").charAt(0);
            File officeFile = new File(office.getPath());
            zipFile = new ZipFile(officeFile);
            Enumeration<? extends ZipEntry> zipEntrys = zipFile.entries();
            ZipOutputStream zipout = new ZipOutputStream(outputStream);
            int len = -1;
            byte[] buffer = new byte[1024];
            while (zipEntrys.hasMoreElements()) {
                ZipEntry next = zipEntrys.nextElement();
                InputStream is = zipFile.getInputStream(next);
                // 把输入流的文件传到输出流中 如果是xl/worksheets/sheets1.xml由我们输入
                String fileName = next.toString();
                zipout.putNextEntry(new ZipEntry(fileName));
                try {
                    if (officeTemplate.endsWith(".xlsx")) {
                        if ("xl/worksheets/sheet1.xml".equals(fileName) || "xl/sharedStrings.xml".equals(fileName)) {
                            // 获取模板文件
                            Template template = null;
                            if (!engine.getDevMode()) {
                                template = fileTemplateCache.get(officeTemplate);
                            }
                            if (template == null) {
                                ByteArrayOutputStream documentStream = new ByteArrayOutputStream();
                                while ((len = is.read(buffer)) != -1) {
                                    documentStream.write(buffer, 0, len);
                                }
                                is.close();
                                String documentXml = documentStream.toString("UTF-8");
//                                String content = correctXml4Jfinal(documentXml, templateStartFlag,templateEndFlag);
                                String content = documentXml;
                                template = engine.getTemplateByString(content);
                                if (!engine.getDevMode()) {
                                    fileTemplateCache.put(officeTemplate, template);
                                }
                            }
                            // 写入渲染数据的模板文件
                            String content = template.renderToString(templateParams);
                            zipout.write(content.getBytes("UTF-8"));
                        } else { // 写入原来的文件流
                            while ((len = is.read(buffer)) != -1) {
                                zipout.write(buffer, 0, len);
                            }
                        }
                    }
                    if (officeTemplate.endsWith(".docx")) {
                        if ("word/document.xml".equals(fileName)) {
                            // 获取模板文件
                            Template template = null;
                            if (!engine.getDevMode()) {
                                template = fileTemplateCache.get(officeTemplate);
                            }
                            if (template == null) {
                                ByteArrayOutputStream documentStream = new ByteArrayOutputStream();
                                while ((len = is.read(buffer)) != -1) {
                                    documentStream.write(buffer, 0, len);
                                }
                                is.close();
                                String documentXml = documentStream.toString("UTF-8");
                                String content = correctXml4Jfinal(documentXml, templateStartFlag, templateEndFlag);
                                template = engine.getTemplateByString(content);
                                if (!engine.getDevMode()) {
                                    fileTemplateCache.put(officeTemplate, template);
                                }
                            }
                            // 写入渲染数据的模板文件
                            String content = template.renderToString(templateParams);
                            zipout.write(content.getBytes("UTF-8"));
                        } else if (fileName.startsWith("word/media/") && fileName.length() > 11) { // 媒体文件夹下
                            String name = fileName.substring(11);
                            boolean exist = false;
                            if (mediaFiles != null) {
                                for (String key : mediaFiles.keySet()) { // 写入替换的文件流
                                    String mappingKey = config.getProperty(name.split("\\.")[0]);
                                    if (mappingKey != null && key.equals(mappingKey)) {
                                        zipout.write(mediaFiles.get(key));
                                        exist = true;
                                        break;
                                    }
                                }
                            }
                            if (!exist) {
                                while ((len = is.read(buffer)) != -1) {
                                    zipout.write(buffer, 0, len);
                                }
                            }
                        } else { // 写入原来的文件流
                            while ((len = is.read(buffer)) != -1) {
                                zipout.write(buffer, 0, len);
                            }
                        }
                    }

                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            zipout.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
