#!/bin/bash

env | egrep '^MY' | cat - /tmp/my_cron > /etc/cron.d/github-status-cron

cron && tail -f /var/log/cron.log
