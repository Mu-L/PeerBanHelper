@echo off
chcp 65001
title PeerBanHelper (控制台模式)
start ./jre/bin/java.exe -Xmx512M -Xss512k -XX:+UseG1GC -XX:+UseStringDeduplication -Dsun.net.useExclusiveBind=false -Djava.awt.headless=true -XX:+ShrinkHeapInSteps -Dpbh.release=portable -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Dconsole.encoding=UTF-8 -jar PeerBanHelper.jar nogui
pause