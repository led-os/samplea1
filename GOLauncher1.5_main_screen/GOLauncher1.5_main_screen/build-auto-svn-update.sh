# 获得SVN版本号

# svn log --revision BASE:HEAD
# shows all commit logs since you last updated

# svn log |head -100 //查看最新100行记录

#(svn log -r {2013-08-20}:HEAD | perl -nwle 'print unless m/^((r\d)|($))/') > svnchangelog.txt
# 只显示log提交信息

SVN_CUR_REVISION=$(svn log -l1 | grep -m 1 ^r[0-9]* | awk '{print $1}' | awk '{
tmp=match($0, /[0-9]/)
r=substr($0, 0, tmp-1)
version=substr($0, tmp)
printf version }' )
echo "Current svn version " ${SVN_CUR_REVISION}

#(svn log --revision ${SVN_CUR_REVISION}:HEAD | perl -nwle 'print unless m/^((r\d)|($))/') >> svnchangelog.txt
#新创建一个svnchangelog文件并空出第一行
echo > svnchangelog.txt
echo "===============================桌面主包======================" >> svnchangelog.txt
(svn log --revision ${SVN_CUR_REVISION}:HEAD) >> svnchangelog.txt
echo "show all log begin to update"

svn update
SVN_UPDATED_REVISION=$(svn log -l1 | grep -m 1 ^r[0-9]* | awk '{print $1}' | awk '{
tmp=match($0, /[0-9]/)
r=substr($0, 0, tmp-1)
version=substr($0, tmp)
printf version }' ) 
echo ${SVN_UPDATED_REVISION} > ./res/raw/svn.txt
echo "Updated svn version " ${SVN_UPDATED_REVISION}

#cat svnchangelog.txt
