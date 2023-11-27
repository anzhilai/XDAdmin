package com.anzhilai.admin.web.基础数据;

import com.anzhilai.core.base.*;
import com.anzhilai.core.database.DataTable;
import com.anzhilai.core.database.SqlInfo;

//@domain JSXX角色信息
@XTable
public class JSXX角色信息 extends BaseModel {
    public final static String F_TableName = "JSXX角色信息";

    @XColumn
    public String 角色名称;
    public final static String F_角色名称 = "角色名称";

    @XColumn(text = true)
    public String 角色描述;
    public final static String F_角色描述 = "角色描述";

    @XColumn
    public String 是否内置 = "否";
    public final static String F_是否内置 = "是否内置";

    public enum E_数据权限 {
        全部数据, 所属部门, 个人数据, 指定部门, 指定人员
    }

    @XColumn
    public String 数据权限 = E_数据权限.所属部门.toString();
    public final static String F_数据权限 = "数据权限";


    //url列表,这个是一个json字符串
    @XColumn(columnDefinition = "text")
    public String 功能列表;
    public final static String F_功能列表 = "功能列表";


    public class QueryModel extends BaseQuery {
        public QueryModel(BaseModel bm) {
            super(bm);
        }

        @XQuery
        public String 角色名称;
    }

    @Override
    public QueryModel CreateQueryModel() {
        return new QueryModel(this);
    }

    @Override
    public DataTable GetList(BaseQuery bq) throws Exception {
        QueryModel qm = (QueryModel) bq;
        bq.hasDataRight = false;
        SqlInfo su = new SqlInfo().CreateSelect();
        su.AppendColumn(JSXX角色信息.F_TableName, F_id);
        su.AppendColumn(JSXX角色信息.F_TableName, F_角色名称);
        su.AppendColumn(JSXX角色信息.F_TableName, F_角色描述);
        su.AppendColumn(JSXX角色信息.F_TableName, F_数据权限);
        su.AppendColumn(JSXX角色信息.F_TableName, F_功能列表);
        su.From(JSXX角色信息.F_TableName);
        return bq.GetList(su);
    }

}
