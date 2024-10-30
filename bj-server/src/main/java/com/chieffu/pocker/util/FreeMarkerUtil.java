package com.chieffu.pocker.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

public class FreeMarkerUtil {
    public static String format(String templateString, Object model) throws IOException, TemplateException {
        // 创建 FreeMarker 配置对象
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        // 加载模板文件
        Template template = new Template("abc",templateString,cfg);
        // 创建 StringWriter 对象来接收输出
        StringWriter out = new StringWriter();
        // 处理模板并输出结果
        template.process(model, out);
        // 输出结果
        return out.toString();
    }
    public static void main(String[] args) throws TemplateException, IOException {
        String data = "<html><body>${name}</body></html>";
        String result = FreeMarkerUtil.format(data, new HashMap(){{this.put("name","mama");}});
        System.out.println(result);
    }
}
