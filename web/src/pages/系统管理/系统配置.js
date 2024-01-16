import React from "react";
import PropTypes from 'prop-types'
import {XUpload, XBasePage, XButton, XCard, XFlex, XDivider, XForm, XGrid, XInput, XModal, XPopConfirm, XRadioGroup, XTableGrid, XBaseApp} from "xdcoreweb";

//@menu 系统配置
export default class 系统配置 extends XBasePage {

  static defaultProps = {
    ...super.defaultProps,
  };

  static propTypes = {
    ...super.propTypes,
  };

  constructor(props) {
    super(props);
    this.state.visibleColumns = [
      {field: "配置项", keyword: true,},
      {field: "配置值"},
      {
        title: '操作', field: '操作', width: 200, visible: this.CheckOperation("编辑"),
        render: (text, record) => {
          return <XFlex>
            <XButton isA={true} onClick={() => this.showSaveModal(record)} text={"修改"}/>
            <XDivider/>
            <XPopConfirm width={"auto"} title="是否删除以下所有记录?"
                         onOK={() => this.DeleteTableData('xtpz/delete', record, this.table)}>
              <XButton isA={true} text={"删除"}/>
            </XPopConfirm>
          </XFlex>
        },
      }
    ];
  }

  showSaveModal(data) {
    if (!data) {
      data = {};
    }
    const labelWidth = "100px";
    const Ele = <XForm infoUrl={"xtpz/queryinfo"} useServerInfo={false} infoData={data} inited={(e) => this.form = e}>
      <XGrid columnGap={"10px"} rowGap={"10px"} rowsTemplate={["auto"]}>
        <XInput field={"id"} visible={false} parent={() => this.form}/>
        <XInput labelWidth={labelWidth} field={"配置项"} isRequired={true} parent={() => this.form}/>
        <XInput labelWidth={labelWidth} field={"配置值"} parent={() => this.form}/>
      </XGrid>
    </XForm>;
    XModal.ModalShow("配置信息", async () => {
      return this.SaveFormData(this.form, "xtpz/save", this.table, {});
    }, Ele, '950px',);
  }

  download() {
    XBaseApp.DownloadDomainModel();
  }

  showPushModal() {
    let form = null;
    const labelWidth = "140px";
    const Ele = (
      <XForm useServerInfo={false} inited={(e) => form = e}>
        <XGrid columnGap={"10px"} rowGap={"10px"} rowsTemplate={["auto"]}>
          <XInput isRequired={true} field={"上传链接"} labelWidth={labelWidth}  parent={() => form}/>
          <XInputNum isRequired={true} field={"截图等待时间"} label={"截图等待时间(ms)"} labelWidth={labelWidth}
                    value={3000} min={1000} parent={() => form}/>
        </XGrid>
      </XForm>
    );
    return XModal.ModalShow("推送设计元模型", async () => {
      let m = form.ValidateEditorValues();
      if (m) {
        XMessage.ShowInfo(m);
        return false;
      }
      let values = form.GetValues();
      await pushModalData(values.上传链接, values.截图等待时间);
      return true;
    }, Ele, '800px',);
  }

  pushModalData = async (pushUrl, sleepTime) => {
    const MenuData = this.GetAllMenuData()
    if (MenuData.length == 0) {
      XMessage.ShowInfo("系统没有菜单");
      return false;
    }
    let rootElement = XBasePage.GetApp().rootElement;
    let loadMenu = async (menus) => {
      let menuList = [];
      if (menus) {
        for (let i = 0; i < menus.length; i++) {
          let item = menus[i];
          let menu = {
            菜单名称: item.name,
            菜单标识: item.path ? item.path.split("/")[item.path.split("/").length - 1] : "",
            菜单路径: item.path,
            菜单描述: "",
            界面原型图: "",
            子菜单列表: [],
            界面元素列表: [],
          };
          menuList.push(menu);
          if (item.component) {
            _this.GotoUrl(item.path);
            await _this.Sleep(sleepTime);
            let page = XBasePage.GetApp().page;
            menu.界面原型图 = await XHtml2canvas.GetImageBase64(rootElement, 200, 0, 0);//截图
            menu.界面原型图 = XString.substring(menu.界面原型图, "data:image/jpeg;base64,".length);
            if (page) {
              menu.菜单描述 = page.props?.desc;
              page.GetChildren && page.GetChildren()?.forEach(component => {
                if (component) {
                  let 元素类型 = "显示组件";//显示组件|编辑组件
                  let 组件名称 = component._reactInternals?.type.ComponentName;
                  let text = component.GetText ? component.GetText() : "";
                  let name = component.props.name;
                  let url = component.props.dataSourceUrl;
                  if (text) {
                    元素类型 = "编辑组件";
                    name = text;
                  }
                  let 元素名称 = (name ? name : "") + (组件名称 ? 组件名称 : "");
                  let 元素描述 = component.props.desc;
                  menu.界面元素列表.push({元素名称, 元素类型, 元素描述, 服务接口名称: url});
                }
              });
            }
          }
          if (item.children && item.children.length > 0) {
            menu.子菜单列表 = await loadMenu(item.children);
          }
        }
      }
      return menuList;
    };
    let url = this.GetCurrentMenuKey();
    let loadedMenuData = await loadMenu(MenuData);
    this.GotoUrl(url);
    let formData = new FormData();
    formData.append("MenuData", XHtml2canvas.strtoFile(JSON.stringify(loadedMenuData), "MenuData.dat"));
    let result = await this.RequestUploadFile("xtpz/upload", formData);
    if (result.Success) {
      this.RequestServerPost(pushUrl, { MenuData: result.Value[0] })
    }
  }


  render() {
    return <XCard paddingTRBL={"10px"}>
      <XTableGrid inited={(e) => this.table = e} dataSourceUrl="xtpz/querylist" extraButtons={(
        <XFlex visible={this.CheckOperation("编辑")}>
          <XButton text={"新增"} onClick={() => this.showSaveModal()}/>
          <XButton text={"推送模型"} onClick={() => this.showPushModal()}/>
          <XButton text={"下载模型"} onClick={() => this.download()}/>
        </XFlex>)} visibleColumns={this.state.visibleColumns}/>
    </XCard>
  }
}
