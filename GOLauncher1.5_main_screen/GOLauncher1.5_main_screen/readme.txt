1.修改local.properties中的路径,改成自己机器上的配置,注意windows使用'\\'作为路径分隔符,linux使用'/',注意此文件不能提交
2.如果对代码混淆,需要注意Manifest中不能出现
    android:debuggable="true"
  并且需要在project.properties中配置
    proguard.config=proguard.cfg
3.如果不混淆,可以用'#'注释掉project.properties的proguard.config=proguard.cfg一行
4.新的混淆规则添加到proguard.cfg, proguard的配置说明参考 http://proguard.sourceforge.net/#manual/usage.html
5.右键运行build.xml

=======================================================================================
批量打包: windows下双击运行makeall.bat, linux下进入工程根目录,在终端执行 bash makeall.sh 


自动化脚本打包说明：
1.将GOLauncherEX，GOLauncherEX_ShellPlugin，AppGameShellWidget三个工程置于同一目录；
2.打包会依赖本地开发环境，因此需要修改build-auto-config.txt配置文件，对应修改GOLauncher,shellplugin，appgamewidget三个工程在本地的名称，SVNLOG_FILE指出导出log信息到本地文件中，ANT_BUILD_PATH是本地ant的目录，找到eclipse安装目录，找到ant所在即可；
3.打包过程包括三个工程的代码更新，均自动进行，打包中途有需要对关键代码检查，按确认后即可自动完成打包与上传；

