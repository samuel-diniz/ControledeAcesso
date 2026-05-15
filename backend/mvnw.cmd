@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET "__MVNW_ARG0_NAME__=%~nx0")
@SET ___MVNW_UGLY_WHITESPACE___=
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_WRAPPER_JAR_NOT_FOUND__=
@SET __MVNW_JAVAEXE__=

@REM Find a java binary
@IF NOT "%JAVA_HOME%"=="" (SET "__MVNW_JAVAEXE__=%JAVA_HOME%\bin\java.exe") ELSE (FOR /F "usebackq tokens=1* delims= " %%A IN (`where java 2^>NUL`) DO IF NOT DEFINED __MVNW_JAVAEXE__ (SET "__MVNW_JAVAEXE__=%%A"))

@IF "%__MVNW_JAVAEXE__%"=="" (
  ECHO Error: JAVA_HOME not set and java not found in PATH
  GOTO error
)

@SET MAVEN_PROJECTBASEDIR=%~dp0
@IF "%MAVEN_PROJECTBASEDIR:~-1%"=="\" SET "MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%"

@SET __MVNW_WRAPPER_JAR__=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
@SET __MVNW_WRAPPER_PROPS__=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties

@IF NOT EXIST "%__MVNW_WRAPPER_JAR__%" (
  SET __MVNW_WRAPPER_JAR_NOT_FOUND__=true
)

@REM Download the wrapper jar if not found
@IF "%__MVNW_WRAPPER_JAR_NOT_FOUND__%"=="true" (
  FOR /F "usebackq tokens=*" %%A IN (`"%__MVNW_JAVAEXE__%"  -classpath "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" ^
    org.apache.maven.wrapper.MavenWrapperMain 2^>NUL`) DO (SET __MVNW_CMD__=%%A)
  IF NOT DEFINED __MVNW_CMD__ (
    CALL :downloadWrapper "%__MVNW_WRAPPER_PROPS__%"
  )
)

@IF NOT EXIST "%__MVNW_WRAPPER_JAR__%" (
  CALL :downloadWrapper "%__MVNW_WRAPPER_PROPS__%"
)

@"%__MVNW_JAVAEXE__%" -classpath "%__MVNW_WRAPPER_JAR__%" ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  org.apache.maven.wrapper.MavenWrapperMain %*

@IF ERRORLEVEL 1 GOTO error
@GOTO end

:downloadWrapper
@SETLOCAL
@FOR /F "usebackq tokens=*" %%A IN (`FINDSTR /r "^wrapperUrl" "%~1" 2^>NUL`) DO SET WRAPPER_URL=%%A
@SET WRAPPER_URL=%WRAPPER_URL:wrapperUrl=%
@SET WRAPPER_URL=%WRAPPER_URL:~1%
@powershell -Command "(New-Object Net.WebClient).DownloadFile('%WRAPPER_URL%', '%__MVNW_WRAPPER_JAR__%')" 2>NUL
@IF ERRORLEVEL 1 (
  @powershell -Command "Invoke-WebRequest -Uri '%WRAPPER_URL%' -OutFile '%__MVNW_WRAPPER_JAR__%'" 2>NUL
)
@ENDLOCAL
@GOTO :EOF

:error
@SET __MVNW_ERROR__=1
:end
@IF NOT "%__MVNW_ERROR__%"=="" (EXIT /B 1)
