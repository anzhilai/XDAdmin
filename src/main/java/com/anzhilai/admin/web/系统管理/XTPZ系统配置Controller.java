package com.anzhilai.admin.web.系统管理;

import com.anzhilai.core.base.*;
import com.anzhilai.core.database.AjaxResult;
import com.anzhilai.core.database.SqlCache;
import com.anzhilai.core.framework.GlobalValues;
import com.anzhilai.core.toolkit.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

//@domain XTPZ系统配置Controller
@Controller
@XController(name = "系统配置接口", isLogin = XController.LoginState.No)
@Transactional(rollbackFor = {Exception.class})
@RequestMapping("/xtpz")
public class XTPZ系统配置Controller extends BaseModelController<XTPZ系统配置> {
    @XController(name = "平台数据同步")
    @RequestMapping(value = "/xdevelop", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String xdevelop(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
        String name = RequestUtil.GetString(request, "name");//项目名称
        String MenuData = RequestUtil.GetString(request, "MenuData");//
        if (StrUtil.isEmpty(MenuData)) {
            return AjaxResult.False("MenuData不能为空").ToJson();
        }
        if (StrUtil.isEmpty(name)) {
            return AjaxResult.False("name不能为空").ToJson();
        }
        File file功能菜单 = new File(GlobalValues.GetUploadFilePath(MenuData));
        if (!file功能菜单.exists()) {
            return AjaxResult.False("MenuData文件不存在").ToJson();
        }
        String[] packageNames = RequestUtil.GetStringArray(request, "packageName");//
        List<Object> 领域模型s = new ArrayList<>();
        List<String> listName = new ArrayList<>();
        listName.add(BaseController.class.getSimpleName());//
        this.Add数据模型(领域模型s, listName, BaseModel.class, BaseModel.class.getSimpleName());
        this.Add数据模型(领域模型s, listName, BaseModelTree.class, BaseModelTree.class.getSimpleName());
        this.Add服务模型(领域模型s, listName, BaseModelController.class, "");
        for (String classname : SqlCache.hashMapClasses.keySet()) {
            Class<?> clazz = SqlCache.hashMapClasses.get(classname);
            if (packageNames != null && packageNames.length > 0) {
                boolean isContains = false;
                for (String packageName : packageNames) {
                    if (clazz.getName().contains(packageName)) {
                        isContains = true;
                        break;
                    }
                }
                if (!isContains) {
                    continue;
                }
            }
            this.Add数据模型(领域模型s, listName, clazz, classname);
            Class<?> clazzCtl = SqlCache.hashMapController.get(classname);
            if (clazzCtl != null) {
                this.Add服务模型(领域模型s, listName, clazzCtl, classname);
            }
        }
        String dir = GlobalValues.GetTempPath();
        File file领域模型 = new File(dir + File.separator + BaseModel.GetUniqueId() + ".dat");
        FileUtil.WriteStringToFile(file领域模型.getPath(), TypeConvert.ToJson(领域模型s));
        File file详情 = new File(dir + File.separator + BaseModel.GetUniqueId() + ".dat");
        Map<String, Object> info = new HashMap<>();
        info.put("项目名称", name);
        FileUtil.WriteStringToFile(file详情.getPath(), TypeConvert.ToJson(info));
        try {
            response.setContentType("application/x-msdownload");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + new String((name + ".zip").getBytes("UTF-8"), "ISO_8859_1"));
            ZipUtil zipHelper = new ZipUtil(response.getOutputStream());
            zipHelper.addEntry("xdevelop/", "domainModel.dat", file领域模型);
            zipHelper.addEntry("xdevelop/", "menuData.dat", file功能菜单);
            zipHelper.addEntry("xdevelop/", "info.dat", file详情);
            zipHelper.closeZos();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            file功能菜单.delete();
            file领域模型.delete();
            file详情.delete();
        }
        return null;
    }

    public void Add服务模型(List<Object> 领域模型s, List<String> listName, Class<?> clazzCtl, String 关联模型) throws Exception {
        if (clazzCtl.getSuperclass() != Object.class) {
            this.Add服务模型(领域模型s, listName, clazzCtl.getSuperclass(), "");
        }
        String 模型名称 = clazzCtl.getSimpleName();
        if (listName.contains(模型名称)) {
            return;
        }
        Map 领域服务模型 = new HashMap();
        领域模型s.add(领域服务模型);
        listName.add(模型名称);
        领域服务模型.put("模型名称", 模型名称);
        领域服务模型.put("模型类型", "服务模型");
        领域服务模型.put("命名空间", clazzCtl.getPackage().getName());
        if (StrUtil.isNotEmpty(关联模型)) {
            领域服务模型.put("关联模型列表", new String[]{关联模型});
        }
        String 继承模型名称 = clazzCtl.getSuperclass() != Object.class ? clazzCtl.getSuperclass().getSimpleName() : "";
        if (BaseController.class.getSimpleName().equals(继承模型名称)) {
            继承模型名称 = "";
        }
        领域服务模型.put("继承模型名称", 继承模型名称);
        List<Object> 服务接口列表 = new ArrayList<>();
        领域服务模型.put("服务接口列表", 服务接口列表);
        RequestMapping crm = clazzCtl.getAnnotation(RequestMapping.class);
        String url0 = crm != null && crm.value().length > 0 ? crm.value()[0] : "";
        领域服务模型.put("模型标识", url0.replaceFirst("/", ""));

        Method[] methods = clazzCtl.getMethods();   //clazzCtl.getDeclaredMethods();
        for (Method method : methods) {
            XController xcc = method.getAnnotation(XController.class);
            if (xcc != null) {
                RequestMapping rm = method.getAnnotation(RequestMapping.class);
                String url1 = TypeConvert.ToString(rm.value().length > 0 ? rm.value()[0] : "");
                Map 服务接口 = new HashMap();
                服务接口.put("接口名称", xcc.name());
                服务接口.put("输入描述", xcc.input());
                服务接口.put("输出描述", xcc.output());
                服务接口.put("接口类型", rm.method().length > 0 ? rm.method()[0].name() : "");
                服务接口.put("接口Url", url1);
                if (method.getDeclaringClass().equals(clazzCtl)
                        && !ScanUtil.IsMethodOverridden(method, clazzCtl.getSuperclass())) {
                    服务接口.put("是否继承", "否");
                } else {
                    服务接口.put("是否继承", "是");
                    continue;//继承不输出
                }
                服务接口列表.add(服务接口);
            }
        }
    }

    public void Add数据模型(List<Object> 领域模型s, List<String> listName, Class<?> clazz, String 模型名称) throws Exception {
        if (clazz.getSuperclass() != Object.class) {
            this.Add数据模型(领域模型s, listName, clazz.getSuperclass(), clazz.getSuperclass().getSimpleName());
        }
        if (listName.contains(模型名称)) {
            return;
        }
        Map 领域数据模型 = new HashMap();
        领域模型s.add(领域数据模型);
        listName.add(模型名称);
        领域数据模型.put("模型名称", 模型名称);
        领域数据模型.put("模型类型", "数据模型");
        领域数据模型.put("模型标识", 模型名称);
        领域数据模型.put("命名空间", clazz.getPackage().getName());
        领域数据模型.put("继承模型名称", clazz.getSuperclass() != Object.class ? clazz.getSuperclass().getSimpleName() : "");
        XTable xt = clazz.getAnnotation(XTable.class);
        if (xt != null) {
            领域数据模型.put("模型描述", xt.description());
        }
        List<Object> 数据字段列表 = new ArrayList<>();
        领域数据模型.put("数据字段列表", 数据字段列表);
        ArrayList<Field> classfields = new ArrayList<>();
        classfields.addAll(Arrays.asList(clazz.getFields()));
        for (Field field : classfields) {
            String columnName = field.getName();
            XColumn xc = field.getAnnotation(XColumn.class);
            if (xc != null) {
                Map 数据字段 = new HashMap();
                数据字段.put("字段名称", columnName);
                String 字段类型 = "文本";
                if (field.getType() == String.class) {
                    if (xc.text()) {
                        字段类型 = "长文本";
                    } else {
                        字段类型 = "文本";
                    }
                } else if (field.getType() == Integer.class || field.getType() == int.class) {
                    字段类型 = "整数";
                } else if (field.getType() == Double.class || field.getType() == double.class) {
                    字段类型 = "浮点数";
                } else if (field.getType() == Float.class || field.getType() == float.class) {
                    字段类型 = "浮点数";
                } else if (field.getType() == Date.class) {
                    字段类型 = "日期";
                }
                数据字段.put("字段类型", 字段类型);//SqlTable.getDbType(field.getType(), xc)
                数据字段.put("是否主键", "id".equals(columnName) ? "是" : "否");
                数据字段.put("是否可空", xc.nullable() ? "是" : "否");
                数据字段.put("是否唯一", xc.unique() ? "是" : "否");
                数据字段.put("长度", xc.length());
                数据字段.put("精度", xc.precision());
                数据字段.put("scale", xc.scale());
                数据字段.put("blob", xc.text());
                数据字段.put("字段描述", xc.description());
                if (field.getDeclaringClass().equals(clazz)) {
                    数据字段.put("是否继承", "否");
                } else {
                    数据字段.put("是否继承", "是");
                    continue;//继承不输出
                }
                数据字段列表.add(数据字段);
            }
        }
    }
}
