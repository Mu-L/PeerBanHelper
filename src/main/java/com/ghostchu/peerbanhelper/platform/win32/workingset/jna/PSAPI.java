package com.ghostchu.peerbanhelper.platform.win32.workingset.jna;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Windows PSAPI 接口定义
 * 用于进程和系统性能信息API
 */
public interface PSAPI extends StdCallLibrary {
    
    PSAPI INSTANCE = Native.load("psapi", PSAPI.class);
    
    /**
     * 清空进程的工作集，将不常用的页面移动到分页文件
     * @param hProcess 进程句柄
     * @return 如果函数成功，返回值为非零值；如果函数失败，返回值为零
     */
    boolean EmptyWorkingSet(WinNT.HANDLE hProcess);
}
