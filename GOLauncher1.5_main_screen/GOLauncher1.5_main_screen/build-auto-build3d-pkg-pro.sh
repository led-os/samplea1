#!/bin/sh
#echo Packaging uid=$1, please wait.....
#echo $1 | tee res/raw/uid.txt > res/raw/gostore_uid.txt

echo "========================================自动进行打包==========="

#外部（build-checkcode.sh）调用时传入2个参数，第一个为当前渠道号，第二个版本名称.

CHANNEL_ID=$1
if [ ! $CHANNEL_ID ]; then 
echo "请输入渠道号："
read CHANNEL_ID
fi

PACKAGE_VERSION=$2
if [ ! $PACKAGE_VERSION ]; then 
echo "请输入版本名称（不用输入‘v’）："
read PACKAGE_VERSION
fi

SVN_CODE=$(cat ./res/raw/svn.txt)
TO_COMMIT_PKG=go_launcher_ex_v${PACKAGE_VERSION}_svn${SVN_CODE}_${CHANNEL_ID}.apk
echo "====To commit pkg**"${TO_COMMIT_PKG}

#开始执行打包
#各人根据各自本机ant的路径来修改
#/opt/eclipse/plugins/org.apache.ant_1.8.2.v20120109-1030/bin/ant -buildfile build-3d-dex-proguard.xml
ANT_BUILD=$(cat build-auto-config.txt | grep ANT_BUILD_PATH | cut -d "=" -f 2)

if [ $IS_DEBUG_MODE = true ]; then
	echo "Debug模式，开始进行不混淆打包"
	sh ${ANT_BUILD} -buildfile build-3d-dex-NOproguard.xml
else
	echo "Release模式，开始进行发布版打包"
	sh ${ANT_BUILD} -buildfile build-3d-dex-proguard.xml
fi;


#确认是否自动安装到手机进行测试
while true;do
#stty -icanon min 0 time 100
echo -n "是否自动安装到手机进行测试？(yes or no)"
read Arg
case $Arg in
Y|y|YES|yes|Yes)
  echo "正在卸载手机中的主包程序..."
  adb uninstall com.gau.go.launcherex
  echo "正在卸载手机中的3D插件包程序..."
  adb uninstall com.gau.golauncherex.plugin.shell
  echo "正在安装测试包到手机中..."
  adb install ./bin/golauncher_release_align.apk
  break;;
N|n|NO|no)
  break;;
"")  #Autocontinue
  break;;
esac
done


#确认检查是否正确
while true;do
#stty -icanon min 0 time 50
echo -n "上述安装包，是否立即上传到svn？(yes or no)"
read Arg
case $Arg in
Y|y|YES|yes|Yes)
  break;;
N|n|NO|no)
echo "exit..."
  exit;;
"")  #Autocontinue
  break;;
esac
done


#创建目录
if [ ! -d ./apk_svn_test ]; then
mkdir "./apk_svn_test"
fi

#上传到指定服务器地址
cd apk_svn_test
#if [ ! -d "GOLauncherEX_v${PACKAGE_VERSION}/.svn" ]; then
#svn add GOLauncherEX_v${PACKAGE_VERSION}
#fi
svn checkout https://svn.3g.net.cn/svn/GTP-docs/GOLauncherEX_for_apk/GOLauncherEX_v${PACKAGE_VERSION}/ 
cp ../bin/golauncher_release_align.apk ./GOLauncherEX_v${PACKAGE_VERSION}/${TO_COMMIT_PKG}

cd GOLauncherEX_v${PACKAGE_VERSION}
svn add ${TO_COMMIT_PKG}
svn commit -m "自动提交的新测试包"${TO_COMMIT_PKG} ${TO_COMMIT_PKG}
if [ $? -eq 0 ]; then
echo =========package ${TO_COMMIT_PKG} commit success=============
echo =========svn=https://svn.3g.net.cn/svn/GTP-docs/GOLauncherEX_for_apk/GOLauncherEX_v${PACKAGE_VERSION}/${TO_COMMIT_PKG}
#将测试包svn地址插入到changelog中
cd ..
cd ..
#sed -i '1ihttps://svn.3g.net.cn/svn/GTP-docs/GOLauncherEX_for_apk/GOLauncherEX_v'${PACKAGE_VERSION}'/'${TO_COMMIT_PKG} svnchangelog.txt

MAIL_TITLE="GOLauncherEX_v"${PACKAGE_VERSION}"_svn"${SVN_CODE}"_autobuild_200包("$(date +%Y%m%d)")"
if(test ${IS_PRIVATE_TEST})
then
  MAIL_TITLE="GOLauncherEX_v"${PACKAGE_VERSION}"_svn"${SVN_CODE}"_autobuild_200去中文内测包("$(date +%Y%m%d)")"
fi

MAIL_PKG_INFO="svn"${SVN_CODE}"_200包"
MAIL_SVN_ADDR="https://svn.3g.net.cn/svn/GTP-docs/GOLauncherEX_for_apk/GOLauncherEX_v"${PACKAGE_VERSION}"/"${TO_COMMIT_PKG}
bash build-sendmail-pro.sh ${MAIL_TITLE} ${MAIL_SVN_ADDR}

#上传build-sendmail-config.txt文件到svn
svn commit -m "更新本次测试包的版本code信息" build-sendmail-config.txt

exit 0
else
echo =========package ${TO_COMMIT_PKG} failded=================
exit 1
fi



