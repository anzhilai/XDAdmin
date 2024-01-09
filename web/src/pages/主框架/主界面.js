import React from "react";
import PropTypes from 'prop-types'
import {Outlet} from "react-router-dom";
import DocumentTitle from 'react-document-title';
import {MenuFoldOutlined, MenuUnfoldOutlined,} from "@ant-design/icons";
import {XBaseStyle, XBaseApp, XCard, XButton, XText, XMenu, XFlex, XModal, XGrid, XImage, XBasePage, XPopover, XIcon} from "xdcoreweb";
import 用户详情 from "./用户详情";

//@menu 人员管理
export default class 主界面 extends XBasePage {

  static propTypes = { 
    ...super.propTypes
  };

  static defaultProps = {
    ...super.defaultProps
  };

  constructor(props) {
    super(props);
    this.state.MenuData = this.GetCurrentUserMenus();
    this.updateHash(false);
    window.addEventListener('hashchange', () => this.updateHash());
  }

  async componentDidMount() {
    super.componentDidMount()
    this.refreshUser();
  }

  async refreshUser() {
    let result = await this.RequestServerPost("ryxx/refresh", {}, false);
    if (result.Success) {
      const data = {};
      data.gatherloginurl = this.GetGatherData()?.gatherloginurl;
      data.gathertoken = result.Value.gathertoken;
      data.gatheruser = result.Value.gatheruser;
      if (result.Value.功能列表) {
        data.gathermenus = JSON.parse(result.Value.功能列表);
      } else {
        data.gathermenus = [];
      }
      this.SaveGatherData(data, true);
      this.setState({MenuData: this.GetCurrentUserMenus(true), refreshUserKey: new Date().getTime()})
    } else {
      this.setState({refreshUserKey: new Date().getTime()})
    }
  }

  updateHash(update = true) {
    let menuChildren = undefined;
    let firstMenu = undefined;
    let selectedMenus = [];
    let menu = this.GetCurrentMenuItem();
    let path = this.GetCurrentMenuKey();
    if (menu) {
      if (!menu.component && menu.children.length > 0) {
        this.clickMenu(menu.children[0].path);
        return;
      }
      let getParent = (menu, parents = []) => {
        if (menu.path !== "/" && menu.parent) {
          getParent(menu.parent, parents);
          parents.push(menu.parent);
          selectedMenus.push(menu.parent.path);
        }
        return parents;
      }
      let parents = getParent(menu);
      firstMenu = parents.length > 0 ? parents[0] : menu;
      menuChildren = firstMenu?.children;
      selectedMenus.push(menu.path);
    } else if (!update && (path === "/" || path === "") && this.state.MenuData.length > 0) {
      this.GotoUrl(this.state.MenuData[0].path);
      return;
    }
    let menuMap = {};
    this.GetCurrentUserMenus()?.forEach(item => {
      menuMap[item.path] = item;
    })
    if (update) {
      this.leftMenu?.onOpenChange(selectedMenus);
      this.setState({firstMenu, menuChildren, selectedMenus});
    } else {
      this.state.firstMenu = firstMenu;
      this.state.menuChildren = menuChildren;
      this.state.selectedMenus = selectedMenus;
    }
  }

  clickMenu(path) {
    let menu = this.GetCurrentMenuItem(path);
    if (menu && !menu.component && menu.children.length > 0) {
      this.clickMenu(menu.children[0].path);
    } else {
      path && this.SetOpenMenu(path);
      this.GotoUrl(path);
    }
  }

  SetOpenMenu(path) {//设置打开菜单
    if (this.leftMenu && path) {
      let keys = [...this.leftMenu.GetOpenKeys()];
      let _path = "";
      path.split("/").forEach(str => {
        if (str) {
          _path += "/" + str;
          if (keys.indexOf(_path) === -1) {
            keys.push(_path);
          }
        }
      });
      this.leftMenu.SetOpenKeys(keys);
    }
  }

  UserClick(item) {
    if (item.name === "退出登录") {
      XModal.Confirm("是否确认退出登录", () => {
        this.ClearGatherData();
        this.GotoUrl(window.config.loginUrl);
        return true;
      })
    } else if (item.name === "个人设置") {
      XModal.ModalShow("个人设置", undefined, <用户详情/>, '60%', "50vh");
    }
  }

  setHideMenu() {
    let {hideMenu = false} = this.state;
    this.setState({hideMenu: !hideMenu}, () => {
      window.setTimeout(() => {
        window.dispatchEvent(new Event('resize'));
        window.setTimeout(() => window.dispatchEvent(new Event('resize')), 10);
      }, 500);
    });
  }

  render() {
    let {hideMenu = false} = this.state;
    return <DocumentTitle title={window.config.name}>
      <XGrid rowsTemplate={["50px", "1fr"]} overflow={XBaseStyle.Overflow.hidden}>
        <XGrid columnsTemplate={["700px", "1fr", "auto"]} boxStyle={{background: "#0586FD"}}>
          <XFlex paddingTRBL={"0px 0px 0px 30px"}>
            <img src="./img/logo.svg"/>
            <div style={{color: "white"}}>{window.config.name}</div>
          </XFlex>
          <div/>
          <XFlex background={"white"} width={"auto"} boxStyle={{
            paddingRight: "50px", justifyContent: "flex-end",
            flexDirection: "row", gap: "10px"
          }}>
            <XPopover content={<XMenu
              boxStyle={{background: "white", border: "1px solid #f0f0f0", width: 160}}
              onMenuItemClick={(item) => this.UserClick(item)}
              data={[{name: "个人设置", icon: "SettingOutlined"},
                {name: "退出登录", icon: "LogoutOutlined"},]}/>}>
              <XFlex boxStyle={{minWidth: 110, cursor: "pointer"}}>
                <img src={"./img/user.svg"}/>
                <XText color={"#ffffff"} cursor={"pointer"} value={this.state.user?.姓名}/>
              </XFlex>
            </XPopover>
          </XFlex>
        </XGrid>
        <XGrid columnsTemplate={[(hideMenu ? "80px" : "200px"), "1fr"]} onResizeCallback={true}
               boxStyle={{transition: "all 0.5s",}}>
          <XGrid rowsTemplate={["1fr", "50px"]} boxStyle={{transition: "all 0.5s",}}>
            <XMenu inited={e => this.leftMenu = e} data={this.state.MenuData} overflow={"auto"}
                   inlineCollapsed={hideMenu}
                   selectedKeys={this.state.selectedMenus} openKeys={this.state.selectedMenus}
                   onMenuItemClick={(item) => this.clickMenu(item.path)}/>
            {hideMenu ?
              <MenuFoldOutlined style={{fontSize: "18px", margin: "auto", color: "gray"}}
                                onClick={() => this.setHideMenu()}/> :
              <MenuUnfoldOutlined style={{fontSize: "18px", margin: "auto", color: "gray"}}
                                  onClick={() => this.setHideMenu()}/>}
          </XGrid>
          <XGrid rowsTemplate={["1fr"]} paddingTRBL={"10px"} >
            {this.state.refreshUserKey && <Outlet/>}
          </XGrid>
        </XGrid>
      </XGrid>
    </DocumentTitle>;
  }
}


