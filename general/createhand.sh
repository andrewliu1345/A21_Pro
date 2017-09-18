#!/usr/bin/env bash
export ProjectPath=$(cd "../$(dirname "$1")"; pwd)
export TargetClassName="com.joesmate.idcreader.HandImage"

export SourceFile="${ProjectPath}/hidapi/src/main/java"
export TargetPath="${ProjectPath}/hidapi/src/main/jni"

cd "${SourceFile}"
javah -d ${TargetPath} -classpath "${SourceFile}" "${TargetClassName}"
echo -d ${TargetPath} -classpath "${SourceFile}" "${TargetClassName}"

#javah -classpath F:\Work\A21\SDK_API\Android\A21\general\src\main\java -d F:\Work\A21\SDK_API\Android\A21\general\src\main\cpp -jni com.joesmate.general.utils.tools