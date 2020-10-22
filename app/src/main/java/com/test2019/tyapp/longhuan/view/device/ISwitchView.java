package com.test2019.tyapp.longhuan.view.device;

import com.test2019.tyapp.longhuan.view.device.common.ICommonView;

public interface ISwitchView extends ICommonView {

    void showOpenView();

    void showCloseView();

    void showErrorTip();

    void showRemoveTip();

    void changeNetworkErrorTip(boolean status);

    void statusChangedTip(boolean status);

//    void devInfoUpdateView();

//    void updateTitle(String titleName);

    void setStatus(boolean status);

    boolean getStatus();

    void setRecognizedText(String text);

    void setReal(String text);
}
