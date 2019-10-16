/*
 * A smart splash screen for react-native apps
 * https://github.com/react-native-component/react-native-smart-splash-screen/
 * Released under the MIT license
 * Copyright (c) 2016 react-native-component <moonsunfall@aliyun.com>
 */

import {
    NativeModules,
} from 'react-native'

const SplashScreenModule = NativeModules.SplashScreen;

export default class SplashScreen {
    static loadLaunchScreenImage(url) {
        SplashScreenModule.loadLaunchScreenImage(url);
    }
    static close(param) {
        SplashScreenModule.close(param);
    }
}

