import React from 'react';
import {XCard} from "xdcoreweb";

let Result = XCard.Result;

const NoFoundPage: React.FC<{}> = () => (
    <Result status="404" title="XD快速开发" subTitle="AI驱动的新一代软件开发工具"/>
);

export default NoFoundPage;
