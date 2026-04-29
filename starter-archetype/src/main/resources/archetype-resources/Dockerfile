#if (!${payaraVersion})
#if (${jakartaEEVersion} == '8')
#set ($payaraVersion = "5.2022.5")
#else
#set ($payaraVersion = "6.2024.2")
#end
#end
#if (${platform} == 'server')
#if (${profile} == 'full')
#set ($imageRepo = "payara/server-full")
#else
#set ($imageRepo = "payara/server-web")
#end
#elseif (${platform} == 'micro')
#set ($imageRepo = "payara/micro")
#end
#if (${dockerJdkTagSupported} == 'true')
#if (${javaVersion} == '25')
#set ($baseImage = "${imageRepo}:${payaraVersion}-jdk25")
#elseif (${javaVersion} == '21')
#set ($baseImage = "${imageRepo}:${payaraVersion}-jdk21")
#elseif (${javaVersion} == '17')
#set ($baseImage = "${imageRepo}:${payaraVersion}-jdk17")
#elseif (${javaVersion} == '11')
#set ($baseImage = "${imageRepo}:${payaraVersion}-jdk11")
#else
#set ($baseImage = "${imageRepo}:${payaraVersion}")
#end
#else
#set ($baseImage = "${imageRepo}:${payaraVersion}")
#end
#if (${build} == 'maven')
#set ($artifactParentDir = "target")
#else
#set ($artifactParentDir = "build/libs")
#end
FROM $baseImage
COPY $artifactParentDir/${artifactId}-${version}.war $DEPLOY_DIR
