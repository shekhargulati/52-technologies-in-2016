FROM python:2.7
MAINTAINER "Shekhar Gulati"
RUN apt-get update -y
RUN apt-get install cron -yqq
COPY crontab /tmp/my_cron
COPY run-crond.sh run-crond.sh
RUN chmod -v +x /run-crond.sh
RUN touch /var/log/cron.log
ENV APP_DIR /app
COPY app.py requirements.txt $APP_DIR/
WORKDIR $APP_DIR
RUN pip install -r requirements.txt
# Run the command on container startup
CMD ["/run-crond.sh"]
