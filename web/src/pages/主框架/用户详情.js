import React from "react";
import PropTypes from 'prop-types'
import {XBasePage, XButton, XCard, XForm, XGrid, XInput, XTabs, XMessage, XRadioGroup, XModal, XText} from "xdcorelib";

//@menu 人员管理
export default class 用户详情 extends XBasePage {

  static propTypes = {
    ...super.propTypes,
  };

  static defaultProps = {
    ...super.defaultProps,
  };

  constructor(props) {
    super(props);
  }

  async exitUser() {
    XMessage.ShowInfo("修改成功，请重新登录系统");
    this.ClearGatherData();
    await this.sleep(2000);
    location.reload();//需要关闭修改个人信息弹出框
  }

  render() {
    let {activeKey = "基本信息"} = this.state;
    let labelWidth = "120px";
    return <XCard boxStyle={{backgroundColor: '#ffffff', width: '100%', borderRadius: '5px'}}>
      <XGrid columnsTemplate={["auto", "1fr"]}>
        <XTabs type={"line"} tabPosition={XTabs.TabPosition.left} onTabChange={(key) => this.setState({activeKey: key})}
               items={["基本信息", "修改密码",]}/>
        <XCard visible={activeKey === "基本信息"}>
          <XForm infoUrl={"ryxx/queryinfo"} useServerInfo={true} infoData={this.state.user}
                 inited={(e) => this.form = e}>
            <XGrid columnGap={"4px"} rowGap={"4px"} rowsTemplate={["auto"]}>
              <XInput field={"id"} labelWidth={labelWidth} visible={false} parent={() => this.form}/>
              <XInput field={"登录账号"} labelWidth={labelWidth} readOnly={true} readOnlyStyle={{width: "auto"}}
                      parent={() => this.form}/>
              <XInput field={"姓名"} labelWidth={labelWidth} isRequired={true} parent={() => this.form}/>
              <XRadioGroup field={"性别"} labelWidth={labelWidth} value={"男"} items={["男", "女"]}
                           parent={() => this.form}/>
              <XInput field={"手机号码"} labelWidth={labelWidth} parent={() => this.form}/>
              <XInput field={"邮箱"} labelWidth={labelWidth} parent={() => this.form}/>
              <XButton text={"保存"} onClick={async () => {
                let data = this.form.GetEditorValues();
                let ret = await this.SaveFormData(this.form, "ryxx/save");
                if (ret) {
                  let gatherData = this.GetGatherData();
                  gatherData.gatheruser = {...gatherData.gatheruser, ...data}
                  this.SaveGatherData(gatherData, true);
                  XMessage.ShowInfo("保存成功")
                }
              }}/>
            </XGrid>
          </XForm>
        </XCard>
        <XCard visible={activeKey === "修改密码"}>
          <XForm infoData={this.state.user} inited={(e) => this.form修改密码 = e}>
            <XGrid columnGap={"4px"} rowGap={"4px"} rowsTemplate={["auto"]}>
              <XInput field={"id"} labelWidth={labelWidth} visible={false} parent={() => this.form修改密码}/>
              <XInput field={"原密码"} isRequired={true} isPwd={true} labelWidth={labelWidth}
                      parent={() => this.form修改密码}/>
              <XInput field={"新密码"} isRequired={true} isPwd={true} labelWidth={labelWidth}
                      parent={() => this.form修改密码}/>
              <XInput field={"重复新密码"} isRequired={true} isPwd={true} labelWidth={labelWidth}
                      parent={() => this.form修改密码}/>
              <XButton text={"保存"} onClick={async () => {
                let p = this.form修改密码.GetEditorValues();
                if (p.新密码 !== p.重复新密码) {
                  XMessage.ShowError("两次密码不一次")
                  return false;
                }
                let ret = await this.SaveFormData(this.form修改密码, "ryxx/modifypassword");
                ret && this.exitUser();
              }}/>
            </XGrid>
          </XForm>
        </XCard>
      </XGrid>
    </XCard>
  }
}
