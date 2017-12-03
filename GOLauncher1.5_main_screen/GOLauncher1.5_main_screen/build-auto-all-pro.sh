#自动打包过程
# echo $(pwd) |sed '{s/\/[^\/]*$//}'
#WORK_SPACE= $(pwd) |sed '{s/\/[^\/]*$//}'
#echo ${WORK_SPACE}
#echo $(pwd)
#echo ${0}

#根据当前工程需要自行修改工程名称

PROJECT_GOLAUNCHER=$(cat build-auto-config.txt | grep PROJECT_GOLAUNCHER | cut -d "=" -f 2)
PROJECT_SHELLPLUGIN=$(cat build-auto-config.txt | grep PROJECT_SHELLPLUGIN | cut -d "=" -f 2)
PROJECT_APPGAMEWIDGET=$(cat build-auto-config.txt | grep PROJECT_APPGAMEWIDGET | cut -d "=" -f 2)
SVNLOG_FILE=${PROJECT_GOLAUNCHER}/svnchangelog.txt
IS_PRIVATE_TEST=false
IS_DEBUG_MODE=false

#svn log file
SVN_LOG_GOLAUNCHER=$(cat build-auto-config.txt | grep SVN_LOG_GOLAUNCHER | cut -d "=" -f 2)
SVN_LOG_SHELLPLUGIN=$(cat build-auto-config.txt | grep SVN_LOG_SHELLPLUGIN | cut -d "=" -f 2)
SVN_LOG_APPGAMEWIDGET=$(cat build-auto-config.txt | grep SVN_LOG_APPGAMEWIDGET | cut -d "=" -f 2)

export PROJECT_GOLAUNCHER
export PROJECT_SHELLPLUGIN
export PROJECT_APPGAMEWIDGET
export SVNLOG_FILE

#更新各项工程
bash build-auto-updateallproject-pro.sh

export IS_PRIVATE_TEST


#询问是否需要混淆
while true;do
#stty -icanon min 0 time 50
echo -n "是否内部测试包？内部测试包将不混淆可调试。(yes or no)"
read Arg
case $Arg in
Y|y|YES|yes|Yes)
  IS_DEBUG_MODE=true
  echo "内部测试包，打包将不混淆且为可调试状态！！！"
  break;;
N|n|NO|no)
  IS_DEBUG_MODE=false
  echo "需要混淆，将以Relase模式打包！！！"
  break;;
esac
done

export IS_DEBUG_MODE



#代码检查
bash build-auto-checkcode-pro.sh

if [ $? -eq 0 ]; then
echo =====================================================================
echo ================打包成功,安装包已上传至svn==================
echo =====================================================================
	cp ${SVN_LOG_GOLAUNCHER} svnchangelog-golauncherex-last.txt
	cp ${SVN_LOG_SHELLPLUGIN} svnchangelog-shellplugin-last.txt
	cp ${SVN_LOG_APPGAMEWIDGET} svnchangelog-appgamewidget-last.txt
exit 0
else
echo ======================================================================
echo ================打包失败,请注意保存当前更新的svnchangelog.txt中的log日志信息=================
echo ======================================================================
	DATE=`date +%Y%m%d%H%M`
	cp ${SVN_LOG_GOLAUNCHER} svnchangelog-golauncherex_copy_${DATE}.txt
	cp ${SVN_LOG_SHELLPLUGIN} svnchangelog-shellplugin_copy_${DATE}.txt
	cp ${SVN_LOG_APPGAMEWIDGET} svnchangelog-appgamewidget_copy_${DATE}.txt
exit 1
fi





