#if (!${payaraVersion})
#if (${jakartaEEVersion} == '8')
#set ($payaraVersion = "5.2022.5")
#else
#set ($payaraVersion = "6.2023.8")
#end
#end
#if (${platform} == 'server')
#if (${javaVersion} == '17')
#if (${profile} == 'full')
#set ($baseImage = "payara/server-full:${payaraVersion}-jdk17")
#else
#set ($baseImage = "payara/server-web:${payaraVersion}-jdk17")
#end
#elseif (${javaVersion} == '11')
#if (${profile} == 'full')
#set ($baseImage = "payara/server-full:${payaraVersion}-jdk11")
#else
#set ($baseImage = "payara/server-web:${payaraVersion}-jdk11")
#end
#else
#if (${profile} == 'full')
#set ($baseImage = "payara/server-full:${payaraVersion}")
#else
#set ($baseImage = "payara/server-web:${payaraVersion}")
#end
#end
#elseif (${platform} == 'micro')
#if (${javaVersion} == '17')
#set ($baseImage = "payara/micro:${payaraVersion}-jdk17")
#elseif (${javaVersion} == '11')
#set ($baseImage = "payara/micro:${payaraVersion}-jdk11")
#else
#set ($baseImage = "payara/micro:${payaraVersion}")
#end
#end
FROM $baseImage
COPY target/${artifactId}-${version}.war $DEPLOY_DIR
