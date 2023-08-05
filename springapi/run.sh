#!/bin/bash

#这里可替换为你自己的执行程序
APP_NAME=${APP_NAME:-springapi}
JAR_FILE=${JAR_FILE:-springapi-2.0.final.jar}
ACTIVE_PROFILE=${ACTIVE_PROFILE:-dev}
SERVER_PORT=${SERVER_PORT:-8320}
TMP_DIR=${TMP_DIR:-./temp}
LOG_DIR=${LOG_DIR:-./logs}
JVM_OPTS="-Xms128M -Xmx1024M"
RUN_IN_CONTAINER=${RUN_IN_CONTAINER:-false}
SPRING_OPTS=
APP_OPTS=

if [ ! -d $TMP_DIR ]; then
  if ! mkdir -p $TMP_DIR; then
    echo "$TMP_DIR create failed."
    exit 1
  fi
fi

if [ ! -d $LOG_DIR ]; then
  if ! mkdir -p $LOG_DIR; then
    echo "$LOG_DIR create failed."
    exit 1
  fi
fi

# unset empty environment variables
#for var in $(compgen -A export -X '!FSU_*'); do
#  printf "%s=%s\n" "$var" "${!var@Q}"
#  if [ "${!var@Q}" = "''" ]; then
#    echo "--- unset ${var}"
#    unset "${var}"
#  fi
#done

#使用说明，用来提示输入参数?
usage() {
  echo "Usage: runfsu.sh [start|stop|restart|status]"
  exit 1
}

#检查程序是否在运行
#is_exist() {
#  if [ -e app.pid ]; then
#    pid=$(cat app.pid)
#  else
#    return 1
#  fi
#
#  #如果不存在返回1，存在返回0
#  if [ -z "${pid}" ]; then
#    return 1
#  else
#    return 0
#  fi
#}

#检查程序是否在运行
is_exist(){
  pid=`ps -ef|grep $APP_NAME|grep -v grep|awk '{print $2}' `
  #如果不存在返囿，存在返囿
  if [ -z "${pid}" ]; then
   return 1
  else
    return 0
  fi
}

#启动方法
start() {
  while getopts ':p:a:' OPT; do
    case $OPT in
    p)
      SERVER_PORT="$OPTARG"
      ;;
    a)
      ACTIVE_PROFILE="$OPTARG"
      ;;
    ?)
      echo "Error: invalid option" 1>&2
      exit 0
      ;;
    esac
  done

  shift $((OPTIND - 1))
#  SPRING_OPTS="-Dspring.config.location=./conf/ -Dlogback.configurationFile=./conf/logback.xml -Dspring.profiles.active=${ACTIVE_PROFILE} -Djava.io.tmpdir=${TMP_DIR} -Dserver.port=${SERVER_PORT} -Dspring.application.name=${APP_NAME}"
  SPRING_OPTS="-Dspring.profiles.active=${ACTIVE_PROFILE} -Djava.io.tmpdir=${TMP_DIR} -Dserver.port=${SERVER_PORT} -Dspring.application.name=${APP_NAME}"

  if is_exist; then
    echo "${APP_NAME} is already running. pid=${pid}."
  else
    if [ ${RUN_IN_CONTAINER} = 'true' ]; then
      # If running in container, start in foreground and use the logging driver provided by the container engine.
      java ${JVM_OPTS} ${SPRING_OPTS} ${APP_OPTS} -jar ./$JAR_FILE
    else
      nohup java ${JVM_OPTS} ${SPRING_OPTS} ${APP_OPTS} -jar ./$JAR_FILE > springapi.log 2>&1 & echo $! > app.pid
      echo "${APP_NAME} start success in $(cat app.pid)"
    fi
  fi
}

#停止方法
stop() {
  if is_exist; then
    kill $pid
    echo "$pid stopped"
  else
    echo "${APP_NAME} is not running"
  fi
}

#输出运行状态?
status() {
  if is_exist; then
    echo "${APP_NAME} is running. Pid is ${pid}"
  else
    echo "${APP_NAME} is NOT running."
  fi
}

#重启
restart() {
  stop
  start "$@"
}

while getopts ':h' OPT; do
  case $OPT in
  h)
    usage
    exit 0
    ;;
  ?)
    echo "Error: invalid option" 1>&2
    exit 0
    ;;
  esac
done

subcommand=$1
shift

#根据输入参数，选择执行对应方法，不输入则执行使用说明?
case "$subcommand" in
"start")
  start "$@"
  ;;
"stop")
  stop
  ;;
"status")
  status
  ;;
"restart")
  restart "$@"
  ;;
*)
  usage
  ;;
esac
