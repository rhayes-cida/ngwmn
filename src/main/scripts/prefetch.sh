#!/bin/bash

HOSTPORT=localhost:8080

mkdir -p /tmp/gwdp/prefetch
cd /tmp/gwdp/prefetch
# all agencies: 'IL+EPA' 'MPCA' 'NJGS' 'ISWS' 'MN+DNR' 'TWDB' 'USGS' 'MBMG'
# Note that a 20-second delay is too long for USGS wells (there are >1000)
for AGENCY_CD in 'NJGS' 'IL+EPA'
do
    url="http://${HOSTPORT}/ngwmn/wells?servlet=prefetch&agency_cd=${AGENCY_CD}&type=LOG&type=QUALITY&type=WATERLEVEL"
    wget --background --recursive --no-directories --append-output="wget-log.${AGENCY_CD}" --level=2 --wait=20 "${url}"
done
