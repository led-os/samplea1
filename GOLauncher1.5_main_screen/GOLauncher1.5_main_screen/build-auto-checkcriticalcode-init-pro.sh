# 在AndroidManifest.xml中获取VersionName值

function assert()
{
	if [ "$1" = "$2" ]; then
		echo "ok for" $3
	else
		echo "============error for " $3 "=" $1
		echo "#########but WANT TO "$2
	fi;
}

USERCHANNELID=$(cat check-critical-code.txt | grep USERCHANNELID | cut -d "=" -f 2)
USERID=$(cat ./res/raw/uid.txt)
assert ${USERID} ${USERCHANNELID} "uid"

GOSTOREUID=$(cat check-critical-code.txt | grep GOSTOREUID | cut -d "=" -f 2)
GOSTORE_ID=$(cat ./res/raw/gostore_uid.txt)
assert ${GOSTORE_ID} ${GOSTOREUID} "gostore_uid"

#函数说明：cat 文件：连接文件;grep "xxx":按xxx分类；grep -v "^//":对有//打头的取反操作；cut -d "=":按“=”分类；-f 2：取得分类后的第二段
#GO精品服务器地址--学文
GOSTORE_URL_HOST3=$(cat check-critical-code.txt | grep GOSTORE_URL_HOST3 | cut -d "=" -f 2)
JAVA_GOSTORE_URL_HOST3=$(cat ./src/com/jiubang/ggheart/apps/gowidget/gostore/common/GoStorePublicDefine.java | grep "目前使用的正式服务器地址" | grep -v "^//" | cut -d "=" -f 2 | cut -d ";" -f 1 )
assert ${JAVA_GOSTORE_URL_HOST3} ${GOSTORE_URL_HOST3} "gostore"

#消息中心服务器地址--金松
MSGCENTER_HOSTURL_BASE=$(cat check-critical-code.txt | grep MSGCENTER_HOSTURL_BASE | cut -d "=" -f 2)
JAVA_MSGCENTER_HOSTURL_BASE=$(cat ./src/com/jiubang/ggheart/apps/desks/diy/messagecenter/ConstValue.java | grep "正式服务器地址" | cut -d "=" -f 2 | cut -d ";" -f 1)
assert ${JAVA_MSGCENTER_HOSTURL_BASE} ${MSGCENTER_HOSTURL_BASE} "messagecenter"

#应用中心服务器地址--学文
APP_CENTER_URL_CHINA=$(cat check-critical-code.txt | grep APP_CENTER_URL_CHINA | cut -d "=" -f 2)
JAVA_APP_CENTER_URL_CHINA=$(cat ./src/com/jiubang/ggheart/appgame/appcenter/help/AppsNetConstant.java | grep "应用中心国内域名正式服务器地址" | cut -d "=" -f 2 | cut -d ";" -f 1)
assert ${JAVA_APP_CENTER_URL_CHINA} ${APP_CENTER_URL_CHINA} "appscenter"

APP_CENTER_URL_OTHERS=$(cat check-critical-code.txt | grep APP_CENTER_URL_OTHERS | cut -d "=" -f 2)
JAVA_APP_CENTER_URL_OTHERS=$(cat ./src/com/jiubang/ggheart/appgame/appcenter/help/AppsNetConstant.java | grep "应用中心国外域名正式服务器地址" | cut -d "=" -f 2 | cut -d ";" -f 1)
assert ${JAVA_APP_CENTER_URL_OTHERS} ${APP_CENTER_URL_OTHERS} "appscenter2"

#Scheduleaskandler.java--huyong
#AUTO_CHECK_DELAY=$(cat check-critical-code.txt | grep AUTO_CHECK_DELAY | cut -d "=" -f 2)
#echo ${AUTO_CHECK_DELAY} "==========="
#JAVA_AUTO_CHECK_DELAY=$(cat ./src/com/jiubang/ggheart/data/ScheduleTaskHandler.java | grep "AUTO_CHECK_DELAY")
#echo ${JAVA_AUTO_CHECK_DELAY}

#1/5屏广告
ADVERT_HOSTURL_BASE=$(cat check-critical-code.txt | grep ADVERT_HOSTURL_BASE | cut -d "=" -f 2)
JAVA_ADVERT_HOSTURL_BASE=$(cat ./src/com/jiubang/ggheart/components/advert/AdvertConstants.java | grep "正式广告地址" | cut -d "=" -f 2 | cut -d ";" -f 1)
assert ${JAVA_ADVERT_HOSTURL_BASE} ${ADVERT_HOSTURL_BASE} "1/5屏广告"

#AdmobSwitchUtil.java--ADmob广告
ADMOB_DEBUG=$(cat check-critical-code.txt | grep ADMOB_DEBUG | cut -d "=" -f 2)
JAVA_ADMOB_DEBUG=$(cat ./src/com/jiubang/ggheart/data/statistics/AdmobSwitchUtil.java | grep "private final static boolean DEBUG" | cut -d "=" -f 2 | cut -d ";" -f 1)
assert ${JAVA_ADMOB_DEBUG} ${ADMOB_DEBUG} "ADmob广告 in AdmobSwitchUtil.java"
ADMOB_URL=$(cat check-critical-code.txt | grep ADMOB_URL | cut -d "=" -f 2)
JAVA_ADMOB_URL=$(cat ./src/com/jiubang/ggheart/data/statistics/AdmobSwitchUtil.java | grep "正式admob地址" | cut -d "=" -f 2 | cut -d ";" -f 1)
assert ${JAVA_ADMOB_URL} ${ADMOB_URL} "ADmob广告 in AdmobSwitchUtil.java"

BANNER_ALL_ID=$(cat check-critical-code.txt | grep BANNER_ALL_ID | cut -d "=" -f 2)
JAVA_BANNER_ALL_ID=$(cat ./src/com/jiubang/ggheart/admob/AdConstanst.java | grep "private static final String BANNER_ALL_ID" | cut -d "=" -f 2 | cut -d ";" -f 1)
assert ${JAVA_BANNER_ALL_ID} ${BANNER_ALL_ID} "ADmob广告 in AdConstanst.java"
FULL_SCREEN_ALL_ID=$(cat check-critical-code.txt | grep FULL_SCREEN_ALL_ID | cut -d "=" -f 2)
JAVA_FULL_SCREEN_ALL_ID=$(cat ./src/com/jiubang/ggheart/admob/AdConstanst.java | grep "private static final String FULL_SCREEN_ALL_ID" | cut -d "=" -f 2 | cut -d ";" -f 1)
assert ${JAVA_FULL_SCREEN_ALL_ID} ${FULL_SCREEN_ALL_ID} "ADmob广告 in AdConstanst.java"

#####################to write
SIDEADVERT_BASE_HOSTURL_PRO=$(cat check-critical-code.txt | grep SIDEADVERT_BASE_HOSTURL_PRO | cut -d "=" -f 2)
JAVA_SIDEADVERT_BASE_HOSTURL_PRO=$(cat ./src/com/jiubang/ggheart/components/sidemenuadvert/SideAdvertConstants.java | grep "侧边栏广告正式地址" | cut -d "=" -f 2 | cut -d ";" -f 1)
assert ${JAVA_SIDEADVERT_BASE_HOSTURL_PRO} ${SIDEADVERT_BASE_HOSTURL_PRO} "功能表侧边栏--侧边广告"
GOWIDGET_BASE_HOSTURL_PRO=$(cat check-critical-code.txt | grep GOWIDGET_BASE_HOSTURL_PRO | cut -d "=" -f 2)
JAVA_GOWIDGET_BASE_HOSTURL_PRO=$(cat ./src/com/jiubang/ggheart/components/sidemenuadvert/SideAdvertConstants.java | grep "gowidget正式地址" | cut -d "=" -f 2 | cut -d ";" -f 1)
assert ${JAVA_GOWIDGET_BASE_HOSTURL_PRO} ${GOWIDGET_BASE_HOSTURL_PRO} "功能表侧边栏--gowidget"
GOWIDGET_BACKUP_HOSTURL_PRO=$(cat check-critical-code.txt | grep GOWIDGET_BACKUP_HOSTURL_PRO | cut -d "=" -f 2)
JAVA_GOWIDGET_BACKUP_HOSTURL_PRO=$(cat ./src/com/jiubang/ggheart/components/sidemenuadvert/SideAdvertConstants.java | grep "gowidget备用地址" | cut -d "=" -f 2 | cut -d ";" -f 1)
assert ${JAVA_GOWIDGET_BACKUP_HOSTURL_PRO} ${GOWIDGET_BACKUP_HOSTURL_PRO} "功能表侧边栏--gowidget备用地址"

#LauncherEnv.java
SIT=$(cat check-critical-code.txt | grep "SIT" | cut -d "=" -f 2)
JAVA_SIT=$(cat ./src/com/jiubang/ggheart/launcher/LauncherEnv.java | grep "boolean SIT" | cut -d "=" -f 2 | cut -d ";" -f 1)
assert ${JAVA_SIT} ${SIT} "LauncherEnv.java-SIT"

