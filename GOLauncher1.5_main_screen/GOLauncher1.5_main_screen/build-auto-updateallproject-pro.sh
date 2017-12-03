
echo "========================================项目工程更新==========="
#确认检查是否正确
while true;do
#stty -icanon min 0 time 100
echo -n "是否需要更新各项工程代码(yes or no)"
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


#svn log file
SVN_LOG_GOLAUNCHER=$(cat build-auto-config.txt | grep SVN_LOG_GOLAUNCHER | cut -d "=" -f 2)
SVN_LOG_SHELLPLUGIN=$(cat build-auto-config.txt | grep SVN_LOG_SHELLPLUGIN | cut -d "=" -f 2)
SVN_LOG_APPGAMEWIDGET=$(cat build-auto-config.txt | grep SVN_LOG_APPGAMEWIDGET | cut -d "=" -f 2)

SVN_PRESVNLOGCODE_GOLauncherEX=$(cat build-sendmail-config.txt | grep ${PROJECT_GOLAUNCHER}_SVN_CODE | cut -d "=" -f 2)
SVN_PRESVNLOGCODE_GOLauncherEX_ShellPlugin=$(cat build-sendmail-config.txt | grep ${PROJECT_SHELLPLUGIN}_SVN_CODE | cut -d "=" -f 2)
SVN_PRESVNLOGCODE_AppGameWidget=$(cat build-sendmail-config.txt | grep ${PROJECT_APPGAMEWIDGET}_SVN_CODE | cut -d "=" -f 2)


#更新工程，参数1：工程路径，参数2：svn日志文件名，参数3：主工程路径
function updateProject()
{
	cd $1
	echo "更新："$1"("pwd")"
	SVN_CUR_REVISION=$(svn log -l1 | grep -m 1 ^r[0-9]* | awk '{print $1}' | awk '{
		tmp=match($0, /[0-9]/)
		r=substr($0, 0, tmp-1)
		version=substr($0, tmp)
		printf version }' )
	echo "Current svn version:" ${SVN_CUR_REVISION}

	let SVN_CUR_REVISION+=1
	
	SVN_PRE_REVISION=$4
 	let SVN_PRE_REVISION+=1
	if [ "" = "${SVN_PRE_REVISION}" ]; then 
		SVN_PRE_REVISION=${SVN_CUR_REVISION}
	fi
	echo "Pre send mail svn version=="${SVN_PRE_REVISION}

	(svn log --revision ${SVN_PRE_REVISION}:HEAD) >> ../$3/$2

	echo "svn update"
	svn update
	SVN_UPDATED_REVISION=$(svn log -l1 | grep -m 1 ^r[0-9]* | awk '{print $1}' | awk '{
		tmp=match($0, /[0-9]/)
		r=substr($0, 0, tmp-1)
		version=substr($0, tmp)
		printf version }' ) 
	echo "Updated svn version:" ${SVN_UPDATED_REVISION}

	#如果当前是主工程，保存svn号
	if [ "$1" = "$3" ];then
		echo ${SVN_UPDATED_REVISION} > ./res/raw/svn.txt
	fi

	cd ..
}

#代码更新
updateProject ${PROJECT_GOLAUNCHER} ${SVN_LOG_GOLAUNCHER} ${PROJECT_GOLAUNCHER} ${SVN_PRESVNLOGCODE_GOLauncherEX}
updateProject ${PROJECT_SHELLPLUGIN} ${SVN_LOG_SHELLPLUGIN} ${PROJECT_GOLAUNCHER} ${SVN_PRESVNLOGCODE_GOLauncherEX_ShellPlugin}
updateProject ${PROJECT_APPGAMEWIDGET} ${SVN_LOG_APPGAMEWIDGET} ${PROJECT_GOLAUNCHER} ${SVN_PRESVNLOGCODE_AppGameWidget}

#增加对图片文件的更新处理
cd ${PROJECT_SHELLPLUGIN}
bash changepngjustforcn.sh
cd ..


