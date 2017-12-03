#!/bin/bash

#读取程序版本信息
echo "读取程序版本信息"
VERSION_CODE=$(cat AndroidManifest.xml | grep android:versionCode | grep -P '(([0-9])*)*' -o)
VERSION_NAME=$(cat AndroidManifest.xml | grep android:versionName | grep -P '(([0-9]|[a-z]|[A-Z])*\.([0-9]|[a-z]|[A-Z])*)*' -o)
TAG="VERSION_CODE"
OLD=$(cat build-sendmail-config.txt | grep ${TAG} | cut -d "=" -f 2)
sed -i 's/'${TAG}'='${OLD}'/'${TAG}'='${VERSION_CODE}'/g' build-sendmail-config.txt
TAG="VERSION_NAME"
OLD=$(cat build-sendmail-config.txt | grep ${TAG} | cut -d "=" -f 2)
sed -i 's/'${TAG}'='${OLD}'/'${TAG}'='${VERSION_NAME}'/g' build-sendmail-config.txt
echo "version_code:"${VERSION_CODE}
echo "version_name:"${VERSION_NAME}

#读取svn版本信息
echo "读取svn版本信息"
PROJECT_GOLAUNCHER=$(cat build-auto-config.txt | grep PROJECT_GOLAUNCHER | cut -d "=" -f 2)
PROJECT_SHELLPLUGIN=$(cat build-auto-config.txt | grep PROJECT_SHELLPLUGIN | cut -d "=" -f 2)
PROJECT_APPGAMEWIDGET=$(cat build-auto-config.txt | grep PROJECT_APPGAMEWIDGET | cut -d "=" -f 2)

function getSVNCode()
{
	cd $1
	SVN_UPDATED_REVISION=$(svn log -l1 | grep -m 1 ^r[0-9]* | awk '{print $1}'	| awk '{tmp=match($0, /[0-9]/) 
		r=substr($0, 0, tmp-1) 
		version=substr($0, tmp) 
		printf version }' ) 
	OLD_SVN_CODE=$(cat ../$2/build-sendmail-config.txt | grep $1"_SVN_CODE" | cut -d "=" -f 2)
	sed -i 's/'$1'_SVN_CODE='${OLD_SVN_CODE}'/'$1'_SVN_CODE='${SVN_UPDATED_REVISION}'/g' ../$2/build-sendmail-config.txt
	echo $1":"${SVN_UPDATED_REVISION}
	cd ..
}

cd ..
getSVNCode ${PROJECT_GOLAUNCHER} ${PROJECT_GOLAUNCHER}
getSVNCode ${PROJECT_SHELLPLUGIN} ${PROJECT_GOLAUNCHER}
getSVNCode ${PROJECT_APPGAMEWIDGET} ${PROJECT_GOLAUNCHER}
cd ${PROJECT_GOLAUNCHER}
