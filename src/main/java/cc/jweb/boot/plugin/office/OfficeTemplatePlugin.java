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
