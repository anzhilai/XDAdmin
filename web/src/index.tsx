import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import WebMenus from "./pages/WebMenu";
import 登录 from "./pages/主框架/登录";
import 主界面 from "./pages/主框架/主界面";
import NoFoundPage from "./404";
import {XBaseApp} from "xdcorelib";

//@menu 人员管理
ReactDOM.createRoot(document.getElementById("root")).render(
  <XBaseApp menus={WebMenus} getRoutes={(app) => {
    return [
      {
        path: "/login",
        component: <登录/>,
      },
      {
        path: "/",
        component: <主界面/>,
        children: [...app.MenuRoutes, {path: "/*", component: <NoFoundPage/>,}],
      },
    ];
  }}/>);
