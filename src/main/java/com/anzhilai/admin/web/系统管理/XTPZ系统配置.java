package com.anzhilai.admin.web.系统管理;

import com.anzhilai.core.base.*;
import com.anzhilai.core.database.DataTable;
import com.anzhilai.core.database.SqlInfo;

//@domain XTPZ系统配置
@XTable
public class XTPZ系统配置 extends BaseModel {
    public final static String F_TableName = "XTPZ系统配置";

    @XColumn
    public String 配置项;
    public final static String F_配置项 = "配置项";

    @XColumn(text = true)
    public String 配置值;
    public final static String F_配置值 = "配置值";

    @Override
    public void Save() throws Exception {
        super.Save();
    }

    public class QueryModel extends BaseQuery {
        public QueryModel(BaseModel bm) {
            super(bm);
        }

        @XQuery
        public String 配置项;

    }

    @Override
    public BaseQuery CreateQueryModel() {
        return new QueryModel(this);
    }

    @Override
    public DataTable GetList(BaseQuery bq) throws Exception {
        SqlInfo su = new SqlInfo().CreateSelect();
        su.AppendColumn(XTPZ系统配置.F_TableName, F_id);
        su.AppendColumn(XTPZ系统配置.F_TableName, F_配置项);
        su.AppendColumn(XTPZ系统配置.F_TableName, F_配置值);
        su.From(XTPZ系统配置.F_TableName);
        return bq.GetList(su);
    }

    public static String Get系统配置(String pzx) throws Exception {
        XTPZ系统配置 xtpz = XTPZ系统配置.GetObjectById(XTPZ系统配置.class, "内部配置" + pzx);
        if (xtpz == null) {
            return "";
        }
        return xtpz.配置值;
    }

    public static void Save系统配置(String pzx, String value) throws Exception {
        XTPZ系统配置 xtpz = XTPZ系统配置.GetObjectById(XTPZ系统配置.class, "内部配置" + pzx);
        if (xtpz == null) {
            xtpz = new XTPZ系统配置();
            xtpz.id = "内部配置" + pzx;
            xtpz.配置项 = pzx;
        }
        xtpz.配置值 = value;
        xtpz.Save();
    }

    public static String Get系统配置(String pzlx, String pzx, String value) throws Exception {
        XTPZ系统配置 xtpz = XTPZ系统配置.GetObjectById(XTPZ系统配置.class, "内部配置" + pzx);
        if (xtpz == null) {
            xtpz = new XTPZ系统配置();
            xtpz.id = "内部配置" + pzx;
            xtpz.配置项 = pzx;
            xtpz.配置值 = value;
            xtpz.Save();
        }
        return xtpz.配置值;
    }

    public static String Get系统配置(String pzlx, String pzx, String defaultValue, String 备注) throws Exception {
        return Get系统配置(pzlx, pzx, defaultValue, 备注, false);
    }

    public static String Get系统配置(String pzlx, String pzx, String defaultValue, String 备注, boolean update) throws Exception {
        XTPZ系统配置 xtpz = GetObjectById(XTPZ系统配置.class, pzlx + pzx);
        if (xtpz == null) {
            xtpz = new XTPZ系统配置();
            xtpz.id = pzlx + pzx;
            xtpz.配置项 = pzlx + "-" + pzx;
            xtpz.配置值 = defaultValue;
            xtpz.Save();
        } else {
            if (update) {
                xtpz.配置项 = pzlx + "-" + pzx;
                xtpz.配置值 = defaultValue;
                xtpz.UpdateFields(F_配置项, F_配置值);
            }
        }
        return xtpz.配置值;
    }
}
