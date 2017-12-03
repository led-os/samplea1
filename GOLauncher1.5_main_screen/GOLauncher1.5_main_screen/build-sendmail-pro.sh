#!/bin/bash

bash build-sendmail-config.sh

PROJECT_GOLAUNCHER=$(cat build-auto-config.txt | grep PROJECT_GOLAUNCHER | cut -d "=" -f 2)
PROJECT_SHELLPLUGIN=$(cat build-auto-config.txt | grep PROJECT_SHELLPLUGIN | cut -d "=" -f 2)
PROJECT_APPGAMEWIDGET=$(cat build-auto-config.txt | grep PROJECT_APPGAMEWIDGET | cut -d "=" -f 2)

MAIL_TO=$(cat build-sendmail-config.txt | grep MAIL_SEND_TO | cut -d "=" -f 2)
MAIL_CC=$(cat build-sendmail-config.txt | grep MAIL_SEND_CC | cut -d "=" -f 2)
PROJECT_TITLE=$(cat build-sendmail-config.txt | grep PROJECT_TITLE | cut -d "=" -f 2)
SVN_CODE_GOLauncherEX=$(cat build-sendmail-config.txt | grep ${PROJECT_GOLAUNCHER}_SVN_CODE | cut -d "=" -f 2)
SVN_CODE_GOLauncherEX_ShellPlugin=$(cat build-sendmail-config.txt | grep ${PROJECT_SHELLPLUGIN}_SVN_CODE | cut -d "=" -f 2)
SVN_CODE_AppGameWidget=$(cat build-sendmail-config.txt | grep ${PROJECT_APPGAMEWIDGET}_SVN_CODE | cut -d "=" -f 2)
VERSION_CODE=$(cat build-sendmail-config.txt | grep VERSION_CODE | cut -d "=" -f 2)
VERSION_NAME=$(cat build-sendmail-config.txt | grep VERSION_NAME | cut -d "=" -f 2)
PACKAGE_DATE=`date +%Y-%m-%d%t%H:%M:%S`

PACKAGE_SIZE=$(ls -l "./bin/golauncher_release_align.apk" | cut -d " " -f 5)
echo "========="${PACKAGE_SIZE}

USERID=$(cat ./res/raw/uid.txt)

MAIL_SUBJEFT=$1
MAIL_SVN_ADDR=$2
if [ ! $MAIL_SUBJEFT ]; then 
echo "请输入包的版本名称（去掉V）："
read PACKAGE_VERSION
echo "请输入渠道号："
read CHANNEL_ID
SVN_CODE=$(cat ./res/raw/svn.txt)
MAIL_SUBJEFT="GOLauncherEX_v"${PACKAGE_VERSION}"_svn"${SVN_CODE}"_autobuild_"${CHANNEL_ID}"包("$(date +%Y%m%d)")"

TO_COMMIT_PKG=go_launcher_ex_v${PACKAGE_VERSION}_svn${SVN_CODE}_${CHANNEL_ID}.apk
MAIL_SVN_ADDR="https://svn.3g.net.cn/svn/GTP-docs/GOLauncherEX_for_apk/GOLauncherEX_v"${PACKAGE_VERSION}"/"${TO_COMMIT_PKG}

echo "MAIL_SUBJEFT="${MAIL_SUBJEFT}
echo "MAIL_SVN_ADDR="${MAIL_SVN_ADDR}

fi




#生成邮件正文，将纯文本转换为html格式

#first part
MAIL_CONTENT="<html><head>
		<style> 
table{border-right:1px solid #ccc;border-bottom:1px solid #ccc} 
table td{border-left:1px solid #ccc;border-top:1px solid #ccc;padding-left:8px;padding-right:8px;} 
*{font-size:14px; }
.important_text{color:#369; font-weight: bold;}
.log_info{color:#000; }
.normal_info{color:#666; }
.maintitle{font-size: 18px;}
.title{background-color:#369; border:1px solid #369; color:#fff}
</style>
	</head><body><div> <br/> <br/> <br/></div><table style='line-height:28px' cellpadding='0' cellspacing='0' width='800'>
			<tr>
				<td align='center' rowspan='5' class='maintitle' width='50%'>"${PROJECT_TITLE}"</td>
				<td class='normal_info' width='25%'>桌面主包</td>
				<td width='25%'><span class='important_text'>"${SVN_CODE_GOLauncherEX}"</span></td>
			</tr>
			<tr>
				<td class='normal_info'>3D部分</td>
				<td>"${SVN_CODE_GOLauncherEX_ShellPlugin}"</td>
			</tr>
			<tr>
				<td class='normal_info'>应用中心widget</td>
				<td>"${SVN_CODE_AppGameWidget}"</td>
			</tr>
		</table>"

QUDAO_INFO="<a href='"${MAIL_SVN_ADDR}"'>"${USERID}"</a>""<br>("${MAIL_SVN_ADDR}")"

MAIL_CONTENT=${MAIL_CONTENT}"<table style='line-height:28px;margin-top:10px;' cellpadding='0' cellspacing='0'  width='800'>
			<tr>
				<td class='normal_info' width='25%'>渠道</td>
				<td class='important_text' width='75%'>"${QUDAO_INFO}"</td>
			</tr>
			<tr>
				<td class='normal_info' width='25%'>VersionCode</td>
				<td width='75%' class='important_text'>"${VERSION_CODE}"</td>
			</tr>
			<tr>
				<td class='normal_info' width='25%'>VersionName</td>
				<td width='75%' class='important_text'>"${VERSION_NAME}"</td>
			</tr>
			<tr>
				<td class='normal_info' width='25%'>打包日期</td>
				<td width='75%'>"${PACKAGE_DATE}"</td>
			</tr>
			<tr>
				<td class='normal_info' width='25%'>文件大小(Byte)</td>
				<td width='75%'>${PACKAGE_SIZE}</td>
			</tr>
			<tr>
				<td class='normal_info' width='25%'>备注</td>
				<td width='75%'>无</td>
			</tr>
		</table>"

#添加一个工程的svn日志，参数1：工程标题名称，参数2：工程svn日志文件名
function addProjectLog()
{
	MAIL_CONTENT=${MAIL_CONTENT}"<table style='line-height:28px;margin-top:10px;'  cellpadding='0' cellspacing='0'  width='800'>
			<tr>
				<td align='center' colspan='3' class='title' width='100%'>"$1"</td>
			</tr>"

	#1 as log start
	VAL_FLAG_SHOULD_START_NEW_LOG=0
	VAL_FLAG_IN_WRITE_LOG=0
	VAL_FLAG_IS_EMPTY=0
	while read line
	do
		# replace " -" & " \-" to avoid html exception in thunderbird
		line=${line// -/-}
		line=${line// \\-/-}

		# log start with "--------------------"
		if [ "" != "${line}" ];then
		if [ "" != "${line}" -a "" = "${line//-/}" ];then
			if [ ${VAL_FLAG_IN_WRITE_LOG} -eq 1 ];then
				MAIL_CONTENT=${MAIL_CONTENT}"</td></tr>"
				VAL_FLAG_IN_WRITE_LOG=0
			fi
			#remenber new log start
			VAL_FLAG_SHOULD_START_NEW_LOG=1
		else
			if [ ${VAL_FLAG_SHOULD_START_NEW_LOG} -eq 1 ];then
				VAL_TMP=${line#*|}
				VAL_TMP2=${VAL_TMP#*|}
				MAIL_CONTENT=${MAIL_CONTENT}"<tr>
					<td class='normal_info' width='25%'>"${line%%|*}"</td>
					<td class='normal_info' width='25%'>"${VAL_TMP%%|*}"</td>
					<td class='normal_info' width='50%'>"${VAL_TMP2%%|*}"</td>
				</tr>"
			else
				if [ ${VAL_FLAG_IN_WRITE_LOG} -eq 1 ];then
					MAIL_CONTENT=${MAIL_CONTENT}"</br>"${line}
				else
					MAIL_CONTENT=${MAIL_CONTENT}"<tr><td colspan='3' width='100%'>"${line}
					VAL_FLAG_IN_WRITE_LOG=1
				fi
			fi
			VAL_FLAG_SHOULD_START_NEW_LOG=0
			VAL_FLAG_IS_EMPTY=1
		fi
		fi
	done < $2

	if [ ${VAL_FLAG_SHOULD_START_NEW_LOG} -eq 1 -a ${VAL_FLAG_IS_EMPTY} -eq 0 ];then
		MAIL_CONTENT=${MAIL_CONTENT}"<tr><td class='normal_info' align='center' colspan='3' width='100%'>无</td></tr>"
	fi
	MAIL_CONTENT=${MAIL_CONTENT}"</table>"
}

SVN_LOG_GOLAUNCHER=$(cat build-auto-config.txt | grep SVN_LOG_GOLAUNCHER | cut -d "=" -f 2)
SVN_LOG_SHELLPLUGIN=$(cat build-auto-config.txt | grep SVN_LOG_SHELLPLUGIN | cut -d "=" -f 2)
SVN_LOG_APPGAMEWIDGET=$(cat build-auto-config.txt | grep SVN_LOG_APPGAMEWIDGET | cut -d "=" -f 2)
addProjectLog "桌面主包" ${SVN_LOG_GOLAUNCHER}
addProjectLog "3D部分" ${SVN_LOG_SHELLPLUGIN}
addProjectLog "应用中心widget" ${SVN_LOG_APPGAMEWIDGET}

MAIL_CONTENT=${MAIL_CONTENT}"<table></table></body></html>"

#生成邮件命令
MAIL="to='"${MAIL_TO}"',cc='"${MAIL_CC}"',format='1',subject='"${MAIL_SUBJEFT}"',body='"${MAIL_CONTENT}"'"
thunderbird -compose ${MAIL}

