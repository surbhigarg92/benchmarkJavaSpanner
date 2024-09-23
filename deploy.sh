#!/usr/bin/env sh

set -euo pipefail

function iterate_over() {
  let num=${1}
  let num_i=$(( num ))
  echo $num_i
  for i in $(seq 1 $num);
  do
    sed -e "s|{{num}}|$i|" k8s.template.yaml | kubectl apply -f -
  done
#  svcpaths=$(cat k8s_service.template.yaml)
#  for i in $(seq 1 $num);
#  do
#    tempPath=$(cat <<EOF
#            - path: /v{{num}}
#              pathType: ImplementationSpecific
#              backend:
#                service:
#                  name: spannerdemo{{num}}
#                  port:
#                    number: 600{{num}}
#EOF)
#  printf $tempPath
#  svcpaths=$(echo $tempPath | sed -e "s|{{num}}|$i|" )
#  done
#  cat $svcpaths
}


iterate_over "$@"