package com.anzhilai.admin.web.基础数据;

import com.anzhilai.admin.web.系统管理.XTPZ系统配置;
import com.anzhilai.core.base.*;
import com.anzhilai.core.database.DataTable;
import com.anzhilai.core.database.SqlInfo;
import com.anzhilai.core.toolkit.StrUtil;

import java.util.*;

//@domain RYXX人员信息
@XTable
public class RYXX人员信息 extends BaseUser {

    public final static String F_TableName = "RYXX人员信息";
    public final static String Data_Admin = "admin";

    @XColumn(foreignTable = ZZBM组织部门.F_TableName)//主要部门
    public String 组织部门id;
    public final static String F_组织部门id = "组织部门id";

    @XColumn(foreignTable = JSXX角色信息.F_TableName)
    public String 角色信息id;
    public final static String F_角色信息id = "角色信息id";

    @XColumn
    public String 姓名;
    public final static String F_姓名 = "姓名";


    @XColumn
    @XIndex(unique = true)
    public String 登录账号;
    public final static String F_登录账号 = "登录账号";

    @Override
    public String GetLoginName() {
        return 登录账号;
    }

    @XColumn
    public String 登录密码;
    public final static String F_登录密码 = "登录密码";
    public final static String F_原密码 = "原密码";
    public final static String F_新密码 = "新密码";

    @XColumn
    public Date 登录时间;
    public final static String F_登录时间 = "登录时间";


    @Override
    public String GetPassword() {
        return 登录密码;
    }

    @XColumn
    public String loginKey;
    public final static String F_loginKey = "loginKey";

    @Override
    public String GetLoginKey() {
        return loginKey;
    }

    public String UpdateLoginKey() throws Exception {
        loginKey = BaseModel.GetUniqueId();
        this.UpdateFields(F_loginKey);
        return loginKey;
    }

    @XColumn
    @XIndex(unique = true)
    public String 手机号码;
    public final static String F_手机号码 = "手机号码";

    @XColumn
    public Date 入职时间;
    public final static String F_入职时间 = "入职时间";

    @XColumn
    public String 职位;
    public final static String F_职位 = "职位";

    @XColumn
    public String 员工工号;
    public final static String F_员工工号 = "员工工号";

    @XColumn
    public String 邮箱;
    public final static String F_邮箱 = "邮箱";

    @XColumn
    public Date 出生日期;
    public final static String F_出生日期 = "出生日期";


    @XColumn
    public String 是否锁定 = "否";
    public final static String F_是否锁定 = "是否锁定";

    public boolean GetBool是否锁定() {
        return "是".equals(是否锁定);
    }

    @XColumn
    public String 性别;
    public final static String F_性别 = "性别";

    @XColumn(text = true)
    public String 备注;
    public final static String F_备注 = "备注";

    @XColumn(text = true)
    public String 头像;
    public final static String F_头像 = "头像";

    @XColumn(text = true)
    public String 家庭地址;
    public final static String F_家庭地址 = "家庭地址";


    @XColumn
    public String OpenID;
    public final static String F_OpenID = "OpenID";

    @XColumn
    public String 登录首页;
    public final static String F_登录首页 = "登录首页";

    @Override
    public List<Map> GetListUniqueFieldAndValues() {
        List<Map> l = new ArrayList<>();
        Map m = new HashMap();
        m.put(F_登录账号, this.登录账号);
        l.add(m);
        if (StrUtil.isNotEmpty(this.手机号码)) {
            Map m2 = new HashMap();
            m2.put(F_手机号码, this.手机号码);
            l.add(m2);
        }
        return l;
    }

    @Override
    public String SaveValidate() throws Exception {
        String s = "";
        if (!Data_Admin.equals(this.id) && Data_Admin.equals(this.登录账号)) {
            s += "新建登录账号不能为admin\r\n";
        }
        return s;
    }

    public JSXX角色信息 Get人员角色() throws Exception {
        JSXX角色信息 s = GetObjectById(JSXX角色信息.class, this.角色信息id);
        if (s == null) {
            s = new JSXX角色信息();
        }
        return s;
    }

    public String Get功能列表() throws Exception {
        return Get人员角色().功能列表;
    }

    ZZBM组织部门 zzbm;

    public ZZBM组织部门 Get组织部门() throws Exception {
        if (zzbm == null) {
            zzbm = ZZBM组织部门.GetObjectById(ZZBM组织部门.class, this.组织部门id);
        }
        return zzbm;
    }

    public String Get数据权限() throws Exception {
        if (Data_Admin.equals(this.id)) {
            return JSXX角色信息.E_数据权限.全部数据.name();
        }
        return Get人员角色().数据权限;
    }

    @Override
    public void Save() throws Exception {
        if (StrUtil.isEmpty(this.登录密码)) {
            String 默认密码 = XTPZ系统配置.Get系统配置("系统配置", "用户默认密码", "123456", "用户默认密码");
            if (StrUtil.isEmpty(默认密码)) {
                默认密码 = "123456";
            }
            this.登录密码 = RYXX人员信息.FormatPwd(默认密码);
        }
        if (StrUtil.isEmpty(this.登录账号)) {
            this.登录账号 = null;
        }
        if (StrUtil.isEmpty(this.手机号码)) {
            this.手机号码 = null;
        }
        super.Save();
    }

    //重要,通过此可以设置数据权限
    @Override
    public void SetQueryListDataRight(BaseQuery bq, SqlInfo su) throws Exception {
        SqlInfo sqlInfo = new SqlInfo();
        if (su.TableList.contains(ZZBM组织部门.F_TableName) && StrUtil.isNotEmpty(bq.UserTreePath)) {
            String where = ZZBM组织部门.F_TableName + "." + ZZBM组织部门.F_TreePath + " like ?";
            sqlInfo.And(where).AddParam(bq.UserTreePath + "%");
        }
        if (su.TableList.contains(RYXX人员信息.F_TableName) && StrUtil.isNotEmpty(bq.UserID)) {
            String where = RYXX人员信息.F_TableName + "." + RYXX人员信息.F_id + " = ?";
            sqlInfo.And(where).AddParam(bq.UserID);
        }
        if (sqlInfo != null && StrUtil.isNotEmpty(sqlInfo.sbwhere.toString())) {
            bq.AddCustomSqlCond(sqlInfo);
        }
    }


    public class QueryModel extends BaseQuery {
        public QueryModel(BaseModel bm) {
            super(bm);
        }

        @XQuery(column = RYXX人员信息.F_组织部门id, type = XQuery.QueryType.noEqual)
        public String 组织部门idNoEqual;
        @XQuery
        public String 姓名;
        @XQuery
        public String 手机号码;
        @XQuery(type = XQuery.QueryType.equal)
        public String OpenID;
        @XQuery(table = ZZBM组织部门.F_TableName, column = ZZBM组织部门.F_TreePath, type = XQuery.QueryType.leftlike)
        public String 组织部门TreePath;
        @XQuery(type = XQuery.QueryType.equal)
        public String 组织部门id;
        @XQuery(type = XQuery.QueryType.equal)
        public String 角色信息id;
        @XQuery(type = XQuery.QueryType.equal)
        public String 职级信息id;
    }

    @Override
    public BaseQuery CreateQueryModel() {
        return new QueryModel(this);
    }

    public List<Map<String, String>> GetListForeignColumns() {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> m = new HashMap<>();
        m.put(F_foreignKey, F_组织部门id);
        m.put(F_foreignTable, ZZBM组织部门.F_TableName);
        m.put(F_columnField, ZZBM组织部门.F_组织名称);
        list.add(m);
        return list;
    }

    @Override
    public DataTable GetList(BaseQuery bq) throws Exception {
        SqlInfo su = new SqlInfo().CreateSelect();
        su.AppendColumn(RYXX人员信息.F_TableName, F_id);
        su.AppendColumn(RYXX人员信息.F_TableName, F_组织部门id);
        su.AppendColumn(RYXX人员信息.F_TableName, F_角色信息id);
        su.AppendColumn(RYXX人员信息.F_TableName, F_登录时间);
        su.AppendColumn(RYXX人员信息.F_TableName, F_姓名);
        su.AppendColumn(RYXX人员信息.F_TableName, F_性别);
        su.AppendColumn(RYXX人员信息.F_TableName, F_头像);
        su.AppendColumn(RYXX人员信息.F_TableName, F_登录账号);
        su.AppendColumn(RYXX人员信息.F_TableName, F_手机号码);
        su.AppendColumn(RYXX人员信息.F_TableName, F_备注);
        su.AppendColumn(RYXX人员信息.F_TableName, F_出生日期);
        su.AppendColumn(RYXX人员信息.F_TableName, F_入职时间);
        su.AppendColumn(RYXX人员信息.F_TableName, F_家庭地址);
        su.AppendColumn(RYXX人员信息.F_TableName, F_员工工号);
        su.AppendColumn(RYXX人员信息.F_TableName, F_是否锁定);
        su.AppendColumn(RYXX人员信息.F_TableName, F_登录首页);
        su.AppendColumn(ZZBM组织部门.F_TableName, ZZBM组织部门.F_组织名称);
        su.AppendColumn(JSXX角色信息.F_TableName, JSXX角色信息.F_角色名称);
        su.From(RYXX人员信息.F_TableName);
        su.InnerJoin(ZZBM组织部门.F_TableName, ZZBM组织部门.F_id, RYXX人员信息.F_组织部门id);
        su.LeftJoin(JSXX角色信息.F_TableName, JSXX角色信息.F_id, RYXX人员信息.F_角色信息id);
        return bq.GetList(su);
    }

    @Override
    public void InitTestData() throws Exception {
        SaveTestData(Data_Admin, "123456", "");
    }

    public void SaveTestData(String name, String pwd, String 组织id) throws Exception {
        RYXX人员信息 j = RYXX人员信息.GetObjectById(RYXX人员信息.class, name);
        if (j == null) {
            j = new RYXX人员信息();
            j.id = name;
            j.姓名 = name;
            j.登录账号 = name;
            j.登录密码 = BaseUser.FormatPwd(pwd);
            j.组织部门id = 组织id;
            j.Save();
        }
    }
}
