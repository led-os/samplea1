MAIL_TO=$(cat build-auto-sendmailconfig.txt | grep MAIL_SEND_TO | cut -d "=" -f 2)
MAIL_CC=$(cat build-auto-sendmailconfig.txt | grep MAIL_SEND_CC | cut -d "=" -f 2)
MAIL_SUBJEFT=$1

#生成邮件正文，将纯文本转换为html格式
MAIL_CONTENT_PKG_INFO=$3
MAIL_CONTENT_SVN_ADDR=$2
MAIL_CONTENT="<html><body><p>"${MAIL_CONTENT_PKG_INFO}"</br><a href='"${MAIL_CONTENT_SVN_ADDR}"'>"${MAIL_CONTENT_SVN_ADDR}"</a></br>"

#读取SVN记录
while read line
do
MAIL_CONTENT=${MAIL_CONTENT}"</br>"${line}
done < svnchangelog.txt
MAIL_CONTENT=${MAIL_CONTENT}"</p></body></html>"

#生成邮件命令
MAIL="to='"${MAIL_TO}"',cc='"${MAIL_CC}"',format='1',subject='"${MAIL_SUBJEFT}"',body='"${MAIL_CONTENT}"'"
thunderbird -compose ${MAIL}

