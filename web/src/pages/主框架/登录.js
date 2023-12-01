import {XBasePage, XBaseStyle, XButton, XCheckGroup, XFlex, XForm, XGrid, XInput, XInputPwd, XMessage} from "xdcorelib";
import {CopyrightOutlined} from "@ant-design/icons";
import DocumentTitle from "react-document-title";
import React from "react";

//@menu 人员管理
export default class 登录 extends XBasePage {

  static propTypes = {
    ...super.propTypes
  };

  static defaultProps = {
    ...super.defaultProps,
      valiateUser: false,
  };

  constructor(props) {
    super(props)
  }

  async loginClick() {
    let e = this.loginForm.ValidateEditorValues();
    if (e) {
      XMessage.ShowError(e);
      return;
    }
    let postData = this.loginForm.GetEditorValues();
    console.log(postData);
    const result = await this.RequestServerPost('ryxx/login', postData);
    if (result.Success) {
      const data = {};
      data.gatherloginurl = this.GetCurrentMenuKey();
      data.gathertoken = result.Value.gathertoken;
      data.gatheruser = result.Value.gatheruser;
      if (result.Value.功能列表) {
        data.gathermenus = JSON.parse(result.Value.功能列表);
      } else {
        data.gathermenus = {};
      }
      let isremember = postData?.记住密码 && postData?.记住密码?.length > 0;
      this.SaveGatherData(data, isremember);
      this.GotoUrl(window.config.mainUrl);
    }
  }

  render() {
    let title = window.config.name;
    return <DocumentTitle title={title}>
      <XGrid backgroundImage={"./img/loginback.png"} rowsTemplate={["auto", "1fr", "500px", "1fr"]}
             justifyContent={"center"}>
        <XFlex justifyContent={"center"} backgroundImage={"./img/loginTitleBack.svg"}>
          <span style={{color: "white", height: 40, lineHeight: "40px", fontSize: 20}}> {title}</span>
        </XFlex>
        <div/>
        <XGrid backgroundColor={"var(--xdtheme-default-login-backcolor-op)"} boxStyle={{borderRadius: "27px"}}
               alignItems={"center"} justifyItems={"center"} columnsTemplate={["600px", "520px"]}>
          <div></div>
          <XFlex contentVAlign={XBaseStyle.Align.center} paddingTRBL={"0px 20px 0px 90px"}>
            <XForm inited={(v) => this.loginForm = v}/>
            <XGrid justifyContent={"center"} rowGap={"20px"}
                   rowsTemplate={["20px", "46px", "auto", "auto", "20px"]} boxStyle={{
              borderRadius: "4px", border: "1px solid #35404C", background: "#2c333ac2"
            }} paddingTRBL={"20px"}>
              <div/>
              <div style={{color: "white", textAlign: "center", fontSize: 18}}> 登录</div>
              <XGrid rowGap={"20px"}>
                <XGrid rowsTemplate={['auto', 'auto']} rowGap={"5px"}>
                  <XFlex boxStyle={{color: "#DFDADA"}}><img height={"26px"} src={"./img/user.svg"}/>账号:</XFlex>
                  <XInput field={"登录账号"} parent={() => this.loginForm} showLabel={false}
                          isRequired={true}
                          validateText={"请输入登录账号！"}
                          onPressEnter={() => this.loginClick()}/>
                </XGrid>
                <XGrid rowsTemplate={['auto', 'auto']} rowGap={"5px"}>
                  <XFlex boxStyle={{color: "#DFDADA"}}> <img height={"26px"} src={"./img/loginPassword.svg"}/>
                    密码:</XFlex>
                  <XInputPwd field={"登录密码"} parent={() => this.loginForm} isRequired={true}
                             showLabel={false}
                             validateText={"请输入登录密码"}
                             onPressEnter={() => this.loginClick()}/>
                </XGrid>
                <XFlex justifyContent={"space-between"}>
                  <XButton isA={true} text={"忘记密码"}/></XFlex>
              </XGrid>
              <XFlex boxStyle={{gap: "30px"}} horizontalAlign={XBaseStyle.Align.center}>
                <XButton text={"登录"} parent={() => this.loginForm} onClick={() => this.loginClick()}/>
              </XFlex>
            </XGrid>
          </XFlex>
        </XGrid>
      </XGrid>
    </DocumentTitle>
  }
}
