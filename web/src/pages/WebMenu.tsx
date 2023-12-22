import React from "react";
import 组织部门 from "./基础数据/组织部门";
import 人员管理 from "./基础数据/人员管理";
import 角色权限 from "./基础数据/角色权限";
import 日志查看 from "./系统管理/日志查看";
import 系统配置 from "./系统管理/系统配置";

let appConfig = {rootPath: "./"}

export function SetRootPath(path: string) {
    appConfig.rootPath = path;
}

export function GetAssetPath(path: string) {
    return appConfig.rootPath + (path.startsWith("./") ? path.substring(2) : path);
}

export function ShowDataInfo(data: {}, showDialog = true) {

}

export default [
    {
        name: "用户管理",
        path: "yhgl",
        icon: "DesktopOutlined",
        children: [
            {
                name: '组织部门',
                path: 'zzbm',
                icon: 'DesktopOutlined',
                component: <组织部门/>
            }, {
                name: '人员管理',
                path: 'rygl',
                icon: 'DesktopOutlined',
                component: <人员管理/>
            }, {
                name: '角色权限',
                path: 'jsqx',
                icon: 'DesktopOutlined',
                component: <角色权限/>
            }
        ]
    },
    {
        name: '系统管理',
        path: 'xtsz',
        icon: "DesktopOutlined",
        children: [{
            name: '系统配置',
            path: 'xtpz',
            icon: 'SettingOutlined',
            component: <系统配置/>
        }, {
            name: '日志查看',
            path: 'rzgl',
            icon: 'DesktopOutlined',
            component: <日志查看/>
        }]
    }
];
