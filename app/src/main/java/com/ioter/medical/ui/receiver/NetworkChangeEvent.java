package com.ioter.medical.ui.receiver;

public class NetworkChangeEvent
    {
        public boolean isConnected;

        //是否存在网络
        public NetworkChangeEvent(boolean isConnected)
        {
            this.isConnected = isConnected;
        }
    }