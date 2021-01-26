@echo off
cls
if exist build rmdir /s /q build
mkdir build
cd src
javac -d ../build com/krzem/jwt_auth/Main.java&&jar cvmf ../manifest.mf ../build/jwt_auth.jar -C ../build *&&goto run
cd ..
goto end
:run
cd ..
pushd "build"
for /D %%D in ("*") do (
	rd /S /Q "%%~D"
)
for %%F in ("*") do (
	if /I not "%%~nxF"=="jwt_auth.jar" del "%%~F"
)
popd
cls
java -jar build/jwt_auth.jar
:end
