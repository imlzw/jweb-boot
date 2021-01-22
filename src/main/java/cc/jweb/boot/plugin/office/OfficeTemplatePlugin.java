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
import com.jfinal.plugin.IPlugin;


/**
 * Docx文件模板插件
 *
 * @author
 *
*/
public class OfficeTemplatePlugin implements IPlugin {

    private Setting setting = null;
    public OfficeTemplatePlugin(Setting setting){
        this.setting = setting;
    }

    @Override
    public boolean start() {
        return OfficeTemplateEngine.init(setting);
    }

    @Override
    public boolean stop() {
        OfficeTemplateEngine.clear();
        return true;
    }

}
