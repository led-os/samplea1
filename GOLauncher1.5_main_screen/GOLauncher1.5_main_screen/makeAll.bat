@IF NOT EXIST gen mkdir gen
@IF EXIST apk RD /S /Q apk
@mkdir apk

::����汾��
@echo ������汾�ţ�
@set /p version=
@echo ********************* GOLauncherEX�汾��Ϊ %version% *********************

@echo off
@set /a StartS=%time:~6,2%
@set /a StartM=%time:~3,2%

::�ȱ������Ϳ�, �����ǩ����build.bat���
@call D:\Android\eclipse-SDK-3.6-win32\eclipse\plugins\org.apache.ant_1.7.1.v20100518-1145\bin\ant.bat -buildfile .\build-compile.xml

call build.bat 301 %version%
call build.bat 311 %version%
call build.bat 314 %version%
call build.bat 325 %version%
call build.bat 351 %version%
call build.bat 353 %version%
call build.bat 357 %version%
call build.bat 322 %version%
call build.bat 302 %version%
call build.bat 303 %version%
call build.bat 328 %version%
call build.bat 331 %version%
call build.bat 500 %version%
call build.bat 529 %version%

::�˴��ظ�ѭ������������޸�(200, 1, 210) ��200 Ϊ��ʵid��210Ϊ����id��1Ϊ����

::201-210 10��
for /L %%i in (201, 1, 210) do call build.bat %%i %version%

::311-315 5��
for /L %%i in (311, 1, 315) do call build.bat %%i %version%

::317-318 2��
for /L %%i in (317, 1, 318) do call build.bat %%i %version%

::350-370  21��
for /L %%i in (350, 1, 370) do call build.bat %%i %version%

::301-310 ʮ��
for /L %%i in (301, 1, 310) do call build.bat %%i %version%

::400-419 20��
for /L %%i in (400, 1, 419) do call build.bat %%i %version%

:: 321
call build.bat 321 %version%

::323-350 28��
for /L %%i in (323, 1, 350) do call build.bat %%i %version%

::371-399  20��
for /L %%i in (371, 1, 399) do call build.bat %%i %version%

::500-560  61��
for /L %%i in (500, 1, 560) do call build.bat %%i %version%

::450-470  21��
for /L %%i in (450, 1, 470) do call build.bat %%i %version%

::420-450 31��
for /L %%i in (420, 1, 450) do call build.bat %%i %version%

>.\res\raw\gostore_uid.txt set/p=200<nul
>.\res\raw\uid.txt set/p=200<nul

::������ʱ��
@set /a EndS=%time:~6,2%
@set /a EndM=%time:~3,2%

@set /a diffS_=%EndS%-%StartS%
@set /a diffM_=%EndM%-%StartM%

@if %diffS_% LSS 0 (
@set /a diffM_ = %diffM_% - 1
@set /a diffS_ = 60 + %diffS_%
)
@echo �������ʱ%diffM_%��%diffS_%��

pause