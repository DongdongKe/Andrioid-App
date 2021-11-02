package com.example.android_mobile_app.Movesense;

interface BaseView<T extends BasePresenter> {
    void setPresenter(T presenter);

}
