package com.hiscene.plugin;

import android.os.IInterface;
import android.os.RemoteException;

/**
 * Created by hujun on 2019-06-18.
 */

public interface CommonProvider extends IInterface {
    String getJsonData(String jsonParm) throws RemoteException;
}
