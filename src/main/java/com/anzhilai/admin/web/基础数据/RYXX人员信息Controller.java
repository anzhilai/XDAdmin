package com.anzhilai.admin.web.基础数据;

import com.anzhilai.core.base.BaseModel;
import com.anzhilai.core.base.BaseModelController;
import com.anzhilai.core.base.BaseUser;
import com.anzhilai.core.base.XController;
import com.anzhilai.core.database.AjaxResult;
import com.anzhilai.core.framework.GlobalValues;
import com.anzhilai.core.toolkit.*;
import com.anzhilai.admin.web.系统管理.XTPZ系统配置;
import com.anzhilai.admin.web.系统管理.XTRZ系统日志;
import com.anzhilai.core.toolkit.encrypt.RSAUtil;
import com.anzhilai.core.toolkit.image.VerifyImageUtil;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;


//@domain RYXX人员信息Controller
@Controller
@XController(name = RYXX人员信息.F_TableName)
@Transactional(rollbackFor = {Exception.class})
@RequestMapping("/ryxx")
public class RYXX人员信息Controller<T extends RYXX人员信息> extends BaseModelController<RYXX人员信息> {
    private static Logger log = Logger.getLogger(RYXX人员信息Controller.class);

    @XController(name = "获取验证码", isLogin = XController.LoginState.No)
    @RequestMapping(value = "/verify_code", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verify_code(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
        String type = RequestUtil.GetString(request, "type");
        Map<String, Object> params = null;
        if ("slide".equals(type)) {
            String key = RequestUtil.GetString(request, "key");
            boolean validate = false;
            if (StrUtil.isNotEmpty(key)) {
                validate = VerifyCodeUtil.ValidateSlideCode(key);
                params = new HashMap<>();
                params.put("validate", validate);
            }
            if (!validate) {
                String rootPath = GlobalValues.GetApplicationPath() + File.separator;
                File root = new File(rootPath + "validateImgs");
                if (!root.exists()) {
                    root = new File(rootPath + "static" + File.separator + "validateImgs");
                }
                double cutZoom = RequestUtil.GetDoubleParameter(request, "cutZoom");
                if (root.exists()) {
                    ArrayList<File> imgs = new ArrayList<>();
                    for (File f : root.listFiles()) {
                        if (f.isFile()) {
                            imgs.add(f);
                        }
                    }
                    if (imgs.size() > 0) {
                        File img = imgs.get(new Random().nextInt(imgs.size()));
                        VerifyImageUtil.VerifyImage verifyImage = new VerifyImageUtil(cutZoom).getVerifyImage(img.getPath());
                        String verifyCode = DateUtil.GetDateTimeString(new Date()) + "_" + verifyImage.XPosition;
                        params = new HashMap<>();
                        params.put("validate", validate);
                        params.put("key", RSAUtil.encrypt(verifyCode, VerifyCodeUtil.GetPublicKey())); //公钥加密
                        params.put("top", verifyImage.YPosition);
                        params.put("bgImg", verifyImage.srcImage);
                        params.put("cutImg", verifyImage.cutImage);
                    }
                }
            }
            if (params == null) {
                return AjaxResult.False("模板图片不存在").ToJson();
            }
        } else {
            int length = RequestUtil.GetIntParameter(request, "length");
            int w = RequestUtil.GetIntParameter(request, "w");
            int h = RequestUtil.GetIntParameter(request, "h");
            if (length <= 0) {
                length = 4;
            }
            if (w <= 0) {
                w = 200;
            }
            if (h <= 0) {
                h = 80;
            }
            String verifyCode = DateUtil.GetDateTimeString(new Date()) + "_" + VerifyCodeUtil.generateVerifyCode(length);
            ByteArrayOutputStream out = new ByteArrayOutputStream();//输出图片
            VerifyCodeUtil.outputImage(w, h, out, verifyCode);
            params = new HashMap<>();
            params.put("key", RSAUtil.encrypt(verifyCode, VerifyCodeUtil.GetPublicKey())); //公钥加密
            params.put("img", java.util.Base64.getEncoder().encodeToString(out.toByteArray()));
        }
        return AjaxResult.True(params).ToJson();
    }

    @XController(name = "登录", isLogin = XController.LoginState.No)
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String Login(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
        String 验证码 = RequestUtil.GetParameter(request, "验证码");
        String 验证码key = RequestUtil.GetParameter(request, "验证码key");
        if (StrUtil.isNotEmpty(验证码key)) {
            if (!VerifyCodeUtil.DecryptVerifyCode(验证码key).equalsIgnoreCase(验证码)) {
                return AjaxResult.False("验证码不正确").ToJson();
            }
        }
        String userName = RequestUtil.GetParameter(request, RYXX人员信息.F_登录账号);
        String userPass = RequestUtil.GetParameter(request, RYXX人员信息.F_登录密码);
        log.info(userName + "login");
        RYXX人员信息 user = RYXX人员信息.GetObjectByFieldValue(GetClass(), RYXX人员信息.F_登录账号, userName);
        if (user == null) {
            user = RYXX人员信息.GetObjectByFieldValue(GetClass(), RYXX人员信息.F_手机号码, userName);
        }
        if (user == null) {
            return AjaxResult.False("用户未注册，请先注册用户").ToJson();
        }
        if (!user.登录密码.equals(BaseUser.FormatPwd(userPass))) {
            return AjaxResult.False("密码错误，请重新输入").ToJson();
        }
        return LoginOk(user, user.UpdateLoginKey(), true);
    }

    @XController(name = "token登录", isLogin = XController.LoginState.No)
    @RequestMapping(value = "/token_login", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String tokenLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
        String token = RequestUtil.GetParameter(request, "token");
        if (StrUtil.isNotEmpty(token)) {
            BaseUser user = BaseUser.GetUserByToken(token);
            if (user != null && user.getClass() == RYXX人员信息.class) {
                log.info(user.GetLoginName() + "login");
                RYXX人员信息 ryxx = (RYXX人员信息) user;
                return LoginOk(ryxx, ryxx.loginKey, true);
            }
        }
        return AjaxResult.False("无效token").ToJson();
    }

    @XController(name = "刷新用户信息")
    @RequestMapping(value = "/refresh", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String refresh(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
        String token = RequestUtil.GetParameter(request, "gathertoken");
        if (StrUtil.isNotEmpty(token)) {
            BaseUser user = GlobalValues.GetSessionUser();
            if (user != null && user.getClass() == RYXX人员信息.class) {
                RYXX人员信息 ryxx = (RYXX人员信息) user;
                String loginKey = null;
                DecodedJWT decodedJWT = BaseUser.DecodedToken(token);
                Claim claim = decodedJWT.getClaim("loginKey");
                if (claim != null) {
                    loginKey = claim.asString();
                }
                return LoginOk(ryxx, loginKey, false);
            }
        }
        return AjaxResult.False("无效token").ToJson();
    }

    public String LoginOk(RYXX人员信息 user, String loginKey, boolean writeLog) throws Exception {
        String 是否允许同时登录 = XTPZ系统配置.Get系统配置("系统配置", "是否允许同时登录", "是");
        if ("是".equals(是否允许同时登录)) {
            loginKey = null;
        }
        Date expiresAt = null;
        int JWT登录过期时间 = TypeConvert.ToInteger(XTPZ系统配置.Get系统配置("JWT", "登录过期时间", "0", "天数"));
        if (JWT登录过期时间 > 0) {
            expiresAt = DateUtil.AddDay(new Date(), JWT登录过期时间);
        }
        String token = BaseUser.GetTokenFromUser(user, loginKey, expiresAt);
        GlobalValues.SetSessionUser(user);
        AjaxResult result = AjaxResult.True();
        result.AddValue(BaseUser.F_GatherTOKEN, token);
        Map mu = user.ToMap();
        mu.remove(RYXX人员信息.F_登录密码);
        result.AddValue(BaseUser.F_GatherUser, mu);
        if (StrUtil.isEmpty(user.角色信息id) && StrUtil.isNotEqual(RYXX人员信息.Data_Admin, user.id)) {
            return AjaxResult.False("用户未设置角色,请联系管理员").ToJson();
        }
        mu.put(JSXX角色信息.F_数据权限, user.Get数据权限());
        mu.put("组织部门", user.Get组织部门());
        result.AddValue(JSXX角色信息.F_功能列表, user.Get功能列表());
        if (writeLog) {
            RYXX人员信息 bu = (RYXX人员信息) GlobalValues.GetSessionUser();
            XTRZ系统日志.Save系统日志(XTRZ系统日志.RZLX日志类型.通知, bu.姓名 + "登录系统");
        }
        return result.ToJson();
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        AjaxResult ar = AjaxResult.True();
        RYXX人员信息 bu = (RYXX人员信息) GlobalValues.GetSessionUser();
        XTRZ系统日志.Save系统日志(XTRZ系统日志.RZLX日志类型.通知, bu.姓名 + "退出系统");
        return ar.ToJson();
    }


    @XController(name = "详情")
    @RequestMapping(value = "/queryinfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String queryinfo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
        String id = RequestUtil.GetString(request, BaseModel.F_id);
        RYXX人员信息 t = RYXX人员信息.GetObjectById(GetClass(), id);
        if (t != null) {
            Map m = t.ToMap();
            m.remove(RYXX人员信息.F_登录密码);
            return AjaxResult.True(m).ToJson();
        } else {
            return AjaxResult.False("信息不存在").ToJson();
        }
    }

    @XController(name = "修改密码")
    @RequestMapping(value = "/modifypassword", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String modifypassword(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
        RYXX人员信息 user = RYXX人员信息.GetObjectById(GetClass(), RequestUtil.GetString(request, RYXX人员信息.F_id));
        if (user != null) {
            String oldpass = RequestUtil.GetParameter(request, RYXX人员信息.F_原密码);
            String newPass = RequestUtil.GetParameter(request, RYXX人员信息.F_新密码);
            if (user.登录密码.equals(RYXX人员信息.FormatPwd(oldpass))) {
                user.Update(RYXX人员信息.F_登录密码, RYXX人员信息.FormatPwd(newPass));
                return AjaxResult.True().ToJson();
            } else {
                return AjaxResult.False("原密码不正确").ToJson();
            }
        }
        return AjaxResult.False("用户不存在").ToJson();
    }

    @XController(name = "重置密码")
    @RequestMapping(value = "/resetpassword", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String resetpassword(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
        RYXX人员信息 user = RYXX人员信息.GetObjectById(GetClass(), RequestUtil.GetString(request, RYXX人员信息.F_id));
        if (user != null) {
            String 默认密码 = XTPZ系统配置.Get系统配置("系统配置", "用户默认密码", "123456", "用户默认密码");
            if (StrUtil.isEmpty(默认密码)) {
                默认密码 = "123456";
            }
            user.Update(RYXX人员信息.F_登录密码, RYXX人员信息.FormatPwd(默认密码));
            return AjaxResult.True().ToJson();
        }
        return AjaxResult.False("用户不存在").ToJson();
    }


}
