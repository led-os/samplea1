# 在AndroidManifest.xml中获取VersionName值
echo "========================================关键代码检查==========="
bash build-auto-checkcriticalcode-init-pro.sh

echo "========================================图片资源尺寸大小检查==========="
bash build-auto-checkdrawable-pro.sh

echo "========================================AndroidManifest.xml."
PACKAGE_VERSION=$(cat AndroidManifest.xml | grep android:version)
VERSION=$(cat ./res/values/version.xml | grep curVersion)
VERSIONNUMBER=$(cat ./res/values/version.xml | grep curVersion | grep -o "[0-9].*[0-9]")
CHANGELOG_VERSION=$(cat ./res/values/changelog.xml | grep updateVersion)
LARGEHEAP=$(cat AndroidManifest.xml | grep android:largeHeap)
echo ${PACKAGE_VERSION}
echo "res/values/version.xml========" ${VERSION} 
echo "res/values/changelog.xml======" ${CHANGELOG_VERSION}
echo "package_version===============" ${VERSIONNUMBER}
echo  ${LARGEHEAP}
echo "========================================uid.txt"
USERID=$(cat ./res/raw/uid.txt)
echo "uid=" ${USERID}
GOSTORE_ID=$(cat ./res/raw/gostore_uid.txt)
echo "gostore_uid=" ${GOSTORE_ID}

echo "========================================ErrorReporter.java"
#SVNINREPORTER=$(cat ./src/org/acra/ErrorReporter.java | grep "String SVN = " )
#echo ${SVNINREPORTER}
OOM_FLAG=$(cat ./src/org/acra/ErrorReporter.java | grep track.contains\(OUT_OF_MEMORY_FITER_EA\) )
echo ${OOM_FLAG}

echo "========================================ShellAdmin.java"
GLVIEW_DEBUG=$(cat ../GOLauncherEX_ShellPlugin/src/com/jiubang/shell/ggheart/plugin/ShellAdmin.java | grep GLView.DBG)
echo ${GLVIEW_DEBUG}

#确认检查是否正确
while true;do
#stty -icanon min 0 time 150
echo -n "上述关键代码，是否确认正确？(yes or no)"
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
 
echo
echo "Continue to update svn..."
#执行打包动作
BUILD_VERSION=$(cat AndroidManifest.xml | grep android:versionName | grep -P '(([0-9]|[a-z]|[A-Z])*\.([0-9]|[a-z]|[A-Z])*)*' -o)
bash build-auto-build3d-pkg-pro.sh ${USERID} ${VERSIONNUMBER}
#sh build-auto-build3d-pkg.sh ${BUILD_VERSION}


